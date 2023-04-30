package saladbar


/**
  * InterpreterError
  * 
  * used by Interpreter class
  *
  * @param msg
  */
case class InterpreterError(msg: String) extends Exception{
  override def toString: String = s"INTERPRETER ERROR: $msg"
}
