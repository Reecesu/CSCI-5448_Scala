package saladbar


/**
  * NoConversions
  * 
  * OO Pattern: Template
  */
case object NoConversions extends TypeCondition {


    /**
      * performUop
      * 
      * $v1 must already be of expected type
      *
      * @param uop
      * @param v1
      * @param sc
      * @return
      */
    def performUop[A](uop: Uop, v1: Value)(sc: Value => A): A = {

        def hUopArith(f: Double => Double): A = {
            v1 match {
                case N(n1) => sc(N(f(n1)))
                case _ => throw new NoConversionsError(s"$v1 not valid subject to $uop")
            }
        }

        uop match {
            case Neg => hUopArith{ -_ }  // silly, it looks like a face     v -_- v
            case Sin => hUopArith{ Math.sin }
            case Cos => hUopArith{ Math.cos }
            case Log => hUopArith{ Math.log }
            case Exp => hUopArith{ Math.exp }
            case Not => v1 match {
                case B(b1) => sc(B(!b1))
                case _ => throw new NoConversionsError(s"$v1 not valid subject to $uop")
            }
        }
    }


    /**
      * checkBop1
      * 
      * Confirm expected type of the left-hand valued operand of the this event
      *
      * @param bop
      * @param v1
      * @param sc
      * @return
      */
    def checkBop1[A](bop: Bop, v1: Value)(sc: () => A): A = {
        bop match {
            case And | Or =>  v1 match {
                case B(_) => sc()
                case _ => throw new NoConversionsError(s"$v1 not valid subject as first argument to $bop")
            }
            case Eq | Neq | Eqq | Neqq | Seq => sc()
            case Gt | Geq | Lt | Leq | Plus => v1 match {
                case N(_) | S(_) => sc()
                case _ => throw new NoConversionsError(s"$v1 not valid subject as first argument to $bop")
            }
            case Minus | Times | Div => v1 match {
                case N(_) => sc()
                case _ => throw new NoConversionsError(s"$v1 not valid subject as first argument to $bop")
            }
            case _ => throw new NoConversionsError(s"valid $bop not provided")
        }
    }


    /**
      * performShortCircuitBop
      * 
      * currently only And/Or support shortcircuiting ops
      * here, v1 must already represent true/false
      * 
      * TODO: refactor as `attemptExecution` and have an fc to hold the step on e2 logic
      *     from client.
      *
      * @param bop
      * @param v1
      * @param e2
      * @param sc
      * @return
      */
    def performShortCircuitBop[A](bop: Bop, v1: Value, e2: Expr)(sc: Expr => A): A = {
        v1 match {
            case B(b1) => bop match {
                case And => if (b1) sc(e2) else sc(v1)
                case Or => if (b1) sc(v1) else sc(e2)
               
            }
            case _ =>  bop match {
                case Seq => sc(e2)
                case _ => throw new NoConversionsError("expected boolean")
            }
        }
    }


    /**
      * performBop
      * 
      * arguments $v1 and $v2 must already be of expected type in order to perform execution
      *
      * @param bop
      * @param v1
      * @param v2
      * @param sc
      * @return
      */
    def performBop[A](bop: Bop, v1: Value, v2: Value)(sc: Value => A): A = {

        def hCmp[B](f: (Double, Double) => Boolean)(g: (String, String) => Boolean): A = {
            (v1, v2) match {
            case (N(n1), N(n2)) => sc(B(f(n1, n2)))
            case (S(s1), S(s2)) => sc(B(g(s1, s2)))
            case _ => throw new NoConversionsError(s"invalid value types on $bop: $v1, $v2")
            }
        }

        def hEqality[B](f: (Value, Value) => Boolean): A = {
            (v1, v2) match {
                case (N(_), N(_)) 
                    | (S(_), S(_)) 
                    | (B(_), B(_)) 
                    | (Closure(_, _, _), Closure(_, _, _))
                    | (Undefined, Undefined) => sc(B(f(v1, v2)))
                case _ => throw new NoConversionsError(s"invalid value types on $bop: $v1, $v2")
            }
        }

        bop match {
            case Plus => (v1, v2) match {
                case (N(n1), N(n2)) => sc(N(n1 + n2))
                case (S(s1), S(s2)) => sc(S(s1 + s2))
                case _ => throw new NoConversionsError(s"invalid value types on $bop: $v1, $v2")
            }
            case Times => (v1, v2) match {
                case (N(n1), N(n2)) => sc(N(n1 * n2))
                // TODO: strings as a different type symantic
                case _ => throw new NoConversionsError(s"invalid value types on $bop: $v1, $v2")
            }
            case Minus => (v1, v2) match {
                case (N(n1), N(n2)) => sc(N(n1 - n2))
                case _ => throw new NoConversionsError(s"invalid value types on $bop: $v1, $v2")
            }
            case Div => (v1, v2) match {
                case (N(n1), N(n2)) => sc(N(n1 / n2))
                case _ => throw new NoConversionsError(s"invalid value types on $bop: $v1, $v2")
            }
            case Geq => hCmp { _ >= _ }{ _ >= _ }
            case Gt => hCmp { _ > _ }{ _ > _ }
            case Leq => hCmp { _ <= _ }{ _ <= _ }
            case Lt => hCmp { _ < _ }{ _ < _ }
            case Eq => hEqality{ _ == _ }
            case Eqq => hEqality{ _ == _ }
            case Neq => hEqality{ _ != _ }
            case Neqq => hEqality{ _ != _ }
            case _ => ???
        }
    }


    /**
      * checkIf
      * 
      * with no conversion the value must already represent true of false
      *
      * @param v1
      * @param sc
      * @param fc
      * @return
      */
    def checkIf[A](v1: Value)( sc: Boolean => A )( fc: () => A ): A = {
        v1 match {
            case B(b) => sc(b)
            case _ => fc()
        }
    }


    /**
      * toString
      *
      * used as repr for parts of code, do not modify 
      * unless you are ready to change repr access methods
      * 
      * @return
      */
    override def toString: String = {
        "none"
    }

}

