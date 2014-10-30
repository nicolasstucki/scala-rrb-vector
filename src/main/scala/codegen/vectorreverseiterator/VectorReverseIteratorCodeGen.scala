package codegen.vectorreverseiterator

import codegen.vectorpointer.VectorPointerCodeGen
import codegen.VectorProperties

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorReverseIteratorCodeGen {
    self: VectorProperties with VectorPointerCodeGen =>


    // Field names

    val rit_startIndex = TermName("startIndex")

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
            ..${assertions(q"0 <= $rit_startIndex")}
            if ($rit_hasNextVar) {
                val idx = $endIndex - 1
                $focusOn(idx)
                lastIndexOfBlock = idx
                $rit_lo = (idx - focusStart) & $blockMask
                $rit_endLo = math.max($rit_startIndex - $focusStart - lastIndexOfBlock, 0)
            } else {
                // init with fake first element that will be ignored
                lastIndexOfBlock = 0
                lo = 0
                endLo = 0
                display0 = new Array[AnyRef](1)
            }
         """
    }

    protected def rit_nextCode() = {
        q"""
            if ($rit_hasNextVar) {
                val res = ${displayAt(0)}($rit_lo).asInstanceOf[$A]
                $rit_lo -= 1
                if ($rit_lo >= $rit_endLo) {
                    res
                } else {
                    val newBlockIndex = $rit_lastIndexOfBlock - $blockWidth
                    if ($focusStart <= newBlockIndex) {
                        val _focusStart = $focusStart
                        val newBlockIndexInFocus = newBlockIndex - _focusStart
                        $gotoPrevBlockStart(newBlockIndexInFocus, newBlockIndexInFocus ^ ($rit_lastIndexOfBlock - _focusStart))
                        $rit_lastIndexOfBlock = newBlockIndex
                        $rit_lo = $blockMask
                        $rit_endLo = math.max($rit_startIndex - $focusStart - $focus, 0)
                        res
                    } else if (startIndex < $focusStart) {
                        val newIndex = $focusStart - 1
                        $focusOn(newIndex)
                        $rit_lastIndexOfBlock = newIndex
                        $rit_lo = (newIndex - $focusStart) & $blockMask
                        $rit_endLo = math.max($rit_startIndex - $focusStart - $rit_lastIndexOfBlock, 0)
                        res
                    } else {
                        $rit_hasNextVar = false
                        res
                    }
                }
            } else {
                throw new NoSuchElementException("reached iterator end")
            }
        """
    }

}
