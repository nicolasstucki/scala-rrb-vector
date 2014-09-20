package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.rbvector.RBVector

class VectorAppendBenchmarks extends PerformanceTest.OfflineRegressionReport with BaseVectorBenchmark {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "append" config(
          Key.exec.minWarmupRuns -> 2000,
          Key.exec.maxWarmupRuns -> 5000
          ) in {

            performance of "append x65" in {

                performance of s"Height $height" in {
                    using(vectors(from, to, by)) curve ("Vector") in { vec =>
                        var i = 0
                        var v = vec
                        while (i < 65) {
                            v = v :+ 0
                            i += 1
                        }
                        sideeffect = v.length
                    }

                    using(rbvectors(from, to, by)) curve ("rbVector") in { vec =>
                        var i = 0
                        var v = vec
                        while (i < 65) {
                            v = v :+ 0
                            i += 1
                        }
                        sideeffect = v.length
                    }

                    using(rrbvectors(from, to, by)) curve ("rrbVector") in { vec =>
                        var i = 0
                        var v = vec
                        while (i < 65) {
                            v = v :+ 0
                            i += 1
                        }
                        sideeffect = v.length
                    }
                }
            }

        }
    }

}