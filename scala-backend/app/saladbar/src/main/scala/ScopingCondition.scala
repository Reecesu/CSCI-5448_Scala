package saladbar


/**
  * ScopingCondition
  */
abstract class ScopingCondition {
  def substFunctions[A](evalConditions: EvalConditions, 
      e: Expr,
      x: String,
      esub: Expr)(sc: Expr => A):A 
}

