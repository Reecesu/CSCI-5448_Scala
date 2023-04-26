package saladbar


/**
  * FunCall(e1, e2)
  * from concrete syntax: e1(e2)
  *
  * @param e1
  * @param e2
  */
case class FunCall(e1: Expr, e2: Expr) extends Expr {


    override def toString: String =  s"{$e1($e2)}"


    private def foo[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        this match {
            case FunCall(Closure(id_parameter, e_functionBody, EmptyEnv), e2) =>
                e_functionBody.substitute(evalConditions, id_parameter, e2)(sc)
            case FunCall(Closure(id_parameter, e_functionBody, Extend(x, v, env)), e2) => {
                if (id_parameter == x) FunCall(Closure(id_parameter, e_functionBody, env), e2).foo(evalConditions)(sc)
                else e_functionBody.substitute(evalConditions, x, v){
                    newFuncitonBody => FunCall(Closure(id_parameter, newFuncitonBody, env), e2).foo(evalConditions)(sc)
                }
            }
            case FunCall(Closure(id_parameter, e_functionBody, env@ExtendRec(f, x, e, envcl)), e2)  => {
                if (id_parameter == f) FunCall(Closure(id_parameter, e_functionBody, envcl), e2).foo(evalConditions)(sc)
                else e_functionBody.substitute(evalConditions, f, Closure(x, e, env)){
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


}

