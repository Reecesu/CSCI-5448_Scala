package saladbar


/**
  * ImplicitConversions
  * 
  * OO PATTERN: Template
  * 
  * Think javascript type changes, so 
  * true + 5 == 6
  * true && 5 == true
  * "hello" + 5 == "hello5"
  * "5" + 5 == "55"
  * ... and more ...
  */
case object ImplicitConversions extends TypeCondition {



    /**
      * performUop
      * 
      * always convert to expected type of the relevant operation
      *   `!` i.e. `Not`: expects a logical operand
      *   the others expect a numerical operand
      *
      * @param uop
      * @param v1
      * @param sc
      * @return
      */
    def performUop[A](uop: Uop, v1: Value)(sc: Value => A): A = {
        uop match {
            case Neg => sc(N(-v1.toNum))
            case Sin => sc(N(Math.sin(v1.toNum)))
            case Cos => sc(N(Math.cos(v1.toNum)))
            case Log => sc(N(Math.log(v1.toNum)))
            case Exp => sc(N(Math.exp(v1.toNum)))
            case Not => sc(B(!v1.toBool))
        }
    }


    /**
      * checkBop1
      * 
      * always valid here. We will type convert on the fly
      *
      * @param bop
      * @param v1
      * @param sc
      * @return
      */
    def checkBop1[A](bop: Bop, v1: Value)(sc: () => A): A = {
        sc()
    }


    /**
      * performShortCircuitBop
      * 
      * currently only And/Or support shortcircuiting ops
      * They each expect logical operands.
      *
      * @param bop
      * @param v1
      * @param e2
      * @param sc
      * @return
      */
    def performShortCircuitBop[A](bop: Bop, v1: Value, e2: Expr)(sc: Expr => A): A = {
        bop match {
            case And => if (v1.toBool) sc(e2) else sc(v1)
            case Or => if (v1.toBool) sc(v1) else sc(e2)
            case Seq => sc(e2)
            case _ => ???
        }
    }


    /**
      * performBop
      * 
      * always convert to expected type of the relevant operation
      * === and !== compare both type and value
      * Comparison, == and != works on strings iff both operands are already strings,
      *     else convert to numbers, then compare
      * Plus: if either is a string, convert both to strings.
      * remainder expects numbers.
      *
      * @param bop
      * @param v1
      * @param v2
      * @param sc
      * @return
      */
    def performBop[A](bop: Bop, v1: Value, v2: Value)(sc: Value => A): A = {

        def hCmp(f: (Double, Double) => Boolean)(g: (String, String) => Boolean): A = {
            (v1, v2) match {
                case (S(s1), S(s2)) => sc(B(g(s1, s2)))
                case _ => sc(B(f(v1.toNum, v2.toNum)))
            }
        }

        // TODO: bop class can have an (A, A) => B method
        bop match {
            case Geq => hCmp { _ >= _ }{ _ >= _ }
            case Gt => hCmp { _ > _ }{ _ > _ }
            case Leq => hCmp { _ <= _ }{ _ <= _ }
            case Lt => hCmp { _ < _ }{ _ < _ }
            case Eq => hCmp { _ == _ }{ _ == _ }
            case Neq => hCmp { _ != _ }{ _ != _ }
            case Eqq => sc(B(v1 == v2))
            case Neqq => sc(B(v1 != v2))
            case Plus => (v1, v2) match {
                case (S(_), _) | (_, S(_)) => sc(S(v1.toString + v2.toString))
                case _ => sc(N(v1.toNum + v2.toNum))
            }
            case Times => sc(N(v1.toNum * v2.toNum))
            case Minus => sc(N(v1.toNum - v2.toNum))
            case Div => {
                val n2 = v2.toNum
                if (n2.isNaN()) sc(N(n2))
                else sc(N(v1.toNum / n2))
            }
        }
    }


    /**
      * checkIf
      * 
      * with implicit conversion the value can always be made into logical value true/false
      *
      * @param v1
      * @param sc
      * @param fc
      * @return
      */
    def checkIf[A](v1: Value)( sc: Boolean => A )( fc: () => A ): A = {
        sc(v1.toBool)
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
        "implicit"
    }

    
}

