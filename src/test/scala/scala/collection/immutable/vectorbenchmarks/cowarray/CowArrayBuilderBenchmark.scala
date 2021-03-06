package scala.collection.immutable.vectorbenchmarks.cowarray

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.BuilderBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

import scala.collection.immutable.cowarray._

abstract class CowArrayAbstractBuilderBenchmark[A] extends BuilderBenchmarks[A] with CowArrayBenchmark[A] {
    def buildVector(n: Int): Int = {
        var i = 0
        var b = CowArray.newBuilder[A]
        val e = element(0)
        while (i < n) {
            b += e
            i += 1

        }
        b.result().length
    }
}

class CowArrayBuilderIntBenchmark extends CowArrayAbstractBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

class CowArrayBuilderStringBenchmark extends CowArrayAbstractBuilderBenchmark[String] with VectorGeneratorType.StringGenerator


abstract class CowArrayAbstractBuilderHintedBenchmark[A] extends BuilderBenchmarks[A] with CowArrayBenchmark[A] {

    override def vectorName = super.vectorName + "Hinted"

    def buildVector(n: Int): Int = {
        var i = 0
        var b = CowArray.newBuilder[A]
        b.sizeHint(n)
        val e = element(0)
        while (i < n) {
            b += e
            i += 1

        }
        b.result().length
    }
}

class CowArrayBuilderHintedIntBenchmark extends CowArrayAbstractBuilderHintedBenchmark[Int] with VectorGeneratorType.IntGenerator

class CowArrayBuilderHintedStringBenchmark extends CowArrayAbstractBuilderHintedBenchmark[String] with VectorGeneratorType.StringGenerator