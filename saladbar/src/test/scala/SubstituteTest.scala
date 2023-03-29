import org.scalatest.funsuite._

class SubstituteTest extends AnyFunSuite {
    def substituteTest(interpreter: Interpreter, e: Expr, x: String, v: Value, r: Expr): Unit = {
        assert(interpreter.substitute(e,x,v) == r)
    }
    def dynamicSubstituteTest(e: Expr, x: String, v: Value, r: Expr): Unit = {
        val interpreter = new Interpreter(DynamicScope)
        substituteTest(interpreter, e, x, v, r)
    }
  
    test("dynamic basic"){
        // CONTEXT: let x = 2 in 1
        dynamicSubstituteTest(N(1), "x", N(2), N(1))
    }
    test("dynamic subst let"){
        // CONTEXT: let x = 2 in let x = x in x
        dynamicSubstituteTest(Let("x", Ident("x"), Ident("x")), "x", N(2), Let("x", N(2), Ident("x")))
    }
    test("dynamic subst function"){
        // CONTEXT: let x = 1 in let f = function(y) x in 2
        // let f = function(y) x in 2
        val e = Let("f", FunDef("y", Ident("x")), N(2))
        val x = "x"
        val v = N(1)
        val r = Let("f", Closure("y", Ident("x"), Extend(x, v, EmptyEnv)), N(2))
        dynamicSubstituteTest(e, x, v, r)
    }
    test("dynamic subst function complex"){
        // CONTEXT: let x = 1 in let f = function (y) x in let x = 2.0 in {f(3.0)}
        // let f = function (y) x in let x = 2.0 in {f(3.0)}
        val e = Let("f", FunDef("y", Ident("x")), Let("x", N(2), FunCall(Ident("f"), N(3))))
        val x = "x"
        val v = N(1)
        val r = Let("f", Closure("y", Ident("x"), Extend(x, v, EmptyEnv)), Let("x", N(2), FunCall(Ident("f"), N(3))))
        dynamicSubstituteTest(e, x, v, r)
    }
}
