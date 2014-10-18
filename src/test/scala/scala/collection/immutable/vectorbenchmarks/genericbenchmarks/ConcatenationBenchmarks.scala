package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.Key

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

abstract class ConcatenationBenchmarks[A] extends BaseVectorBenchmark[A] {
    // Used in immutable.vector to bound the sizes
    def to(n: Int): Int = n

    performanceOfVectors { height =>
        val (from, to_, by) = fromToBy(height)
        // Avoid benchmarking vector concatenation on big immutable.Vector (too slow)

        var sideeffect = 0

        performance of "concatenation" config(
          Key.exec.minWarmupRuns -> 3000,
          Key.exec.maxWarmupRuns -> 6000
          ) in {
            val times = 1000
            performance of s"concat vector of 1024 elements $times times" in {

                for (otherSize <- Seq(512, 1000, 1024)) {
                    val otherVector = tabulatedVector(otherSize)

                    performance of s"Vector_$otherSize ++ vector" in {

                        performance of s"Height $height" in {
                            using(generateVectors(from, to(to_), by)) curve vectorName in { vec =>
                                var i = 0
                                var sum = 0
                                while (i < times) {
                                    val v = plusPlus(otherVector, vec)
                                    sum += v.length
                                    i += 1
                                }
                                sideeffect = sum
                            }

                        }
                    }

                    performance of s"vector ++ Vector$otherSize" in {

                        performance of s"Height $height" in {
                            using(generateVectors(from, to(to_), by)) curve vectorName in { vec =>
                                var i = 0
                                var sum = 0
                                while (i < times) {
                                    val v = plusPlus(vec, otherVector)
                                    sum += v.length
                                    i += 1
                                }
                                sideeffect = sum
                            }
                        }
                    }
                }

            }
        }
    }

}