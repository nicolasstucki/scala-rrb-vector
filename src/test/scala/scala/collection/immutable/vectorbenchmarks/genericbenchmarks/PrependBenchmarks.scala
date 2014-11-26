package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class PrependBenchmarks[A] extends BaseVectorBenchmark[A] {
    self: PerformanceTest =>

    def prepend(vec: Vec, n: Int, times: Int): Int

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "prepend" in {
            for (elems <- Seq(8, 256)) {
                performance of s"prepend $elems elements" in {
                    performance of s"Height $height" in {
                        using(generateVectors(from, to, by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                            sideeffect = prepend(vec, elems, 1)
                        }
                    }
                }
            }
        }

    }


}