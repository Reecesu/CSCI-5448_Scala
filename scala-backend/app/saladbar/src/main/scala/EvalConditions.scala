package saladbar


/**
  * EvalConditions
  *
  * @param sc: ScopingCondition of interest
  * @param tc: TypeCondition of interest
  * @param lec: LazyEagerCondition of interest
  */
class EvalConditions(private val sc: ScopingCondition, 
                     private val tc: TypeCondition, 
                     private val lec: LazyEagerCondition) {

  def getSc: ScopingCondition = this.sc
  def getTc: TypeCondition = this.tc
  def getLec: LazyEagerCondition = this.lec


  override def toString: String = {
    s"scoping_conditions: $sc, type_conditions: $tc, lazy_eager_condition: $lec"
  }


}

