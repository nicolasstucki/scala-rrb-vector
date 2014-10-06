package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class AppendBenchmarks[A] extends BaseVectorBenchmark[A] {
    self: PerformanceTest =>

    def sum32(vec: Vec, times: Int): Int

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "append" config(
          Key.exec.minWarmupRuns -> 2000,
          Key.exec.maxWarmupRuns -> 5000
          ) in {
            val times = 10000
            performance of s"append 32 elements, $times times" in {

                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        sideeffect = sum32(vec, times)
                    }
                }
            }
        }

    }


}