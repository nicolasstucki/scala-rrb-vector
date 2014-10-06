package codegen.vectorclass

import scala.reflect.runtime.universe._

private[codegen] trait VectorCode {

    val endIndex = TermName("endIndex")

    val appendedBack = TermName("appendedBack")
    val concatenated = TermName("concatenated")
    val rebalanced = TermName("rebalanced")


    val assertVectorInvariant = TermName("assertVectorInvariant")
}
