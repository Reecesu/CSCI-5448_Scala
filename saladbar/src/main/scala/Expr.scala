
case class ExprToStringError(msg: String) extends Exception {
    override def toString(): String = s"EXPR TO STRING: $msg"
}

sealed abstract class Expr {
    override def toString: String
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A
    def isValue: Boolean
}
case class Binary(bop: Bop, e1: Expr, e2: Expr) extends Expr {
    override def toString: String =  s"($e1 $bop $e2)"
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        (e1, e2) match {
            case (v1, v2) if v1.isValue && v2.isValue => evalConditions.getTc.performBop(bop, v1.asInstanceOf[Value], v2.asInstanceOf[Value])(sc)
            case (v1, e2) if v1.isValue => evalConditions.getTc.checkBop1(bop, v1.asInstanceOf[Value]){
                () => e2.step(evalConditions){
                    e2p => sc(Binary(bop, v1, e2p))
                }
            }
            case (e1, e2) => e1.step(evalConditions){
                e1p => sc(Binary(bop, e1p, e2))
            }
        }
    }

    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        e1.substitute(evalConditions, x, esub){ 
            e1p => e2.substitute(evalConditions, x, esub){
                e2p => sc(Binary(bop, e1p, e2p))
            }
        }
    }
    def isValue: Boolean = false

}
case class Let(id: String, e1: Expr, e2: Expr) extends Expr {
    override def toString: String = s"let $id = $e1 in $e2"
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        evalConditions.getLec.check(e1){
            () => e2.substitute(evalConditions, id, e1)(sc)
        }{
            () => e1.step(evalConditions){
                e1p => sc(Let(id, e1p, e2))
            }
        }
    }
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        if (x == id) e1.substitute(evalConditions, x, esub){
            e1p => sc(Let(id, e1p, e2))
        }
        else e1.substitute(evalConditions, x, esub){
            e1p => e2.substitute(evalConditions, x, esub){
                e2p => sc(Let(id, e1p, e2p))
            }
        }
    }
    def isValue: Boolean = false
}
case class Ident(id: String) extends Expr {
    override def toString: String = id
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        sc(LettuceError(new InterpreterError(s"Unbound variable found: $id")))
    }
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        if (x == id) sc(esub)
        else sc(this)
    }
    def isValue: Boolean = false
}
case class FunDef(id_parameter: String, e_functionBody: Expr) extends Expr {
    override def toString: String = s"function ($id_parameter) $e_functionBody"
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        sc(Closure(id_parameter, e_functionBody, EmptyEnv))
    }
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        evalConditions.getSc.substFunctions(evalConditions, this, x, esub){
            ep => sc(ep)
        }
    }
    def isValue: Boolean = false
}
case class FunCall(e1: Expr, e2: Expr) extends Expr {
    override def toString: String =  s"{$e1($e2)}"

    private def foo[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        this match {
            case FunCall(Closure(id_parameter, e_functionBody, EmptyEnv), e2) =>
                e_functionBody.substitute(evalConditions, id_parameter, e2)(sc)
            case FunCall(Closure(id_parameter, e_functionBody, Extend(x, v, env)), e2) => {
                e_functionBody.substitute(evalConditions, x, v){
                    newFuncitonBody => FunCall(Closure(id_parameter, newFuncitonBody, env), e2).foo(evalConditions)(sc)
                }
            }
            case FunCall(Closure(id_parameter, e_functionBody, env@ExtendRec(f, x, e, envcl)), e2)  => {
                e_functionBody.substitute(evalConditions, f, Closure(x, e, env)){
                    newFunctionBody => FunCall(Closure(id_parameter, newFunctionBody, envcl), e2).foo(evalConditions)(sc)
                }
            }
            case _ => sc(LettuceError(new InterpreterError(s"failure in foo on input $this")))
        }
  }
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        e1 match {
            case v1@Closure(id_parameter, e_funcitonBody, env) => {
                evalConditions.getLec.check(e2){
                    () => this.foo(evalConditions)(sc)
                }{
                    () => e2.step(evalConditions){
                        e2p => sc(FunCall(v1, e2p))
                    }
                }
            }
            case v1 if v1.isValue => sc(LettuceError( new InterpreterError(s"invlaid function call $this")))
            case _ => e1.step(evalConditions){
                e1p => sc(FunCall(e1p, e2))
            }
        }
        
    }
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        e1.substitute(evalConditions, x, esub){
            e1p => e2.substitute(evalConditions, x, esub){
                e2p => sc(FunCall(e1p, e2p))
            }
        }
    }
    def isValue: Boolean = false
}
case class LetRec(id_functionName: String,
        id_parameter: String, 
        e_funcitonBody: Expr, 
        e2: Expr) extends Expr {

    override def isValue: Boolean = false
    override def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        val env: Environment = ExtendRec(id_functionName, id_parameter, e_funcitonBody, EmptyEnv)
        val v: Value = Closure(id_parameter, e_funcitonBody, env)
        e2.substitute(evalConditions, id_functionName, v)(sc)
    }
    override def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        ???
        // TODO: update to closure only on parse...
    }
    override def toString: String = s"letrec $id_functionName = function($id_parameter) { $e_funcitonBody } in $e2"
}



sealed trait Bop {
    override def toString: String
}
case object Plus extends Bop {
    override def toString: String = "+"
}
case object Times extends Bop{
    override def toString: String = "*"
}
















abstract class Value extends Expr {
    def toNum: Double
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = throw new InterpreterError(s"cannot step on a value: $this")
    override def isValue = true
}
case class N(n: Double) extends Value {
    def toNum = n
    override def toString: String = s"$n"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = sc(this)
}
case class Closure(id_parameter: String, e_funcitonBody: Expr, env: Environment) extends Value {
    def toNum = Double.NaN
    override def toString: String = s"CLOSURE($id_parameter, $e_funcitonBody, $env)"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        evalConditions.getSc.substFunctions(evalConditions, this, x, esub){
            ep => sc(ep)
        }
    }
}
case class LettuceError(err: Throwable) extends Value {
    def toNum = Double.NaN
    override def toString: String = s"LETTUCE ERROR: $err"
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = sc(this)
}












case class LookupError(x: String) extends Exception {
    override def toString(): String = s"Expected $x"
}
sealed trait Environment {
    def lookup(x: String): Expr = {
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
case class Extend(id: String, e: Expr, env: Environment) extends Environment
case class ExtendRec(id_functionName: String, 
        id_parameter: String, 
        e_funcitonBody: Expr, 
        env: Environment) extends Environment