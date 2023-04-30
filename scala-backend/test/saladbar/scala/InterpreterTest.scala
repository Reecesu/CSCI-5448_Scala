import org.scalatest.funsuite._
import saladbar._


/**
  * InterpreterTest
  * 
  * For initial testing of new expressions.
  * preference to use IntegrationTest class for official testing
  */
class InterpreterTest extends AnyFunSuite {

    def interpreterTest(interpreter: Interpreter, e: Expr, l: List[Expr]): Unit = {
        val lFound = interpreter.evaluate(e){r => r}
        try {
            assert(lFound == l)
        } catch {
            case _: Throwable => {
                (lFound zip l ) foreach { case (ef, ei) => println(s"found: $ef\nexpected: $ei\nequivalent? ${ef == ei}\n\n")}
                assert(lFound == l)
            }
        }
    }
    def lexicalInterpreterTest(e: Expr, l: List[Expr]): Unit = {
        val interpreter = new Interpreter(new EvalConditions(LexicalScope, NoConversions, EagerCondition))
        interpreterTest(interpreter, e, l)
    }
    def dynamicInterpreterTest(e: Expr, l: List[Expr]): Unit = {
        val interpreter = new Interpreter(new EvalConditions(DynamicScope, NoConversions, EagerCondition))
        interpreterTest(interpreter, e, l)
    }

    def interpreterValueTest(interpreter: Interpreter, e: Expr, v: Value): Unit = {
        val lFound = interpreter.evaluate(e){ r => r }
        val vFound = lFound.reverse.head
        try {
            assert(vFound == v)
        } catch {
            case _: Throwable => {
                lFound foreach println
                assert(vFound == v)
            }
        }
    }
    def lexicalInterpreterValueTest(e: Expr, v: Value): Unit = {
        val interpreter = new Interpreter(new EvalConditions(LexicalScope, NoConversions, EagerCondition))
        interpreterValueTest(interpreter, e, v)
    }
    def dynamicInterpreterValueTest(e: Expr, v: Value): Unit = {
        val interpreter = new Interpreter(new EvalConditions(DynamicScope, NoConversions, EagerCondition))
        interpreterValueTest(interpreter, e, v)
    }


    test("number") {
        val e = N(2)
        val l = List(e)
        lexicalInterpreterTest(e, l)
    }

    test("ident failure") {
        val e = Ident("x")
        val l = List(e, LettuceError(new InterpreterError("Unbound variable found: x")))
        lexicalInterpreterTest(e, l)
    }

    test("let") {
        // let y = 1 + 2 in 4 * y
        val e = Let("y", Binary(Plus, N(1), N(2)), Binary(Times, N(4), Ident("y")))
        val l = List(e,
            // let y = 3 in 4 * y
            Let("y", N(3), Binary(Times, N(4), Ident("y"))),
            // 4 * 3
            Binary(Times, N(4),  N(3)),
            // 12
            N(12),
            )
        lexicalInterpreterTest(e, l)
    }

    test("closure") {
        // function(x) 1
        val e = Closure("x", N(1), EmptyEnv)
        val l = List(e)
        lexicalInterpreterTest(e, l)
    }

    test("static test") {
        // let x = 1 in let f = function(y) x in let x = 2 in f(3)
        // value should be 1
        val e = Let("x", N(1), Let("f", Closure("y", Ident("x"), EmptyEnv), Let("x", N(2), FunCall(Ident("f"), N(3)))))
        val l = List(e, 
            Let("f", Closure("y", N(1), EmptyEnv), Let("x", N(2), FunCall(Ident("f"), N(3)))),
            Let("x", N(2), FunCall(Closure("y", N(1), EmptyEnv), N(3))),
            FunCall(Closure("y", N(1), EmptyEnv), N(3)),
            N(1)
        )
        lexicalInterpreterTest(e, l)
    }

    test("dynamic test") {
        // let x = 1 in let f = function(y) x in let x = 2 in f(3)
        // value should be 2
        val e = Let("x", N(1), Let("f", Closure("y", Ident("x"), EmptyEnv), Let("x", N(2), FunCall(Ident("f"), N(3)))))
        val l = List(e, 
            Let("f", Closure("y", Ident("x"), Extend("x", N(1), EmptyEnv)), Let("x", N(2), FunCall(Ident("f"), N(3)))),
            Let("x", N(2), FunCall(Closure("y", Ident("x"), Extend("x", N(1), EmptyEnv)), N(3))),
            FunCall(Closure("y", Ident("x"), Extend("x", N(2), Extend("x", N(1), EmptyEnv))), N(3)),
            N(2)
        )
        dynamicInterpreterTest(e, l)
    }

    // IDK this semantic yet
    // test("letrec") {
    //     // letrec f = function(x) 1 in 2
    //     val e = LetRec("f", "x", N(1), N(2))
    //     val l = List(e, 
    //         // let f = closure(x, 1, {}) in 2
    //         LetRec("f")
    //         // 2
    //         Closure("x", N(1), EmptyEnv))
    //     staticInterpreterTest(e, l)
    // }

    test("rec lexical"){
        // letrec f = function(x) if (1 >= x) 1 else x * f(x - 1) in f(2)
        val ebody = IfThenElse(Binary(Geq, N(1), Ident("x")), 
                N(1), 
                Binary(Times, Ident("x"), FunCall(Ident("f"), Binary(Minus, Ident("x"), N(1)))))
        val closure = Closure("x", ebody, EmptyEnv)
        val e = LetRec("f", closure, FunCall(Ident("f"), N(2)))
        val fFirstSub = Closure("x", ebody, ExtendRec("f", "x", ebody, EmptyEnv))
        val l = List(e, 
            // closure(x, ..., ExtendRec(f, closure, {})(2)
            FunCall(fFirstSub, N(2)),
            IfThenElse(Binary(Geq, N(1), N(2)), 
                N(1), 
                Binary(Times, N(2), FunCall(fFirstSub, Binary(Minus, N(2), N(1))))),
            IfThenElse(B(false), 
                N(1), 
                Binary(Times, N(2), FunCall(fFirstSub, Binary(Minus, N(2), N(1))))),
            Binary(Times, N(2), FunCall(fFirstSub, Binary(Minus, N(2), N(1)))),
            Binary(Times, N(2), FunCall(fFirstSub, N(1))),
            Binary(Times, N(2), IfThenElse(Binary(Geq, N(1), N(1)), 
                N(1), 
                Binary(Times, N(1), FunCall(fFirstSub, Binary(Minus, N(1), N(1)))))),
            Binary(Times, N(2), IfThenElse(B(true), 
                N(1), 
                Binary(Times, N(1), FunCall(fFirstSub, Binary(Minus, N(1), N(1)))))),
            Binary(Times, N(2), N(1)),
            N(2)
        )
        lexicalInterpreterTest(e, l)
    }

    test("rec more"){
        // letrec f = function(x) if (1 >= x) 1 else x * f(x - 1) in f(2)
        val ebody = IfThenElse(Binary(Geq, N(1), Ident("x")), 
                N(1), 
                Binary(Times, Ident("x"), FunCall(Ident("f"), Binary(Minus, Ident("x"), N(1)))))
        val closure = Closure("x", ebody, EmptyEnv)
        val eMaker: Double => Expr = (d) => LetRec("f", closure, FunCall(Ident("f"), N(d)))
        lexicalInterpreterValueTest(eMaker(-1), N(1))
        lexicalInterpreterValueTest(eMaker(3), N(6))
        dynamicInterpreterValueTest(eMaker(-1), N(1))
        dynamicInterpreterValueTest(eMaker(3), N(6))
    }

    test("and"){
      lexicalInterpreterValueTest(Binary(And, B(true), N(5)), N(5))
    }
    test("or"){
      lexicalInterpreterValueTest(Binary(Or, B(true), N(5)), B(true))
    }

    test("cos"){
      lexicalInterpreterValueTest(Unary(Cos, N(0)), N(1))
    }

}
