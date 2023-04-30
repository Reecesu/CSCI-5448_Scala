package saladbar



/**
  * Expr
  * 
  * an expression
  * 
  * TODO: implement Print as special top level
  * TODO: implement Seq as bop
  * 
  * OO PATTERN: Composite
  */
abstract class Expr {


    // By default, expressions are not values, Value child will change this
    def isValue: Boolean = false


    // Children should override the toString method to show the deparse of the expression
    override def toString: String


    // $this steps to a new expression subject to the $evalConditions, pass result to $sc
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A


    // Find all free instances of $x in $this and replace with $esub, unless $evalConditions dicatate otherwise
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A

   

    /**
      * stepWrapper
      * 
      * Do not override
      * enables the interpreter to step through evaluation for us and handle errors
      * as it deems appropriate.
      * 
      * OO PATTERN: adapter
      *
      * @param evalConditions
      * @param sc
      * @param fc
      * @return
      */
    def stepWrapper[A](evalConditions: EvalConditions)(sc: Expr => A)(fc: Throwable => A): A = {
        try {
            this.step(evalConditions)(sc)
        } catch {
            case err: Throwable => fc(err)
        }
    }


}

