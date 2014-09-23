package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.rbvector.RBVector
import scala.collection.immutable.rrbvector.RRBVector

class VectorAppendBenchmarks extends PerformanceTest.OfflineRegressionReport with BaseVectorBenchmark {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "append" config(
          Key.exec.minWarmupRuns -> 2000,
          Key.exec.maxWarmupRuns -> 5000
          ) in {
            val times = 10000
            performance of s"append 32 elements, $times times" in {

                performance of s"Height $height" in {
                    using(vectors(from, to, by)) curve ("Vector") in { vec =>
                        var i = 0
                        var v = vec
                        var sum = 0
                        while (i < times) {
                            v = vec :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7
                            sum += v.length
                            i += 1
                        }
                        sideeffect = sum
                    }

                    using(rbvectors(from, to, by)) curve ("rbVector") in { vec =>
                        var i = 0
                        var v = vec
                        var sum = 0
                        while (i < times) {
                            v = vec :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7
                            sum += v.length
                            i += 1
                        }
                        sideeffect = sum
                    }

                    /* The operation :+ has not been implemented in RRBVector */
                    //                    using(rrbvectors(from, to, by)) curve ("rrbVector") in { vec =>
                    //                    var i = 0
                    //                    var v = vec
                    //                    var sum = 0
                    //                    while (i < times) {
                    //                        v = vec :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7
                    //                        sum += v.length
                    //                        i += 1
                    //                    }
                    //                    sideeffect = sum
                    //                    }
                }
            }
        }
    }

}