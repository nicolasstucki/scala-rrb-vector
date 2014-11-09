package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class AppendBenchmarks[A] extends BaseVectorBenchmark[A] {
    self: PerformanceTest =>

    def sum1(vec: Vec, times: Int): Int
    def sum8(vec: Vec, times: Int): Int
    def sum(vec: Vec, n: Int, times: Int): Int

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "append" config(
          Key.exec.minWarmupRuns -> 500,
          Key.exec.maxWarmupRuns -> 2000
          ) in {
            val times = 10000

            performance of s"append 1 element, $times times" in {

                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        sideeffect = sum1(vec, times)
                    }
                }
            }

            performance of s"append 8 element, $times times" in {

                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        sideeffect = sum8(vec, times)
                    }
                }
            }

            performance of s"append 32 elements, $times times" in {

                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        sideeffect = sum(vec, 32, times)
                    }
                }
            }

            performance of s"append 100 elements, $times times" in {

                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        sideeffect = sum(vec, 100, times)
                    }
                }
            }
        }

    }


}