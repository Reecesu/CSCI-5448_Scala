package saladbar


/**
  * TryCatch(e1, e2)
  * 
  * from concrete syntax: try { e1 } catch { e2 }
  * 
  * OO PATTERN: Composite
  * 
  * Please note that while this is seen in the course notes for 3155, this is
  * original development from Spencer Wilson that inspired that course note 
  * in big step semantics
  *
  * @param e1
  * @param e2
  */
case class TryCatch(e1: Expr, e2: Expr) extends Expr {


    override def toString: String = s"try { $e1 } catch { $e2 }"

    /**
      * step
      *
      * attempt to evaluate e1 to a value v1
      * if an error is thrown during that evaluation,
      *     then evaluate e2
      *     else return v1
      * 
      * @param evalConditions
      * @param sc
      * @return
      */
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        if (e1.isValue) {
            sc(e1)
        } else {
            try {
                e1.step(evalConditions){
                    e1p => sc(TryCatch(e1p, e2))
                }
            } catch {
                case err: Throwable => {
                    println(s"TRY_CATCH found error: $err")
                    sc(e2)
                }
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
                e2p => sc(TryCatch(e1p, e2p))
            }
        }
    }


}

