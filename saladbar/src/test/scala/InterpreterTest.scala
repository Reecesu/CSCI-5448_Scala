import org.scalatest.funsuite._

class InterpreterTest extends AnyFunSuite {
    test("number") {
        val e = N(2)
        val l = List(e)
        assert(Interpreter.evaluate(e) == l)
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
        assert(Interpreter.evaluate(e) == l)
    }
}
