package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Key, PerformanceTest}

class VectorApplyBenchmarks extends PerformanceTest.OfflineReport with BaseVectorBenchmark {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "apply" in {

            performance of "3x iterations" in {
                performance of s"Height $height" in {

                    using(vectors(from, to, by)) curve ("Vector") config(
                      Key.exec.minWarmupRuns -> (if (height == 1) 1200 else if (height == 2) 600 else 500).asInstanceOf[Int],
                      Key.exec.maxWarmupRuns -> (if (height == 1) 1500 else if (height == 2) 1000 else 1000).asInstanceOf[Int]
                      ) in { xs =>
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

                    using(rbvectors(from, to, by)) curve ("rbVector") config(
                      Key.exec.minWarmupRuns -> (if (height == 1) 3000 else if (height == 2) 3000 else 2000).asInstanceOf[Int],
                      Key.exec.maxWarmupRuns -> (if (height == 1) 5000 else if (height == 2) 5000 else 4000).asInstanceOf[Int]
                      ) in { xs =>
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

                    using(vectors(from, to, by)) curve ("Vector") config(
                      Key.exec.minWarmupRuns -> (if (height == 1) 1200 else if (height == 2) 600 else 110).asInstanceOf[Int],
                      Key.exec.maxWarmupRuns -> (if (height == 1) 1500 else if (height == 2) 1000 else 200).asInstanceOf[Int]
                      ) in { xs =>
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

            performance of "iteration 100k elements" in {
                val end = 100000
                performance of s"Height $height" in {

                    using(vectors(from, to, by)) curve ("Vector") config(
                      Key.exec.minWarmupRuns -> (if (height == 1) 3000 else if (height == 2) 3000 else 2000).asInstanceOf[Int],
                      Key.exec.maxWarmupRuns -> (if (height == 1) 5000 else if (height == 2) 5000 else 4000).asInstanceOf[Int]
                      ) in { xs =>
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

                    using(rbvectors(from, to, by)) curve ("rbVector") config(
                      Key.exec.minWarmupRuns -> (if (height == 1) 1000 else if (height == 2) 750 else 500).asInstanceOf[Int],
                      Key.exec.maxWarmupRuns -> (if (height == 1) 2000 else if (height == 2) 1000 else 750).asInstanceOf[Int]
                      ) in { xs =>
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