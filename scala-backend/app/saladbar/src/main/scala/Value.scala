package saladbar


/**
  * Value
  * 
  * a collection of values. 
  */
abstract class Value extends Expr {
    def toNum: Double
    def toBool: Boolean
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = throw new InterpreterError(s"cannot step on a value: $this")
    override def isValue = true
}
case class N(n: Double) extends Value {
    def toNum = n
    def toBool: Boolean = n != 0
    override def toString: String = s"$n"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = sc(this)
}
case class Closure(id_parameter: String, e_funcitonBody: Expr, env: Environment) extends Value {
    def toNum = Double.NaN
    def toBool: Boolean = true
    override def toString: String = s"CLOSURE($id_parameter, { $e_funcitonBody }, $env)"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        evalConditions.getSc.substFunctions(evalConditions, this, x, esub){
            ep => sc(ep)
        }
    }
}
case class LettuceError(err: Throwable) extends Value {
    def toNum = Double.NaN
    def toBool: Boolean = false
    override def toString: String = s"LETTUCE ERROR: $err"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = sc(this)
}
case class B(b: Boolean) extends Value {
    def toNum = if (b) 1 else 0
    def toBool: Boolean = b
    override def toString: String = s"$b"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = sc(this) 
}
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
case object Undefined extends Value {
    def toNum = Double.NaN
    def toBool: Boolean = false
    override def toString: String = "undefined"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = sc(this) 
}
