package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.Key

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

abstract class ConcatenationBenchmarks[A] extends BaseVectorBenchmark[A] {
    // Used in immutable.vector to bound the sizes
    def to(n: Int): Int = n

    performanceOfVectors { height =>
        // To avoid benchmarking vector concatenation on big immutable.Vector (too slow)
        val (from, to_, by) = fromToBy(height)

        var sideeffect = 0

        val warmups = 3000

        performance of "concatenation" config(
          Key.exec.minWarmupRuns -> warmups,
          Key.exec.maxWarmupRuns -> warmups
          ) in {
            for (otherSize <- Seq(10000, 100)) {
                val otherVector = tabulatedVector(otherSize)
                performance of s"Vector_$otherSize ++ vector" in {
                    performance of s"Height $height" in {
                        using(generateVectors(from, to(to_), by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                            val v = plusPlus(otherVector, vec)
                            sideeffect = v.length
                        }
                    }
                }

                performance of s"vector ++ Vector$otherSize" in {
                    performance of s"Height $height" in {
                        using(generateVectors(from, to(to_), by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                            val v = plusPlus(vec, otherVector)
                            sideeffect = v.length
                        }
                    }
                }
            }
        }
    }

}