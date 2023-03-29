import scala.util.parsing.combinator.RegexParsers


case class SyntaxError(s: String) extends Exception {
    override def toString: String = { 
        s"Syntax Error: $s"
    }   
}

/**
  * ADDAPTED FROM: https://github.com/sriram0339/LettucePlaygroundScala
  *   - https://github.com/sriram0339/LettucePlaygroundScala/blob/master/src/main/scala/edu/colorado/csci3155/LettuceAST/LettuceParser.scala
  */
class Parser extends RegexParsers {
    def floatingPointNumber: Parser[String] = { 
        """-?(\d+(\.\d*)?|\d*\.\d+)([eE][+-]?\d+)?[fFdD]?""".r
    }   

    def identifier: Parser[String] = { 
        """[a-zA-Z_][a-zA-Z0-9_]*""".r
    }   
    
    def funDefinition: Parser[FunDef] = { 
         ("function" ~"(") ~> identifier ~ (")" ~> exprLev1)  ^^ {
            case id~e => FunDef(id, e)

        }   
    }  

    def funCallArgs: Parser[Expr] = {
        "(" ~> exprLev1 <~ ")"
    }

    def exprLev1: Parser[Expr] = {
        val letOpt = ("let" ~> identifier) ~ ("=" ~> exprLev1) ~ ("in" ~> exprLev1)  ^^ {
            case s1 ~ e1 ~ e2 => Let(s1, e1, e2)
        }

        val recFunDefOpt = ("letrec" ~> identifier) ~ ("=" ~> funDefinition) ~ ("in" ~> exprLev1 ) ^^ {
            case s1 ~ fd ~ e2 =>
                fd match {
                    case FunDef(id, e1) => LetRec (s1,id, e1, e2)
                    case _ => throw SyntaxError(s"Unexpected case in letrec definition: $fd")
                }
        }

        val funDefOpt = funDefinition ^^ { s => s }

        letOpt | recFunDefOpt | funDefOpt | exprAS
    }

    def exprAS: Parser[Expr] = {
        exprMD ~ opt( "+" ~ exprAS ) ^^ {
            case e1 ~ Some("+" ~ e2) => Binary(Plus, e1, e2)
            case e1 ~ None => e1
        }
    }

    def exprMD: Parser[Expr] = {
        exprVal ~ opt( "*" ~ exprMD ) ^^ {
            case e1 ~ Some("*" ~ e2) => Binary(Times, e1, e2)
            case e1 ~ None => e1
        }
    }

    def exprVal: Parser[Expr] = {
        ( floatingPointNumber ^^ { s => N(s.toFloat)} ) |
          (  "(" ~> exprLev1 <~ ")" ) |
          ( identifier ~ rep(funCallArgs) ^^ {
            case s~Nil => Ident(s)
            case s~l => l.foldLeft[Expr](Ident(s)) { case (e, lj) => FunCall(e, lj) }
          })
    }



    def parse(s: String): Expr = {
        val e = parseAll(exprLev1, s)  // RegexParsers.parseAll
        e match {
            case Success(e, _) => e
            case Failure(msg, _) => throw new IllegalArgumentException("Failure:" + msg)
            case Error(msg, _) => throw new IllegalArgumentException("Error: " + msg)
        }
    }
}
