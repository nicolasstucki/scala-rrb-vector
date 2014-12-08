package scala.collection.immutable.vectorbenchmarks.mbrrbvector.xunbalanced

import scala.collection.immutable.mbrrbvector.MbRRBVector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator.MbRRBVectorGenerator

trait MbRRBVectorAbstractBenchmark[@miniboxed A] extends BaseVectorBenchmark[A] with MbRRBVectorGenerator[A] {
    override def minHeight = 3

    override def generateVectors(from: Int, to: Int, by: Int) = sizes(from, to, by).map(((size) => randomVectorOfSize(size)(defaultVectorConfig(111))));

    override def vectorName: String = if (MbRRBVector.compileAssertions) throw new IllegalStateException("MbRRBVector.compileAssertions must be false to run benchmarks.") else super.vectorName.+("XUnbalanced")
}
