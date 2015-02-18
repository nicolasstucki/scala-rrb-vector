package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.{Gen, Key}

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

abstract class Concatenation2Benchmarks[A] extends BaseVectorBenchmark[A] {
    // Used in immutable.vector to bound the sizes
    def to(n: Int): Int = n

    performanceOfVectors { height =>
        // To avoid benchmarking vector concatenation on big immutable.Vector (too slow)
        val (from, to_, by) = fromToBy(height)

        var sideeffect = 0

        val warmups = 4000

        performance of "concatenation2" config(
          Key.exec.minWarmupRuns -> warmups,
          Key.exec.maxWarmupRuns -> 2 * warmups
          ) in {

//            performance of s"vectorLHS ++ vectorRHS fixed sum" in {
//                performance of s"Height $height" in {
//                    using(generateVectorsFixedSum(32*32*32)) curve vectorName setUp { x: (Vec, Vec) => System.gc()} in { vecs =>
//                        val v = plusPlus(vecs._1, vecs._2)
//                        sideeffect = v.length
//                    }
//                }
//            }

            performance of s"vectorLHS ++ vectorRHS fixed LHS" in {
                performance of s"Height $height" in {
                    using(generateVectorsFixedLHS(32*32*16+1, from, to_, by)) curve vectorName setUp { x: (Vec, Vec) => System.gc()} in { vecs =>
                        val v = plusPlus(vecs._1, vecs._2)
                        sideeffect = v.length
                    }
                }
            }

            performance of s"vectorLHS ++ vectorRHS fixed RHS" in {
                performance of s"Height $height" in {
                    using(generateVectorsFixedRHS(32*32*16+1, from, to_, by)) curve vectorName setUp { x: (Vec, Vec) => System.gc()} in { vecs =>
                        val v = plusPlus(vecs._1, vecs._2)
                        sideeffect = v.length
                    }
                }
            }
        }
    }

}