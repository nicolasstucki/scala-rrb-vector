package scala
package collection
package immutable
package rbvector

import java.io.Serializable

import scala.annotation.unchecked.uncheckedVariance

import scala.collection.generic.{GenericCompanion, GenericTraversableTemplate, CanBuildFrom, IndexedSeqFactory}

import scala.collection.mutable.Builder
import scala.compat.Platform

/**
 * Created by nicolasstucki on 19/09/2014.
 */

object RBVector extends IndexedSeqFactory[RBVector] {
    def newBuilder[A]: Builder[A, RBVector[A]] = new RBVectorBuilder[A]

    implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, RBVector[A]] =
        ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

    private[immutable] val NIL = new RBVector[Nothing](0)

    override def empty[A]: RBVector[A] = NIL

}

final class RBVector[+A] private[immutable](private[immutable] val endIndex: Int)
  extends AbstractSeq[A]
  with IndexedSeq[A]
  with GenericTraversableTemplate[A, RBVector]
  with IndexedSeqLike[A, RBVector[A]]
  with RBVectorPointer[A@uncheckedVariance]
  with Serializable {
    //  with CustomParallelizable[A, ParVector[A]] {
    self =>


    override def companion: GenericCompanion[RBVector] = RBVector

    def length = endIndex

    override def lengthCompare(len: Int): Int = length - len

    private[collection] final def initIterator[B >: A](s: RBVectorIterator[B]) {
        s.initFrom(this)
        if (s.depth > 1) s.gotoPos(0, 0 ^ focus)
    }

    private[collection] final def initReverseIterator[B >: A](s: RBVectorReverseIterator[B]) {
        s.initFrom(this)
        if (s.depth > 1) s.gotoPos(endIndex - 1, (endIndex - 1) ^ focus)
    }

    override def iterator: RBVectorIterator[A] = {
        val s = new RBVectorIterator[A](0, endIndex)
        initIterator(s)
        s
    }

    override /*SeqLike*/ def reverseIterator: Iterator[A] = {
        val s = new RBVectorReverseIterator[A](0, endIndex)
        initReverseIterator(s)
        s
    }

    override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[RBVector[A], B, That]): That =
        if (bf eq IndexedSeq.ReusableCBF) appendedBack(elem).asInstanceOf[That] // just ignore bf
        else super.:+(elem)(bf)

    def apply(index: Int): A = {
        if (0 <= index && index < endIndex) getElem(index, index ^ focus)
        else throw new IndexOutOfBoundsException(index.toString)
    }

    override /*IterableLike*/ def head: A = {
        if (isEmpty) throw new UnsupportedOperationException("empty.head")
        apply(0)
    }

    override /*TraversableLike*/ def tail: RBVector[A] = {
        if (isEmpty) throw new UnsupportedOperationException("empty.tail")
        drop(1)
    }

    override /*TraversableLike*/ def last: A = {
        if (isEmpty) throw new UnsupportedOperationException("empty.last")
        apply(length - 1)
    }

    override /*TraversableLike*/ def init: RBVector[A] = {
        if (isEmpty) throw new UnsupportedOperationException("empty.init")
        dropRight(1)
    }

    override /*IterableLike*/ def slice(from: Int, until: Int): RBVector[A] =
        take(until).drop(from)

    override /*IterableLike*/ def splitAt(n: Int): (RBVector[A], RBVector[A]) = (take(n), drop(n))


    private[immutable] def appendedBack[B >: A](value: B): RBVector[B] = {
        val vec = new RBVector[A](endIndex + 1)
        if (endIndex == 0) {
            vec.display0 = new Array[AnyRef](32)
            vec.display0(0) = value.asInstanceOf[AnyRef]
            vec.depth = 1
            vec.hasWritableTail = true
        } else {
            val lastIndex = endIndex - 1

            vec.initFrom(this)
            vec.focus = focus

            if ((focus ^ ~31) != (lastIndex ^ ~31)) {
                vec.gotoPos(lastIndex, lastIndex ^ focus)
                vec.focus = lastIndex
            }

            if (!hasWritableTail) {
                if ((lastIndex & 31) < 31) {
                    vec.display0 = copyOf(vec.display0)
                    vec.stabilize(lastIndex)
                } else {
                    vec.stabilize(lastIndex) // TODO: Improve performance. May not need to stabilize all the way down
                    vec.gotoNextBlockStartWritable(endIndex, endIndex ^ vec.focus)
                    vec.focus = endIndex
                }
            } else {
                hasWritableTail = false
            }

            vec.display0(endIndex & 31) = value.asInstanceOf[AnyRef]
            vec.hasWritableTail = (endIndex & 31) < 31
        }
        vec
    }

}

private[immutable] trait RBVectorPointer[A] {
    private[immutable] var focus: Int = 0
    private[immutable] var depth: Int = _

    private[immutable] var display0: Array[AnyRef] = _
    private[immutable] var display1: Array[AnyRef] = _
    private[immutable] var display2: Array[AnyRef] = _
    private[immutable] var display3: Array[AnyRef] = _
    private[immutable] var display4: Array[AnyRef] = _
    private[immutable] var display5: Array[AnyRef] = _

    var hasWritableTail = false

    private[immutable] final def initFrom[U](that: RBVectorPointer[U]): Unit = initFrom(that, that.depth)

    private[immutable] final def initFrom[U](that: RBVectorPointer[U], depth: Int) = {
        this.depth = depth
        (depth - 1) match {
            case -1 =>
            case 0 =>
                display0 = that.display0
            case 1 =>
                display1 = that.display1
                display0 = that.display0
            case 2 =>
                display2 = that.display2
                display1 = that.display1
                display0 = that.display0
            case 3 =>
                display3 = that.display3
                display2 = that.display2
                display1 = that.display1
                display0 = that.display0
            case 4 =>
                display4 = that.display4
                display3 = that.display3
                display2 = that.display2
                display1 = that.display1
                display0 = that.display0
            case 5 =>
                display5 = that.display5
                display4 = that.display4
                display3 = that.display3
                display2 = that.display2
                display1 = that.display1
                display0 = that.display0
        }
    }

    private[immutable] final def getElem(index: Int, xor: Int): A = {
        if (xor < (1 << 5)) {
            // level = 0
            display0(index & 31).asInstanceOf[A]
        } else
        if (xor < (1 << 10)) {
            // level = 1
            display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else
        if (xor < (1 << 15)) {
            // level = 2
            display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else
        if (xor < (1 << 20)) {
            // level = 3
            display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else
        if (xor < (1 << 25)) {
            // level = 4
            display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]((index >> 15) & 31).asInstanceOf[Array[AnyRef]]((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else
        if (xor < (1 << 30)) {
            // level = 5
            display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]((index >> 20) & 31).asInstanceOf[Array[AnyRef]]((index >> 15) & 31).asInstanceOf[Array[AnyRef]]((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else {
            // level = 6
            throw new IllegalArgumentException()
        }
    }

    private[immutable] final def gotoPos(index: Int, xor: Int): Unit = {
        if (xor < (1 << 5)) {
            // level = 0 (could maybe removed)
        } else
        if (xor < (1 << 10)) {
            // level = 1
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else
        if (xor < (1 << 15)) {
            // level = 2
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else
        if (xor < (1 << 20)) {
            // level = 3
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else
        if (xor < (1 << 25)) {
            // level = 4
            display3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else
        if (xor < (1 << 30)) {
            // level = 5
            display4 = display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
            display3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else {
            // level = 6
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
        if (xor < (1 << 10)) {
            // level = 1
            if (depth == 1) {
                display1 = new Array(32);
                display1(0) = display0;
                depth += 1
            }
            display0 = new Array(32)
            display1((index >> 5) & 31) = display0
        } else
        if (xor < (1 << 15)) {
            // level = 2
            if (depth == 2) {
                display2 = new Array(32);
                display2(0) = display1;
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(32)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
        } else
        if (xor < (1 << 20)) {
            // level = 3
            if (depth == 3) {
                display3 = new Array(32);
                display3(0) = display2;
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(32)
            display2 = new Array(32)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
            display3((index >> 15) & 31) = display2
        } else
        if (xor < (1 << 25)) {
            // level = 4
            if (depth == 4) {
                display4 = new Array(32);
                display4(0) = display3;
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
        } else
        if (xor < (1 << 30)) {
            // level = 5
            if (depth == 5) {
                display5 = new Array(32);
                display5(0) = display4;
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
        } else {
            // level = 6
            throw new IllegalArgumentException()
        }
    }

    private[immutable] final def stabilize(index: Int) = (depth - 1) match {
        case 5 =>
            display5 = copyOf(display5)
            display4 = copyOf(display4)
            display3 = copyOf(display3)
            display2 = copyOf(display2)
            display1 = copyOf(display1)
            display5((index >> 25) & 31) = display4
            display4((index >> 20) & 31) = display3
            display3((index >> 15) & 31) = display2
            display2((index >> 10) & 31) = display1
            display1((index >> 5) & 31) = display0
        case 4 =>
            display4 = copyOf(display4)
            display3 = copyOf(display3)
            display2 = copyOf(display2)
            display1 = copyOf(display1)
            display4((index >> 20) & 31) = display3
            display3((index >> 15) & 31) = display2
            display2((index >> 10) & 31) = display1
            display1((index >> 5) & 31) = display0
        case 3 =>
            display3 = copyOf(display3)
            display2 = copyOf(display2)
            display1 = copyOf(display1)
            display3((index >> 15) & 31) = display2
            display2((index >> 10) & 31) = display1
            display1((index >> 5) & 31) = display0
        case 2 =>
            display2 = copyOf(display2)
            display1 = copyOf(display1)
            display2((index >> 10) & 31) = display1
            display1((index >> 5) & 31) = display0
        case 1 =>
            display1 = copyOf(display1)
            display1((index >> 5) & 31) = display0
        case 0 =>
    }

    private[immutable] final def copyOf(a: Array[AnyRef]) = {
        val b = new Array[AnyRef](a.length)
        Platform.arraycopy(a, 0, b, 0, a.length)
        b
    }

}


class RBVectorIterator[+A](startIndex: Int, endIndex: Int)
  extends AbstractIterator[A]
  with Iterator[A]
  with RBVectorPointer[A@uncheckedVariance] {

    private var blockIndex: Int = startIndex & ~31
    private var lo: Int = startIndex & 31
    private var endLo = math.min(endIndex - blockIndex, 32)

    def hasNext = _hasNext

    private var _hasNext = blockIndex + lo < endIndex

    def next(): A = {
        if (!_hasNext) throw new NoSuchElementException("reached iterator end")

        val res = display0(lo).asInstanceOf[A]
        lo += 1

        if (lo == endLo) {
            if (blockIndex + lo < endIndex) {
                val newBlockIndex = blockIndex + 32
                gotoNextBlockStart(newBlockIndex, blockIndex ^ newBlockIndex)
                blockIndex = newBlockIndex
                endLo = math.min(endIndex - blockIndex, 32)
                lo = 0
            } else {
                _hasNext = false
            }
        }

        res
    }
}

class RBVectorReverseIterator[+A](startIndex: Int, endIndex: Int)
  extends AbstractIterator[A]
  with Iterator[A]
  with RBVectorPointer[A@uncheckedVariance] {

    private var blockIndex: Int = (endIndex - 1) & ~31
    private var lo: Int = (endIndex - 1) & 31

    private var endLo = math.max(startIndex - blockIndex, 0)

    def hasNext = _hasNext

    private var _hasNext = startIndex <= blockIndex + lo

    def next(): A = {
        if (!_hasNext) throw new NoSuchElementException("reached iterator end")

        val res = display0(lo).asInstanceOf[A]
        lo -= 1

        if (lo < endLo) {
            if (startIndex < blockIndex + lo) {
                val newBlockIndex = blockIndex - 32
                gotoPrevBlockStart(newBlockIndex, blockIndex ^ newBlockIndex)
                blockIndex = newBlockIndex
                endLo = math.max(startIndex - blockIndex, 0)
                lo = 31
            } else {
                _hasNext = false
            }
        }

        res
    }
}

final class RBVectorBuilder[A]() extends Builder[A, RBVector[A]] with RBVectorPointer[A@uncheckedVariance] {

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

    def result: RBVector[A] = {
        val size = blockIndex + lo
        if (size == 0)
            return RBVector.empty
        val s = new RBVector[A](size) // should focus front or back?
        s.initFrom(this)
        if (depth > 1) s.gotoPos(0, size - 1)
        s
    }

    def clear(): Unit = {
        display0 = new Array[AnyRef](32)
        depth = 1
        blockIndex = 0
        lo = 0
    }


}