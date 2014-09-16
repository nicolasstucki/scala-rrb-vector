package scala.collection.immutable.rrbvector

import java.lang.Math._

import scala.annotation.unchecked.uncheckedVariance
import scala.collection._

//import scala.collection.generic.GenTraversableFactory.GenericCanBuildFrom

import scala.collection.generic.{CanBuildFrom, GenericCompanion, GenericTraversableTemplate, IndexedSeqFactory}
import scala.collection.immutable.IndexedSeq
import scala.collection.mutable.Builder

object Vector extends IndexedSeqFactory[Vector] {
    def newBuilder[A]: Builder[A, Vector[A]] = new VectorBuilder[A]

    @inline implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, Vector[A]] =
        ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

    private lazy val RRB_NIL = new Vector[Nothing]

    override def empty[A]: Vector[A] = RRB_NIL.asInstanceOf[Vector[A]]

    def apply[A]() = empty[A]

    // create a single element vector
    def apply[A](elem: A): Vector[A] = new Vector[A].initDisplay1(Array(elem.asInstanceOf[AnyRef]), 1)


}

final class Vector[+A] private[immutable]
  extends AbstractSeq[A]
  with IndexedSeq[A]
  with GenericTraversableTemplate[A, Vector]
  with IndexedSeqLike[A, Vector[A]]
  with VectorInternal[A@uncheckedVariance]
  with RelaxedVectorPointer[A@uncheckedVariance]
  with VectorProps
  with Serializable
  /*with CustomParallelizable[A, ParVector[A]] */ {
    self =>

    override def companion: GenericCompanion[Vector] = Vector

    //    override def par = new ParVector(this)

    //    override def toVector: Vector[A] = this

    override def length = _length

    override def lengthCompare(len: Int): Int = length - len

    override def iterator: VectorIterator[A] = {
        val iterator = new VectorIterator[A]
        iterator.initIterator(root, height, length)
        iterator
    }

    // Function1 api

    override def apply(idx: Int): A = getElement(idx)


    // SeqLike api

    override /*SeqLike*/
    def reverseIterator: Iterator[A] = new AbstractIterator[A] {
        private var i = self.length

        def hasNext: Boolean = 0 < i

        def next(): A = {
            if (0 < i) {
                i -= 1
                self(i)
            } else Iterator.empty.next()
        }
    }

    override /*IterableLike*/ def head: A = {
        if (isEmpty) throw new UnsupportedOperationException("empty.head")
        apply(0)
    }

    override /*TraversableLike*/ def tail: Vector[A] = {
        if (isEmpty) throw new UnsupportedOperationException("empty.tail")
        drop(1)
    }

    override /*TraversableLike*/ def last: A = {
        if (isEmpty) throw new UnsupportedOperationException("empty.last")
        apply(length - 1)
    }

    override /*TraversableLike*/ def init: Vector[A] = {
        if (isEmpty) throw new UnsupportedOperationException("empty.init")
        dropRight(1)
    }

    //    override /*IterableLike*/ def slice(from: Int, until: Int): RRBVector[A] =
    //        take(until).drop(from)
    //
    //    override /*IterableLike*/ def splitAt(n: Int): (RRBVector[A], RRBVector[A]) = (take(n), drop(n))

    override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[Vector[A], B, That]): That = {
        that match {
            case vec: Vector[B] => this.concatenated[B](vec).asInstanceOf[That]
            case _ => super.++(that)
        }
    }

    @inline override def +:[B >: A, That](elem: B)(implicit bf: CanBuildFrom[Vector[A], B, That]): That = {
        (Vector(elem) ++ this).asInstanceOf[That]
    }

    override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[Vector[A], B, That]): That =
        if (bf eq IndexedSeq.ReusableCBF) appendedBack(elem).asInstanceOf[That]
        else super.:+(elem)(bf)

    //    override def patch[B >: A, That](from: Int, patch: GenSeq[B], replaced: Int)(implicit bf: CanBuildFrom[RRBVector[A], B, That]): That = {
    //        // just ignore bf
    //        val insert = patch.nonEmpty
    //        val delete = replaced != 0
    //        if (insert || delete) {
    //            val prefix = take(from)
    //            val rest = drop(from + replaced)
    //            ((prefix ++ patch).asInstanceOf[Vector[B]] ++ rest).asInstanceOf[That]
    //        } else this.asInstanceOf[That]
    //    }

}


private[immutable] trait VectorProps {
    protected final val WIDTH_SHIFT = 5
    protected final val WIDTH = (1 << WIDTH_SHIFT)
    // sets min standard size for a slot ie w-invar
    protected final val INVAR = 1
    // sets number of extra slots allowed, ie linear search limit
    protected final val EXTRAS = 2

}