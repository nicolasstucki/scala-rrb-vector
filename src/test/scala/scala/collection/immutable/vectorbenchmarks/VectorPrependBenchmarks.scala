package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Key, PerformanceTest}

class VectorPrependBenchmarks extends PerformanceTest.OfflineReport with BaseVectorBenchmark {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0
        val v1 = Vector(42)

        measure method "prepend" config(
          Key.exec.minWarmupRuns -> (if (height <= 2) 1000 else 200).asInstanceOf[Int],
          Key.exec.maxWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int]
          ) in {

            performance of s"Height $height" in {
                using(sizes(from, to, by)) curve ("Vector") in { len =>
                    var i = 0
                    var vector = Vector.empty[Int]
                    while (i < len) {
                        vector = i +: vector
                        i += 1
                    }
                    sideeffect = i
                }

                using(sizes(from, to, by)) curve ("rbVector") in { len =>
                    var i = 0
                    var vector = rrbvector.Vector.empty[Int]
                    while (i < len) {
                        vector = i +: vector
                        i += 1
                    }
                    sideeffect = i
                }
            }
        }
    }
}