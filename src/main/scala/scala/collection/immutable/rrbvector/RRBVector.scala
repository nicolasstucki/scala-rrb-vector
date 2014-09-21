package scala
package collection
package immutable
package rrbvector

import java.io.Serializable

import scala.annotation.tailrec
import scala.annotation.unchecked.uncheckedVariance

import scala.collection.generic.{GenericCompanion, GenericTraversableTemplate, CanBuildFrom, IndexedSeqFactory}
import scala.collection.mutable.Builder
import scala.compat.Platform

/**
 * Created by nicolasstucki on 19/09/2014.
 */

object RRBVector extends IndexedSeqFactory[RRBVector] {
    def newBuilder[A]: Builder[A, RRBVector[A]] = new RRBVectorBuilder[A]

    implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, RRBVector[A]] =
        ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

    private[immutable] val NIL = new RRBVector[Nothing](0)

    override def empty[A]: RRBVector[A] = NIL

}

final class RRBVector[+A] private[immutable](val endIndex: Int)
  extends AbstractSeq[A]
  with IndexedSeq[A]
  with GenericTraversableTemplate[A, RRBVector]
  with IndexedSeqLike[A, RRBVector[A]]
  with RRBVectorPointer[A@uncheckedVariance]
  with Serializable {
    //  with CustomParallelizable[A, ParVector[A]] {
    self =>

    override def companion: GenericCompanion[RRBVector] = RRBVector

    def length = endIndex

    override def lengthCompare(len: Int): Int = length - len


    // Iterators

    private[collection] final def initIterator[B >: A](s: RRBVectorIterator[B]) {
        s.initWithFocusFrom(this)
        if (depth > 0) s.resetIterator()
    }

    private[collection] final def initIterator[B >: A](s: RRBVectorReverseIterator[B]) {
        s.initWithFocusFrom(this)
        if (depth > 0) s.initIterator()
    }

    override def iterator: Iterator[A] = {
        val s = new RRBVectorIterator[A](0, endIndex)
        initIterator(s)
        s
    }

    override def reverseIterator: Iterator[A] = {
        val s = new RRBVectorReverseIterator[A](0, endIndex)
        initIterator(s)
        s
    }

    // SeqLike

    def /*SeqLike*/ apply(index: Int): A = {
        if (focusStart <= index && index < focusEnd) {
            val indexInFocus = index - focusStart
            getElem(indexInFocus, indexInFocus ^ focus)
        } else if (0 <= index && index < endIndex) {
            gotoPosRelaxed(index, 0, endIndex, depth)
            display0((index - focusStart) & 31).asInstanceOf[A]
        } else {
            throw new IndexOutOfBoundsException(index.toString)
        }

    }


    // IterableLike

    override /*IterableLike*/ def head: A = {
        if (isEmpty) throw new UnsupportedOperationException("empty.head")
        apply(0)
    }

    override /*IterableLike*/ def slice(from: Int, until: Int): RRBVector[A] =
        take(until).drop(from)

    override /*IterableLike*/ def splitAt(n: Int): (RRBVector[A], RRBVector[A]) = (take(n), drop(n))


    // TraversableLike

    override /*TraversableLike*/ def tail: RRBVector[A] = {
        if (isEmpty) throw new UnsupportedOperationException("empty.tail")
        drop(1)
    }

    override /*TraversableLike*/ def last: A = {
        if (isEmpty) throw new UnsupportedOperationException("empty.last")
        apply(length - 1)
    }

    override /*TraversableLike*/ def init: RRBVector[A] = {
        if (isEmpty) throw new UnsupportedOperationException("empty.init")
        dropRight(1)
    }


}

private[immutable] trait RRBVectorPointer[A] {

    private[immutable] var focus: Int = 0
    private[immutable] var focusStart: Int = 0
    private[immutable] var focusEnd: Int = 0

    private[immutable] var depth: Int = _

    private[immutable] var display0: Array[AnyRef] = _
    private[immutable] var display1: Array[AnyRef] = _
    private[immutable] var display2: Array[AnyRef] = _
    private[immutable] var display3: Array[AnyRef] = _
    private[immutable] var display4: Array[AnyRef] = _
    private[immutable] var display5: Array[AnyRef] = _


    // Relaxed radix based methods

    private[immutable] final def initWithFocusFrom[U](that: RRBVectorPointer[U]): Unit = {
        setFocus(that.focus, that.focusStart, that.focusEnd)
        initFrom(that)
    }

    private[immutable] final def setFocus(focus: Int, focusStart: Int, focusEnd: Int) = {
        this.focus = focus
        this.focusStart = focusStart
        this.focusEnd = focusEnd
    }

    @tailrec
    private[immutable] final def gotoPosRelaxed(index: Int, start: Int, end: Int, depth: Int): Unit = {
        val display = depth match {
            case 0 => null
            case 1 => display0
            case 2 => display1
            case 3 => display2
            case 4 => display3
            case 5 => display4
            case _ => throw new IllegalArgumentException("depth=" + depth)
        }

        if (depth > 1 && display(display.length - 1) != null) {
            val sizes = display(display.length - 1).asInstanceOf[Array[Int]]
            val is = getRelaxedIndex(index - start, sizes)
            depth match {
                case 2 => display0 = display(is).asInstanceOf[Array[AnyRef]]
                case 3 => display1 = display(is).asInstanceOf[Array[AnyRef]]
                case 4 => display2 = display(is).asInstanceOf[Array[AnyRef]]
                case 5 => display3 = display(is).asInstanceOf[Array[AnyRef]]
                case 6 => display4 = display(is).asInstanceOf[Array[AnyRef]]
                case _ => throw new IllegalArgumentException("depth=" + depth)
            }
            gotoPosRelaxed(index, if (is == 0) start else start + sizes(is - 1), start + sizes(is), depth - 1)
        } else {
            val indexInFocus = index - start
            setFocus(indexInFocus, start, end)
            gotoPos(indexInFocus, 1 << (5 * (depth - 1)))
        }
    }

    private final def getRelaxedIndex(indexInSubTree: Int, sizes: Array[Int]) = {
        var is = 0 //ix >> ((height - 1) * WIDTH_SHIFT)
        while (sizes(is) <= indexInSubTree)
            is += 1
        is
    }

    // Radix based methods

    private[immutable] final def initFrom[U](that: RRBVectorPointer[U]): Unit = initFrom(that, that.depth)

    private[immutable] final def initFrom[U](that: RRBVectorPointer[U], depth: Int) = {
        this.depth = depth
        depth match {
            case 0 =>
            case 1 =>
                display0 = that.display0
            case 2 =>
                display1 = that.display1
                display0 = that.display0
            case 3 =>
                display2 = that.display2
                display1 = that.display1
                display0 = that.display0
            case 4 =>
                display3 = that.display3
                display2 = that.display2
                display1 = that.display1
                display0 = that.display0
            case 5 =>
                display4 = that.display4
                display3 = that.display3
                display2 = that.display2
                display1 = that.display1
                display0 = that.display0
            case 6 =>
                display5 = that.display5
                display4 = that.display4
                display3 = that.display3
                display2 = that.display2
                display1 = that.display1
                display0 = that.display0
            case _ =>
                throw new IllegalStateException()
        }
    }

    private[immutable] final def getElem(index: Int, xor: Int): A = {
        if /* level = 0 */ (xor < (1 << 5)) {
            display0(index & 31).asInstanceOf[A]
        } else if /* level = 1 */ (xor < (1 << 10)) {
            display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else if /* level = 2 */ (xor < (1 << 15)) {
            display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else if /* level = 3 */ (xor < (1 << 20)) {
            display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else if /* level = 4 */ (xor < (1 << 25)) {
            display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]((index >> 15) & 31).asInstanceOf[Array[AnyRef]]((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else if /* level = 5 */ (xor < (1 << 30)) {
            display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]((index >> 20) & 31).asInstanceOf[Array[AnyRef]]((index >> 15) & 31).asInstanceOf[Array[AnyRef]]((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else /* level < 0 || 5 < level */ {
            throw new IllegalArgumentException()
        }
    }

    /* variant designed for performance base on index locality
    private[immutable] final def getElem(index: Int, xor: Int): A = {
        if (xor >= (1 << 5)) {
            // TODO test this with random access
            focus = index
            gotoPos(index, xor)
        }
        display0(index & 31).asInstanceOf[A]
    }
    */


    // go to specific position
    // requires structure is at pos oldIndex = xor ^ index,
    // ensures structure is at pos index
    private[immutable] final def gotoPos(index: Int, xor: Int): Unit = {
        if /* level = 0 */ (xor < (1 << 5)) {
            // could maybe removed
        } else if /* level = 1 */ (xor < (1 << 10)) {
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if /* level = 2 */ (xor < (1 << 15)) {
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if /* level = 3 */ (xor < (1 << 20)) {
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if /* level = 4 */ (xor < (1 << 25)) {
            display3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if /* level = 5 */ (xor < (1 << 30)) {
            display4 = display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
            display3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else /* level < 0 || 5 < level */ {
            throw new IllegalArgumentException()
        }
    }


    // xor: oldIndex ^ index
    private[immutable] final def gotoNextBlockStart(index: Int, xor: Int): Unit = {
        // goto block start pos
        if (xor < (1 << 10)) {
            // level = 1
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 15)) {
            // level = 2
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 20)) {
            // level = 3
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2(0).asInstanceOf[Array[AnyRef]]
            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 25)) {
            // level = 4
            display3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            display2 = display3(0).asInstanceOf[Array[AnyRef]]
            display1 = display2(0).asInstanceOf[Array[AnyRef]]
            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 30)) {
            // level = 5
            display4 = display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
            display3 = display4(0).asInstanceOf[Array[AnyRef]]
            display2 = display3(0).asInstanceOf[Array[AnyRef]]
            display1 = display2(0).asInstanceOf[Array[AnyRef]]
            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        } else {
            // level = 6
            throw new IllegalArgumentException()
        }
    }

    // xor: oldIndex ^ index
    private[immutable] final def gotoPrevBlockStart(index: Int, xor: Int): Unit = {
        // goto block start pos
        if (xor < (1 << 10)) {
            // level = 1
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 15)) {
            // level = 2
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 20)) {
            // level = 3
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2(31).asInstanceOf[Array[AnyRef]]
            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 25)) {
            // level = 4
            display3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            display2 = display3(31).asInstanceOf[Array[AnyRef]]
            display1 = display2(31).asInstanceOf[Array[AnyRef]]
            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 30)) {
            // level = 5
            display4 = display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
            display3 = display4(31).asInstanceOf[Array[AnyRef]]
            display2 = display3(31).asInstanceOf[Array[AnyRef]]
            display1 = display2(31).asInstanceOf[Array[AnyRef]]
            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        } else {
            // level = 6
            throw new IllegalArgumentException()
        }
    }

    // USED BY BUILDER

    // xor: oldIndex ^ index
    private[immutable] final def gotoNextBlockStartWritable(index: Int, xor: Int): Unit = {
        // goto block start pos
        if /* level = 1 */ (xor < (1 << 10)) {
            if (depth == 1) {
                display1 = new Array(32)
                display1(0) = display0
                depth += 1
            }
            display0 = new Array(32)
            display1((index >> 5) & 31) = display0
        } else if /* level = 2 */ (xor < (1 << 15)) {
            if (depth == 2) {
                display2 = new Array(32)
                display2(0) = display1
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(32)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
        } else if /* level = 3 */ (xor < (1 << 20)) {
            if (depth == 3) {
                display3 = new Array(32)
                display3(0) = display2
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(32)
            display2 = new Array(32)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
            display3((index >> 15) & 31) = display2
        } else if /* level = 4 */ (xor < (1 << 25)) {
            if (depth == 4) {
                display4 = new Array(32)
                display4(0) = display3
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(32)
            display2 = new Array(32)
            display3 = new Array(32)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
            display3((index >> 15) & 31) = display2
            display4((index >> 20) & 31) = display3
        } else if /* level = 5 */ (xor < (1 << 30)) {
            if (depth == 5) {
                display5 = new Array(32)
                display5(0) = display4
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(32)
            display2 = new Array(32)
            display3 = new Array(32)
            display4 = new Array(32)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
            display3((index >> 15) & 31) = display2
            display4((index >> 20) & 31) = display3
            display5((index >> 25) & 31) = display4
        } else /* level < 0 || 5 < level */ {
            throw new IllegalArgumentException()
        }
    }
}

class RRBVectorIterator[+A](startIndex: Int, endIndex: Int)
  extends AbstractIterator[A]
  with Iterator[A]
  with RRBVectorPointer[A@uncheckedVariance] {

    private var blockIndex: Int = _
    private var lo: Int = _
    private var endLo: Int = _

    private var _hasNext: Boolean = startIndex < endIndex

    def hasNext = _hasNext

    private[immutable] final def resetIterator(): Unit = {
        if (focusStart <= startIndex && startIndex < focusEnd)
            gotoPos(startIndex, startIndex ^ focus)
        else
            gotoPosRelaxed(startIndex, 0, endIndex, depth)
        blockIndex = focusStart
        lo = startIndex - focusStart
        endLo = math.min(focusEnd - blockIndex, 32)
    }

    def next(): A = {
        if (!_hasNext) throw new NoSuchElementException("reached iterator end")

        val res = display0(lo).asInstanceOf[A]
        lo += 1

        if (lo == endLo) {
            val newBlockIndex = blockIndex + endLo
            if (newBlockIndex < focusEnd) {
                gotoNextBlockStart(newBlockIndex, blockIndex ^ newBlockIndex)
            } else if (newBlockIndex < endIndex) {
                gotoPosRelaxed(newBlockIndex, 0, endIndex, depth)
            } else {
                _hasNext = false
            }
            blockIndex = newBlockIndex
            lo = 0
            endLo = math.min(focusEnd - blockIndex, 32)
        }

        res
    }
}


class RRBVectorReverseIterator[+A](startIndex: Int, endIndex: Int)
  extends AbstractIterator[A]
  with Iterator[A]
  with RRBVectorPointer[A@uncheckedVariance] {

    private var blockIndexInFocus: Int = _
    private var lo: Int = _
    private var endLo: Int = _

    private var _hasNext: Boolean = startIndex < endIndex

    def hasNext = _hasNext

    private[immutable] final def initIterator(): Unit = {
        val idx = endIndex - 1
        if (focusStart <= idx && idx < focusEnd)
            gotoPos(idx, idx ^ focus)
        else
            gotoPosRelaxed(idx, 0, endIndex, depth)
        val indexInFocus = idx - focusStart
        blockIndexInFocus = indexInFocus & ~31
        lo = indexInFocus & 31
        endLo = math.max(startIndex - focusStart - blockIndexInFocus, 0)
    }

    def next(): A = {
        if (!_hasNext) throw new NoSuchElementException("reached iterator end")

        val res = display0(lo).asInstanceOf[A]
        lo -= 1

        if (lo < endLo) {
            val newBlockIndex = blockIndexInFocus - 32
            if (focusStart <= newBlockIndex) {
                gotoPrevBlockStart(newBlockIndex, newBlockIndex ^ blockIndexInFocus)
                blockIndexInFocus = newBlockIndex
                lo = 31
                endLo = math.max(startIndex - focusStart - focus, 0)
            } else if (startIndex <= focus - 1) {
                val newIndexInFocus = focus - 1
                gotoPosRelaxed(newIndexInFocus, 0, endIndex, depth)
                blockIndexInFocus = newIndexInFocus & ~31
                lo = newIndexInFocus & 31
                endLo = math.max(startIndex - focusStart - blockIndexInFocus, 0)
            } else {
                _hasNext = false
            }
        }

        res
    }
}


final class RRBVectorBuilder[A]() extends Builder[A, RRBVector[A]] with RRBVectorPointer[A@uncheckedVariance] {

    display0 = new Array[AnyRef](32)
    depth = 1

    private var blockIndex = 0
    private var lo = 0

    def +=(elem: A): this.type = {
        if (lo >= display0.length) {
            val newBlockIndex = blockIndex + 32
            gotoNextBlockStartWritable(newBlockIndex, blockIndex ^ newBlockIndex)
            blockIndex = newBlockIndex
            lo = 0
        }
        display0(lo) = elem.asInstanceOf[AnyRef]
        lo += 1
        this
    }

    override def ++=(xs: TraversableOnce[A]): this.type =
        super.++=(xs)

    def result: RRBVector[A] = {
        val size = blockIndex + lo
        if (size == 0)
            return RRBVector.empty
        val s = new RRBVector[A](size)
        this.depth match {
            case 1 => // Do nothing
            case 2 =>
                val a = new Array[AnyRef](33)
                Platform.arraycopy(display1, 0, a, 0, display1.length)
                display1 = a
            case 3 =>
                val a = new Array[AnyRef](33)
                Platform.arraycopy(display2, 0, a, 0, display2.length)
                display2 = a
            case 4 =>
                val a = new Array[AnyRef](33)
                Platform.arraycopy(display3, 0, a, 0, display3.length)
                display3 = a
            case 5 =>
                val a = new Array[AnyRef](33)
                Platform.arraycopy(display4, 0, a, 0, display4.length)
                display4 = a
            case 6 =>
                val a = new Array[AnyRef](33)
                Platform.arraycopy(display5, 0, a, 0, display5.length)
                display5 = a
            case _ => throw new IllegalStateException()
        }
        s.initFrom(this)
        if (depth > 1)
            s.gotoPos(0, size - 1)
        s.setFocus(0, 0, size)
        s
    }

    def clear(): Unit = {
        display0 = new Array[AnyRef](32)
        depth = 1
        blockIndex = 0
        lo = 0
    }
}