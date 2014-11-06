package codegen
package vector
package reverseiterator

import vector.vectorpointer.VectorPointerCodeGen

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
    val rit_initIteratorFrom = TermName("initIteratorFrom")


    // Method definitions

    protected def rit_initIteratorFromCode(that: TermName) = {
        q"""
            ..${assertions(q"0 <= $rit_startIndex", q"$rit_startIndex <= $endIndex", q"$endIndex <= $that.$endIndex")}
            $initWithFocusFrom($that)
            $rit_hasNextVar = $rit_startIndex < $endIndex
            if ($rit_hasNextVar) {
                val idx = $endIndex - 1
                $focusOn(idx)
                $rit_lastIndexOfBlock = idx
                $rit_lo = (idx - focusStart) & $blockMask
                $rit_endLo = math.max($rit_startIndex -  $focusStart - $rit_lastIndexOfBlock, 0)
            }
            else {
                $rit_lastIndexOfBlock = 0
                $rit_lo = 0
                $rit_endLo = 0
                ${displayAt(0)} = new Array[AnyRef](1)
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
