package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class PrependBenchmarks[@miniboxed A] extends BaseVectorBenchmark[A] {
    self: PerformanceTest =>

    def prepend(vec: Vec, n: Int, times: Int): Int

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "prepend" in {
            val elems = 256
            val warmups = if (height == 1) (100 * 256) / elems else if (height == 2) (30 * 256) / elems else 10
            performance of s"prepend $elems elements" config(
              Key.exec.minWarmupRuns -> warmups,
              Key.exec.maxWarmupRuns -> warmups
              ) in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                        sideeffect = prepend(vec, elems, 1)
                    }
                }
            }
        }

    }


}