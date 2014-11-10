package scala.collection.immutable.vectorbenchmarks.rrbvector.xunbalanced

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.BuilderBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class RRBVectorAbstractBuilderBenchmark[A] extends BuilderBenchmarks[A] with RRBVectorAbstractBenchmark[A] {
    def buildVector(n: Int, elems: Int): Int = {
        var i = 0
        var sum = 0
        var b = RRBVector.newBuilder[A]
        val e = element(0)
        while (i < elems) {
            val m = math.min(n, elems - i)
            var j = 0
            while (j < m) {
                b += e
                i += 1
                j += 1
            }
            sum = b.result().length
            b.clear()
        }
        sum
    }
}

class RRBVectorBuilderIntBenchmark extends RRBVectorAbstractBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

class RRBVectorBuilderStringBenchmark extends RRBVectorAbstractBuilderBenchmark[String] with VectorGeneratorType.StringGenerator