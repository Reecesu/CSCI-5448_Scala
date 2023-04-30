import org.scalatest.funsuite._
import saladbar._


/**
  * LookupTest
  * 
  * unit tests for Environment.lookup method
  */
class LookupTest extends AnyFunSuite {

    def testDNE(env: Environment, id: String) = {
        try {
            env lookup id
        } catch {
            case _: LookupError => assert(true)
            case _: Throwable => assert(false)
        }
    }

    test("empty"){
        testDNE(EmptyEnv, "x")
        testDNE(EmptyEnv, "y")
    }

    test("success non-empty") {
        assert(N(1) == (Extend("x", N(1), EmptyEnv) lookup "x"))
        assert(N(1) == (Extend("y", N(1), EmptyEnv) lookup "y"))
    }
  
    test("failure non-empty") {
        testDNE( Extend("x", N(1), EmptyEnv), "y")
    }

    test("success nested") {
        val env = Extend("y", N(2), Extend("x", N(1), EmptyEnv))
        assert(N(1) == (env lookup "x"))
        assert(N(2) == (env lookup "y"))
    }

    test("success rec") {
        val env = ExtendRec("g", "y", N(1), EmptyEnv)
        val v = Closure("y", N(1), env)
        assert(v == (env lookup "g"))
    }
}
