package saladbar


/**
  * DynamicScopingError
  * 
  * Used in DynamicScope
  *
  * @param msg
  */
case class DynamicScopeError(msg: String) extends Exception{


  override def toString: String = s"DYNAMIC SCOPING ERROR: $msg"


}

