package scala.collection.immutable.vectorbenchmarks.vector

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.ConcatenationBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class VectorAbstractConcatenationBenchmark[A] extends ConcatenationBenchmarks[A] with VectorBenchmark[A] {
    // Used in immutable.vector to bound the sizes
    override def to(n: Int): Int = math.min(n, 8200)
}

class VectorConcatenationIntBenchmark extends VectorAbstractConcatenationBenchmark[Int] with VectorGeneratorType.IntGenerator

class VectorConcatenationStringBenchmark extends VectorAbstractConcatenationBenchmark[String] with VectorGeneratorType.StringGenerator