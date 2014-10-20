package codegen.vectorreverseiterator

import codegen.vectorpointer.VectorPointerCodeGen
import codegen.VectorProperties

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorReverseIteratorCodeGen {
    self: VectorProperties with VectorPointerCodeGen =>


    // Field names

    val rit_startIndex = TermName("startIndex")
    val rit_endIndex = TermName("endIndex")

    val rit_lastIndexOfBlock = TermName("lastIndexOfBlock")
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
            $rit_lastIndexOfBlock = idx
            $rit_lo = (idx - $focusStart) & $blockMask
            $rit_endLo = math.max($rit_startIndex - $focusStart - $rit_lastIndexOfBlock, 0)
         """
    }

    protected def rit_nextCode() = {
        q"""
            if ($rit_hasNextVar) {
                val res = $display0($rit_lo).asInstanceOf[A]
                $rit_lo -= 1

                if ($rit_lo < $rit_endLo) {
                    val newBlockIndex = $rit_lastIndexOfBlock - $blockWidth
                    if ($focusStart <= newBlockIndex) {
                        val _focusStart = $focusStart
                        val newBlockIndexInFocus = newBlockIndex - _focusStart
                        $gotoPrevBlockStart(newBlockIndexInFocus, newBlockIndexInFocus ^ ($rit_lastIndexOfBlock - _focusStart))
                        $rit_lastIndexOfBlock = newBlockIndex
                        $rit_lo = $blockMask
                        $rit_endLo = math.max($rit_startIndex - $focusStart - $focus, 0)
                    } else if (startIndex < $focusStart) {
                        val newIndex = $focusStart - 1
                        $gotoPosRelaxed(newIndex, 0, $rit_endIndex, $depth)
                        $rit_lastIndexOfBlock = newIndex
                        $rit_lo = (newIndex - $focusStart) & $blockMask
                        $rit_endLo = math.max($rit_startIndex - $focusStart - $rit_lastIndexOfBlock, 0)
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
