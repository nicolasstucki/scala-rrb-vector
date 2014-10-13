package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.Gen

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorutils.BaseVectorGenerator


trait VectorBenchmark[A] extends BaseVectorBenchmark[A] with BaseVectorGenerator.VectorGenerator[A] {

    override def generateVectors(from: Int, to: Int, by: Int): Gen[Vector[A]] =
        for {
            size <- sizes(from, to, by)
        } yield Vector.tabulate(size)(element)
}

trait RRBVectorBenchmark[A] extends BaseVectorBenchmark[A] with BaseVectorGenerator.RRBVectorGenerator[A] {

    override def generateVectors(from: Int, to: Int, by: Int): Gen[RRBVector[A]] = for {
        size <- sizes(from, to, by)
    } yield RRBVector.tabulate(size)(element)
}

