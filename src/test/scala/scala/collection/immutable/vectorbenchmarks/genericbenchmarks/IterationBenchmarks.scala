package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter.Key

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class IterationBenchmarks[A] extends BaseVectorBenchmark[A] {

    override val maxHeight: Int = 4

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        performance of "iteration" in {

            performance of "iterator: iterate through all elements" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        val it = vec.iterator
                        var seff = 0
                        while (it.hasNext) {
                            seff = it.next().hashCode()
                        }
                        sideeffect = seff
                    }
                }
            }

            performance of "reverseIterator: iterate through all elements" in {
                performance of s"Height $height" in {
                    using(generateVectors(from, to, by)) curve vectorName in { vec =>
                        val it = vec.reverseIterator
                        var seff = 0
                        while (it.hasNext) {
                            seff = it.next().hashCode()
                        }
                        sideeffect = seff
                    }
                }
            }
        }
    }

}