package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class ParMapBenchmarks[A] extends BaseVectorBenchmark[A] {
    self: PerformanceTest =>

    def mapSelfFun(x: A): A

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "map" config(
          Key.exec.minWarmupRuns -> 100,
          Key.exec.maxWarmupRuns -> 500
          ) in {
            performance of s"map into self (x=>x)" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        sideeffect = (vec map mapSelfFun).length
                    }
                }
            }
            for (cores <- Seq(1, 2, 4, 8)) {
                performance of s"par.map into self (x=>x)" in {
                    performance of s"$cores cores" config (
                      Key.machine.cores -> cores
                      ) in {
                        performance of s"Height $height" in {
                            using(generateVectors(from, to, by)) curve vectorName in { vec =>
                                sideeffect = (vec.par map mapSelfFun).length
                            }
                        }
                    }
                }
            }
        }

    }


}