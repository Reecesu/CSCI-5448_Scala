
sealed trait Expr {
    override def toString(): String = this match {
        case Binary(bop, e1, e2) => s"($e1 $bop $e2)"
        case Let(id, e1, e2) => s"let $id = $e1 in $e2"
        case N(n) => s"$n"
        case Error => "ERROR"
        case Ident(x) => x
    }
    def isValue = this.isInstanceOf[Value]
}
case class Binary(bop: Bop, e1: Expr, e2: Expr) extends Expr
case class Let(id: String, e1: Expr, e2: Expr) extends Expr
case class Ident(id: String) extends Expr

sealed trait Bop {
    override def toString(): String = this match {
        case Plus => "+"
        case Times =>  "*"
    }
}
case object Plus extends Bop
case object Times extends Bop


sealed trait Value extends Expr
case class N(n: Double) extends Value
case object Error extends Value