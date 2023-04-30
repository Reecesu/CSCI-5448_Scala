package saladbar


/**
  * Closure(id_parameter, e_funcitonBody, env)
  * 
  * a sudo concrete syntax of function. 
  * developers write function(id_parameter) e_funcitonBody
  * and it parses to CLOSURE(id_parameter, e_funcitonBody, env) 
  * for scoping reasons
  * 
  * OO PATTERN: Composite
  *
  * @param id_parameter
  * @param e_funcitonBody
  * @param env
  */
case class Closure(id_parameter: String, e_funcitonBody: Expr, env: Environment) extends Value {
    def toNum = Double.NaN
    def toBool: Boolean = true
    override def toString: String = s"CLOSURE($id_parameter, { $e_funcitonBody }, $env)"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        evalConditions.getSc.substFunctions(evalConditions, this, x, esub){
            ep => sc(ep)
        }
    }
}

