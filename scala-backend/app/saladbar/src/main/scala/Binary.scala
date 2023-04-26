package saladbar


/**
  * Binary(bop, e1, e2)
  * from concrete syntax: e1 bop e2
  * 
  * attempt to evaluate e1 to a value v1
  * attempt to evaluate e2 to a value v2
  * attempt to apply bop logic to values v1 and v2
  * 
  * NOTE: that Or as well as And have short circuting logic
  *
  * @param bop
  * @param e1
  * @param e2
  */
case class Binary(bop: Bop, e1: Expr, e2: Expr) extends Expr {


    override def toString: String =  s"($e1 $bop $e2)"


    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
      if (bop == And | bop == Or) {
        (e1, e2) match {
          case (v1, e2) if v1.isValue => evalConditions.getTc.performShortCircuitBop(bop, v1.asInstanceOf[Value], e2)(sc)
          case (e1, e2) => e1.step(evalConditions){
            e1p => sc(Binary(bop, e1p, e2))
          }
        }
      } else {
        (e1, e2) match {
            case (v1, v2) if v1.isValue && v2.isValue => evalConditions.getTc.performBop(bop, v1.asInstanceOf[Value], v2.asInstanceOf[Value])(sc)
            case (v1, e2) if v1.isValue => evalConditions.getTc.checkBop1(bop, v1.asInstanceOf[Value]){
                () => e2.step(evalConditions){
                    e2p => sc(Binary(bop, v1, e2p))
                }
            }
            case (e1, e2) => e1.step(evalConditions){
                e1p => sc(Binary(bop, e1p, e2))
            }
        }
      }
    }


    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        e1.substitute(evalConditions, x, esub){ 
            e1p => e2.substitute(evalConditions, x, esub){
                e2p => sc(Binary(bop, e1p, e2p))
            }
        }
    }


}

