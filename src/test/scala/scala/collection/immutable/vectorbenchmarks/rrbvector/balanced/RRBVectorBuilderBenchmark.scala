package scala.collection.immutable.vectorbenchmarks.rrbvector.balanced

import org.scalameter.Key

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.BuilderBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType


class RRBVectorBuilderIntBenchmark extends BaseVectorBenchmark[Int] with RRBVectorAbstractBenchmark[Int] with VectorGeneratorType.IntGenerator {
    def buildVector(n: Int): Int = 0
    override def maxHeight: Int = 4

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        var sideeffect = 0

        val warmups = if (height <= 2) 2000 else if (height == 3) 50 else 20
        measure method "builder" config(
          Key.exec.minWarmupRuns -> warmups,
          Key.exec.maxWarmupRuns -> warmups
          ) in {
            performance of s"build vectors of n elements" in {

                performance of s"Height $height" in {
                    using(sizes(from, to, by)) setUp { size => System.gc()} curve vectorName in { n =>
                        var i = 0
                        var b = RRBVector.newBuilder[Int]
                        while (i < n) {
                            b += i
                            i += 1
                        }
                        sideeffect = b.result().length
                    }
                }
            }
        }
    }
}

