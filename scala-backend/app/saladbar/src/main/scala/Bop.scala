package saladbar


/**
  * Bop
  * 
  * A collection of binary operations
  * Completed in a single file due to size of the structures
  */
sealed trait Bop {
    override def toString: String
}


case object And extends Bop{
    override def toString: String = "&&"
}


case object Or extends Bop{
    override def toString: String = "||"
}


case object Plus extends Bop {
    override def toString: String = "+"
}


case object Times extends Bop{
    override def toString: String = "*"
}


case object Div extends Bop{
    override def toString: String = "/"
}


case object Minus extends Bop {
    override def toString: String = "-"
}


case object Geq extends Bop {
    override def toString: String = ">="
}


case object Gt extends Bop {
    override def toString: String = ">"
}


case object Leq extends Bop {
    override def toString: String = "<="
}


case object Lt extends Bop {
    override def toString: String = "<"
}


case object Eq extends Bop {
    override def toString: String = "=="
}


case object Eqq extends Bop {
    override def toString: String = "==="
}


case object Neq extends Bop {
    override def toString: String = "!="
}


case object Neqq extends Bop {
    override def toString: String = "!=="
}


case object Seq extends Bop {
    override def toString: String = ";"
}

