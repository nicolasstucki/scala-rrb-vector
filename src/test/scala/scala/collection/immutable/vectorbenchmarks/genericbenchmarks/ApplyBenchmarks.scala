package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.Key

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

abstract class ApplyBenchmarks[A] extends BaseVectorBenchmark[A] {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "apply" config(
          Key.exec.minWarmupRuns -> 800,
          Key.exec.maxWarmupRuns -> 1000) in {

            performance of "10k iteration" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        var i = 0
                        var sum = vec(0)
                        val len = vec.length
                        val until = 1000
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
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        var i = 1000
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

            def benchmarkFunctionPseudoRandom[Vec <: IndexedSeq[A]](vec: Vec, seed: Int) = {
                val rnd = new scala.util.Random(seed)
                var i = 0
                var sum = vec(0)
                val len = vec.length
                while (i < 10000) {
                    sum = vec.apply(rnd.nextInt(len))
                    i += 1
                }
                sideeffect = sum.hashCode()
            }

            performance of "10k pseudo-random indices (seed=42)" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in (benchmarkFunctionPseudoRandom(_, 42))
                }
            }

            performance of "10k pseudo-random indices (seed=274181)" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in (benchmarkFunctionPseudoRandom(_, 274181))
                }
            }

            performance of "10k pseudo-random indices (seed=53426)" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in (benchmarkFunctionPseudoRandom(_, 53426))
                }
            }
        }
    }
}