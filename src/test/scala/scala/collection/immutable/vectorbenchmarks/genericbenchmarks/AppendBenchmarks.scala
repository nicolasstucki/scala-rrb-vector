package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class AppendBenchmarks[A] extends BaseVectorBenchmark[A] {
    self: PerformanceTest =>

    // TODO remove 'times' parameter
    def append(vec: Vec, n: Int, times: Int): Int

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "append" in {
            for (elems <- Seq(8, 256)) {
                performance of s"append $elems elements" in {

                    performance of s"Height $height" in {
                        using(generateVectors(from, to, by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                            sideeffect = append(vec, elems, 1)
                        }
                    }
                }
            }
        }

    }

}