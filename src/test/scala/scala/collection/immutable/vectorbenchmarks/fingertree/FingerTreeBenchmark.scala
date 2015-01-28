package scala.collection.immutable.vectorbenchmarks.fingertree

import org.scalameter.Gen
import scala.collection.mutable.Builder

import scala.collection.{GenTraversableOnce, CustomParallelizable, IndexedSeqLike, AbstractSeq}
import scala.collection.generic.{GenericCompanion, CanBuildFrom, IndexedSeqFactory, GenericTraversableTemplate}
import scala.collection.immutable._
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator

import de.sciss.fingertree.{IndexedSeq => NonCollFingerTree, Measure}


trait FingerTreeBenchmark[A] extends BaseVectorBenchmark[A] with FingerTreeGenerator[A] {

    override def generateVectors(from: Int, to: Int, by: Int, sizesName: String): Gen[FingerTree[A]] =
        for {
            size <- sizes(from, to, by, sizesName)
        } yield tabulatedVector(size)


    def generateVectors2(from: Int, to: Int, by: Int): Gen[(Vec, Vec)] = {
        for {
            size1 <- sizes(from, to, by, "size1")
            size2 <- sizes(from, to, by, "size2")
        } yield (tabulatedVector(size1), tabulatedVector(size2))
    }

}

trait FingerTreeGenerator[A] extends BaseVectorGenerator[A] {
    override final type Vec = FingerTree[A]

    final def vectorClassName: String = "FingerTree"


    override final def newBuilder() = FingerTree.newBuilder[A]

    override final def tabulatedVector(n: Int): Vec =
        FingerTree.tabulate(n)(element)

    override final def rangedVector(start: Int, end: Int): Vec =
        FingerTree.range(start, end) map element

    override final def emptyVector: Vec = FingerTree.empty[A]

    override def iterator(vec: Vec, start: Int, end: Int) = {
        ???
        //        val it = new VectorIterator[A](start, end)
        //        vec.initIterator(it)
        //        it
    }

    override def plus(vec: Vec, elem: A): Vec = vec :+ elem

    override def plus(elem: A, vec: Vec): Vec = elem +: vec

    override final def plusPlus(vec1: Vec, vec2: Vec): Vec = vec1 ++ vec2

    override final def take(vec: Vec, n: Int): Vec = vec.take(n)

    override final def drop(vec: Vec, n: Int): Vec = vec.drop(n)
}

final class FingerTree[+A] private[immutable](private[immutable] val fingerTree: NonCollFingerTree[AnyRef])
  extends AbstractSeq[A]
  with IndexedSeq[A]
  with GenericTraversableTemplate[A, FingerTree]
  with IndexedSeqLike[A, FingerTree[A]] {
    self =>

    override def companion: GenericCompanion[FingerTree] = FingerTree

    override def length = fingerTree.size

    override def apply(idx: Int) = fingerTree.apply(idx).asInstanceOf[A]

    override def +:[B >: A, That](elem: B)(implicit bf: CanBuildFrom[FingerTree[A], B, That]): That =
        if (bf eq IndexedSeq.ReusableCBF)
            new FingerTree[B](elem.asInstanceOf[AnyRef] +: fingerTree).asInstanceOf[That]
        else super.+:(elem)(bf)

    override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[FingerTree[A], B, That]): That =
        if (bf eq IndexedSeq.ReusableCBF)
            new FingerTree[B](fingerTree :+ elem.asInstanceOf[AnyRef]).asInstanceOf[That]
        else super.:+(elem)(bf)

    override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[FingerTree[A], B, That]): That = {
        if (that.isInstanceOf[FingerTree[B]]) {
            new FingerTree[B](fingerTree ++ that.asInstanceOf[FingerTree[B]].fingerTree).asInstanceOf[That]
        } else super.++(that)
    }

    override def iterator = fingerTree.iterator map (_.asInstanceOf[A])

}

final class FingerTreeBuilder[A]() extends Builder[A, FingerTree[A]] {
    var fingerTree = NonCollFingerTree.empty[AnyRef]

    override def +=(elem: A) = {
        fingerTree = fingerTree :+ elem.asInstanceOf[AnyRef]
        this
    }

    override def result() = new FingerTree[A](fingerTree)

    override def clear() = {
        fingerTree = NonCollFingerTree.empty[AnyRef]
    }
}

object FingerTree extends IndexedSeqFactory[FingerTree] {
    def newBuilder[A]: Builder[A, FingerTree[A]] = new FingerTreeBuilder[A]

    implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, FingerTree[A]] =
        ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

    override def empty[A]: FingerTree[A] = new FingerTree(NonCollFingerTree.empty[AnyRef])

}
