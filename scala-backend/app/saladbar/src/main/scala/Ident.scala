package saladbar


/**
  * Ident(id)
  * from concrete syntax: id
  *
  * @param id
  */
case class Ident(id: String) extends Expr {


    override def toString: String = id


    /**
      * step
      * 
      * An ident cannot be stepped on, this is a runtime error of the code
      * 
      * TODO: move to static time check for program soundness
      *
      * @param evalConditions
      * @param sc
      * @return
      */
    def step[A](evalConditions: EvalConditions)(sc: Expr => A): A = {
        sc(LettuceError(new InterpreterError(s"Unbound variable found: $id")))
    }


    /**
      * substitute
      * 
      * if $this is the $x I am looking for, then make it $esub,
      * else, leave it as $x as is it is not the free varaible of interest.
      *
      * @param evalConditions
      * @param x
      * @param esub
      * @param sc
      * @return
      */
    def substitute[A](evalConditions: EvalConditions, x: String, esub: Expr)(sc: Expr => A): A = {
        if (x == id) sc(esub)
        else sc(this)
    }


}

