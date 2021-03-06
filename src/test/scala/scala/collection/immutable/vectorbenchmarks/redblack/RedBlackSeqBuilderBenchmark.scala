package scala.collection.immutable.vectorbenchmarks.redblack

import scala.collection.immutable.redblack._
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.BuilderBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class RedBlackSeqAbstractBuilderBenchmark[A] extends BuilderBenchmarks[A] with RedBlackSeqBenchmark[A] {
    def buildVector(n: Int): Int = {
        var i = 0
        var b = RedBlackSeq.newBuilder[A]
        val e = element(0)
        while (i < n) {
            b += e
            i += 1

        }
        b.result().length
    }
}

class RedBlackSeqBuilderIntBenchmark extends RedBlackSeqAbstractBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

class RedBlackSeqBuilderStringBenchmark extends RedBlackSeqAbstractBuilderBenchmark[String] with VectorGeneratorType.StringGenerator