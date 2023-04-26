package saladbar


/**
  * Bop
  * 
  * a collection of binary operations
  */
sealed trait Bop {
    override def toString: String
    def checkBop1[A](v1: Value)(sc: () => A)(fc: () => A): A = this match {
        case And | Or =>  v1 match {
            case B(_) => sc()
            case _ => fc()
        }
        case Eq | Neq | Eqq | Neqq => sc()
        case Gt | Geq | Lt | Leq | Plus => v1 match {
            case N(_) | S(_) => sc()
            case _ => fc()
        }
        case Minus | Times | Div => v1 match {
            case N(_) => sc()
            case _ => fc()
        }
        case _ => ???
    }
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

