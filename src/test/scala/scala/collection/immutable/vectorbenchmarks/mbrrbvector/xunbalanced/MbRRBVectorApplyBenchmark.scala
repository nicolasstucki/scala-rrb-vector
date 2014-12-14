package scala.collection.immutable.vectorbenchmarks.mbrrbvector.xunbalanced

import org.scalameter.Key

import scala.collection.immutable.mbrrbvector.MbRRBVector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

import scala.collection.immutable.vectorutils._


class MbRRBVectorApplyIntBenchmark extends BaseVectorBenchmark[Int] with MbRRBVectorAbstractBenchmark[Int] with VectorGeneratorType.IntGenerator {
    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "apply" config(
          Key.exec.minWarmupRuns -> 1000,
          Key.exec.maxWarmupRuns -> 1000) in {

            performance of "10k iteration" in {
                performance of s"Height $height" in {
                    using(generateIntVectors(from, to, by)) curve vectorName in { vec =>
                        var i = 0
                        var sum = vec(0)
                        val len = vec.length
                        val until = 10000
                        while (i < until) {
                            sum = vec.apply(i % len)
                            i += 1
                        }
                        sideeffect = sum.hashCode()
                    }
                }
            }

            performance of "10k reverse iteration" in {
                performance of s"Height $height" in {
                    using(generateIntVectors(from, to, by)) curve vectorName in { vec =>
                        var i = 10000
                        var sum = vec(0)
                        val len = vec.length
                        while (i > 0) {
                            i -= 1
                            sum = vec.apply(i % len)
                        }
                        sideeffect = sum.hashCode()
                    }
                }
            }

            performance of "10k pseudo-random indices (seed=42)" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in {vec=>sideeffect = benchmarkFunctionPseudoRandom(vec, 42)}
                }
            }

            performance of "10k pseudo-random indices (seed=274181)" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in{vec=>sideeffect = benchmarkFunctionPseudoRandom(vec, 274181)}
                }
            }

            performance of "10k pseudo-random indices (seed=53426)" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in {vec=>sideeffect = benchmarkFunctionPseudoRandom(vec, 53426)}
                }
            }
        }
    }

}