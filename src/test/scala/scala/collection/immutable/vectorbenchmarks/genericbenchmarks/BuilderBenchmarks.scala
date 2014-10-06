
package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.Key

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

abstract class BuilderBenchmarks[A] extends BaseVectorBenchmark[A] {

    def buildVector(n: Int, m: Int): Int

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "builder" config(
          Key.exec.minWarmupRuns -> 2000,
          Key.exec.maxWarmupRuns -> 5000
          ) in {
            val elements = 1000000
            performance of s"build vectors using $elements elements" in {

                performance of s"Height $height" in {
                    using(sizes(from, to, by)) curve vectorName in { n =>
                        sideeffect = buildVector(n, elements)
                    }
                }
            }
        }
    }

}