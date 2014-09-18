package scala.collection.immutable.vectorbenchmarks

import org.scalameter.PerformanceTest

class VectorUpdatedBenchmarks extends PerformanceTest.OfflineReport with BaseVectorBenchmark {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0
        val v1 = Vector(42)
        /*
        measure method "updated" config(
          exec.minWarmupRuns -> (if (height <= 2) 1000 else 200).asInstanceOf[Int],
          exec.maxWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int]
          ) in {

            performance of s"Height $height" in {
                using(vectors(from, to, by)) curve ("Vector") in { xs =>
                    var i = 0
                    var sum = 0
                    val len = xs.length
                    val until = len * 3
                    while (i < until) {
                        xs.updated(i % len, i)
                        i += 1
                    }
                    sideeffect = sum
                }
                // TODO rbVector updated is currently using the builder to reconstruct the whole vector
                using(rbvectors(from, to, by)) curve ("rbVector") in { xs =>
                    var i = 0
                    var sum = 0
                    val len = xs.length
                    val until = len * 3
                    while (i < until) {
                        xs.updated(i % len, i)
                        i += 1
                    }
                    sideeffect = sum
                }
            }
        }
        */
    }
}