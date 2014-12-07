package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Key, PerformanceTest}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

abstract class ParMapBenchmarks[A] extends BaseVectorBenchmark[A] {
    self: PerformanceTest =>

    override val maxHeight: Int = 4

    def mapSelfFun(x: A): A

    def mapBenchFun(x: A): A

    def mapBenchFun2(x: A): A

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "map" in {
            performance of s"map into self (x=>x)" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                        sideeffect = (vec map mapSelfFun).length
                    }
                }
            }

            for (threadPoolSize <- Seq(1, 2, 4, 8)) {
                performance of s"par.map into self (x=>x)" in {
                    performance of s"$threadPoolSize threads in pool" in {
                        performance of s"Height $height" in {
                            using(generateVectors(from, to, by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                                val parvec = vec.par
                                parvec.tasksupport = ParSupport.getTaskSupport(threadPoolSize)
                                sideeffect = (parvec map mapSelfFun).length
                            }
                        }
                    }
                }
            }
        }

        if (height <= 3) {
            measure method "map" in {
                performance of s"map into mapBencFun" in {
                    performance of s"Height $height" in {
                        using(generateVectors(from, to, by)) curve vectorName in { vec =>
                            sideeffect = (vec map mapBenchFun).length
                        }
                    }
                }
                for (threadPoolSize <- Seq(1, 2, 4, 8)) {
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


//            measure method "map" in {
//                performance of s"map into mapBencFun2" in {
//                    performance of s"Height $height" in {
//                        using(generateVectors(from, to, by)) curve vectorName in { vec =>
//                            sideeffect = (vec map mapBenchFun2).length
//                        }
//                    }
//                }
//                for (threadPoolSize <- Seq(1, 2, 4, 8)) {
//                    performance of s"par.map into mapBencFun2" in {
//                        performance of s"$threadPoolSize threads in pool" in {
//                            performance of s"Height $height" in {
//                                using(generateVectors(from, to, by)) curve vectorName in { vec =>
//                                    val parvec = vec.par
//                                    parvec.tasksupport = ParSupport.getTaskSupport(threadPoolSize)
//                                    sideeffect = (parvec map mapBenchFun2).length
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
}