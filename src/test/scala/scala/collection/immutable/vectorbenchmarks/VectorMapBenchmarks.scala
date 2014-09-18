package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Key, PerformanceTest}

class VectorMapBenchmarks extends PerformanceTest.OfflineReport with BaseVectorBenchmark {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0
        val v1 = Vector(42)

        measure method "map" config(
          Key.exec.minWarmupRuns -> (if (height <= 2) 1000 else 200).asInstanceOf[Int],
          Key.exec.maxWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int]
          ) in {
            performance of s"Height $height" in {
                using(vectors(from, to, by)) curve ("Vector") in {
                    _.map(_ % 2 == 0)
                }

                using(rbvectors(from, to, by)) curve ("rbVector") in {
                    _.map(_ % 2 == 0)
                }
            }
        }
    }
}
