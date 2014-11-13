package scala.collection.immutable.vectorbenchmarks.rrbvector.balanced

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator.RRBVectorGenerator

trait RRBVectorAbstractBenchmark[A] extends BaseVectorBenchmark[A] with RRBVectorGenerator[A] {
    override def generateVectors(from: Int, to: Int, by: Int) = sizes(from, to, by).map(((size) => tabulatedVector(size)));

    override def vectorName: String = if (RRBVector.compileAssertions) throw new IllegalStateException("RRBVector.compileAssertions must be false to run benchmarks.") else super.vectorName.+("Balanced")
}
