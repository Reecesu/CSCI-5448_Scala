package saladbar


/**
  * LettuceError
  * non concrete syntax, can occur during evaluation
  *
  * @param err
  */
case class LettuceError(err: Throwable) extends Value {
    def toNum = Double.NaN
    def toBool: Boolean = false
    override def toString: String = s"LETTUCE ERROR: $err"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = sc(this)
}
