
abstract class ScopingCondition {
  def substFunctions(interpreter: Interpreter, e: Expr, x: String, v: Value): Expr 
}

case class DynamicScopingError(msg: String) extends Exception{
  override def toString: String = s"DYNAMIC SCOPING ERROR: $msg"
}
case class LexicalScopingError(msg: String) extends Exception{
  override def toString: String = s"LEXICAL SCOPING ERROR: $msg"
}

// OO PATTERN: strategy, maybe adaptor
case object LexicalScope extends ScopingCondition {
    def substFunctions(interpreter: Interpreter, e: Expr, x: String, v: Value): Expr = {
      e match {
        case FunDef(id_parameter, e_functionBody) => 
          if (x == id_parameter) e
          else FunDef(id_parameter, interpreter.substitute(e_functionBody, x, v))
        case Closure(id_parameter, e_functionBody, env) => 
          if (x == id_parameter) e
          else Closure(id_parameter, interpreter.substitute(e_functionBody, x, v), env)
        case _ => throw new LexicalScopingError("Failed substfunciton on input $e")
      }
    }
}

case object DynamicScope extends ScopingCondition {
  def substFunctions(interpreter: Interpreter, e: Expr, x: String, v: Value): Expr = {
    e match {
      case FunDef(id_parameter, e_functionBody) => 
        Closure(id_parameter, e_functionBody, Extend(x, v, EmptyEnv))
      case Closure(id_parameter, e_functionBody, env) => 
        Closure(id_parameter, e_functionBody, Extend(x, v, env))
      case _ => throw new DynamicScopingError("Failed substfunciton on input $e")
    }
  }
}