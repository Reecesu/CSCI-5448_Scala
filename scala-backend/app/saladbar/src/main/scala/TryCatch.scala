package saladbar


/**
  * TryCatch(e1, e2)
  * from concrete syntax: try { e1 } catch { e2 }
  * 
  * attempt to evaluate e1 to a value v1
  * if an error is thrown during that evaluation,
  *     then evaluate e2
  *     else return v1
  */
case class TryCatch(e1: Expr, e2: Expr) extends Expr {


    override def toString: String = s"try { $e1 } catch { $e2 }"


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


    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        e1.substitute(evalConditions, x, esub){
            e1p => e2.substitute(evalConditions, x, esub){
                e2p => sc(TryCatch(e1p, e2p))
            }
        }
    }


}

