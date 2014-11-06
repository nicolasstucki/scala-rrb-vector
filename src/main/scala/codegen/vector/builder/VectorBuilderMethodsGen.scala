package codegen
package vector
package builder

import vectorpointer.VectorPointerCodeGen

import scala.reflect.runtime.universe._

trait VectorBuilderMethodsGen {
    self: VectorBuilderCodeGen with VectorPointerCodeGen with VectorProperties =>

    def generateVectorBuilderMethods() = {
        val statements = Seq(
            q"${displayAt(0)} = new Array[AnyRef]($blockWidth)",
            q"$depth = 1")
        val fields = Seq(
            q"private var $b_blockIndex = 0",
            q"private var $b_lo = 0",
            q"private var $b_acc: $vectorClassName[$A] = null")

        val methods = Seq(
            plusEqDef, plusPlusEqDef, resultDef, clearDef,
            b_endIndexDef, currentResultDef, clearCurrentDef)

        statements ++ fields ++ methods
    }

    protected def b_endIndexDef = {
        val code = b_endIndexCode()
        q"private[collection] def $endIndex = $code"
    }

    protected def plusEqDef = {
        val elem = TermName("elem")
        val code = plusEqCode(q"$elem")
        q"def $b_plusEq($elem: $A): this.type = $code"
    }

    protected def plusPlusEqDef = {
        val xs = TermName("xs")
        val code = plusPlusEqCode(xs)
        q"override def $b_plusPlusEq($xs: TraversableOnce[$A]): this.type = $code"
    }

    protected def resultDef = {
        val code = resultCode()
        q"def $b_result(): $vectorClassName[$A] = $code"
    }

    protected def clearDef = q"def $b_clear(): Unit = ${clearCode()}"

    protected def currentResultDef = {
        val code = currentResultCode()
        q"private def $b_currentResult(): $vectorClassName[$A] = $code"
    }
    protected def clearCurrentDef = q"private def $b_clearCurrent(): Unit = ${clearCurrentCode()}"

}

