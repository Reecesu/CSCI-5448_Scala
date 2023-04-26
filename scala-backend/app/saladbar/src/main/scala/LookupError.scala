package saladbar


/**
  * LookupError
  *
  * @param x
  */
case class LookupError(x: String) extends Exception {
    override def toString(): String = s"Expected $x"
}

