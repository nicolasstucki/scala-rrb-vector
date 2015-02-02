package scala
package collection
package immutable
package cowarray

import scala.collection.generic.{IndexedSeqFactory, CanBuildFrom, GenericCompanion, GenericTraversableTemplate}

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


    override def iterator = new CowArrayIterator[A](array)

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
            System.arraycopy(newArray, 0, newArray, 0, idx)
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

}

final class CowArrayIterator[+A](array: Array[AnyRef])
  extends Iterator[A] {
    var i = -1
    var end = array.length - 1

    override def hasNext = i < end

    override def next() = {
        val _i = i + 1
        i = _i
        array(_i).asInstanceOf[A]
    }
}

final class CowArrayBuilder[A]() extends mutable.Builder[A, CowArray[A]] {
    var array = Array.newBuilder[AnyRef]

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