package codegen.vectoriterator

import codegen.VectorPackage
import codegen.vectorpointer.VectorPointerCode

import scala.reflect.runtime.universe._

private[codegen] trait VectorIteratorClass {
    self: VectorPointerCode with VectorPackage =>

    def generateVectorIteratorClass() =
        q"""
            class $vectorIteratorName[+$A]($it_iteratorStartIndex: Int, $it_endIndex: Int)
                extends AbstractIterator[$A]
                with Iterator[$A]
                with $vectorPointerClassName[$A @uncheckedVariance] {

                private var $it_blockIndex: Int = _
                private var $it_lo: Int = _
                private var $it_endLo: Int = _

                private var $it_hasNextVar: Boolean = $it_iteratorStartIndex < $it_endIndex

                def hasNext = $it_hasNextVar

                private[immutable] final def resetIterator(): Unit = {
                    if ($focusStart <= $it_iteratorStartIndex && $it_iteratorStartIndex < $focusEnd)
                        $gotoPos($it_iteratorStartIndex, $it_iteratorStartIndex ^ $focus)
                    else
                        $gotoPosRelaxed($it_iteratorStartIndex, 0, $it_endIndex, $depth)
                    $it_blockIndex = $focusStart
                    $it_lo = $it_iteratorStartIndex - $focusStart
                    $it_endLo = math.min($focusEnd - $it_blockIndex, $treeBranchWidth)
                }

                def next(): A = {
                    if ($it_hasNextVar) {
                        val res = $display0($it_lo).asInstanceOf[A]
                        $it_lo += 1

                        if ($it_lo == endLo) {
                            val newBlockIndex = blockIndex + $it_endLo
                            if (newBlockIndex < focusEnd) {
                                $gotoNextBlockStart(newBlockIndex, $it_blockIndex ^ newBlockIndex)
                            } else if (newBlockIndex < endIndex) {
                                $gotoPosRelaxed(newBlockIndex, 0, $it_endIndex, $depth)
                            } else {
                                $it_hasNextVar = false
                            }
                            $it_blockIndex = newBlockIndex
                            $it_lo = 0
                            $it_endLo = math.min(focusEnd - blockIndex, $treeBranchWidth)
                        }

                        res
                        } else {
                         throw new NoSuchElementException("reached iterator end")
                        }
                    }
            }
        """

    val it_iteratorStartIndex = TermName("startIndex")
    val it_endIndex = TermName("endIndex")

    val it_blockIndex = TermName("blockIndex")
    val it_lo = TermName("lo")
    val it_endLo = TermName("endLo")
    val it_hasNextVar = TermName("_hasNext")

}