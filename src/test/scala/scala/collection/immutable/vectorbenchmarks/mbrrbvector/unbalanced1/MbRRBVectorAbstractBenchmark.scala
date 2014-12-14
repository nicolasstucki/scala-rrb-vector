package scala.collection.immutable.vectorbenchmarks.mbrrbvector.unbalanced1

import scala.collection.immutable.mbrrbvector.MbRRBVector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator.MbRRBVectorGenerator

trait MbRRBVectorAbstractBenchmark[@miniboxed A] extends BaseVectorBenchmark[A] with MbRRBVectorGenerator[A] {
    override def minHeight = 3

    override def generateVectors(from: Int, to: Int, by: Int) = {
        sizes(from, to, by) map { n =>
            rangedVector(0, n / 2) ++ rangedVector(n / 2, n)
        }
    }

    def generateIntVectors(from: Int, to: Int, by: Int) = sizes(from, to, by).map((size) => {
        val vecBuilder = MbRRBVector.newBuilder[Int]
        (0 until (size / 2)) foreach (vecBuilder += _)
        val v1 = vecBuilder.result()
        vecBuilder.clear()
        ((size / 2) until size) foreach (vecBuilder += _)
        v1 ++ vecBuilder.result()
    })

    override def vectorName: String = if (MbRRBVector.compileAssertions) throw new IllegalStateException("MbRRBVector.compileAssertions must be false to run benchmarks.") else super.vectorName.+("Unbalanced1")
}
