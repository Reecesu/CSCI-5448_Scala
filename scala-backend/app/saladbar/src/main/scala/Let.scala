package saladbar


/**
  * Let(id, e1, e2)
  * 
  * from concrete syntax: let id = e1 in e2
  * 
  * OO PATTERN: Composite
  *
  * @param id
  * @param e1
  * @param e2
  */
case class Let(id: String, e1: Expr, e2: Expr) extends Expr {


    override def toString: String = s"let $id = $e1 in $e2"


    /**
      * step
      * 
      * when $e1 is ready, substitute $e1 into $e2 for $id
      * else step $e1
      *
      * @param evalConditions
      * @param sc
      * @return
      */
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        evalConditions.getLec.check(e1){
            () => e2.substitute(evalConditions, id, e1)(sc)
        }{
            () => e1.step(evalConditions){
                e1p => sc(Let(id, e1p, e2))
            }
        }
    }


    /**
      * substitute
      * 
      * find all free instances of $x in $this and replace with $esub
      * if $id is $x then $x is already bound free in $e2.
      *
      * @param evalConditions
      * @param x
      * @param esub
      * @param sc
      * @return
      */
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        if (x == id) e1.substitute(evalConditions, x, esub){
            e1p => sc(Let(id, e1p, e2))
        }
        else e1.substitute(evalConditions, x, esub){
            e1p => e2.substitute(evalConditions, x, esub){
                e2p => sc(Let(id, e1p, e2p))
            }
        }
    }


}

