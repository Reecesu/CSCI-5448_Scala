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
    override def toString: String = s"'$s'"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = sc(this) 
}
