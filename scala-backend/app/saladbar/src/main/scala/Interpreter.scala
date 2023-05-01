package saladbar


/**
  * Interpreter
  * 
  * Given an expression and a set of evaluation conditions,
  * Determine the sequence of steps during evaluation of the expression.
  * 
  * OO Pattern: Interpreter
  * OO PATTERN: Model (in MVC-VM, not the ViewModel, just the model)
  * 
  * @param evalConditions
  */
class Interpreter(private var evalConditions: EvalConditions) {

  
  /**
    * setEvalConditions
    * 
    * allows you to change the evaluation conditions of interpretation
    * if needed.
    *
    * @param ec
    */
  def setEvalConditions(ec: EvalConditions) = 
      this.evalConditions = ec

    
  /**
    * evaluate
    * 
    * limit users to the first 100 steps only
    *
    * @param e
    * @param sc
    * @return
    */
  def evaluate[A](e: Expr)(sc: List[Expr] => A): A = evaluate(e, 100)(sc)

  
  /**
    * evaluate
    * 
    * Attempt evaluation of $e limited by $maxSteps. If an error
    * occurs in evaluation, let that be the terminating value of 
    * evaluation captured as a `LettuceError` with the relevant
    * error message captured.
    * 
    * e.g. for expression 1 + 2 * 3, sc is called on
    *      List(1+2*3,
    *           1+6,
    *           7)
    *
    * Special credit to Bor-Yuh Evan Chang for initial development of this method
    * and notation for CSCI 3155: https://csci3155.cs.colorado.edu/csci3155-notes.pdf
    * 
    * @param e
    * @param maxSteps
    * @param sc
    * @return
    */
  private def evaluate[A](e: Expr, maxSteps: Int)(sc: List[Expr] => A): A = {
    // println(e)
    if (maxSteps <= 0 || e.isValue) sc(List(e))
    else {
      e.stepWrapper(evalConditions){
        newE => evaluate(newE, maxSteps - 1) {
          expressionSoFar => sc(e :: expressionSoFar)
        }
      }{
        err => evaluate(LettuceError(err), maxSteps - 1){
          expressionSoFar => sc(e :: expressionSoFar)
        }
      }
    }
  }


  override def toString: String = {
    s"INTERPRETER{ $evalConditions }"
  }


}

