package saladbar


/**
  * LexicalScope
  * 
  * OO PATTERN: Template
  */
case object LexicalScope extends ScopingCondition {

    
    /**
      * substFunctions
      * 
      * in lexical scoping on function call, we must substitute the argument for the parameter
      * in the function body before proceding to evaluate the updated function body. This
      * substition counts as the action for this single step of execution.
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
                    if (x == id_parameter) sc(e)
                    else e_functionBody.substitute(evalConditions, x, esub){
                        newFunctionBody => sc(Closure(id_parameter, newFunctionBody, env))
                    }
            case _ => throw new LexicalScopeError("Failed substfunciton on input $e")
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
        "lexical"
    }


}

