package codegen
package vector
package iterator

import vectorpointer.VectorPointerCodeGen

import scala.reflect.runtime.universe._

trait VectorIteratorCodeGen {
    self: VectorProperties with VectorPointerCodeGen =>


    // Field names

    val it_iteratorStartIndex = TermName("startIndex")

    val it_blockIndex = TermName("blockIndex")
    val it_lo = TermName("lo")
    val it_endLo = TermName("endLo")
    val it_hasNextVar = TermName("_hasNext")


    // Method names

    val it_initIteratorFrom = TermName("initIteratorFrom")
    val it_hasNext = TermName("hasNext")
    val it_next = TermName("next")
    val it_remaining = TermName("remaining")


    // Method definitions

    protected def initIteratorFromCode(that: TermName) = {
        q"""
            if ($it_hasNextVar) {
                $focusOn($it_iteratorStartIndex)
                $it_blockIndex = $focusStart + ($focus & ${~blockMask})
                $it_lo = $focus & $blockMask
                $it_endLo = math.min($focusEnd - $it_blockIndex, $blockWidth)
            } else {
                // init with fake first element that will be ignored
                $it_blockIndex = 0
                $it_lo = 0
                $it_endLo = 1
                ${displayAt(0)} = new Array[AnyRef](1)
            }

            $initWithFocusFrom($that)
            $it_hasNextVar = $it_iteratorStartIndex < $endIndex
            if ($it_hasNextVar) {
                $focusOn($it_iteratorStartIndex)
                $it_blockIndex = $focusStart + ($focus & ${~blockMask})
                $it_lo = $focus & $blockMask
                $it_endLo = math.min($focusEnd - $it_blockIndex, $blockWidth)
            }
            else {
                $it_blockIndex = 0
                $it_lo = 0
                $it_endLo = 1
                ${displayAt(0)} = new Array[AnyRef](1)
            }
         """
    }

    protected def nextCode() = {
        val oldBlockIndex = TermName("oldBlockIndex")
        val newBlockIndex = TermName("newBlockIndex")
        val newBlockIndexInFocus = TermName("newBlockIndexInFocus")
        val localLo = TermName("_lo")
        val localEndLo = TermName("_endLo")
        val localFocusStart = TermName("_focusStart")
        val res = TermName("res")
        q"""
            val $localLo = lo
            val $res: $A = ${displayAt(0)}($localLo).asInstanceOf[$A]
            $it_lo = $localLo + 1
            val $localEndLo = $it_endLo
            if ( $localLo + 1 != $localEndLo ) {
                $res
            } else {
                val $oldBlockIndex = $it_blockIndex
                val $newBlockIndex = $oldBlockIndex + $localEndLo
                $it_blockIndex = $newBlockIndex
                $it_lo = 0
                if ( $newBlockIndex < $focusEnd ) {
                    val $localFocusStart = $focusStart
                    val $newBlockIndexInFocus = $newBlockIndex - $localFocusStart
                    $gotoNextBlockStart($newBlockIndexInFocus, $newBlockIndexInFocus ^ ($oldBlockIndex - $localFocusStart))
                } else if ( $newBlockIndex < $endIndex ) {
                    $focusOn($newBlockIndex)
                } else {
                    // reset te lo and blockIndex where no exceptions will be thrown
                    $it_lo = ($focusEnd - 1) & $blockMask
                    $it_blockIndex = $endIndex
                    if ( $it_hasNextVar ) {
                        $it_hasNextVar = false
                    } else {
                        throw new NoSuchElementException("reached iterator end")
                    }
                }
                $it_endLo = math.min($focusEnd - $newBlockIndex, $blockWidth)
                $res
            }
         """
    }

    protected def remainingCode() = {
        q"math.max($endIndex - ($it_blockIndex + $it_lo), 0)"
    }
}
