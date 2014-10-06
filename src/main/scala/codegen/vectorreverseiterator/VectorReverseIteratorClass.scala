package codegen.vectorreverseiterator

import codegen.VectorPackage
import codegen.vectorpointer.VectorPointerCode

import scala.reflect.runtime.universe._

private[codegen] trait VectorReverseIteratorClass {
    self: VectorPackage with VectorPointerCode =>

    def generateVectorReverseIteratorClass() =
        q"""
            class $vectorReverseIteratorName[+$A]($rit_startIndex: Int, $rit_endIndex: Int)
                extends AbstractIterator[$A]
                with Iterator[$A]
                with $vectorPointerClassName[$A @uncheckedVariance] {

                private var $rit_blockIndexInFocus: Int = _
                private var $rit_lo: Int = _
                private var $rit_endLo: Int = _

                private var _hasNext: Boolean = $rit_startIndex < $rit_endIndex

                def hasNext = _hasNext

                private[immutable] final def initIterator(): Unit = {
                    val idx = endIndex - 1
                    if (focusStart <= idx && idx < focusEnd)
                        gotoPos(idx, idx ^ focus)
                    else
                        gotoPosRelaxed(idx, 0, endIndex, depth)
                    val indexInFocus = idx - focusStart
                    $rit_blockIndexInFocus = indexInFocus & ${~31}
                    lo = indexInFocus & 31
                    endLo = math.max(startIndex - focusStart - blockIndexInFocus, 0)
                }

                def next(): A = {
                    if (_hasNext) {

                        val res = $display0(lo).asInstanceOf[A]
                        lo -= 1

                        if (lo < endLo) {
                            val newBlockIndex = blockIndexInFocus - $treeBranchWidth
                            if (focusStart <= newBlockIndex) {
                                gotoPrevBlockStart(newBlockIndex, newBlockIndex ^ $rit_blockIndexInFocus)
                                $rit_blockIndexInFocus = newBlockIndex
                                lo = 31
                                endLo = math.max(startIndex - focusStart - focus, 0)
                            } else if (startIndex <= $rit_blockIndexInFocus - 1) {
                                val newIndexInFocus = $rit_blockIndexInFocus - 1
                                gotoPosRelaxed(newIndexInFocus, 0, endIndex, depth)
                                $rit_blockIndexInFocus = newIndexInFocus & ${~31}
                                $rit_lo = newIndexInFocus & 31
                                endLo = math.max($rit_startIndex - $focusStart - $rit_blockIndexInFocus, 0)
                            } else {
                                _hasNext = false
                            }
                        }

                        res
                    } else {
                        throw new NoSuchElementException("reached iterator end")
                    }
                }
            }
        """

    val rit_startIndex = TermName("startIndex")
    val rit_endIndex = TermName("endIndex")

    val rit_blockIndexInFocus = TermName("blockIndexInFocus")
    val rit_lo = TermName("lo")
    val rit_endLo = TermName("endLo")

}