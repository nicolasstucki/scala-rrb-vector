package scala.collection.immutable.vectorbenchmarks.mbrrbvector.balanced

import org.scalameter.Key

import scala.collection.immutable.mbrrbvector.MbRRBVector
import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.BuilderBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType



class MbRRBVectorBuilderIntBenchmark extends/* MbRRBVectorAbstractBuilderBenchmark[Int] */ BaseVectorBenchmark[Int] with MbRRBVectorAbstractBenchmark[Int] with VectorGeneratorType.IntGenerator {
    def buildVector(n: Int): Int = 0
    override def maxHeight: Int = 4
    var sideeffect = 0

    def benchmark(n: Int) = {
        var i = 0
        var b = MbRRBVector.newBuilder[Int]
        while (i < n) {
            b += i
            i += 1
        }
        sideeffect = b.result().length
    }

    performanceOfVectors { height =>
        val (from, to, by) = fromToBy(height)

        val warmups = if (height <= 2) 2000 else if (height == 3) 50 else 20
        measure method "builder" config(
          Key.exec.minWarmupRuns -> warmups,
          Key.exec.maxWarmupRuns -> warmups
          ) in {
            performance of s"build vectors of n elements" in {

                performance of s"Height $height" in {
                    using(sizes(from, to, by)) setUp { size => System.gc()} curve vectorName in { n =>
                        benchmark(n)
                    }
                }
            }
        }
    }
}

