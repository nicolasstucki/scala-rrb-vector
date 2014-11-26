package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class AppendBenchmarks[A] extends BaseVectorBenchmark[A] {
    self: PerformanceTest =>


    override def maxHeight = 1

    def append(vec: Vec, n: Int): Int

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "append" in {
            for (elems <- Seq(8, 256)) {
                val warmups = if (height == 1) (50 * 256) / elems else if (height == 2) (15 * 256) / elems  else 10
                performance of s"append $elems elements" config(
                  Key.exec.minWarmupRuns -> warmups,
                  Key.exec.maxWarmupRuns -> warmups
                  ) in {

                    performance of s"Height $height" in {
                        using(generateVectors(from, to, by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                            sideeffect = append(vec, elems)
                        }
                    }
                }
            }
        }

    }

}