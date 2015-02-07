package scala.collection.immutable.fingertree

import scala.collection.mutable.Builder
import scala.collection.{GenTraversableOnce, IndexedSeqLike, AbstractSeq}
import scala.collection.generic.{IndexedSeqFactory, CanBuildFrom, GenericCompanion, GenericTraversableTemplate}
import scala.collection.immutable.IndexedSeq


/**
 * Created by nicolasstucki on 01/02/15.
 */

final class FingerTreeSeq[+A] private[immutable](private[immutable] val fingerTree: FingerTreeIndexedSeq[AnyRef])
  extends AbstractSeq[A]
  with IndexedSeq[A]
  with GenericTraversableTemplate[A, FingerTreeSeq]
  with IndexedSeqLike[A, FingerTreeSeq[A]] {
    self =>
    private implicit val measure = Measure.Indexed

    override def companion: GenericCompanion[FingerTreeSeq] = FingerTreeSeq

    override def length = fingerTree.size


    override def lengthCompare(len: Int) = length - len


    override def apply(idx: Int) = fingerTree.apply(idx).asInstanceOf[A]

    override def +:[B >: A, That](elem: B)(implicit bf: CanBuildFrom[FingerTreeSeq[A], B, That]): That =
        if (bf eq IndexedSeq.ReusableCBF)
            new FingerTreeSeq[B](elem.asInstanceOf[AnyRef] +: fingerTree).asInstanceOf[That]
        else super.+:(elem)(bf)

    override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[FingerTreeSeq[A], B, That]): That =
        if (bf eq IndexedSeq.ReusableCBF)
            new FingerTreeSeq[B](fingerTree :+ elem.asInstanceOf[AnyRef]).asInstanceOf[That]
        else super.:+(elem)(bf)

    override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[FingerTreeSeq[A], B, That]): That = {
        if (that.isInstanceOf[FingerTreeSeq[B]]) {
            new FingerTreeSeq[B](fingerTree ++ that.asInstanceOf[FingerTreeSeq[B]].fingerTree).asInstanceOf[That]
        } else super.++(that)
    }

    override def updated[B >: A, That](index: Int, elem: B)(implicit bf: CanBuildFrom[FingerTreeSeq[A], B, That]) = {
        if (bf eq IndexedSeq.ReusableCBF) {
            val (init, tail) = fingerTree.splitAt(index)
            new FingerTreeSeq[B](
                (init :+ elem.asInstanceOf[AnyRef]) ++ tail
            ).asInstanceOf[That]
        } else
            super.updated(index, elem)
    }


    override def iterator = fingerTree.iterator map (_.asInstanceOf[A])

    override def head = {
        if (isEmpty) throw new UnsupportedOperationException
        super.head
    }

    override def last = {
        if (isEmpty) throw new UnsupportedOperationException
        super.last
    }
}

final class FingerTreeSeqBuilder[A]() extends Builder[A, FingerTreeSeq[A]] {
    var fingerTree = FingerTreeIndexedSeq.empty[AnyRef]

    override def +=(elem: A) = {
        fingerTree = fingerTree :+ elem.asInstanceOf[AnyRef]
        this
    }

    override def result() = new FingerTreeSeq[A](fingerTree)

    override def clear() = {
        fingerTree = FingerTreeIndexedSeq.empty[AnyRef]
    }
}

object FingerTreeSeq extends IndexedSeqFactory[FingerTreeSeq] {
    def newBuilder[A]: Builder[A, FingerTreeSeq[A]] = new FingerTreeSeqBuilder[A]

    implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, FingerTreeSeq[A]] =
        ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

    override def empty[A]: FingerTreeSeq[A] = new FingerTreeSeq(FingerTreeIndexedSeq.empty[AnyRef])

}