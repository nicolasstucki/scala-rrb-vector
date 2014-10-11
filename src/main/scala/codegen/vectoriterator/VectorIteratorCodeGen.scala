package codegen.vectoriterator

import codegen.vectorpointer.VectorPointerCodeGen
import codegen.{VectorProperties, VectorPackage}

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorIteratorCodeGen {
    self: VectorProperties with VectorPointerCodeGen =>


    // Field names

    val it_iteratorStartIndex = TermName("startIndex")
    val it_endIndex = TermName("endIndex")

    val it_blockIndex = TermName("blockIndex")
    val it_lo = TermName("lo")
    val it_endLo = TermName("endLo")
    val it_hasNextVar = TermName("_hasNext")


    // Method names

    val it_hasNext = TermName("hasNext")
    val it_next = TermName("next")
    val it_resetIterator = TermName("resetIterator")


    // Method definitions

    protected def resetIteratorCode() = {
        q"""
            if ($focusStart <= $it_iteratorStartIndex && $it_iteratorStartIndex < $focusEnd)
                $gotoPos($it_iteratorStartIndex, $it_iteratorStartIndex ^ $focus)
            else
                $gotoPosRelaxed($it_iteratorStartIndex, 0, $it_endIndex, $depth)
            $it_blockIndex = $focusStart
            $it_lo = $it_iteratorStartIndex - $focusStart
            $it_endLo = math.min($focusEnd - $it_blockIndex, $blockWidth)
         """
    }

    protected def nextCode() = {
        q"""
            if ($it_hasNextVar) {
                val res = $display0($it_lo).asInstanceOf[A]
                $it_lo += 1

                if ($it_lo == $it_endLo) {
                    val newBlockIndex = $it_blockIndex + $it_endLo
                    if (newBlockIndex < $focusEnd) {
                        $gotoNextBlockStart(newBlockIndex, newBlockIndex ^ $it_blockIndex)
                    } else if (newBlockIndex < $it_endIndex) {
                        $gotoPosRelaxed(newBlockIndex, 0, $it_endIndex, $depth)
                    } else {
                        $it_hasNextVar = false
                    }
                    $it_blockIndex = newBlockIndex
                    $it_lo = 0
                    $it_endLo = math.min($focusEnd - $it_blockIndex, $blockWidth)
                }

                res
            } else {
                throw new NoSuchElementException("reached iterator end")
            }
        """
    }

}
