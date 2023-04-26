package saladbar



/**
  * Expr
  * 
  * an expression
  */
abstract class Expr {


    def isValue: Boolean = false


    override def toString: String


    // don't bother to override...
    def stepWrapper[A](evalConditions: EvalConditions)(sc: Expr => A)(fc: Throwable => A): A = {
        try {
            this.step(evalConditions)(sc)
        } catch {
            case err: Throwable => fc(err)
        }
    }


    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A


    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A

   
}

