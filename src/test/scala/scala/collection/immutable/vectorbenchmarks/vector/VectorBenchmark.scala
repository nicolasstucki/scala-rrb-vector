package scala.collection.immutable.vectorbenchmarks.vector

import org.scalameter.Gen

import scala.collection.immutable.Vector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator


trait VectorBenchmark[A] extends BaseVectorBenchmark[A] with BaseVectorGenerator.VectorGenerator[A] {

    override def generateVectors(from: Int, to: Int, by: Int, sizesName: String): Gen[Vector[A]] =
        for {
            size <- sizes(from, to, by, sizesName)
        } yield Vector.tabulate(size)(element)

    def generateVectors2(from: Int, to: Int, by: Int): Gen[(Vec, Vec)] = {
        for {
            size1 <- sizes(from, to, by, "size1")
            size2 <- sizes(from, to, by, "size2")
        } yield (tabulatedVector(size1), tabulatedVector(size2))
    }

}


