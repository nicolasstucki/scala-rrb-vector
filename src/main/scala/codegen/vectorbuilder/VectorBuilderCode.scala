package codegen.vectorbuilder

import scala.reflect.runtime.universe._

trait VectorBuilderCode {

    val b_blockIndex = TermName("blockIndex")
    val b_lo = TermName("lo")

    val b_plusEq = TermName("+=")
    val b_plusPlusEq = TermName("++=")
    val b_result = TermName("result")
    val b_clear = TermName("clear")


 }
