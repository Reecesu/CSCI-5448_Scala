package saladbar


/**
  * LazyCondition
  * 
  * pass by expression semantics. only evaluate an expression when you
  * absolutely have to. currently, this does not memoize
  * 
  * e.g. 
  *   let x = 1 + 2 in x * x 
  *   steps to
  *   (1 + 2) * (1 + 2)
  * 
  * TODO: additional LazyEagerCondition that automagically memoizes for you e.g.
  *   let x = 1 + 2 in x * x 
  *   steps to
  *   lazy(0) * lazy(0) such that lazy(0) holds unevaluate expression 1 + 2
  *   steps to
  *   3 * lazy(0) such that lazy(0) holds evaluated value 3
  * 
  * OO PATTERN: Template
  * 
  */
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

