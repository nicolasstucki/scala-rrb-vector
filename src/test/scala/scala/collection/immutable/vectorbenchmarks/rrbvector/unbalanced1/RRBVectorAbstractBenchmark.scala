package scala.collection.immutable.vectorbenchmarks.rrbvector.unbalanced1

import org.scalameter.Gen

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator.RRBVectorGenerator

trait RRBVectorAbstractBenchmark[A] extends BaseVectorBenchmark[A] with RRBVectorGenerator[A] {
    override def minHeight = 3

    override def generateVectors(from: Int, to: Int, by: Int,sizesName:String) = {
        sizes(from, to, by, sizesName) map { n =>
            rangedVector(0, n / 2) ++ rangedVector(n / 2, n)
        }
    }

    def generateVectors2(from: Int, to: Int, by: Int): Gen[(Vec, Vec)] = {
        for {
            size1 <- sizes(from, to, by, "size1")
            size2 <- sizes(from, to, by, "size2")
        } yield (rangedVector(0, size1 / 2) ++ rangedVector(size1 / 2, size1),rangedVector(0, size2 / 2) ++ rangedVector(size2 / 2, size2))
    }

    override def vectorName: String = if (RRBVector.compileAssertions) throw new IllegalStateException("RRBVector.compileAssertions must be false to run benchmarks.") else super.vectorName.+("Unbalanced1")
}
