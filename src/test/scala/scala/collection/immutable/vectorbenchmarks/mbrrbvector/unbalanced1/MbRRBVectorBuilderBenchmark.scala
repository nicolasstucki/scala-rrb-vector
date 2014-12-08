package scala.collection.immutable.vectorbenchmarks.mbrrbvector.unbalanced1

import scala.collection.immutable.mbrrbvector.MbRRBVector
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.BuilderBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class MbRRBVectorAbstractBuilderBenchmark[A] extends BuilderBenchmarks[A] with MbRRBVectorAbstractBenchmark[A] {
    def buildVector(n: Int): Int = {
        var i = 0
        var b = MbRRBVector.newBuilder[A]
        val e = element(0)
        while (i < n) {
            b += e
            i += 1
        }
        b.result().length
    }
}

class MbRRBVectorBuilderIntBenchmark extends MbRRBVectorAbstractBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

class MbRRBVectorBuilderStringBenchmark extends MbRRBVectorAbstractBuilderBenchmark[String] with VectorGeneratorType.StringGenerator