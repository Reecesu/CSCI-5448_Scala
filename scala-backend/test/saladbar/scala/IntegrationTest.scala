import org.scalatest.funsuite._
import saladbar._


/**
  * TODO: Refactor tests to a single format like this but with stricter
  * testing policies to create easier extensibility.
  */


/**
  * MyO
  * 
  * an object used for testing
  * create an object
  * execute `.exec` to run the tests
  * 
  * LEXICAL:
  *    no conversions vs implicit (eager/lazy)
  * DYNAMIC:
  *    no conversions vs implicit (eager/lazy)
  *
  * @param s
  * @param oe
  * @param vs
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


/**
  * A collection of factories
  * 
  * OO PATTERN: Factory
  */
object MyO {
    def apply(s: String, v: Value): MyO = MyO(s, None, v)
    def apply(s: String, e: Expr, v: Value): MyO = MyO(s, Some(e), v)
    def apply(s: String, oe: Option[Expr], v: Value): MyO = MyO(s, oe, (1 to 8).toList map { _ => v})
    def apply(s: String, vs: List[Value]): MyO = new MyO(s, None, vs)
    def apply(s: String, e: Expr, vs: List[Value]): MyO = new MyO(s, Some(e), vs)
    def apply(s: String, oe: Option[Expr], vs: List[Value]): MyO = new MyO(s, oe, vs)
}


/**
  * IntegrationTest
  * 
  * execute all possible semantics combinations on a given expression
  */
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
        // TODO: improve toString logic so "55" + 2 is "552.0" and not "'55'2.0"
        // this test is correct, we are failing atm.
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


    test("and") {
        MyO("true && 5", List(N(5), N(5), N(5), N(5), N(5), N(5), N(5), N(5))).exec
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO("5 && true", List(err, err, B(true), B(true), err, err, B(true), B(true))).exec
    }

    test("or") {
        MyO("false || 0", List(N(0), N(0), N(0), N(0), N(0), N(0), N(0), N(0))).exec
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO("0 || false", List(err, err, B(false), B(false), err, err, B(false), B(false))).exec
    }

    test("neg") {
        val v = N(2)
        MyO("-(-(2))", List(v, v, v, v, v, v, v, v)).exec
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO("-(-(true)) + 1", List(err, err, v, v, err, err, v, v)).exec
    }

    test("not") {
        val v = B(false)
        MyO("!(!(false))", List(v, v, v, v, v, v, v, v)).exec
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO("!(3155) && idk", List(err, err, v, v, err, err, v, v)).exec
    }

    test("sin") {
        val v = N(0)
        MyO("sin(0)", List(v, v, v, v, v, v, v, v)).exec
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO("sin(!(3155))", List(err, err, v, v, err, err, v, v)).exec
    }

    test("cos") {
        val v = N(1)
        MyO("cos(0)", List(v, v, v, v, v, v, v, v)).exec
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO("cos(!(3155))", List(err, err, v, v, err, err, v, v)).exec
    }

    test("log") {
        val v = N(0)
        MyO("log(1)", List(v, v, v, v, v, v, v, v)).exec
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO("log(!(''))", List(err, err, v, v, err, err, v, v)).exec
    }

    test("exp") {
        val v = N(1)
        MyO("exp(0)", List(v, v, v, v, v, v, v, v)).exec
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO("exp(!(3155))", List(err, err, v, v, err, err, v, v)).exec
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

    test("variance 4") {
        val s = "let x = sin(!('') - 1) + 3 in let f = function(y) x in let x = try { x + true } catch { x + 1 }in f(try { x * false } catch { x * 0 })"
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO(s, List(err, err, N(3), N(3), err, err, N(4), N(4))).exec
    }

    test("div by 0.0 with try catch") {
        // TODO: consider change to no conversion semantic to have same infinity
        val s = "try { 5.0/0.0 } catch { 1.0 }"
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        // TODO: pretify below
        val v2 = N(Double.PositiveInfinity)
        val v1 = v2
        MyO(s, List(v1, v1, 
                    v2, v2, 
                    v1, v1, 
                    v2, v2)).exec
    }

    test("seq") {
        val s = "1 ; 2"
        val v = N(2)
        MyO(s, List(v, v, v, v, v, v, v, v)).exec

        val sp = "let x = 1 + 2 in x * x; x - 1"
        MyO(sp, List(v, v, v, v, v, v, v, v)).exec
    }

    // REMINDER:
        // LEXICAL Block: NC, then Implicite
    // new Interpreter(new EvalConditions(LexicalScope, NoConversions, EagerCondition)),
    // new Interpreter(new EvalConditions(LexicalScope, NoConversions, LazyCondition)),
    // new Interpreter(new EvalConditions(LexicalScope, ImplicitConversions, EagerCondition)),
    // new Interpreter(new EvalConditions(LexicalScope, ImplicitConversions, LazyCondition)), 
        // Dynamic Block: NC, then Implicite
    // new Interpreter(new EvalConditions(DynamicScope, NoConversions, EagerCondition)),
    // new Interpreter(new EvalConditions(DynamicScope, NoConversions, LazyCondition)),
    // new Interpreter(new EvalConditions(DynamicScope, ImplicitConversions, EagerCondition)),
    // new Interpreter(new EvalConditions(DynamicScope, ImplicitConversions, LazyCondition)), 

}
