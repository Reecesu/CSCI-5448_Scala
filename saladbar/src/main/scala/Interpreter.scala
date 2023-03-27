object Interpreter {
  def evaluate(e: Expr): List[Expr] = {
    if (e.isValue) List(e)
    else {
        val ep = step(e)
        e :: evaluate(ep)
    }
  }

  private def step(e: Expr): Expr = {
    e match {
        
        // DO
        case Binary(Plus, N(n1), N(n2)) => N(n1 + n2)
        case Binary(Times, N(n1), N(n2)) => N(n1 * n2)
        case Let(id, v1, e2) if v1.isValue => substitute(e2, id, v1.asInstanceOf[Value])

        // SEARCH
        case Binary(bop, v1, e2) if v1.isValue => Binary(bop, v1, step(e2))
        case Binary(bop, e1, e2) => Binary(bop, step(e1), e2)
        case Let(id, e1, e2) => Let(id, step(e1), e2)

        // ERRORS
        case Ident(y) => ???
        case _ => ???
    }
  }

  private def substitute(e: Expr, x: String, v: Value): Expr = {
    def subst(e: Expr) = substitute(e, x, v)
    e match {
        case Binary(bop, e1, e2) => Binary(bop, subst(e1), subst(e2))
        case Let(id, e1, e2) => 
            if (id == x) Let(id, subst(e1), e2)
            else Let(id, subst(e1), subst(e2))
        case Ident(id) => 
            if (id == x) v
            else e
        case _ => e
    }
}
}
