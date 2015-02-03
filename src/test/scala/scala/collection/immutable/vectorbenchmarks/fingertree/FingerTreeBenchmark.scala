package scala.collection.immutable.vectorbenchmarks.fingertree

import org.scalameter.Gen

import scala.collection.immutable.fingertree._
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator
import scala.collection.immutable.redblack._

trait FingerTreeBenchmark[A] extends BaseVectorBenchmark[A] with FingerTreeSeqGenerator[A] {

    override def generateVectors(from: Int, to: Int, by: Int, sizesName: String): Gen[FingerTreeSeq[A]] =
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

trait FingerTreeSeqGenerator[A] extends BaseVectorGenerator[A] {
    override final type Vec = FingerTreeSeq[A]

    final def vectorClassName: String = "FingerTreeSeq"


    override final def newBuilder() = FingerTreeSeq.newBuilder[A]

    override final def tabulatedVector(n: Int): Vec =
        FingerTreeSeq.tabulate(n)(element)

    override final def rangedVector(start: Int, end: Int): Vec =
        FingerTreeSeq.range(start, end) map element

    override final def emptyVector: Vec = FingerTreeSeq.empty[A]

    override def iterator(vec: Vec, start: Int, end: Int) = {
        vec.take(end).drop(start).iterator
    }

    override def plus(vec: Vec, elem: A): Vec = vec :+ elem

    override def plus(elem: A, vec: Vec): Vec = elem +: vec

    override final def plusPlus(vec1: Vec, vec2: Vec): Vec = vec1 ++ vec2

    override final def take(vec: Vec, n: Int): Vec = vec.take(n)

    override final def drop(vec: Vec, n: Int): Vec = vec.drop(n)
}



