package saladbar


/**
  * Envrionment
  * 
  * A potentially cicular data structure used here to allow
  * for dynamic scoping in a small step interpreter as 
  * elements of closures.
  * 
  * OO PATTERN: Composite
  * 
  * Special credit to Sriram Sankaranarayanan for initial development of this concept
  * and notation for CSCI 3155: https://github.com/sriram0339/csci3155_notebooks
  */
sealed trait Environment {


    /**
      * lookup
      * 
      * determine if $x exists in $this.
      * Note that ExtendRec creates closures dynamically to allow for circular
      * scopes in the event of recursive functions 
      *     `letrec id_functionName = Closure(id_parameter, e_funcitonBody, envCl) in e2.`
      *
      * @param x
      */
    def lookup(x: String): Expr = {
        this match {
            case EmptyEnv => throw new LookupError(x)
            case Extend(id, v, env) => 
                if (id == x) v
                else env lookup x
            case ExtendRec(id_functionName, id_parameter, e_funcitonBody, env) => 
                if (x == id_functionName) Closure(id_parameter, e_funcitonBody, this)
                else env lookup x
        }
    }


}


/**
  * EmptyEnv
  * 
  * the base case of an envrionment
  */
case object EmptyEnv extends Environment


/**
  * Extend(id, e, env)
  * 
  * for any let id = e1 in e2...
  * we extend $env with $id mapped to an expression either $e1 or its value $v1
  *
  * @param id
  * @param e
  * @param env
  */
case class Extend(id: String, e: Expr, env: Environment) extends Environment


/**
  * ExtendRec
  * 
  * For any letrec id_functionName = Closure(id_parameter, e_funcitonBody, envCl) in e2
  * we extend $env with $id_functionName mapped to both $id_parameter and $e_funcitonBody
  * in a way such that new closures can be produced dynamically during lookup
  * operations.
  *
  * @param id_functionName
  * @param id_parameter
  * @param e_funcitonBody
  * @param env
  */
case class ExtendRec(id_functionName: String, 
        id_parameter: String, 
        e_funcitonBody: Expr, 
        env: Environment) extends Environment

