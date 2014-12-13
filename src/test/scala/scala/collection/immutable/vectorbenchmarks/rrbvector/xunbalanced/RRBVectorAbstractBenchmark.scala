package scala.collection.immutable.vectorbenchmarks.rrbvector.xunbalanced

import org.scalameter.Gen

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator.RRBVectorGenerator

trait RRBVectorAbstractBenchmark[A] extends BaseVectorBenchmark[A] with RRBVectorGenerator[A] {
    override def minHeight = 3

    override def generateVectors(from: Int, to: Int, by: Int, sizesName: String) = sizes(from, to, by, sizesName).map(((size) => randomVectorOfSize(size)(defaultVectorConfig(111))));

    def generateVectors2(from: Int, to: Int, by: Int): Gen[(Vec, Vec)] = {
        for {
            size1 <- sizes(from, to, by, "size1")
            size2 <- sizes(from, to, by, "size2")
        } yield (randomVectorOfSize(size1)(defaultVectorConfig(111)), randomVectorOfSize(size2)(defaultVectorConfig(111)))
    }

    override def vectorName: String = if (RRBVector.compileAssertions) throw new IllegalStateException("RRBVector.compileAssertions must be false to run benchmarks.") else super.vectorName.+("XUnbalanced")
}
