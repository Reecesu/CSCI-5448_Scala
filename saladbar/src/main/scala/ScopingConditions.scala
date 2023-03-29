
class EvalConditions(sc: ScopingCondition, tc: TypeCondition, lec: LazyEagerCondition) {
  def getSc: ScopingCondition = this.sc
  def getTc: TypeCondition = this.tc
  def getLec: LazyEagerCondition = this.lec
}

abstract class ScopingCondition {
  def substFunctions[A](evalConditions: EvalConditions, e: Expr, x: String, esub: Expr)(sc: Expr => A):A 
}

case class DynamicScopingError(msg: String) extends Exception{
  override def toString: String = s"DYNAMIC SCOPING ERROR: $msg"
}
case class LexicalScopingError(msg: String) extends Exception{
  override def toString: String = s"LEXICAL SCOPING ERROR: $msg"
}

// OO PATTERN: strategy, maybe adaptor
case object LexicalScope extends ScopingCondition {
  def substFunctions[A](evalConditions: EvalConditions, e: Expr, x: String, esub: Expr)(sc: Expr => A):A = {
    // TODO: rec?
      e match {
        case FunDef(id_parameter, e_functionBody) => 
          if (x == id_parameter) sc(e)
          else  e_functionBody.substitute(evalConditions, x, esub){
           e_functionBodyp => sc(FunDef(id_parameter, e_functionBodyp))
          }
        case Closure(id_parameter, e_functionBody, env) => 
          if (x == id_parameter) sc(e)
          else e_functionBody.substitute(evalConditions, x, esub){
            e_functionBodyp => sc(Closure(id_parameter, e_functionBody, env))
          }
        case _ => throw new LexicalScopingError("Failed substfunciton on input $e")
      }
    }
}

case object DynamicScope extends ScopingCondition {
  def substFunctions[A](evalConditions: EvalConditions, e: Expr, x: String, esub: Expr)(sc: Expr => A):A = {
    e match {
      case FunDef(id_parameter, e_functionBody) => 
        sc(Closure(id_parameter, e_functionBody, Extend(x, esub, EmptyEnv)))
      case Closure(id_parameter, e_functionBody, env) => 
        sc(Closure(id_parameter, e_functionBody, Extend(x, esub, env)))
      case _ => throw new DynamicScopingError("Failed substfunciton on input $e")
    }
  }
}

abstract class TypeCondition {
    def checkBop1[A](bop: Bop, v1: Value)(sc: () => A): A
    def performBop[A,B](bop: Bop, v1: Value, v2: Value)(sc: Value => A): A
}
case object ImplicitConversions extends TypeCondition {
  def checkBop1[A](bop: Bop, v1: Value)(sc: () => A): A = {
    sc()
  }
  def performBop[A,B](bop: Bop, v1: Value, v2: Value)(sc: Value => A): A = {
    bop match {
      case Plus => sc(N(v1.toNum + v2.toNum)) // TODO: Strings
      case Times => sc(N(v1.toNum * v2.toNum))
    }
  }
}

case class NoConversionsError(msg: String) extends Exception {
  override def toString: String = s"NO CONVERSIONS: $msg"
}
case object NoConversions extends TypeCondition {
  def checkBop1[A](bop: Bop, v1: Value)(sc: () => A): A = {
    bop match {
      case Plus | Times => v1 match {
        case N(_) => sc()
        case _ => throw new NoConversionsError(s"$v1 not valid subject as first argument to $bop")
      }
      case _ => throw new NoConversionsError(s"valid $bop not provided")
    }
  }
  def performBop[A,B](bop: Bop, v1: Value, v2: Value)(sc: Value => A): A = {
    bop match {
      case Plus => (v1, v2) match {
        case (N(n1), N(n2)) => sc(N(n1 + n2))
        // TODO: strings
        case _ => throw new NoConversionsError(s"invalid value types on $bop: $v1, $v2")
      }
      case Times => (v1, v2) match {
        case (N(n1), N(n2)) => sc(N(n1 * n2))
        // TODO: strings
        case _ => throw new NoConversionsError(s"invalid value types on $bop: $v1, $v2")
      }
    }
  }
}

sealed abstract class LazyEagerCondition {
  def check[A](e1: Expr)(sc: () => A)(fc: () => A): A
}
case object LazyCondition extends LazyEagerCondition {
    def check[A](e1: Expr)(sc: () => A)(fc: () => A): A = {
      sc()
    }
}
case object EagerCondition extends LazyEagerCondition {
    def check[A](e1: Expr)(sc: () => A)(fc: () => A): A = {
      if (e1.isValue) sc() else fc()
    }
}