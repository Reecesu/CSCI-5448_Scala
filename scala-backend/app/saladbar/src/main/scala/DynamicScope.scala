package saladbar


/**
  * DynamicScope
  * 
  * OO PATTERN: Template
  */
case object DynamicScope extends ScopingCondition {


    /**
      * substFunctions
      * 
      * in dynamic scope for small step we must evaluate function bodies with updated closures
      * that map the argument $esub to the parameter name $x when evaluating the body of the
      * function $e
      *
      * @param evalConditions
      * @param e
      * @param x
      * @param esub
      * @param sc
      * @return
      */
    def substFunctions[A](evalConditions: EvalConditions, e: Expr, x: String, esub: Expr)(sc: Expr => A):A = {
        e match {
            case Closure(id_parameter, e_functionBody, env) => 
                    sc(Closure(id_parameter, e_functionBody, Extend(x, esub, env)))
            case _ => throw new DynamicScopeError("Failed substfunciton on input $e")
        }
    }


    /**
      * toString
      *
      * used as repr for parts of code, do not modify 
      * unless you are ready to change repr access methods
      * 
      * @return
      */
    override def toString: String = {
        "dynamic"
    }


}

