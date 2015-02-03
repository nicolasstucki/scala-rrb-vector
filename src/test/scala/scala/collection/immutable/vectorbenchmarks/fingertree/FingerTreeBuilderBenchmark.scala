package scala.collection.immutable.vectorbenchmarks.fingertree

import scala.collection.immutable.fingertree._
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.BuilderBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class FingerTreeSeqAbstractBuilderBenchmark[A] extends BuilderBenchmarks[A] with FingerTreeBenchmark[A] {
    def buildVector(n: Int): Int = {
        var i = 0
        var b = FingerTreeSeq.newBuilder[A]
        val e = element(0)
        while (i < n) {
            b += e
            i += 1

        }
        b.result().length
    }
}

class FingerTreeSeqBuilderIntBenchmark extends FingerTreeSeqAbstractBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

class FingerTreeSeqBuilderStringBenchmark extends FingerTreeSeqAbstractBuilderBenchmark[String] with VectorGeneratorType.StringGenerator