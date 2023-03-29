case class InterpreterError(msg: String) extends Exception{
  override def toString: String = s"INTERPRETER ERROR: $msg"
}

/**
  * Template scoping conditions to effect substitution. sub out functions for closures.
  * template type changes on value with hBop kind of things
  * test all 4 options well.
  */


class Interpreter(private var scopingCondition: ScopingCondition) {

  def setScopingCondition(sc: ScopingCondition) = 
      this.scopingCondition = sc

  def evaluate(e: Expr): List[Expr] = evaluate(e, 100)
  private def evaluate(e: Expr, maxSteps: Int): List[Expr] = {
    if (maxSteps <= 0 || e.isValue) List(e)
    else {
        val ep = try {
          step(e)
        } catch {
          case t: Throwable => LettuceError(t)
        }
        e :: evaluate(ep, maxSteps - 1)
    }
  }

  private def foo(e: Expr): Expr = {
    e match {
      case FunCall(Closure(id_parameter, e_functionBody, EmptyEnv), v2) if v2.isValue =>
        substitute(e_functionBody, id_parameter, v2.asInstanceOf[Value])
      case FunCall(Closure(id_parameter, e_functionBody, Extend(x, v, env)), v2) if v2.isValue => {
        val newFuncitonBody = substitute(e_functionBody, x, v)
        foo(FunCall(Closure(id_parameter, newFuncitonBody, env), v2))
      }
      case FunCall(Closure(id_parameter, e_functionBody, env@ExtendRec(f, x, e, envcl)), v2) if v2.isValue =>
        val newFuncitonBody = substitute(e_functionBody, f, Closure(x, e, env))
        foo(FunCall(Closure(id_parameter, newFuncitonBody, envcl), v2))
      case _ => throw new InterpreterError(s"failure in foo on input $e")
    }
  }

  private def step(e: Expr): Expr = {
    e match {
        
        // DO
        case Binary(Plus, N(n1), N(n2)) => N(n1 + n2)
        case Binary(Times, N(n1), N(n2)) => N(n1 * n2)
        case Let(id, v1, e2) if v1.isValue => substitute(e2, id, v1.asInstanceOf[Value])
        case FunDef(id, ebody) => Closure(id, ebody, EmptyEnv)
        case FunCall(Closure(id_parameter, e_functionBody, env), v2) if v2.isValue => foo(e)
        

        // SEARCH
        case Binary(bop, v1, e2) if v1.isValue => Binary(bop, v1, step(e2))
        case Binary(bop, e1, e2) => Binary(bop, step(e1), e2)
        case Let(id, e1, e2) => Let(id, step(e1), e2)
        case FunCall(v1@Closure(id_parameter, e_functionBody, env), e2) => FunCall(v1, step(e2))
        case FunCall(v1, e2) if v1.isValue => throw new InterpreterError(s"invalid function call $e")
        case FunCall(e1, e2) => FunCall(step(e1), e2)

        // ERRORS
        case Ident(y) => throw new InterpreterError(s"Unbound variable found: $y")
        case _ => throw new InterpreterError(s"something is not yet implemented for expression $e")
    }
  }

  def substitute(e: Expr, x: String, v: Value): Expr = {
      def subst(e: Expr) = substitute(e, x, v)
      e match {
          case N(_) => e
          case FunDef(_, _) | Closure(_, _, _) => scopingCondition.substFunctions(this, e, x, v)
          case Binary(bop, e1, e2) => Binary(bop, subst(e1), subst(e2))
          case Let(id, e1, e2) => 
              if (id == x) Let(id, subst(e1), e2)
              else Let(id, subst(e1), subst(e2))
          case Ident(id) => 
              if (id == x) v
              else e
          case FunCall(e1, e2) => FunCall(subst(e1), subst(e2))
          
          case _ => throw new InterpreterError(s"failure in substitution on inputs\n\te: $e\n\tx: $x\n\tv: $v")
      }
  }
}
