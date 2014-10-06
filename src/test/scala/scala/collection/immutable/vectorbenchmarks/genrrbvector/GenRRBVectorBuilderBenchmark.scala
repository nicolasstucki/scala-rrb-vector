package scala.collection.immutable.vectorbenchmarks.genrrbvector

import scala.collection.immutable.genrrbvector.GenRRBVector
import scala.collection.immutable.vectorbenchmarks.GenRRBVectorBenchmark
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.BuilderBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class GenRRBVectorAbstractBuilderBenchmark[A] extends BuilderBenchmarks[A] with GenRRBVectorBenchmark[A] {
    def buildVector(n: Int, elems: Int): Int = {
        var i = 0
        var sum = 0
        var b = GenRRBVector.newBuilder[A]
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

class GenRRBVectorBuilderIntBenchmark extends GenRRBVectorAbstractBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

class GenRRBVectorBuilderAnyRefBenchmark extends GenRRBVectorAbstractBuilderBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator