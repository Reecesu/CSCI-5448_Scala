package saladbar


/**
  * Unary(uop, e1)
  * from concrete syntax: uop(e1)
  *
  * OO PATTERN: Composite
  * 
  * @param uop
  * @param e1
  */
case class Unary(uop: Uop, e1: Expr) extends Expr {


    override def toString: String =  s"($uop($e1))"


    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        if (e1.isValue) {
            evalConditions.getTc.performUop(uop, e1.asInstanceOf[Value])(sc)
        } else {
            e1.step(evalConditions) {
                e1p => sc(Unary(uop, e1p))
            }
        }
    }


    /**
      * substitute
      * 
      * find all free instances of $x in $this and replace with $esub
      *
      * @param evalConditions
      * @param x
      * @param esub
      * @param sc
      * @return
      */
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        e1.substitute(evalConditions, x, esub){
            e1p => sc(Unary(uop, e1p))
        }
    }


}


