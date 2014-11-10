package scala.collection.immutable.vectorbenchmarks.rrbvector.xunbalanced

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator.RRBVectorGenerator

trait RRBVectorAbstractBenchmark [A] extends BaseVectorBenchmark[A] with RRBVectorGenerator[A] {
    override def generateVectors(from: Int, to: Int, by: Int) = sizes(from, to, by).map(((size) => randomVectorOfSize(size)(defaultVectorConfig(111))));
    override def vectorName: String = super.vectorName.+("XUnbalanced")
}
