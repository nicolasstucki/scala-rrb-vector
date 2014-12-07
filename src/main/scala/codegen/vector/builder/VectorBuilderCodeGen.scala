package codegen
package vector
package builder

import vectorclass.VectorCodeGen
import vectorpointer.VectorPointerCodeGen

import scala.reflect.runtime.universe._

trait VectorBuilderCodeGen {
    self: VectorPointerCodeGen with VectorCodeGen with VectorProperties =>

    private def b_appendOrConcatThreshold = 1024

    // Field names

    protected final val b_blockIndex = TermName("blockIndex")


    // Method names

    protected final val b_lo = TermName("lo")
    protected final val b_plusEq = TermName("+=")
    protected final val b_plusPlusEq = TermName("++=")
    protected final val b_result = TermName("result")
    protected final val b_clear = TermName("clear")
    protected final val b_acc = TermName("acc")
    protected final val b_currentResult = TermName("currentResult")


    // Method definitions

    protected final val b_clearCurrent = TermName("clearCurrent")

    protected def b_endIndexCode() = {
        q"""
            var sz = $b_blockIndex + $b_lo
            if ($b_acc != null)
                sz += $b_acc.$endIndex
            sz
         """
    }

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

    protected def plusPlusEqCode(xs: TermName) = {
        q"""
             if ($xs.nonEmpty) {
                if ($xs.isInstanceOf[$vectorClassName[$A]]) {
                    val thatVec = $xs.asInstanceOf[$vectorClassName[$A]]
                    if (thatVec.length > ${b_appendOrConcatThreshold}) {
                        if ($endIndex != 0) {
                            $b_acc = this.result() ++ $xs
                            this.$b_clearCurrent()
                        } else if ($b_acc != null) {
                            $b_acc = $b_acc ++ thatVec
                        } else {
                            $b_acc = thatVec
                        }
                    } else {
                        super.++=($xs)
                    }
                } else {
                    super.++=($xs)
                }
            }
            this
         """
    }

    protected def resultCode() = {
        val current = TermName("current")
        val resultVector = TermName("resultVector")
        q"""
            val $current = $b_currentResult()
            val $resultVector =
                if ($b_acc == null) $current
                else $b_acc ++ $current
            ..${assertions(q"$resultVector.$v_assertVectorInvariant()")}
            $resultVector
         """
    }

    protected def clearCode() = {
        q"""
            $b_clearCurrent()
            $b_acc = null
        """
    }

    protected def currentResultCode() = {
        val size = TermName("size")
        val resultVector = TermName("resultVector")
        val depthResult = TermName("_depth")
        q"""
            val $size = $b_blockIndex + $b_lo
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
    protected def clearCurrentCode() = {
        def nullDisplays = (1 to maxTreeLevel) map (i => q"${displayNameAt(i)} = null")
        q"""
            ${displayAt(0)} = new Array[AnyRef]($blockWidth)
            ..$nullDisplays
            $depth = 1
            $b_blockIndex = 0
            $b_lo = 0
         """
    }

}
