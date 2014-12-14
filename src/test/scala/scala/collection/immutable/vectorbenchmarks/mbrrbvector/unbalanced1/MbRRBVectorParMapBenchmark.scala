package scala.collection.immutable.vectorbenchmarks.mbrrbvector.unbalanced1

import scala.collection.immutable.mbrrbvector.MbRRBVector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.ParSupport
import scala.collection.immutable.vectorutils.VectorGeneratorType


class MbRRBVectorParMapIntBenchmark extends BaseVectorBenchmark[Int] with MbRRBVectorAbstractBenchmark[Int] with VectorGeneratorType.IntGenerator {

    final def mapSelfIntFun(x: Int) = x

    final def mapBenchIntFun(x: Int) = {
        mapBenchFunCompute(1)
    }

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "map" in {
            performance of s"map into self (x=>x)" in {
                performance of s"Height $height" in {
                    using(generateIntVectors(from, to, by)) curve vectorName setUp { x: MbRRBVector[Int] => System.gc()} in { vec =>
                        sideeffect = (vec map mapSelfIntFun).length
                    }
                }
            }

            for (threadPoolSize <- Seq(1, 2, 4, 8)) {
                performance of s"par.map into self (x=>x)" in {
                    performance of s"$threadPoolSize threads in pool" in {
                        performance of s"Height $height" in {
                            using(generateIntVectors(from, to, by)) curve vectorName setUp { x: Vec => System.gc()} in { vec =>
                                val parvec = vec.par
                                parvec.tasksupport = ParSupport.getTaskSupport(threadPoolSize)
                                sideeffect = (parvec map mapSelfIntFun).length
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
                        using(generateIntVectors(from, to, by)) curve vectorName in { vec =>
                            sideeffect = (vec map mapBenchIntFun).length
                        }
                    }
                }
                for (threadPoolSize <- Seq(1, 2, 4, 8)) {
                    performance of s"par.map into mapBencFun" in {
                        performance of s"$threadPoolSize threads in pool" in {
                            performance of s"Height $height" in {
                                using(generateIntVectors(from, to, by)) curve vectorName in { vec =>
                                    val parvec = vec.par
                                    parvec.tasksupport = ParSupport.getTaskSupport(threadPoolSize)
                                    sideeffect = (parvec map mapBenchIntFun).length
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
