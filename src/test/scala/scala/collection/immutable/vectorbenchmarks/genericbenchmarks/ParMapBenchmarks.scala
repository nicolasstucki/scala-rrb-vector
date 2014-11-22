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

        measure method "map" config(
          Key.exec.minWarmupRuns -> 300,
          Key.exec.maxWarmupRuns -> 800
          ) in {
            val times = 100

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
            for (threadPoolSize <- Seq(1, 2, 4, 8, 16)) {
                performance of s"par.map into self (x=>x) $times times" in {
                    performance of s"$threadPoolSize threads in pool" in {
                        performance of s"Height $height" in {
                            using(generateVectors(from, to, by)) curve vectorName in { vec =>
                                val parvec = vec.par
                                parvec.tasksupport = ParSupport.getTaskSupport(threadPoolSize)
                                var i = 0
                                while (i < times) {
                                    sideeffect = (parvec map mapSelfFun).length
                                    i += 1
                                }
                            }
                        }
                    }
                }
            }

        }

        val minWarmupRuns = if (height >= 3) 30 else 100
        measure method "map" config(
          Key.exec.minWarmupRuns -> minWarmupRuns,
          Key.exec.maxWarmupRuns -> 150
          ) in {
            performance of s"map into mapBencFun" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        sideeffect = (vec map mapBenchFun).length
                    }
                }
            }
            for (threadPoolSize <- Seq(1, 2, 4, 8, 16)) {
                performance of s"par.map into mapBencFun" in {
                    performance of s"$threadPoolSize threads in pool" in {
                        performance of s"Height $height" in {
                            using(generateVectors(from, to, by)) curve vectorName in { vec =>
                                val parvec = vec.par
                                parvec.tasksupport = ParSupport.getTaskSupport(threadPoolSize)
                                sideeffect = (parvec map mapBenchFun).length
                            }
                        }
                    }
                }
            }
        }

    }

}