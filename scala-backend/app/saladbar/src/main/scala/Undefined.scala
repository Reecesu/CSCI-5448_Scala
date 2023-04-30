package saladbar


/**
  * Undefined
  * concrete syntax: undefined
  * 
  * a common value of printing
  * 
  * OO PATTERN: Composite
  * 
  * TODO: implement printing in saladbar
  */
case object Undefined extends Value {
    def toNum = Double.NaN
    def toBool: Boolean = false
    override def toString: String = "undefined"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = sc(this) 
}

