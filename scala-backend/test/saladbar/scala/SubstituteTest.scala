import org.scalatest.funsuite._
import saladbar._


/**
  * SubstituteTest
  * 
  * Unit tests for Expr.substitute
  */
class SubstituteTest extends AnyFunSuite {
    def substituteTest(ec: EvalConditions, e: Expr, x: String, esub: Expr, expectedResult: Expr): Unit = {
        assert( e.substitute(ec, x, esub){r => r} == expectedResult)
    }
    def dynamicSubstituteTest(e: Expr, x: String, esub: Expr, r: Expr): Unit = {
        val ec = new EvalConditions(DynamicScope, NoConversions, EagerCondition)
        substituteTest(ec, e, x, esub, r)
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
        val e = Let("f", Closure("y", Ident("x"), EmptyEnv), N(2))
        val x = "x"
        val v = N(1)
        val r = Let("f", Closure("y", Ident("x"), Extend(x, v, EmptyEnv)), N(2))
        dynamicSubstituteTest(e, x, v, r)
    }
    test("dynamic subst function complex"){
        // CONTEXT: let x = 1 in let f = function (y) x in let x = 2.0 in {f(3.0)}
        // let f = function (y) x in let x = 2.0 in {f(3.0)}
        val e = Let("f", Closure("y", Ident("x"), EmptyEnv), Let("x", N(2), FunCall(Ident("f"), N(3))))
        val x = "x"
        val v = N(1)
        val r = Let("f", Closure("y", Ident("x"), Extend(x, v, EmptyEnv)), Let("x", N(2), FunCall(Ident("f"), N(3))))
        dynamicSubstituteTest(e, x, v, r)
    }
}
