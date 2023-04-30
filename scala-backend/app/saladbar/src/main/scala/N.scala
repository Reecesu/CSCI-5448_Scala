package saladbar


/**
  * N(n)
  * concrete syntax for a number n
  *
  * OO PATTERN: Composite
  * 
  * @param n
  */
case class N(n: Double) extends Value {
    def toNum = n
    def toBool: Boolean = n != 0
    override def toString: String = s"$n"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = 
            sc(this)
}

