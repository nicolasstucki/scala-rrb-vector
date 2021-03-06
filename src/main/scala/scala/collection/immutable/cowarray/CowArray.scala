package scala
package collection
package immutable
package cowarray

import java.util.NoSuchElementException

import scala.collection.generic.{IndexedSeqFactory, CanBuildFrom, GenericCompanion, GenericTraversableTemplate}
import scala.collection.mutable.ArrayBuffer

/**
 * Created by nicolasstucki on 01/02/15.
 */
final class CowArray[+A] private[immutable](private[immutable] val array: Array[AnyRef])
  extends AbstractSeq[A]
  with IndexedSeq[A]
  with GenericTraversableTemplate[A, CowArray]
  with IndexedSeqLike[A, CowArray[A]] {

    override def companion: GenericCompanion[CowArray] = CowArray

    override def length = if (array == null) 0 else array.length

    override def iterator = {
        val it = new CowArrayIterator[A]()
        it.initFrom(array)
        it
    }

    override def apply(idx: Int) = {
        val _array = array
        if (0 <= idx && idx < _array.length)
            _array(idx).asInstanceOf[A]
        else
            throw new IndexOutOfBoundsException(idx.toString)
    }

    override def updated[B >: A, That](idx: Int, elem: B)(implicit bf: CanBuildFrom[CowArray[A], B, That]) = {
        if (0 > idx && idx >= array.length)
            throw new IndexOutOfBoundsException(idx.toString)

        if (bf eq IndexedSeq.ReusableCBF) {
            val newArray = new Array[AnyRef](array.length)
            System.arraycopy(array, 0, newArray, 0, idx)
            newArray(idx) = elem.asInstanceOf[AnyRef]
            System.arraycopy(array, idx + 1, newArray, idx + 1, array.length - idx - 1)
            new CowArray[B](newArray).asInstanceOf[That]
        } else
            super.updated(idx, elem)

    }

    override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[CowArray[A], B, That]) = {
        if (bf eq IndexedSeq.ReusableCBF) {
            val len = array.length
            val newArray = new Array[AnyRef](len + 1)
            System.arraycopy(array, 0, newArray, 0, len)
            newArray(len) = elem.asInstanceOf[AnyRef]
            new CowArray[B](newArray).asInstanceOf[That]
        } else
            super.:+(elem)
    }

    override def +:[B >: A, That](elem: B)(implicit bf: CanBuildFrom[CowArray[A], B, That]) = {
        if (bf eq IndexedSeq.ReusableCBF) {
            val len = array.length
            val newArray = new Array[AnyRef](len + 1)
            newArray(0) = elem.asInstanceOf[AnyRef]
            System.arraycopy(array, 0, newArray, 1, len)
            new CowArray[B](newArray).asInstanceOf[That]
        } else
            super.:+(elem)
    }

    override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[CowArray[A], B, That]): That = that match {
        case _that: CowArray[B] =>
            val lenLeft = array.length
            val lenRight = _that.array.length
            val newArray = new Array[AnyRef](lenLeft + lenRight)
            System.arraycopy(array, 0, newArray, 0, lenLeft)
            System.arraycopy(_that.array, 0, newArray, lenLeft, lenRight)
            new CowArray[B](newArray).asInstanceOf[That]
        case _ => super.++(that)
    }

    override def head = {
        if (isEmpty) throw new UnsupportedOperationException
        else array(0).asInstanceOf[A]
    }

    override def last = {
        val len = array.length
        if (len == 0) throw new UnsupportedOperationException
        else array(len).asInstanceOf[A]
    }

    override def lengthCompare(len: Int) = length - len
}

final class CowArrayIterator[+A]()
  extends Iterator[A] {
    // assert(array)
    private var i = 0
    private var end = 0
    private var _hasNext: Boolean = _
    private var array: Array[AnyRef] = _

    final def hasNext = _hasNext

    def initFrom(array: Array[AnyRef]) = {
        i = 0
        if (array == null || array.length == 0) {
            end = 0
            _hasNext = false
            this.array = new Array[AnyRef](1)
        } else {
            this.array = array
            end = array.length
            _hasNext = true
        }
    }

    final def next(): A = {
        // keep method size under 35 bytes, so that it can be JIT-inlined
        var _i = i
        val res: A = array(_i).asInstanceOf[A]
        _i += 1
        i = _i
        if (_i >= end)
            checkEnded()
        res
    }

    private[immutable] final def checkEnded(): Unit = {
        i -= 1
        if (_hasNext) {
            _hasNext = false
            return
        } else throw new NoSuchElementException("reached iterator end")
    }

}

final class CowArrayBuilder[A]() extends mutable.Builder[A, CowArray[A]] {
    var array = Array.newBuilder[AnyRef]

    override def sizeHint(size: Int) = array.sizeHint(size)

    override def +=(elem: A) = {
        array += elem.asInstanceOf[AnyRef]
        this
    }

    override def result() = new CowArray[A](array.result())

    override def clear() = {
        array.clear()
    }
}

object CowArray extends IndexedSeqFactory[CowArray] {
    def newBuilder[A]: mutable.Builder[A, CowArray[A]] = new CowArrayBuilder[A]

    implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, CowArray[A]] =
        ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

    override def empty[A]: CowArray[A] = new CowArray[A](Array.empty[AnyRef])
}