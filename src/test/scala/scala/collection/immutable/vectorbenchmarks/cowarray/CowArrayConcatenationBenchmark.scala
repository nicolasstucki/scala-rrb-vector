package scala.collection.immutable.vectorbenchmarks.cowarray

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.ConcatenationBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class CowArrayAbstractConcatenationBenchmark[A] extends ConcatenationBenchmarks[A] with CowArrayBenchmark[A] {

//    override def to(n: Int): Int = math.min(n, 20000)
}

class CowArrayConcatenationIntBenchmark extends CowArrayAbstractConcatenationBenchmark[Int] with VectorGeneratorType.IntGenerator

class CowArrayConcatenationStringBenchmark extends CowArrayAbstractConcatenationBenchmark[String] with VectorGeneratorType.StringGenerator