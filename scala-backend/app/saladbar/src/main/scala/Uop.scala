package saladbar


/**
  * Uop
  * 
  * a collection of unary operations
  * Completed in a single file due to size of the structures
  */
sealed trait Uop {
    override def toString: String
}


case object Neg extends Uop {
    override def toString: String = "-"
}


case object Not extends Uop {
    override def toString: String = "!"
}


case object Sin extends Uop {
    override def toString: String = "sin"
}


case object Cos extends Uop {
    override def toString: String = "cos"
}


case object Log extends Uop {
    override def toString: String = "log"
}


case object Exp extends Uop {
    override def toString: String = "exp"
}

