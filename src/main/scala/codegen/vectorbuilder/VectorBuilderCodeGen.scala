package codegen.vectorbuilder

import codegen.vectorclass.VectorCodeGen
import codegen.vectorpointer.VectorPointerCodeGen
import codegen.VectorProperties

import scala.reflect.runtime.universe._

trait VectorBuilderCodeGen {
    self: VectorPointerCodeGen with VectorCodeGen with VectorProperties =>

    // Field names

    protected final val b_blockIndex = TermName("blockIndex")
    protected final val b_lo = TermName("lo")


    // Method names

    protected final val b_plusEq = TermName("+=")
    protected final val b_plusPlusEq = TermName("++=")
    protected final val b_result = TermName("result")
    protected final val b_clear = TermName("clear")


    // Method definitions

    protected def plusEqCode(elem: Tree) = {
        val newBlockIndex = TermName("newBlockIndex")
        q"""
            if ($b_lo >= $blockWidth) {
                val $newBlockIndex = $b_blockIndex + $blockWidth
                $gotoNextBlockStartWritable($newBlockIndex, $newBlockIndex ^ $b_blockIndex)
                $b_blockIndex = $newBlockIndex
                $b_lo = 0
            }
            ${displayAt(0)}($b_lo) = $elem.asInstanceOf[AnyRef]
            $b_lo += 1
            this
        """
    }

    protected def plusPlusEqCode(xs: Tree) = {
        q"super.++=($xs)"
    }

    protected def resultCode() = {
        val size = TermName("size")
        val resultVector = TermName("resultVector")
        val depthResult = TermName("_depth")
        q"""
            val $size = $endIndex
            if ($size == 0) {
                $vectorObjectName.empty
            } else {
                val $resultVector = new $vectorClassName[A]($size)

                $resultVector.$initFrom(this)
                $resultVector.${displayNameAt(0)} = $copyOf($resultVector.${displayNameAt(0)}, $b_lo, $b_lo)

                // TODO: Optimization: check if stabilization is really necessary on all displays based on the last index.
                val $depthResult = $depth
                if ($depthResult > 1) {
                    $resultVector.$copyDisplays($depthResult, $size - 1)
                    $resultVector.$stabilizeDisplayPath($depthResult, $size - 1)
                }

                $resultVector.$gotoPos(0, $size - 1)
                $resultVector.$initFocus(0, 0, $size, $depthResult, 0)

                ..${assertions(q"$resultVector.$v_assertVectorInvariant()")}

                $resultVector
            }
        """
    }

    protected def clearCode() = {
        def nullDisplays = (1 to 5) map (i => q"${displayNameAt(i)} = null")
        q"""
            ${displayAt(0)} = new Array[AnyRef]($blockWidth)
            ..$nullDisplays
            $depth = 1
            $b_blockIndex = 0
            $b_lo = 0
        """
    }

}
