package saladbar


/**
  * B(b)
  * concrete syntax of a boolean value true or false
  *
  * OO PATTERN: Composite
  * 
  * @param b
  */
case class B(b: Boolean) extends Value {
    def toNum = if (b) 1 else 0
    def toBool: Boolean = b
    override def toString: String = s"$b"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = sc(this) 
}
