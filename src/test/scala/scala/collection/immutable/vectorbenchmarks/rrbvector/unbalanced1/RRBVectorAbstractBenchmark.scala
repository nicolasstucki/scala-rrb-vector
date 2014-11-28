package scala.collection.immutable.vectorbenchmarks.rrbvector.unbalanced1

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator.RRBVectorGenerator

trait RRBVectorAbstractBenchmark[A] extends BaseVectorBenchmark[A] with RRBVectorGenerator[A] {
    override val minHeight = 3
    
    override def generateVectors(from: Int, to: Int, by: Int) = {
        sizes(from, to, by) map { n =>
            val vecs = tabulatedVector(n).splitAt(n / 2)
            vecs._1 ++ vecs._2
        }
    }

    override def vectorName: String = if (RRBVector.compileAssertions) throw new IllegalStateException("RRBVector.compileAssertions must be false to run benchmarks.") else super.vectorName.+("Unbalanced1")
}
