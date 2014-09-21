package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Key, PerformanceTest}

class VectorApplyBenchmarks extends PerformanceTest.OfflineRegressionReport with BaseVectorBenchmark {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "apply" config(
          Key.exec.minWarmupRuns -> 200,
          Key.exec.maxWarmupRuns -> 1000
          ) in {

            performance of "1M iteration" in {
                def benchmarkFunction(vec: Seq[Int]) = {
                    var i = 0
                    var sum = 0
                    val len = vec.length
                    val until = 1000000
                    while (i < until) {
                        sum += vec.apply(i % len)
                        i += 1
                    }
                    sideeffect = sum
                }

                performance of s"Height $height" in {
                    using(vectors(from, to, by)) curve ("Vector") in (benchmarkFunction _)
                    using(rbvectors(from, to, by)) curve ("rbVector") in (benchmarkFunction _)
                    using(rrbvectors(from, to, by)) curve ("rrbVector") in (benchmarkFunction _)
                }
            }

            performance of "1M reverse iteration" in {
                def benchmarkFunction(vec: Seq[Int]) = {
                    var i = 1000000
                    var sum = 0
                    val len = vec.length
                    while (i > 0) {
                        i -= 1
                        sum += vec.apply(i % len)
                    }
                    sideeffect = sum
                }
                performance of s"Height $height" in {
                    using(vectors(from, to, by)) curve ("Vector") in (benchmarkFunction _)
                    using(rbvectors(from, to, by)) curve ("rbVector") in (benchmarkFunction _)
                    using(rrbvectors(from, to, by)) curve ("rrbVector") in (benchmarkFunction _)
                }
            }

        }
    }
}