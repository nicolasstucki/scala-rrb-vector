package codegen.vectoriterator

import codegen.vectorpointer.VectorPointerCodeGen
import codegen.VectorProperties

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
        val oldBlockIndex = TermName("oldBlockIndex")
        val newBlockIndex = TermName("newBlockIndex")
        val localLo = TermName("_lo")
        val localEndLo = TermName("_endLo")
        val res = TermName("res")
        q"""
            val $localLo = lo
            val $res = display0($localLo).asInstanceOf[A]
            lo = $localLo + 1
            val $localEndLo = $it_endLo
            if ( $localLo + 1 != $localEndLo ) {
                $res
            } else {
                val $oldBlockIndex = $it_blockIndex
                val $newBlockIndex = $oldBlockIndex + $localEndLo
                $it_blockIndex = $newBlockIndex;
                lo = 0;
                if ( $newBlockIndex < $focusEnd ) {
                    $gotoNextBlockStart($newBlockIndex, $newBlockIndex ^ $oldBlockIndex)
                } else if ( $newBlockIndex < $it_endIndex ) {
                    $gotoPosRelaxed($newBlockIndex, 0, $it_endIndex, $depth)
                } else {
                    $it_lo = $focusEnd - $newBlockIndex - 1
                    $it_blockIndex = $it_endIndex
                    if ( $it_hasNextVar ) {
                        $it_hasNextVar = false
                    } else {
                        throw new NoSuchElementException("reached iterator end")
                    }
                }
                $it_endLo = math.min($focusEnd.-($newBlockIndex), $blockWidth)
                $res
            }
         """
    }

}
