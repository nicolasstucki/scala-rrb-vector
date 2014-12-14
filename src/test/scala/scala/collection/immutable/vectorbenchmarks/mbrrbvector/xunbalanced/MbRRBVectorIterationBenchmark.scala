package scala.collection.immutable.vectorbenchmarks.mbrrbvector.xunbalanced

import org.scalameter.Key

import scala.collection.immutable.vectorutils.VectorGeneratorType

class MbRRBVectorIterationIntBenchmark extends MbRRBVectorAbstractBenchmark[Int] with VectorGeneratorType.IntGenerator {
    override val maxHeight: Int = 4

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        val warmups = if (height == 1) 500 else if (height == 2) 300 else if (height == 3) 150 else 20
        performance of "iteration" config(
          Key.exec.minWarmupRuns -> warmups,
          Key.exec.minWarmupRuns -> warmups
          ) in {

            performance of "iterator: iterate through all elements" in {
                performance of s"Height $height" in {
                    using(generateIntVectors(from, to, by)) curve vectorName in { vec =>
                        val it = vec.iterator
                        var sum = 0
                        while (it.hasNext) {
                            sum += it.next()
                        }
                        sideeffect = sum
                    }
                }
            }

            performance of "reverseIterator: iterate through all elements" in {
                performance of s"Height $height" in {
                    using(generateIntVectors(from, to, by)) curve vectorName in { vec =>
                        val it = vec.reverseIterator
                        var sum = 0
                        while (it.hasNext) {
                            sum += it.next()
                        }
                        sideeffect = sum
                    }
                }
            }
        }
    }
}