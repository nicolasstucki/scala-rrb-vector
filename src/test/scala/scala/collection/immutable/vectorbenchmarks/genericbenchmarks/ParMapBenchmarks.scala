package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.parallel.ForkJoinTaskSupport

object ParSupport {

    lazy val pool1 = new scala.concurrent.forkjoin.ForkJoinPool(1)
    lazy val pool2 = new scala.concurrent.forkjoin.ForkJoinPool(2)
    lazy val pool4 = new scala.concurrent.forkjoin.ForkJoinPool(4)
    lazy val pool8 = new scala.concurrent.forkjoin.ForkJoinPool(8)
    lazy val pool16 = new scala.concurrent.forkjoin.ForkJoinPool(16)

    def getTaskSupport(n: Int) = n match {
        case 1 => new ForkJoinTaskSupport(pool1)
        case 2 => new ForkJoinTaskSupport(pool2)
        case 4 => new ForkJoinTaskSupport(pool4)
        case 8 => new ForkJoinTaskSupport(pool8)
        case 16 => new ForkJoinTaskSupport(pool16)
    }

}

abstract class ParMapBenchmarks[A] extends BaseVectorBenchmark[A] {
    self: PerformanceTest =>

    def mapSelfFun(x: A): A

    def mapBenchFun(x: A): A

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0
        val times = 100
        measure method "map" config(
          Key.exec.minWarmupRuns -> 300,
          Key.exec.maxWarmupRuns -> 800
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
        measure method "map" config(
          Key.exec.minWarmupRuns -> 100,
          Key.exec.maxWarmupRuns -> 200
          ) in {

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
            for (threadPoolSize <- Seq(1, 2, 4, 8, 16)) {
                performance of s"par.map into mapBencFun $times times" in {
                    performance of s"$threadPoolSize threads in pool" in {
                        performance of s"Height $height" in {
                            using(generateVectors(from, to, by)) curve vectorName in { vec =>
                                val parvec = vec.par
                                parvec.tasksupport = ParSupport.getTaskSupport(threadPoolSize)
                                var i = 0
                                while (i < times) {
                                    sideeffect = (parvec map mapBenchFun).length
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