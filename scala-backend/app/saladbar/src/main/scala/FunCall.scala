package saladbar


/**
  * FunCall(e1, e2)
  * 
  * from concrete syntax: e1(e2)
  * 
  * OO PATTERN: Composite
  *
  * @param e1
  * @param e2
  */
case class FunCall(e1: Expr, e2: Expr) extends Expr {


    override def toString: String =  s"{$e1($e2)}"


    /**
      * applyEnvThenCall
      * 
      * unroll the closure environment into the function body. for each
      * variable in the environment, perform substitution. Avoid capture
      * of function parameter. After the environment is applied, then
      * apply the parameter.
      * 
      * TODO: consider refactor to apply argument first, then transfere applyEnv
      * to be a method of closure
      *
      * @param evalConditions
      * @param sc
      * @return
      */
    private def applyEnvThenCall[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        this match {
            case FunCall(Closure(id_parameter, e_functionBody, EmptyEnv), e2) =>
                e_functionBody.substitute(evalConditions, id_parameter, e2)(sc)
            case FunCall(Closure(id_parameter, e_functionBody, Extend(x, v, env)), e2) => {
                if (id_parameter == x) FunCall(Closure(id_parameter, e_functionBody, env), e2).applyEnvThenCall(evalConditions)(sc)
                else e_functionBody.substitute(evalConditions, x, v){
                    newFuncitonBody => FunCall(Closure(id_parameter, newFuncitonBody, env), e2).applyEnvThenCall(evalConditions)(sc)
                }
            }
            case FunCall(Closure(id_parameter, e_functionBody, env@ExtendRec(f, x, e, envcl)), e2)  => {
                if (id_parameter == f) FunCall(Closure(id_parameter, e_functionBody, envcl), e2).applyEnvThenCall(evalConditions)(sc)
                else e_functionBody.substitute(evalConditions, f, Closure(x, e, env)){
                    newFunctionBody => FunCall(Closure(id_parameter, newFunctionBody, envcl), e2).applyEnvThenCall(evalConditions)(sc)
                }
            }
            case _ => sc(LettuceError(new InterpreterError(s"failure in applyEnvThenCall on input $this")))
        }
    }


    /**
      * step
      * 
      * e1(e2)
      * step e1 to a closure
      * step on e2 to a value (if eager)
      * substitute the argument e2 for the closure parameter in the closures function body.
      *
      * @param evalConditions
      * @param sc
      * @return
      */
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        e1 match {
            case v1@Closure(id_parameter, e_funcitonBody, env) => {
                evalConditions.getLec.check(e2){
                    () => this.applyEnvThenCall(evalConditions)(sc)
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


    /**
      * substitute
      * 
      * find all free instances of $x in $this and replace with $esub
      *
      * @param evalConditions
      * @param x
      * @param esub
      * @param sc
      * @return
      */
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        e1.substitute(evalConditions, x, esub){
            e1p => e2.substitute(evalConditions, x, esub){
                e2p => sc(FunCall(e1p, e2p))
            }
        }
    }


}

