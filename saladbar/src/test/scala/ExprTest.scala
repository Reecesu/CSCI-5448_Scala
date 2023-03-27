import org.scalatest.funsuite._

class ExprTest extends AnyFunSuite {
    test("number") {
        val e = N(2)
        val s = "2.0"
        assert(e.toString == s)
    }

    test("add") {
        val e = Binary(Plus, N(2), N(3))
        val s = "(2.0 + 3.0)"
        assert(e.toString == s)
    }

    test("let basic") {
        val e = Let("x", N(1), N(2))
        val s = "let x = 1.0 in 2.0"
        assert(e.toString == s)
    }
}
