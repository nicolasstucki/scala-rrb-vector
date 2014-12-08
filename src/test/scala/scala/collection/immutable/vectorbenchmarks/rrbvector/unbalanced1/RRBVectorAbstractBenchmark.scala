package scala.collection.immutable.vectorbenchmarks.rrbvector.unbalanced1

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator.RRBVectorGenerator

trait RRBVectorAbstractBenchmark[@miniboxed A] extends BaseVectorBenchmark[A] with RRBVectorGenerator[A] {
    override def minHeight = 3

    override def generateVectors(from: Int, to: Int, by: Int) = {
        sizes(from, to, by) map { n =>
            rangedVector(0, n / 2) ++ rangedVector(n / 2, n)
        }
    }

    override def vectorName: String = if (RRBVector.compileAssertions) throw new IllegalStateException("RRBVector.compileAssertions must be false to run benchmarks.") else super.vectorName.+("Unbalanced1")
}
