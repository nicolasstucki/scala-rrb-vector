package scala.collection.immutable.rrbvectorx

import scala.annotation.unchecked.uncheckedVariance
import scala.collection.{Iterator, AbstractIterator}

private[immutable] class VectorIterator[+A]
  extends AbstractIterator[A]
  with Iterator[A]
  with RelaxedVectorPointer[A@uncheckedVariance] {

    private var blockStart: Int = 0
    private var lo: Int = 0
    private var endLo: Int = 0

    def hasNext = _hasNext

    private var _hasNext = false

    def initIterator(root: AnyRef, height: Int, length: Int): Unit = {
        _hasNext = length > 0
        if (_hasNext) {
            lo = 0
            blockStart = 0
            if (height == 1) {
                display0 = root.asInstanceOf[Array[AnyRef]]
                endLo = length
            } else {
                initDisplayWithoutFocus(root, height, length)
                focusOnPosition(0)
                endLo = display0.length
            }
        }
    }

    def next(): A = {
        if (!_hasNext) throw new NoSuchElementException("reached iterator end")

        val res = display0(lo).asInstanceOf[A]
        lo += 1

        if (lo == endLo) {
            val newBlockStart = blockStart + lo
            if (newBlockStart < _length) {
                focusOnPosition(newBlockStart)
                blockStart = newBlockStart
                endLo = display0.length
                lo = 0
            } else {
                _hasNext = false
            }
        }

        res
    }

}