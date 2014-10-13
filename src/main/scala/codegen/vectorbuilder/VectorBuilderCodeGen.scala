package codegen.vectorbuilder

import codegen.vectorpointer.VectorPointerCodeGen
import codegen.VectorProperties

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorBuilderCodeGen {
    self: VectorPointerCodeGen with VectorProperties =>

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
            $display0($b_lo) = $elem.asInstanceOf[AnyRef]
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
        // ${if (useAssertions) q"vec.assertVectorInvariant()" else q""}
        q"""
            val $size = $b_blockIndex + $b_lo
            if ($size == 0)
                return $vectorObjectName.empty
            val $resultVector = new $vectorClassName[A]($size)

            $resultVector.$initFrom(this)
            $resultVector.$display0 = $copyOf($resultVector.$display0, $b_lo, ${if (CLOSED_BLOCKS) q"$b_lo" else q"$blockWidth"})

            // TODO: Optimization: check if stabilization is really necessary on all displays based on the last index.
            val $depthResult = $depth
            if ($depthResult > 1) {
                $resultVector.$copyDisplays($depthResult, $size - 1)
                $resultVector.$stabilize($depthResult, $size - 1)
            }

            $resultVector.$gotoPos(0, $size - 1)
            $resultVector.$focus = 0
            $resultVector.$focusEnd = $size
            $resultVector.$focusDepth = $depthResult

            $resultVector
        """
    }

    protected def clearCode() = {
        def nullDisplays = (1 to 5) map (i => q"${displayNameAt(i)} = null")
        q"""
            $display0 = new Array[AnyRef]($blockWidth)
            ..$nullDisplays
            $depth = 1
            $b_blockIndex = 0
            $b_lo = 0
        """
    }

}
