package saladbar


/**
  * NoConversionsError
  * 
  * Used in NoConversions
  *
  * @param msg
  */
case class NoConversionsError(msg: String) extends Exception {


  override def toString: String = s"NO CONVERSIONS: $msg"


}

