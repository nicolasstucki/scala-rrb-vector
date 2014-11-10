package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.Key

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class IterationBenchmarks[A] extends BaseVectorBenchmark[A] {

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        if (height > 1) {
            performance of "iteration" config(
              Key.exec.minWarmupRuns -> 500,
              Key.exec.maxWarmupRuns -> 1200
              ) in {

                performance of "iterator: through 1M elements" in {
                    performance of s"Height $height" in {
                        using(generateVectors(from, to, by)) curve vectorName in { vec =>
                            var i = 0
                            val until = 1000000
                            var it = vec.iterator
                            while (i < until) {
                                if (!it.hasNext)
                                    it = vec.iterator
                                it.next()
                                i += 1
                            }
                            sideeffect = it.hashCode()
                        }
                    }
                }

                performance of "reverseIterator: through 1M elements" in {
                    performance of s"Height $height" in {
                        using(generateVectors(from, to, by)) curve vectorName in { vec =>
                            var i = 0
                            val until = 1000000
                            var it = vec.reverseIterator
                            while (i < until) {
                                if (!it.hasNext)
                                    it = vec.reverseIterator
                                it.next()
                                i += 1
                            }
                            sideeffect = it.hashCode()
                        }
                    }
                }
            }
        }
    }
}