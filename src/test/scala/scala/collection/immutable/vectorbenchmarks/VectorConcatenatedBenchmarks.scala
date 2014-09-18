package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Key, PerformanceTest}


class VectorConcatenatedBenchmarks extends PerformanceTest.OfflineReport with BaseVectorBenchmark {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0
        val v1 = Vector(42)
        val v32 = Vector.range(0, 32)
        val v1024 = Vector.range(0, 1024)
        val v32768 = Vector.range(0, 32768)


        measure method "++" config(
          Key.exec.minWarmupRuns -> (if (height <= 2) 3000 else 1000).asInstanceOf[Int],
          Key.exec.maxWarmupRuns -> (if (height <= 2) 5000 else 3000).asInstanceOf[Int]
          ) in {

//            performance of "concat: same size" in {
//                performance of s"Height $height" in {
//                    using(vectors(from, to, by) map (v => (v, Vector(v.iterator)))) curve ("Vector") in {
//                        case (v1, v2) => v1 ++ v2
//                    }
//
//                    using(rbvectors(from, to, by) map (v => (v, rrbvector.Vector(v.iterator)))) curve ("rbVector") in {
//                        case (v1, v2) => v1 ++ v2
//                    }
//                }
//            }
//
//            performance of "concat: _ ++ v1" in {
//                performance of s"Height $height" in {
//                    using(vectors(from, to, by)) curve ("Vector") in {
//                        _ ++ v1
//                    }
//
//                    using(rbvectors(from, to, by)) curve ("rbVector") in {
//                        _ ++ v1
//                    }
//                }
//            }
//
//            performance of "concat: v1 ++ _" in {
//                performance of s"Height $height" in {
//                    using(vectors(from, to, by)) curve ("Vector") in {
//                        v1 ++ _
//                    }
//
//                    using(rbvectors(from, to, by)) curve ("rbVector") in {
//                        v1 ++ _
//                    }
//                }
//            }
//
//            performance of "concat: v32 ++ _" in {
//                performance of s"Height $height" in {
//                    using(vectors(from, to, by)) curve ("Vector") in {
//                        v32 ++ _
//                    }
//
//                    using(rbvectors(from, to, by)) curve ("rbVector") in {
//                        v32 ++ _
//                    }
//                }
//            }
//
//            performance of "concat: _ ++ v32" in {
//                performance of s"Height $height" in {
//                    using(vectors(from, to, by)) curve ("Vector") in {
//                        _ ++ v32
//                    }
//
//                    using(rbvectors(from, to, by)) curve ("rbVector") in {
//                        _ ++ v32
//                    }
//                }
//            }
//
//            performance of "concat: v1024 ++ _" in {
//                performance of s"Height $height" in {
//                    using(vectors(from, to, by)) curve ("Vector") in {
//                        v1024 ++ _
//                    }
//
//                    using(rbvectors(from, to, by)) curve ("rbVector") in {
//                        v1024 ++ _
//                    }
//                }
//            }
//
//            performance of "concat: _ ++ v32768" in {
//                performance of s"Height $height" in {
//                    using(vectors(from, to, by)) curve ("Vector") in {
//                        _ ++ v32768
//                    }
//
//                    using(rbvectors(from, to, by)) curve ("rbVector") in {
//                        _ ++ v32768
//                    }
//                }
//            }

            performance of "concat: _ ++ v32768" in {
                performance of s"Height $height" in {
                    using(vectors(from, to, by)) curve ("Vector") in {
                        _ ++ v32768
                    }

                    using(rbvectors(from, to, by)) curve ("rbVector") in {
                        _ ++ v32768
                    }
                }
            }
        }

    }
}