package saladbar


/**
  * LexicalScopingError
  * 
  * Used in LexicalScope
  *
  * @param msg
  */
case class LexicalScopeError(msg: String) extends Exception{
  override def toString: String = s"LEXICAL SCOPING ERROR: $msg"
}
