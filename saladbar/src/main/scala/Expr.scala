
case class ExprToString(msg: String) extends Exception {
    override def toString(): String = s"EXPR TO STRING: $msg"
}

sealed trait Expr {
    override def toString(): String = this match {
        case Binary(bop, e1, e2) => s"($e1 $bop $e2)"
        case Let(id, e1, e2) => s"let $id = $e1 in $e2"
        case N(n) => s"$n"
        case FunDef(id, e) => s"function ($id) $e"
        case Closure(id, e, env) => s"CLOSURE{ $id, $e, $env }"
        case FunCall(e1, e2) => s"{$e1($e2)}"
        case LettuceError(err) => s"LETTUCE_ERROR($err)"
        case Ident(x) => x
        case _ => throw new ExprToString(s"failed to convert this to a string $this")
    }
    def isValue = this.isInstanceOf[Value]
}
case class Binary(bop: Bop, e1: Expr, e2: Expr) extends Expr
case class Let(id: String, e1: Expr, e2: Expr) extends Expr
case class Ident(id: String) extends Expr
case class FunDef(id_parameter: String, e_funcitonBody: Expr) extends Expr
case class FunCall(e1: Expr, e2: Expr) extends Expr
case class LetRec(id_functionName: String,
        id_parameter: String, 
        e_funcitonBody: Expr, 
        e2: Expr) extends Expr

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
case class Closure(id_parameter: String, e_funcitonBody: Expr, env: Environment) extends Value
case class LettuceError(err: Throwable) extends Value


case class LookupError(x: String) extends Exception {
    override def toString(): String = s"Expected $x"
}
sealed trait Environment {
    def lookup(x: String): Value = {
        this match {
            case EmptyEnv => throw new LookupError(x)
            case Extend(id, v, env) => 
                if (id == x) v
                else env lookup x
            case ExtendRec(id_functionName, id_parameter, e_funcitonBody, env) => 
                if (x == id_functionName) Closure(id_parameter, e_funcitonBody, this)
                else env lookup x
        }
    }
}
case object EmptyEnv extends Environment
case class Extend(id: String, v: Value, env: Environment) extends Environment
case class ExtendRec(id_functionName: String, 
        id_parameter: String, 
        e_funcitonBody: Expr, 
        env: Environment) extends Environment