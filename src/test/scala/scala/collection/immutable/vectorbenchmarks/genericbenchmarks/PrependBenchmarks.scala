package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class PrependBenchmarks[A] extends BaseVectorBenchmark[A] {
    self: PerformanceTest =>

    def prepend(vec: Vec, n: Int, times: Int): Int

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "prepend" config(
          Key.exec.minWarmupRuns -> 2000,
          Key.exec.maxWarmupRuns -> 5000
          ) in {
            val times = 5000

            performance of s"prepend 1 element, $times times" in {

                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        sideeffect = prepend(vec, 1, times)
                    }
                }
            }

            performance of s"prepend 8 element, $times times" in {

                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        sideeffect = prepend(vec, 8, times)
                    }
                }
            }

            performance of s"prepend 32 elements, $times times" in {

                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        sideeffect = prepend(vec, 32, times)
                    }
                }
            }

            performance of s"prepend 100 elements, ${times / 10} times" in {

                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        sideeffect = prepend(vec, 100, times / 10)
                    }
                }
            }
        }

    }


}