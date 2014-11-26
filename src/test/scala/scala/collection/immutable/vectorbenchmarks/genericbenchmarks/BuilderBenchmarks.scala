
package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.Key

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

abstract class BuilderBenchmarks[A] extends BaseVectorBenchmark[A] {

    override val minHeight: Int = 1
    override val maxHeight: Int = 4

    // TODO remove parameter m
    def buildVector(n: Int, m: Int): Int

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "builder" in {
            performance of s"build vectors of n elements" in {

                performance of s"Height $height" in {
                    using(sizes(from, to, by)) setUp { size => System.gc()} curve vectorName in { n =>
                        sideeffect = buildVector(n, n)
                    }
                }
            }
        }
    }

}