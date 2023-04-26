package saladbar


case object LazyCondition extends LazyEagerCondition {


    /**
      * check
      * 
      * Under lazy conditions, it is always okay to move forward with evaluation regardless
      * of the pattern of e1 
      *
      * @param e1
      * @param sc
      * @param fc
      * @return
      */
    def check[A](e1: Expr)(sc: () => A)(fc: () => A): A = {
        sc()
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
        "lazy"
    }

}

