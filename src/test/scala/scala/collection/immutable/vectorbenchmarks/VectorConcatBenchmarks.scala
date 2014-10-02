package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.rrbvector.RRBVector

class VectorConcatBenchmarks extends PerformanceTest.OfflineRegressionReport with BaseVectorBenchmark {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        performance of "concatenation" config(
          Key.exec.minWarmupRuns -> 3000,
          Key.exec.maxWarmupRuns -> 6000
          ) in {
            val times = 1000
            performance of s"concat vector of 1024 elements $times times, $times times" in {


                for (otherSize <- Seq(512, 1000, 1024)) {
                    val otherVector = Vector.range(0, otherSize)
                    val otherRrbvector = RRBVector.range(0, otherSize)

                    performance of s"Vector_$otherSize ++ vector" in {

                        performance of s"Height $height" in {
                            using(vectors(from, math.min(to, 10000), by)) curve "Vector" in { vec =>
                                var i = 0
                                var sum = 0
                                while (i < times) {
                                    val v = otherVector ++ vec
                                    sum += v.length
                                    i += 1
                                }
                                sideeffect = sum
                            }

                            using(rrbVectors(from, to, by)) curve "rrbVector" in { vec =>
                                var i = 0
                                var sum = 0
                                while (i < times) {
                                    val v = otherRrbvector ++ vec
                                    sum += v.length
                                    i += 1
                                }
                                sideeffect = sum
                            }
                        }
                    }

                    performance of s"vector ++ Vector$otherSize" in {

                        performance of s"Height $height" in {
                            using(vectors(from, math.min(to, 3000), by)) curve "Vector" in { vec =>
                                var i = 0
                                var sum = 0
                                while (i < times) {
                                    val v = vec ++ otherVector
                                    sum += v.length
                                    i += 1
                                }
                                sideeffect = sum
                            }

                            using(rrbVectors(from, to, by)) curve "rrbVector" in { vec =>
                                var i = 0
                                var sum = 0
                                while (i < times) {
                                    val v = vec ++ otherRrbvector
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