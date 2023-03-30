import org.scalatest.funsuite._

/**
  * vList order matters
  */
class MyO(s: String, oe: Option[Expr], vs: List[Value]) {

    // Parser test
    val parser = new Parser
    val interpreters: List[Interpreter] = List(
        new Interpreter(new EvalConditions(LexicalScope, NoConversions, EagerCondition)),
        new Interpreter(new EvalConditions(LexicalScope, NoConversions, LazyCondition)),
        new Interpreter(new EvalConditions(LexicalScope, ImplicitConversions, EagerCondition)),
        new Interpreter(new EvalConditions(LexicalScope, ImplicitConversions, LazyCondition)), 
        new Interpreter(new EvalConditions(DynamicScope, NoConversions, EagerCondition)),
        new Interpreter(new EvalConditions(DynamicScope, NoConversions, LazyCondition)),
        new Interpreter(new EvalConditions(DynamicScope, ImplicitConversions, EagerCondition)),
        new Interpreter(new EvalConditions(DynamicScope, ImplicitConversions, LazyCondition)),  
    )

    // interprete test
    def exec: Unit = {
        def myTest(interpreter: Interpreter, e: Expr, v: Value): Unit = {
            val lFound = interpreter.evaluate(e){ r => r }
            val vFound = lFound.reverse.head
            try {
                v match {
                    case LettuceError(_) => vFound match {
                        case LettuceError(_) => assert(true)
                        case _ => assert(false)
                    }
                    case _ => assert(v == vFound)
                }
            } catch {
                case _: Throwable => {
                    println(lFound.foldLeft(s"FAILED test\n\t$interpreter"){
                        (s, e) => s"$s\n\t- $e"
                    })
                    assert(v == vFound)
                }
            }
        }

        // PARSER test
        val e = parser.parse(s)
        oe match {
            case None => assert(true)
            case Some(eExpected) => assert(e == eExpected)
        }

        // INTERPRETER tests
        assert(interpreters.length == vs.length)
        (interpreters zip vs) foreach {
            case (i, v) => myTest(i, e, v)
        }
    }
}

object MyO {
    def apply(s: String, v: Value): MyO = MyO(s, None, v)
    def apply(s: String, e: Expr, v: Value): MyO = MyO(s, Some(e), v)
    def apply(s: String, oe: Option[Expr], v: Value): MyO = MyO(s, oe, (1 to 8).toList map { _ => v})
    def apply(s: String, vs: List[Value]): MyO = new MyO(s, None, vs)
    def apply(s: String, e: Expr, vs: List[Value]): MyO = new MyO(s, Some(e), vs)
    def apply(s: String, oe: Option[Expr], vs: List[Value]): MyO = new MyO(s, oe, vs)
}

class IntegrationTest extends AnyFunSuite {
    test("number"){
        val v = N(1)
        MyO("1", v, v).exec
    }

    test("boolean"){
        val v = B(true)
        MyO("true", v, v).exec
    }

    test("string"){
        val v = S("hello")
        MyO("'hello'", v, v).exec
    }

    test("string plus"){
        val v: Value = S("552.0")
        val tmp = new InterpreterError("failed type conversions")
        val err: Value = LettuceError(tmp)
        MyO("'55' + 2", List(err, err, v, v, err, err, v, v)).exec
    }

    test("string mult"){
         val v = N(110)  // 55 * 2
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO("'55' * 2", List(err, err, v, v, err, err, v, v)).exec
    }

    test("ifelse") {
        val v = N(1)
        MyO("if ( true ) 1 else 2", v).exec
    }

    test("Cmp") {
        MyO("1 >= 2", B(false)).exec
        MyO("'hi' >= 'hi'", B(true)).exec
        MyO("1 > 2", B(false)).exec
        MyO("'hi' > 'hi'", B(false)).exec
        MyO("1 <= 2", B(true)).exec
        MyO("'hi' <= 'hi'", B(true)).exec
        MyO("1 < 2", B(true)).exec
        MyO("'hi' < 'hi'", B(false)).exec
    }

    test("Equality") {
        val err = LettuceError(new InterpreterError("foo"))
        val f = B(false)
        val t = B(true)
        MyO(s"1 == 2", f).exec
        MyO(s"'5' == 5", List(err, err, t, t, err, err, t, t)).exec
        MyO(s"1 === 2", f).exec
        MyO(s"'5' === 5", List(err, err, f, f, err, err, f, f)).exec
        MyO(s"1 != 2", t).exec
        MyO(s"'5' != 5", List(err, err, f, f, err, err, f, f)).exec
        MyO(s"1 !== 2", t).exec
        MyO(s"'5' !== 5", List(err, err, t, t, err, err, t, t)).exec
    }

    test("fact4") {
        val v = N(24)  // 4 * 3  2
        MyO("letrec f = function(x) if (1 >= x) 1 else x * f(x - 1) in f(4)", v).exec
    }

    test("trycatch"){
        val s = "try { true + 1 } catch { 0 }"
        val e = TryCatch(Binary(Plus, B(true), N(1)), N(0))
        val vs = List(N(0), N(0), N(2), N(2), N(0), N(0), N(2), N(2))
        MyO(s, e, vs).exec
    }

    test("variance 1") {
        val s = "let x = 2 + true in let f = function(y) x + y in let x = x + 1 in f(x + 1)"
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO(s, List(err, err, N(8), N(8), err, err, N(9), N(9))).exec
    }

    test("variance 2") {
        val s = "let x = 2 + 1 in let f = function(y) x in let x = x + true in f(x * false)"
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO(s, List(err, N(3), N(3), N(3), err, err, N(4), N(4))).exec
    }

    test("variance 3") {
        val s = "let x = 2 + 1 in let f = function(y) x in let x = try { x + true } catch { x + 1 }in f(try { x * false } catch { x * 0 })"
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO(s, List(N(3), N(3), N(3), N(3), N(4), N(4), N(4), N(4))).exec
    }

    
}
