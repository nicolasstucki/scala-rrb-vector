package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.Key

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark

abstract class UpdateBenchmarks[A] extends BaseVectorBenchmark[A] {
    def to(n: Int): Int = n

    performanceOfVectors { height =>
        val (from, _to, by) = fromToBy(height)

        var sideeffect = 0

        measure method "upadte" config(
          Key.exec.minWarmupRuns -> 1500,
          Key.exec.maxWarmupRuns -> 2000) in {

            performance of "10k iteration" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to(_to), by)) curve vectorName in { vec =>
                        var i = 0
                        var v: IndexedSeq[A] = vec
                        val elem = vec(0)
                        val len = vec.length
                        val until = 10000
                        while (i < until) {
                            v = v.updated(i % len, elem)
                            i += 1
                        }
                        sideeffect = v.length
                    }
                }
            }

            //            performance of "10k reverse iteration" in {
            //                performance of s"Height $height" in {
            //                    using(generateVectors(from, to(_to), by)) curve vectorName in { vec =>
            //                        var i = 10000
            //                        var v: IndexedSeq[A] = vec
            //                        val elem = vec(0)
            //                        val len = vec.length
            //                        while (i > 0) {
            //                            i -= 1
            //                            v = v.updated(i % len, elem)
            //                        }
            //                        sideeffect = v.length
            //                    }
            //                }
            //            }

            def benchmarkFunctionPseudoRandom[Vec <: IndexedSeq[A]](vec: Vec, seed: Int) = {
                val rnd = new scala.util.Random(seed)
                var i = 0
                var v: IndexedSeq[A] = vec
                val elem = vec(0)
                val len = vec.length
                while (i < 10000) {
                    v = v.updated(rnd.nextInt(len), elem)
                    i += 1
                }
                sideeffect = v.length
            }

            performance of "10k pseudo-random indices (seed=42)" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to(_to), by)) curve vectorName in (benchmarkFunctionPseudoRandom(_, 42))
                }
            }

            //            performance of "10k pseudo-random indices (seed=274181)" in {
            //                performance of s"Height $height" in {
            //                    using(generateVectors(from, to, by)) curve vectorName in (benchmarkFunctionPseudoRandom(_, 274181))
            //                }
            //            }
            //
            //            performance of "10k pseudo-random indices (seed=53426)" in {
            //                performance of s"Height $height" in {
            //                    using(generateVectors(from, to, by)) curve vectorName in (benchmarkFunctionPseudoRandom(_, 53426))
            //                }
            //            }
        }
    }
}