package saladbar


/**
  * Value
  * 
  * a collection of values. 
  * Implemented in single file for convenience
  * 
  * OO PATTERN: Composite
  */
abstract class Value extends Expr {
    def toNum: Double
    def toBool: Boolean
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = throw new InterpreterError(s"cannot step on a value: $this")
    override def isValue = true
}

