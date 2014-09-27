package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Key, PerformanceTest}


class VectorIterationBenchmarks extends PerformanceTest.OfflineRegressionReport with BaseVectorBenchmark {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        performance of "iteration" config(
          Key.exec.minWarmupRuns -> 300,
          Key.exec.maxWarmupRuns -> 1000
          ) in {

            performance of "iterator: through 1M elements" in {
                def benchmarkFunction(vec: Seq[Int]) = {
                    var i = 0
                    var sum = 0
                    val until = 1000000
                    var it = vec.iterator
                    while (i < until) {
                        if (!it.hasNext)
                            it = vec.iterator
                        sum += it.next()
                        i += 1
                    }
                    sideeffect = sum
                }

                performance of s"Height $height" in {
                    using(vectors(from, to, by)) curve "Vector" in benchmarkFunction
                    using(rbvectors(from, to, by)) curve "rbVector" in benchmarkFunction
                    using(rrbvectors(from, to, by)) curve "rrbVector" in benchmarkFunction
                }
            }

            performance of "reverseIterator: through 1M elements" in {
                def benchmarkFunction(vec: Seq[Int]) = {
                    var i = 0
                    var sum = 0
                    val until = 1000000
                    var it = vec.reverseIterator
                    while (i < until) {
                        if (!it.hasNext)
                            it = vec.reverseIterator
                        sum += it.next()
                        i += 1
                    }
                    sideeffect = sum
                }

                performance of s"Height $height" in {
                    using(vectors(from, to, by)) curve "Vector" in benchmarkFunction
                    using(rbvectors(from, to, by)) curve "rbVector" in benchmarkFunction
                    using(rrbvectors(from, to, by)) curve "rrbVector" in benchmarkFunction
                }
            }

        }
    }
}