package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class ParMapBenchmarks[A] extends BaseVectorBenchmark[A] {
    self: PerformanceTest =>

    def mapSelfFun(x: A): A

    def mapBenchFun(x: A): A

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0
        val times = 10
        measure method "map" config(
          Key.exec.minWarmupRuns -> 1000,
          Key.exec.maxWarmupRuns -> 5000
          ) in {
            performance of s"map into self (x=>x) $times times" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        var i = 0
                        while (i < times) {
                            sideeffect = (vec map mapSelfFun).length
                            i += 1
                        }
                    }
                }
            }
            for (cores <- Seq(1, 2, 4, 8)) {
                performance of s"par.map into self (x=>x) $times times" in {
                    performance of s"$cores cores" config (
                      Key.machine.cores -> cores
                      ) in {
                        performance of s"Height $height" in {
                            using(generateVectors(from, to, by)) curve vectorName in { vec =>
                                var i = 0
                                while (i < times) {
                                    sideeffect = (vec.par map mapSelfFun).length
                                    i += 1
                                }
                            }
                        }
                    }
                }
            }

            performance of s"map into mapBencFun $times times" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        var i = 0
                        while (i < times) {
                            sideeffect = (vec map mapBenchFun).length
                            i += 1
                        }
                    }
                }
            }
            for (cores <- Seq(1, 2, 4, 8)) {
                performance of s"par.map into mapBencFun $times times" in {
                    performance of s"$cores cores" config (
                      Key.machine.cores -> cores
                      ) in {
                        performance of s"Height $height" in {
                            using(generateVectors(from, to, by)) curve vectorName in { vec =>
                                var i = 0
                                while (i < times) {
                                    sideeffect = (vec.par map mapBenchFun).length
                                    i += 1
                                }
                            }
                        }
                    }
                }
            }
        }

    }

}