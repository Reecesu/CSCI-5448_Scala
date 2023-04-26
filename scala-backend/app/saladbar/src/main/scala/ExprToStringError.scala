package saladbar


/**
  * ExprToStringError
  *
  * @param msg
  */
case class ExprToStringError(msg: String) extends Exception {


    override def toString(): String = s"EXPR TO STRING: $msg"

    
}

