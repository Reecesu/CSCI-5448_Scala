package saladbar


/**
  * Ident(id)
  * from concrete syntax: id
  *
  * @param id
  */
case class Ident(id: String) extends Expr {


    override def toString: String = id


    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        sc(LettuceError(new InterpreterError(s"Unbound variable found: $id")))
    }


    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        if (x == id) sc(esub)
        else sc(this)
    }


}

