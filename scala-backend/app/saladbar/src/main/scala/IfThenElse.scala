package saladbar


/**
  * IfThenElse(e1, e2, e3)
  * 
  * from concrete syntax: if (e1) e2 else e3
  * 
  * OO PATTERN: Composite
  */
case class IfThenElse(e1: Expr, e2: Expr, e3: Expr) extends Expr {


    override def toString: String = s"if ($e1) { $e2 } else { $e3 }"

    
    /**
      * step
      *
      * if e1 evaluates to a truethy value,
      *     then evaluate e2
      *     else evaluate e3
      * 
      * @param evalConditions
      * @param sc
      * @return
      */
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        if (e1.isValue) {
            evalConditions.getTc.checkIf(e1.asInstanceOf[Value]){
                (b: Boolean) => sc(if (b) e2 else e3)
            }{
                () => sc(LettuceError(new InterpreterError(s"issue with ifthen else on condition $e1")))
            }
        } else {
            e1.step(evalConditions){
                e1p => sc(IfThenElse(e1p, e2, e3))
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
            e1p => e2.substitute(evalConditions, x, esub){
                e2p => e3.substitute(evalConditions, x, esub){
                    e3p => sc(IfThenElse(e1p, e2p, e3p))
                }
            }
        }
    }


}

