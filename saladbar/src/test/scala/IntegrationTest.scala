import org.scalatest.funsuite._

case class MyO(s: String, vLex: Value, vDyn: Value, vLexImplicit: Value, vDynImplicit: Value) {
    val parser = new Parser
    val e = parser.parse(s)

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
                    println(interpreter)
                    lFound foreach println
                    assert(v == vFound)
                }
            }
        }
        val interpreter = new Interpreter(new EvalConditions(LexicalScope, NoConversions, EagerCondition))
        myTest(interpreter, e, vLex)
        interpreter.setEvalConditions(new EvalConditions(DynamicScope, NoConversions, EagerCondition))
        myTest(interpreter, e, vDyn)
        interpreter.setEvalConditions(new EvalConditions(LexicalScope, ImplicitConversions, EagerCondition))
        myTest(interpreter, e, vLexImplicit)
        interpreter.setEvalConditions(new EvalConditions(DynamicScope, ImplicitConversions, EagerCondition))
        myTest(interpreter, e, vDynImplicit)

        interpreter.setEvalConditions(new EvalConditions(LexicalScope, NoConversions, LazyCondition))
        myTest(interpreter, e, vLex)
        interpreter.setEvalConditions(new EvalConditions(DynamicScope, NoConversions, LazyCondition))
        myTest(interpreter, e, vDyn)
        interpreter.setEvalConditions(new EvalConditions(LexicalScope, ImplicitConversions, LazyCondition))
        myTest(interpreter, e, vLexImplicit)
        interpreter.setEvalConditions(new EvalConditions(DynamicScope, ImplicitConversions, LazyCondition))
        myTest(interpreter, e, vDynImplicit)
    }
}

class IntegrationTest extends AnyFunSuite {
    test("number"){
        val v = N(1)
        MyO("1", v, v, v, v).exec
    }

    test("boolean"){
        val v = B(true)
        MyO("true", v, v, v, v).exec
    }

    test("string"){
        val v = S("hello")
        MyO("'hello'", v, v, v, v).exec
    }

    test("string plus"){
        val v = S("552.0")
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO("'55' + 2", err, err, v, v).exec
    }

    test("string mult"){
         val v = N(55 * 2)
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO("'55' * 2", err, err, v, v).exec
    }

    test("ifelse") {
        val v = N(1)
        MyO("if ( true ) 1 else 2", v, v, v, v).exec
    }

    test("fact4") {
        val v = N(4*3*2)
        MyO("letrec f = function(x) if (1 >= x) 1 else x * f(x - 1) in f(4)", v, v, v, v).exec
    }

    test("variance 1") {
        val tmp = new InterpreterError("failed type conversions")
        val err = LettuceError(tmp)
        MyO("let x = 2 + true in let f = function(y) x + y in let x = x + 1 in f(x + 1)", LettuceError(tmp), LettuceError(tmp), N(8), N(9)).exec
    }

    /**
      * BEHAVE diff eager and lazy
      * let x = 2 + 1 in
      *     let f = function(y) x in
      *         let x = x + true in
      *             f(x * false)
      * 
      * static, no, eager: ERR
      * static, no, lazy: 3
      * staic, yes, eager: 3
      * static, yes, lazy: 3
      * dynamic, no, eager: ERR
      * dynamic, no, lazy: ERR
      * dynamic, yes, eager: 4
      * dynamic, yes, lazy: 4    
      **/
    // test("variance 2") {
    //     val tmp = new InterpreterError("failed type conversions")
    //     MyO("let x = 2 + 1 in let f = function(y) x + y in let x = x + true in f(x + 1)", LettuceError(tmp), LettuceError(tmp), N(8), N(9)).exec
    // }
}
