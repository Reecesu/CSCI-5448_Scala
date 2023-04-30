package saladbar


/**
  * LazyEagerCondition
  * 
  * OO PATTERN: Template
  */
abstract class LazyEagerCondition {


    /**
      * check
      * 
      * confirm if $e1 is ready for substitution subject to the
      * scoping condition. if so, execute $sc, else execute $fc
      *
      * @param e1
      * @param sc
      * @param fc
      * @return
      */
    def check[A](e1: Expr)(sc: () => A)(fc: () => A): A


}

