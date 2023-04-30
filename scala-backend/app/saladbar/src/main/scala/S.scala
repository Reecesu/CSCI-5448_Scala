package saladbar


/**
  * S(s)
  * concrete syntax of string s
  *
  * OO PATTERN: Composite
  * 
  * @param s
  */
case class S(s: String) extends Value {
    def toNum = {
        try {
            s.toDouble
        } catch {
            case _: Throwable => Double.NaN
        }
    }
    def toBool: Boolean = s != ""
    // TODO: improve toString logic so "55" + 2 is "552.0" and not "'55'2.0"
    override def toString: String = s"'$s'"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = sc(this) 
}
