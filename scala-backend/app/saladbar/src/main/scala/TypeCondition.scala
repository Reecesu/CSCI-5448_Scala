package saladbar


/**
  * TypeCondition
  * 
  * OO PATTERN: Template
  */
abstract class TypeCondition {

    
    /**
      * checkBop1
      * 
      * encapsulate determining next steps on a binary
      * operation $bop based on the value of the left sub-expression
      * $v1. If the value is appropiate for the type condition and the
      * $bop, then execute $sc
      *
      * @param bop
      * @param v1
      * @param sc
      * @return
      */
    def checkBop1[A](bop: Bop, v1: Value)(sc: () => A): A


    /**
      * performUop
      *
      * encapsulate execution of the the unary operation $uop
      * logic subject to the typing condition and the value $v1
      * 
      * @param uop
      * @param v1
      * @param sc
      * @return
      */
    def performUop[A](uop: Uop, v1: Value)(sc: Value => A): A


    /**
      * performShortCircuitBop
      * 
      * Some binary operations will short circuit, for `e1 bop e2` only
      * e1 needs to be a value v1 before evaluation.
      *
      * @param bop
      * @param v1
      * @param e2
      * @param sc
      * @return
      */
    def performShortCircuitBop[A](bop: Bop, v1: Value, e2: Expr)(sc: Expr => A): A


    /**
      * performBop
      * 
      * for some v1 bop v2, determine if the operation is valid, if so,
      * perform logic and pass result to sc
      *
      * @param bop
      * @param v1
      * @param v2
      * @param sc
      * @return
      */
    def performBop[A](bop: Bop, v1: Value, v2: Value)(sc: Value => A): A

    /**
      * checkIf
      * 
      * if $v1 is valid as boolean subject to the type condition, then 
      * execute sc on true/false
      * else execute the failure continuation $fc
      *
      * @param v1
      * @param sc
      * @param fc
      * @return
      */
    def checkIf[A](v1: Value)( sc: Boolean => A )( fc: () => A ): A


}






