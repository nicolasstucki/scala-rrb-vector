package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Gen, Key}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

abstract class ConcatenationBenchmarks[A] extends BaseVectorBenchmark[A] {
    // Used in immutable.vector to bound the sizes
    def to(n: Int): Int = n

    override def minHeight = 3

    override def maxHeight = 3

    override def points = super.points / 2

    def generateVectors2(from: Int, to: Int, by: Int): Gen[(Vec, Vec)]

    performanceOfVectors { height =>
        // To avoid benchmarking vector concatenation on big immutable.Vector (too slow)
        val (from, to_, by) = fromToBy(height)

        var sideeffect = 0

        val warmups = 3000

        performance of "concatenation" config(
          Key.exec.minWarmupRuns -> warmups,
          Key.exec.maxWarmupRuns -> 2 * warmups
          ) in {
            performance of s"vector1 ++ vector2" in {
                performance of s"Height $height" in {
                    using(generateVectors2(from, to_, by)) curve vectorName setUp { x: (Vec, Vec) => System.gc()} in { vecs =>
                        val v = plusPlus(vecs._1, vecs._2)
                        sideeffect = v.length
                    }
                }
            }

        }
    }

}