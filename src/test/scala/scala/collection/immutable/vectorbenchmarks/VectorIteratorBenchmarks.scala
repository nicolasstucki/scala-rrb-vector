package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Key, PerformanceTest}

class VectorIteratorBenchmarks extends PerformanceTest.OfflineReport with BaseVectorBenchmark {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0
        val v1 = Vector(42)

        measure method "iterator iteration" config(
          Key.exec.minWarmupRuns -> (if (height <= 2) 1000 else 200).asInstanceOf[Int],
          Key.exec.maxWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int]
          ) in {
            performance of s"Height $height" in {
                using(vectors(from, to, by)) curve ("Vector") in { vec =>
                    val it = vec.iterator
                    var value = 0
                    while (it.hasNext) {
                        value = it.next()
                    }
                    sideeffect = value
                }

                using(rbvectors(from, to, by)) curve ("rbVector") in { vec =>
                    val it = vec.iterator
                    var value = 0
                    while (it.hasNext) {
                        value = it.next()
                    }
                    sideeffect = value
                }
            }
        }

        measure method "reverseIterator iteration" config(
          Key.exec.minWarmupRuns -> (if (height <= 2) 1000 else 200).asInstanceOf[Int],
          Key.exec.maxWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int]
          ) in {
            performance of s"Height $height" in {
                using(vectors(from, to, by)) curve ("Vector") in { vec =>
                    val it = vec.iterator
                    var value = 0
                    while (it.hasNext) {
                        value = it.next()
                    }
                    sideeffect = value
                }

                using(rbvectors(from, to, by)) curve ("rbVector") in { vec =>
                    val it = vec.iterator
                    var value = 0
                    while (it.hasNext) {
                        value = it.next()
                    }
                    sideeffect = value
                }
            }
        }

    }
}
