package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Key, PerformanceTest}

class VectorApplyBenchmarks extends PerformanceTest.OfflineReport with BaseVectorBenchmark {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0
        val v1 = Vector(42)


        measure method "apply" config(
          Key.exec.minWarmupRuns -> (if (height <= 2) 1000 else 200).asInstanceOf[Int],
          Key.exec.maxWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int]
          ) in {

            performance of "3x iterations" in {
                performance of s"Height $height" in {

                    using(vectors(from, to, by)) curve ("Vector") in { xs =>
                        var i = 0
                        var sum = 0
                        val len = xs.length
                        val until = len * 3
                        while (i < len) {
                            sum += xs.apply(i % len)
                            i += 1
                        }
                        sideeffect = sum
                    }

                    using(rbvectors(from, to, by)) curve ("rbVector") in { xs =>
                        var i = 0
                        var sum = 0
                        val len = xs.length
                        val until = len * 3
                        while (i < until) {
                            sum += xs.apply(i % len)
                            i += 1
                        }
                        sideeffect = sum
                    }
                }
            }

            performance of "3x reverse iteration" in {
                performance of s"Height $height" in {
                    using(vectors(from, to, by)) curve ("Vector") in { xs =>
                        val len = xs.length
                        var i = len * 3 - 1
                        var sum = 0
                        while (i >= 0) {
                            sum += xs.apply(i % len)
                            i -= 1
                        }
                        sideeffect = sum
                    }

                    using(rbvectors(from, to, by)) curve ("rbVector") in { xs =>
                        val len = xs.length
                        var i = len * 3 - 1
                        var sum = 0
                        while (i >= 0) {
                            sum += xs.apply(i % len)
                            i -= 1
                        }
                        sideeffect = sum
                    }
                }
            }

            performance of "iteration 10k elements" in {
                val end = 10000
                performance of s"Height $height" in {
                    using(vectors(from, to, by)) curve ("Vector") in { xs =>
                        var i = 0
                        var sum = 0
                        val len = xs.length
                        val until = end
                        while (i < until) {
                            sum += xs.apply(i % len)
                            i += 1
                        }
                        sideeffect = sum
                    }

                    using(rbvectors(from, to, by)) curve ("rbVector") in { xs =>
                        var i = 0
                        var sum = 0
                        val len = xs.length
                        val until = end
                        while (i < until) {
                            sum += xs.apply(i % len)
                            i += 1
                        }
                        sideeffect = sum
                    }
                }
            }
        }
    }
}