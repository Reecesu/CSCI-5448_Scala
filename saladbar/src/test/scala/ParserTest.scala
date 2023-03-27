import org.scalatest.funsuite._

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

    test("times"){
        assert(Binary(Times, N(1), N(2)) == parser.parse("1 * 2"))
    }

    test("ident"){
        assert(Ident("y") == parser.parse("y"))
    }

    test("let"){
        assert(Let("z", N(1), N(2)) == parser.parse("let z = 1 in 2"))
    }

    test("integration"){
        assert(Let("a", Binary(Plus, N(1), N(2)), Binary(Times, Ident("a"), N(3))) 
                == parser.parse("let a = 1 + 2 in a * 3"))
    }
}