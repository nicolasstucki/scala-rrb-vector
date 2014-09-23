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

            performance of s"append n times" in {

                performance of s"Height $height" in {
                    using(sizes(from, to, by)) curve ("Vector") in { n =>
                        var i = 0
                        var v = Vector.empty[Int]
                        while (i < n) {
                            v = v :+ 0
                            i += 1
                        }
                        sideeffect = v.length
                    }

                    using(sizes(from, to, by)) curve ("rbVector") in { n =>
                        var i = 0
                        var v = RBVector.empty[Int]
                        while (i < n) {
                            v = v :+ 0
                            i += 1
                        }
                        sideeffect = v.length
                    }

                    /* The operation :+ has not been implemented in RRBVector */
                    //                    using(sizes(from, to, by)) curve ("rrbVector") in { n =>
                    //                         var i = 0
                    //                    var v = RRBVector.empty[Int]
                    //                    while (i < n) {
                    //                        v = v :+ 0
                    //                        i += 1
                    //                    }
                    //                    sideeffect = v.length
                    //                    }
                }
            }

            performance of s"append n times using builder" in {

                performance of s"Height $height" in {
                    using(sizes(from, to, by)) curve ("Vector") in { n =>
                        var i = 0
                        var v = Vector.newBuilder[Int]
                        while (i < n) {
                            v += 0
                            i += 1
                        }
                        sideeffect = v.result().length
                    }

                    using(sizes(from, to, by)) curve ("rbVector") in { n =>
                        var i = 0
                        var v = RBVector.newBuilder[Int]
                        while (i < n) {
                            v += 0
                            i += 1
                        }
                        sideeffect = v.result().length
                    }

                    using(sizes(from, to, by)) curve ("rrbVector") in { n =>
                        var i = 0
                        var v = RRBVector.newBuilder[Int]
                        while (i < n) {
                            v += 0
                            i += 1
                        }
                        sideeffect = v.result().length
                    }
                }
            }
        }
    }

}