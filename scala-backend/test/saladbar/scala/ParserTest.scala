import org.scalatest.funsuite._
import saladbar._


/**
  * ParserTest
  * 
  * unit tests for the Parser only
  */
class ParserTest extends  AnyFunSuite {
    val parser = new Parser

    test("num 2"){
        assert(N(2.0) == parser.parse("2"))
    }
    test("num 3"){
        assert(N(3.0) == parser.parse("3"))
    }

    test("plus"){
        assert(Binary(Plus, N(1), N(2)) == parser.parse("1 + 2"))
    }

    test("plusString"){
        assert(Binary(Plus, S("55"), N(2.0)) == parser.parse("'55' + 2"))
    }

    test("times"){
        assert(Binary(Times, N(1), N(2)) == parser.parse("1 * 2"))
    }

    test("Eq1"){
        assert(Binary(Eq, N(1), N(2)) == parser.parse("1 == 2"))
    }
    test("Eq2"){
        assert(Binary(Eq, S("5"), N(5)) == parser.parse("'5' == 5"))
    }
    test("Eqq1"){
        assert(Binary(Eqq, N(1), N(2)) == parser.parse("1 === 2"))
    }
    test("Eqq2"){
        assert(Binary(Eqq, S("5"), N(5)) == parser.parse("'5' === 5"))
    }

    test("ident"){
        assert(Ident("y") == parser.parse("y"))
    }

    test("let"){
        assert(Let("z", N(1), N(2)) == parser.parse("let z = 1 in 2"))
    }

    test("fundef"){
        assert(Closure("y", N(1), EmptyEnv) == parser.parse("function(y) 1"))
    }
    test("letrec"){
        assert(LetRec("g", Closure("z", N(1), EmptyEnv), N(2)) == parser.parse("letrec g = function(z) 1 in 2"))
    }

    test("integration"){
        assert(Let("a", Binary(Plus, N(1), N(2)), Binary(Times, Ident("a"), N(3))) 
                == parser.parse("let a = 1 + 2 in a * 3"))
    }

    // // OUR parser actually doesn't allow this. we only allow <id>(e2)
    // test("funcall fake but valid"){
    //     assert(FunCall(N(1), N(2)) == parser.parse("1(2)"))
    // }
    // test("funcall"){
    //     assert(FunCall(FunDef("x", N(1)), N(2)) == parser.parse("function(x) 1(2)"))
    // }

    test("funcall fake but valid"){
        assert(FunCall(Ident("f"), N(1)) == parser.parse("f(1)"))
    }
    test("funcall"){
        assert(Let("f", Closure("x", N(1), EmptyEnv), FunCall(Ident("f"), N(2))) == parser.parse("let f = function(x) 1 in f(2)"))
    }

    test("negation") {
        assert(Unary(Neg, N(1)) == parser.parse("-(1)"))
    }

    test("cos") {
        assert(Unary(Cos, N(0)) == parser.parse("cos(0)"))
    }
}