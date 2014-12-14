package scala.collection.immutable.vectorbenchmarks.mbrrbvector.xunbalanced

import scala.collection.immutable.mbrrbvector.MbRRBVector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorutils.BaseVectorGenerator
import scala.collection.immutable.vectorutils.BaseVectorGenerator.MbRRBVectorGenerator

trait MbRRBVectorAbstractBenchmark[@miniboxed A] extends BaseVectorBenchmark[A] with MbRRBVectorGenerator[A] {
    override def minHeight = 3

    override def generateVectors(from: Int, to: Int, by: Int) = sizes(from, to, by).map(((size) => randomVectorOfSize(size)(defaultVectorConfig(111))));

    def generateIntVectors(from: Int, to: Int, by: Int) = sizes(from, to, by).map((size) => {
        def randomVectorOfSize(n: Int)(implicit config: BaseVectorGenerator.Config): MbRRBVector[Int] = {
            def randomVectorFromRange(start: Int, end: Int): MbRRBVector[Int] = end - start match {
                case 0 => MbRRBVector.empty[Int]
                case m if m > 0 && config.maxSplitSize < m =>
                    val mid = start + config.rnd.nextInt(m) + 1
                    val v1 = randomVectorFromRange(start, mid)
                    val v2 = randomVectorFromRange(mid, end)
                    v1 ++ v2
                case m if m > 0 && config.maxSplitSize >= m =>
                    val vecBuilder = MbRRBVector.newBuilder[Int]
                    (start until end) foreach (vecBuilder += _)
                    vecBuilder.result()
                case _ => throw new IllegalArgumentException()
            }

            randomVectorFromRange(0, n)
        }
        randomVectorOfSize(size)(BaseVectorGenerator.defaultVectorConfig(111))
    })

    override def vectorName: String = if (MbRRBVector.compileAssertions) throw new IllegalStateException("MbRRBVector.compileAssertions must be false to run benchmarks.") else super.vectorName.+("XUnbalanced")
}
