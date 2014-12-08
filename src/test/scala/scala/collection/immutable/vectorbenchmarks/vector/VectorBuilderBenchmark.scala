package scala.collection.immutable.vectorbenchmarks.vector

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.BuilderBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class VectorAbstractBuilderBenchmark[@miniboxed A] extends BuilderBenchmarks[A] with VectorBenchmark[A] {
    def buildVector(n: Int): Int = {
        var i = 0
        var b = Vector.newBuilder[A]
        val e = element(0)
        while (i < n) {
            b += e
            i += 1

        }
        b.result().length
    }
}

class VectorBuilderIntBenchmark extends VectorAbstractBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

class VectorBuilderStringBenchmark extends VectorAbstractBuilderBenchmark[String] with VectorGeneratorType.StringGenerator