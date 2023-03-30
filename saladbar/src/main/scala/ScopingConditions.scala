
class EvalConditions(sc: ScopingCondition, tc: TypeCondition, lec: LazyEagerCondition) {
  def getSc: ScopingCondition = this.sc
  def getTc: TypeCondition = this.tc
  def getLec: LazyEagerCondition = this.lec
  override def toString: String = {
    s"scoping_conditions: $sc, type_conditions: $tc, lazy_eager_condition: $lec"
  }
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
        case Closure(id_parameter, e_functionBody, env) => 
          if (x == id_parameter) sc(e)
          else e_functionBody.substitute(evalConditions, x, esub){
            newFunctionBody => sc(Closure(id_parameter, newFunctionBody, env))
          }
        case _ => throw new LexicalScopingError("Failed substfunciton on input $e")
      }
    }
  override def toString: String = {
    "lexical"
  }
}

case object DynamicScope extends ScopingCondition {
  def substFunctions[A](evalConditions: EvalConditions, e: Expr, x: String, esub: Expr)(sc: Expr => A):A = {
    e match {
      case Closure(id_parameter, e_functionBody, env) => 
        sc(Closure(id_parameter, e_functionBody, Extend(x, esub, env)))
      case _ => throw new DynamicScopingError("Failed substfunciton on input $e")
    }
  }
  override def toString: String = {
    "dynamic"
  }
}

abstract class TypeCondition {
    def checkBop1[A](bop: Bop, v1: Value)(sc: () => A): A
    def performBop[A,B](bop: Bop, v1: Value, v2: Value)(sc: Value => A): A
    def checkIf[A](v1: Value)( sc: Boolean => A )( fc: () => A ): A
}
case object ImplicitConversions extends TypeCondition {
  def checkBop1[A](bop: Bop, v1: Value)(sc: () => A): A = {
    sc()
  }
  def performBop[A,B](bop: Bop, v1: Value, v2: Value)(sc: Value => A): A = {
    // TODO: bop class can have an (A, A) => B method
    bop match {
      case Plus => (v1, v2) match {
        case (S(_), _) | (_, S(_)) => sc(S(v1.toString + v2.toString)) // TODO: Strings
        case _ => sc(N(v1.toNum + v2.toNum))
      }
      case Times => sc(N(v1.toNum * v2.toNum))
      case Minus => sc(N(v1.toNum - v2.toNum))
      case Geq => sc(B(v1.toNum >= v2.toNum))
    }
  }
  def checkIf[A](v1: Value)( sc: Boolean => A )( fc: () => A ): A = {
    sc(v1.toBool)
  }
  override def toString: String = {
    "implicite"
  }
}

case class NoConversionsError(msg: String) extends Exception {
  override def toString: String = s"NO CONVERSIONS: $msg"
}
case object NoConversions extends TypeCondition {
  def checkBop1[A](bop: Bop, v1: Value)(sc: () => A): A = {
    bop match {
      case Plus | Times | Minus | Geq => v1 match {
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
        case (S(s1), S(s2)) => sc(S(s1 + s2))
        case _ => throw new NoConversionsError(s"invalid value types on $bop: $v1, $v2")
      }
      case Times => (v1, v2) match {
        case (N(n1), N(n2)) => sc(N(n1 * n2))
        // TODO: strings
        case _ => throw new NoConversionsError(s"invalid value types on $bop: $v1, $v2")
      }
      case Minus => (v1, v2) match {
        case (N(n1), N(n2)) => sc(N(n1 - n2))
        case _ => throw new NoConversionsError(s"invalid value types on $bop: $v1, $v2")
      }
      case Geq => (v1, v2) match {
        case (N(n1), N(n2)) => sc(B(n1 >= n2))
        case _ => throw new NoConversionsError(s"invalid value types on $bop: $v1, $v2")
      }
    }
  }

  def checkIf[A](v1: Value)( sc: Boolean => A )( fc: () => A ): A = {
    v1 match {
      case B(b) => sc(b)
      case _ => fc()
    }
  }

  override def toString: String = {
    "none"
  }

}

sealed abstract class LazyEagerCondition {
  def check[A](e1: Expr)(sc: () => A)(fc: () => A): A
}
case object LazyCondition extends LazyEagerCondition {
    def check[A](e1: Expr)(sc: () => A)(fc: () => A): A = {
      sc()
    }
  override def toString: String = {
    "lazy"
  }
}
case object EagerCondition extends LazyEagerCondition {
    def check[A](e1: Expr)(sc: () => A)(fc: () => A): A = {
      if (e1.isValue) sc() else fc()
    }

    override def toString: String = {
    "eager"
  }
}