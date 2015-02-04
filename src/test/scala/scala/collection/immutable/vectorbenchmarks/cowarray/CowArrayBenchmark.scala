package scala.collection.immutable.vectorbenchmarks.cowarray

import org.scalameter.Gen

import scala.collection.immutable.cowarray._
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils._

trait CowArrayBenchmark[A] extends BaseVectorBenchmark[A] with CowArrayGenerator[A] {

    override def generateVectors(from: Int, to: Int, by: Int, sizesName: String): Gen[CowArray[A]] =
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

trait CowArrayGenerator[A] extends BaseVectorGenerator[A] {
    override final type Vec = CowArray[A]

    final def vectorClassName: String = "CowArray"


    override final def newBuilder() = CowArray.newBuilder[A]

    override final def tabulatedVector(n: Int): Vec =
        CowArray.tabulate(n)(element)

    override final def rangedVector(start: Int, end: Int): Vec =
        CowArray.range(start, end) map element

    override final def emptyVector: Vec = CowArray.empty[A]

    override def iterator(vec: Vec, start: Int, end: Int) = vec.take(end).drop(start).iterator

    override def plus(vec: Vec, elem: A): Vec = vec :+ elem

    override def plus(elem: A, vec: Vec): Vec = elem +: vec

    override final def plusPlus(vec1: Vec, vec2: Vec): Vec = vec1 ++ vec2

    override final def take(vec: Vec, n: Int): Vec = vec.take(n)

    override final def drop(vec: Vec, n: Int): Vec = vec.drop(n)
}