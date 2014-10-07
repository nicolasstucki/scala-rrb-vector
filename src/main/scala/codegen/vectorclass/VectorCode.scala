package codegen.vectorclass

import scala.reflect.runtime.universe._

private[codegen] trait VectorCode {

    val endIndex = TermName("endIndex")

    val take = TermName("take")
    val dropRight = TermName("dropRight")

    private[vectorclass] val appendedBack = TermName("appendedBack")
    private[vectorclass] val takeFront0 = TermName("takeFront0")

    private[vectorclass] val concatenated = TermName("concatenated")
    private[vectorclass] val rebalanced = TermName("rebalanced")


    val assertVectorInvariant = TermName("assertVectorInvariant")
}
