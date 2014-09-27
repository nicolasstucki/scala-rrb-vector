
package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.rbvector.RBVector
import scala.collection.immutable.rrbvector.RRBVector

class VectorBuilderBenchmarks extends PerformanceTest.OfflineRegressionReport with BaseVectorBenchmark {

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
                    using(sizes(from, to, by)) curve "Vector" in { n =>
                        var i = 0
                        var sum = 0
                        while (i < elements) {
                            var b = Vector.newBuilder[Int]
                            for (j <- 0 until math.min(n, elements - i)) {
                                b += i
                                i += 1
                            }
                            sum = b.result().length
                        }
                        sideeffect = sum
                    }

                    using(sizes(from, to, by)) curve "rbVector" in { n =>
                        var i = 0
                        var sum = 0
                        while (i < elements) {
                            var b = RBVector.newBuilder[Int]
                            for (j <- 0 until math.min(n, elements - i)) {
                                b += i
                                i += 1
                            }
                            sum = b.result().length
                        }
                        sideeffect = sum
                    }

                    using(sizes(from, to, by)) curve "rrbVector" in { n =>
                        var i = 0
                        var sum = 0
                        while (i < elements) {
                            var b = RRBVector.newBuilder[Int]
                            for (j <- 0 until math.min(n, elements - i)) {
                                b += i
                                i += 1
                            }
                            sum = b.result().length
                        }
                        sideeffect = sum
                    }

                }
            }
        }
    }

}