package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

abstract class ParMapBenchmarks[A] extends BaseVectorBenchmark[A] {
    self: PerformanceTest =>

    def mapSelfFun(x: A): A

    def mapBenchFun(x: A): A
    def mapBenchFun2(x: A): A

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        val minWarmupRuns0 = if (height >= 4) 15 else if (height >= 3) 30 else 300
        val maxWarmupRuns0 = if (height >= 4) 40 else if (height >= 3) 80 else 800
        measure method "map" config(
          Key.exec.minWarmupRuns -> minWarmupRuns0,
          Key.exec.maxWarmupRuns -> maxWarmupRuns0
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

        val minWarmupRuns1 = if (height >= 4) 10 else if (height >= 3) 30 else 100
        val maxWarmupRuns1 = if (height >= 4) 25 else if (height >= 3) 60 else 150
        measure method "map" config(
          Key.exec.minWarmupRuns -> minWarmupRuns1,
          Key.exec.maxWarmupRuns -> maxWarmupRuns1
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

        val minWarmupRuns2 = if (height >= 3) 30 else 150
        val maxWarmupRuns2 = if (height >= 3) 150 else 200
        if (height <= 3) {
            measure method "map" config(
              Key.exec.minWarmupRuns -> minWarmupRuns2,
              Key.exec.maxWarmupRuns -> maxWarmupRuns2
              ) in {
                performance of s"map into mapBencFun2" in {
                    performance of s"Height $height" in {
                        using(generateVectors(from, to, by)) curve vectorName in { vec =>
                            sideeffect = (vec map mapBenchFun2).length
                        }
                    }
                }
                for (threadPoolSize <- Seq(1, 2, 4, 8, 16)) {
                    performance of s"par.map into mapBencFun2" in {
                        performance of s"$threadPoolSize threads in pool" in {
                            performance of s"Height $height" in {
                                using(generateVectors(from, to, by)) curve vectorName in { vec =>
                                    val parvec = vec.par
                                    parvec.tasksupport = ParSupport.getTaskSupport(threadPoolSize)
                                    sideeffect = (parvec map mapBenchFun2).length
                                }
                            }
                        }
                    }
                }
            }
        }

    }

}