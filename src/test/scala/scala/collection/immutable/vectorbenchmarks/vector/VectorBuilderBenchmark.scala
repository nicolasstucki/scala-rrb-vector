package scala.collection.immutable.vectorbenchmarks.vector

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.BuilderBenchmarks
import scala.collection.immutable.vectorbenchmarks.VectorBenchmark
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class VectorAbstractBuilderBenchmark[A] extends BuilderBenchmarks[A] with VectorBenchmark[A] {
    def buildVector(n: Int, elems: Int): Int = {
        var i = 0
        var sum = 0
        var b = Vector.newBuilder[A]
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

class VectorBuilderIntBenchmark extends VectorAbstractBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

class VectorBuilderStringBenchmark extends VectorAbstractBuilderBenchmark[String] with VectorGeneratorType.StringGenerator