package codegen
package vectorbuilder

import codegen.vectorpointer.VectorPointerCodeGen
import codegen.vectorclass.VectorCodeGen

import scala.annotation.tailrec
import scala.reflect.runtime.universe._


trait VectorBuilderMethodsGen {
    self: VectorBuilderCodeGen with VectorPointerCodeGen with VectorCodeGen with VectorProperties =>

    def generateVectorBuilderMethods() = {
        val statements = Seq(
            q"$display0 = new Array[AnyRef]($blockWidth)",
            q"$depth = 1")
        val fields = Seq(
            q"private var $b_blockIndex = 0",
            q"private var $b_lo = 0")
        val methods = Seq(plusEqDef, plusPlusEqDef, resultDef, clearDef)

        statements ++ fields ++ methods
    }

    protected def plusEqDef = {
        val elem = TermName("elem")
        val code = plusEqCode(q"$elem")
        q"def $b_plusEq($elem: $A): this.type = $code"
    }

    protected def plusPlusEqDef = {
        val xs = TermName("xs")
        val code = plusPlusEqCode(q"$xs")
        q"override def $b_plusPlusEq($xs: TraversableOnce[$A]): this.type = $code"
    }

    protected def resultDef = {
        val code = resultCode()
        def assertedCode = q"val res = $code; res.$v_assertVectorInvariant(); res"
        q"def $b_result(): $vectorClassName[$A] = ${if (useAssertions) assertedCode else code}"
    }

    protected def clearDef = q"def $b_clear(): Unit = ${clearCode()}"

}
