case class InterpreterError(msg: String) extends Exception{
  override def toString: String = s"INTERPRETER ERROR: $msg"
}

/**
  * Template scoping conditions to effect substitution. sub out functions for closures.
  * template type changes on value with hBop kind of things
  * test all 4 options well.
  */


class Interpreter(private var evalConditions: EvalConditions) {

  def setEvalConditions(ec: EvalConditions) = 
      this.evalConditions = ec

  def evaluate[A](e: Expr)(sc: List[Expr] => A): A = evaluate(e, 100)(sc)
  private def evaluate[A](e: Expr, maxSteps: Int)(sc: List[Expr] => A): A = {
    if (maxSteps <= 0 || e.isValue) sc(List(e))
    else {
      // TODO: capture lettuce error?
      e.stepWrapper(evalConditions){
        ep => evaluate(ep, maxSteps - 1) {
          t => sc(e :: t)
        }
      }{
        err => evaluate(LettuceError(err), maxSteps - 1)(sc)
      }
    }
  }

}
