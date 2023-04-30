package saladbar


/**
  * LetRec(id_functionName, closure, e2)
  * 
  * from concrete syntax: letrec id_functionName = closure in e2
  * 
  * OO PATTERN: Composite
  *
  * @param id_functionName
  * @param closure
  * @param e2
  */
case class LetRec(id_functionName: String,
        closure: Closure,
        e2: Expr) extends Expr {


    override def toString: String = s"letrec $id_functionName = $closure in $e2"


    override def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        val Closure(id_parameter, e_functionBody, env) = closure
        val newEnv: Environment = ExtendRec(id_functionName, id_parameter, e_functionBody, env)
        val v: Value = Closure(id_parameter, e_functionBody, newEnv)
        e2.substitute(evalConditions, id_functionName, v)(sc)
    }


    /**
      * substitute
      * 
      * find all free instances of $x in $this and replace with $esub
      * if $id_functionName is equal to $x then $x is already bound in both $e_functionBody,
      *     as well as $e2
      * if $id_parameter is equal to $x, then $x is already boudn in $e_functionBody
      *
      * @param evalConditions
      * @param x
      * @param esub
      * @param sc
      * @return
      */
    override def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        // letrec f = cl(x) e {env} in e2
        val Closure(id_parameter, e_functionBody, envcl) = closure
        if (x == id_functionName) {
            sc(this)
        } else if (x == id_parameter) {
            e2.substitute(evalConditions, x, esub){
                e2p => sc(LetRec(id_functionName, closure, e2p))
            }
        } else {
            e_functionBody.substitute(evalConditions, x, esub){
                newFuncitonBody => e2.substitute(evalConditions, x, esub){
                    e2p => sc(LetRec(id_functionName, Closure(id_parameter, newFuncitonBody, envcl), e2p))
                }
            }
        }
    }


}

