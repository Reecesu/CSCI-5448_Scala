package saladbar


/**
  * ScopingCondition
  */
abstract class ScopingCondition {

  /**
    * substFunctions
    * 
    * Scoping condition as Lexical or Dynamic will impact how
    * functions evaluate.
    *
    * @param evalConditions
    * @param e
    * @param x
    * @param esub
    * @param sc
    * @return
    */
  def substFunctions[A](evalConditions: EvalConditions, 
      e: Expr,
      x: String,
      esub: Expr)(sc: Expr => A):A 
}

