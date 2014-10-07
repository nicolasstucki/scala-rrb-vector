package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.Key


import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

abstract class SplitBenchmarks[A] extends BaseVectorBenchmark[A] {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        val times = 10000

        measure method "take" config(
          Key.exec.minWarmupRuns -> 3000,
          Key.exec.maxWarmupRuns -> 6000
          ) in {

            performance of s"take half x$times" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        val n = vec.length / 2
                        var sum = 0
                        var i = 0
                        val lim = times
                        while (i < lim) {
                            val vec2 = take(vec, n)
                            sum += vec2.length
                            i += 1
                        }
                        sideeffect = sum
                    }
                }
            }

            performance of s"take quarter x$times" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        val n = vec.length / 4
                        var sum = 0
                        var i = 0
                        val lim = times
                        while (i < lim) {
                            val vec2 = take(vec, n)
                            sum += vec2.length
                            i += 1
                        }
                        sideeffect = sum
                    }
                }
            }

            performance of s"take three quarters x$times" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        val n = (3 * vec.length) / 4
                        var sum = 0
                        var i = 0
                        val lim = times
                        while (i < lim) {
                            val vec2 = take(vec, n)
                            sum += vec2.length
                            i += 1
                        }
                        sideeffect = sum
                    }
                }
            }
        }
    }
}