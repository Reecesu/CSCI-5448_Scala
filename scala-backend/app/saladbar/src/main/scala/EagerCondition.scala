package saladbar


/**
  * EagerCondition
  * 
  * OO PATTERN: Template
  */
case object EagerCondition extends LazyEagerCondition {

    /**
      * check
      * 
      * Under eager conditions, it is only okay to move forward with evaluation
      * iff the expression $e1 is a value
      *
      * @param e1
      * @param sc
      * @param fc
      * @return
      */
    def check[A](e1: Expr)(sc: () => A)(fc: () => A): A = {
        if (e1.isValue) sc() else fc()
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
        "eager"
    }
}

