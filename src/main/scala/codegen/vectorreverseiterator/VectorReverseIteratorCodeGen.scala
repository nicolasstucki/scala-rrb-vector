package codegen.vectorreverseiterator

import codegen.vectorpointer.VectorPointerCodeGen
import codegen.{VectorProperties, VectorPackage}

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorReverseIteratorCodeGen {
    self: VectorProperties with VectorPointerCodeGen =>


    // Field names

    val rit_startIndex = TermName("startIndex")
    val rit_endIndex = TermName("endIndex")

    val rit_blockIndexInFocus = TermName("blockIndex")
    val rit_lo = TermName("lo")
    val rit_endLo = TermName("endLo")
    val rit_hasNextVar = TermName("_hasNext")


    // Method names

    val rit_hasNext = TermName("hasNext")
    val rit_next = TermName("next")
    val rit_resetIterator = TermName("resetIterator")


    // Method definitions

    protected def rit_resetIteratorCode() = {
        q"""
            val idx = $rit_endIndex - 1
            if ($focusStart <= idx && idx < $focusEnd) {
                $gotoPos(idx, idx ^ $focus)
            } else {
                $gotoPosRelaxed(idx, 0, $rit_endIndex, $depth)
            }
            val indexInFocus = idx - $focusStart
            $rit_blockIndexInFocus = indexInFocus & ${~blockMask}
            $rit_lo = indexInFocus & ${blockMask}
            $rit_endLo = math.max($rit_startIndex - $focusStart - $rit_blockIndexInFocus, 0)
         """
    }

    protected def rit_nextCode() = {
        q"""
            if ($rit_hasNextVar) {
                val res = $display0($rit_lo).asInstanceOf[A]
                $rit_lo -= 1

                if ($rit_lo < $rit_endLo) {
                    val newBlockIndex = $rit_blockIndexInFocus - $blockWidth
                    if ($focusStart <= newBlockIndex) {
                        $gotoPrevBlockStart(newBlockIndex, newBlockIndex ^ $rit_blockIndexInFocus)
                        $rit_blockIndexInFocus = newBlockIndex
                        $rit_lo = $blockMask
                        $rit_endLo = math.max($rit_startIndex - $focusStart - $focus, 0)
                    } else if (startIndex <= $rit_blockIndexInFocus - 1) {
                        val newIndexInFocus = $rit_blockIndexInFocus - 1
                        $gotoPosRelaxed(newIndexInFocus, 0, $rit_endIndex, $depth)
                        $rit_blockIndexInFocus = newIndexInFocus & ${~blockMask}
                        $rit_lo = newIndexInFocus & $blockMask
                        $rit_endLo = math.max($rit_startIndex - $focusStart - $rit_blockIndexInFocus, 0)
                    } else {
                        $rit_hasNextVar = false
                    }
                }

                res
            } else {
                throw new NoSuchElementException("reached iterator end")
            }
        """
    }

}
