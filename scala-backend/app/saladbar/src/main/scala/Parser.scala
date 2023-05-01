package saladbar



import scala.util.parsing.combinator.RegexParsers



case class SyntaxError(s: String) extends Exception {
    override def toString: String = { 
        s"Syntax Error: $s"
    }   
}


/**
  * Parser
  * 
  * OO PATTERN: Parser
  * 
  * relevant grammar in EBNF::
  * 
  *           exprLev1 ::= if (exprLev1) exprLev1 else exprLev1
  *                    | try { exprLev1 } catch { exprLev1 }
  *                    | let exprLev1 = exprLev1 in exprLev1
  *                    | letrec exprLev1 = funDefinition in exprLev1
  *                    | functionDefinition
  *                    | exprAndOr
  * functionDefinition ::= function(identifier) exprLev1
  *          exprAndOr ::= exprCmp { && exprAndOr | \|\| exprAndOr }
  *            exprCmp ::= exprAS { === exprCmp 
  *                               | !==  exprCmp
  *                               | >=  exprCmp
  *                               | > exprCmp
  *                               | <=  exprCmp
  *                               | <  exprCmp
  *                               | ==  exprCmp
  *                               | !=  exprCmp
  *                               | ; exprCmp }
  *             exprAS ::= exprMD { + exprAS | - exprAS }
  *             exprMD ::= exprVal { * exprMD | / exprMD }
  *            exprVal ::= n
  *                    | b
  *                    | { sin | cos | log | exp | ! | - }(exprLev1)
  *                    | s
  *                    | (exprLev1)
  *                    | identifier
  * 
  * identifier is a valid variable name
  * n is a floating point number
  * b is a boolean value true or false all lowercase
  * s is a string
  * 
  * NOTE requirement for () in uop(e)
  * 
  * TODO: straiten out the parser with more atomic parsing
  * 
  * ADDAPTED FROM: https://github.com/sriram0339/LettucePlaygroundScala/blob/master/src/main/scala/edu/colorado/csci3155/LettuceAST/LettuceParser.scala
  * and: https://github.com/csci3155/pppl-labdev/blob/main/src/main/scala/jsy/lab5/Parser.scala
  * 
  */
class Parser extends RegexParsers {


    def floatingPointNumber: Parser[String] = { 
        """-?(\d+(\.\d*)?|\d*\.\d+)([eE][+-]?\d+)?[fFdD]?""".r
    }   


    def identifier: Parser[String] = { 
        """[a-zA-Z_][a-zA-Z0-9_]*""".r
    }  


    def strLitteral: Parser[String] = { 
        """[^']*""".r
    }


    def funDefinition: Parser[Closure] = { 
         ("function" ~"(") ~> identifier ~ (")" ~> exprLev1)  ^^ {
            case id~e => Closure(id, e, EmptyEnv)
        }   
    }  


    def funCallArgs: Parser[Expr] = {
        "(" ~> exprLev1 <~ ")"
    }


    def exprLev1: Parser[Expr] = {
        val ifthenelseOpt = ("if" ~ "(" ~> exprLev1) ~ (")" ~> exprLev1) ~ ("else" ~> exprLev1)  ^^ {
            case e1 ~ e2 ~ e3 => IfThenElse(e1, e2, e3)
        }
        
        val trycatchOpt = ("try" ~ "{" ~> exprLev1) ~ ("}" ~ "catch" ~ "{" ~> exprLev1) ~ "}" ^^ {
            case e1 ~ e2 ~ _ => TryCatch(e1, e2)
        }
        val letOpt = ("let" ~> identifier) ~ ("=" ~> exprLev1) ~ ("in" ~> exprLev1)  ^^ {
            case s1 ~ e1 ~ e2 => Let(s1, e1, e2)
        }

        val recFunDefOpt = ("letrec" ~> identifier) ~ ("=" ~> funDefinition) ~ ("in" ~> exprLev1 ) ^^ {
            case s1 ~ fd ~ e2 =>
                fd match {
                    case Closure(_, _, _) => LetRec (s1, fd, e2)
                    case _ => throw SyntaxError(s"Unexpected case in letrec definition: $fd")
                }
        }

        val funDefOpt = funDefinition ^^ { s => s }

        ifthenelseOpt | trycatchOpt | letOpt | recFunDefOpt | funDefOpt | exprAndOr
    }

    def exprAndOr: Parser[Expr] = {
        exprCmp ~ opt( ("&&"|"||") ~ exprAndOr ) ^^ {
            case e1 ~ Some("&&" ~ e2) => Binary(And, e1, e2)
            case e1 ~ Some("||" ~ e2) => Binary(Or, e1, e2)
            case e1 ~ None => e1
        }
    }

    def exprCmp: Parser[Expr] = {
        // NOTE: order matters: need === before == or it won't work correctly here.
        exprAS ~ opt( ("==="|"!=="|">="|">"|"<="|"<"|"=="|"!="|";") ~ exprCmp ) ^^ {
            case e1 ~ Some(">=" ~ e2) => Binary(Geq, e1, e2)
            case e1 ~ Some(">" ~ e2) => Binary(Gt, e1, e2)
            case e1 ~ Some("<=" ~ e2) => Binary(Leq, e1, e2)
            case e1 ~ Some("<" ~ e2) => Binary(Lt, e1, e2)
            case e1 ~ Some("==" ~ e2) => Binary(Eq, e1, e2)
            case e1 ~ Some("===" ~ e2) => Binary(Eqq, e1, e2)
            case e1 ~ Some("!=" ~ e2) => Binary(Neq, e1, e2)
            case e1 ~ Some("!==" ~ e2) => Binary(Neqq, e1, e2)
            case e1 ~ Some(";" ~ e2) => Binary(Seq, e1, e2)
            case e1 ~ None => e1
        }
    }
    
    def exprAS: Parser[Expr] = {
        exprMD ~ opt( ("+"|"-") ~ exprAS ) ^^ {
            case e1 ~ Some("+" ~ e2) => Binary(Plus, e1, e2)
            case e1 ~ Some("-" ~ e2) => Binary(Minus, e1, e2)
            case e1 ~ None => e1
        }
    }

    def exprMD: Parser[Expr] = {
        exprVal ~ opt( ("*"|"/") ~ exprMD ) ^^ {
            case e1 ~ Some("*" ~ e2) => Binary(Times, e1, e2)
            case e1 ~ Some("/" ~ e2) => Binary(Div, e1, e2)
            case e1 ~ None => e1
        }
    }

    def exprVal: Parser[Expr] = {
        ( floatingPointNumber ^^ { s => N(s.toFloat)} ) |
        ( "true" ^^ { _ => B(true) } ) |
        ( "false" ^^ { _ => B(false) } ) |
        // NOTE requirement for () in uop(e)
        ( ( "sin" | "cos" | "log" | "exp" | "!" | "-" ) ~ ("(" ~> exprLev1 <~ ")") ^^{
            case "sin" ~ e => Unary(Sin, e)
            case "cos" ~ e => Unary(Cos, e)
            case "log" ~ e => Unary(Log, e)
            case "exp" ~ e => Unary(Exp, e)
            case "!" ~ e => Unary(Not, e)
            case "-" ~ e => Unary(Neg, e)
        } ) |
        // https://github.com/csci3155/pppl-labdev/blob/main/src/main/scala/jsy/lab5/Parser.scala
        ( "'" ~> strLitteral <~ "'" ^^ { str => S(str) } ) |
          (  "(" ~> exprLev1 <~ ")" ) |
          ( identifier ~ rep(funCallArgs) ^^ {
            case s~Nil => Ident(s)
            case s~l => l.foldLeft[Expr](Ident(s)) { case (e, lj) => FunCall(e, lj) }
          })
    }


    /**
      * parse
      * 
      * parse string $s of assumed concrete langauge syntax
      * to Expr $e of abstract langauge syntax. return $e.
      *
      * @param s
      */
    def parse(s: String): Expr = {
        val e = parseAll(exprLev1, s)  // RegexParsers.parseAll
        e match {
            case Success(e, _) => e
            case Failure(msg, _) => throw new IllegalArgumentException("Failure:" + msg)
            case Error(msg, _) => throw new IllegalArgumentException("Error: " + msg)
        }
    }


}

