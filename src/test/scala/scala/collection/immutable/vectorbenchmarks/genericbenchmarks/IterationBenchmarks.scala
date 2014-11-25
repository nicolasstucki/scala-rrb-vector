package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.Key

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class IterationBenchmarks[A] extends BaseVectorBenchmark[A] {

    override val minHeight: Int = 2
    override val maxHeight: Int = 4
    override val independentSamples = 32
    override val benchRunsPerSample = 32

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0


        performance of "iteration" config(
          Key.exec.minWarmupRuns -> 500,
          Key.exec.maxWarmupRuns -> 2000
          ) in {

            performance of "iterator: iterate through all elements" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        val it = vec.iterator
                        while (it.hasNext) {
                            it.next()
                        }
                        sideeffect = it.hashCode()
                    }
                }
            }

            performance of "reverseIterator: iterate through all elements" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        val it = vec.reverseIterator
                        while (it.hasNext) {
                            it.next()
                        }
                        sideeffect = it.hashCode()
                    }
                }
            }
        }
    }

}