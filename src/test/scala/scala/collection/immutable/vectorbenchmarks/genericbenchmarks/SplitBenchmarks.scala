package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.Key


import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

abstract class SplitBenchmarks[A] extends BaseVectorBenchmark[A] {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "take" config(
          Key.exec.minWarmupRuns -> 500,
          Key.exec.maxWarmupRuns -> 1000) in {

            performance of s"take half" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                        sideeffect = take(vec, vec.length / 2).length
                    }
                }
            }

            performance of s"take quarter" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                        sideeffect = take(vec, vec.length / 4).length
                    }
                }
            }

        }


        measure method "drop" config(
          Key.exec.minWarmupRuns -> 500,
          Key.exec.maxWarmupRuns -> 1000) in {

            performance of s"drop half" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                        sideeffect = drop(vec, vec.length / 2).length
                    }
                }
            }

            performance of s"drop quarter" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                        sideeffect = drop(vec, vec.length / 4).length
                    }
                }
            }
        }
    }
}