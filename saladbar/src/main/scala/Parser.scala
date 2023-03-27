import scala.util.parsing.combinator.RegexParsers

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

    def exprLev1: Parser[Expr] = {
        val letOpt = ("let" ~> identifier) ~ ("=" ~> exprLev1) ~ ("in" ~> exprLev1)  ^^ {
            case s1 ~ e1 ~ e2 => Let(s1, e1, e2)
        }

        letOpt | exprAS
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
          ( identifier  ^^ {
              case s => Ident(s)
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
