package scala.collection.immutable.vectorbenchmarks.vector

import org.scalameter.Gen

import scala.collection.immutable.Vector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator


trait VectorBenchmark[@miniboxed A] extends BaseVectorBenchmark[A] with BaseVectorGenerator.VectorGenerator[A] {

    override def generateVectors(from: Int, to: Int, by: Int): Gen[Vector[A]] =
        for {
            size <- sizes(from, to, by)
        } yield Vector.tabulate(size)(element)
}


