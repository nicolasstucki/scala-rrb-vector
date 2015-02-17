package scala.collection.immutable.vectorbenchmarks.vector

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.ConcatenationBenchmarks
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.Concatenation2Benchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class VectorAbstractConcatenationBenchmark[A] extends ConcatenationBenchmarks[A] with VectorBenchmark[A] {
    // Used in immutable.vector to bound the sizes
    override def to(n: Int): Int = math.min(n, 20000)

    override def points = super.points / 2
}

class VectorConcatenationIntBenchmark extends VectorAbstractConcatenationBenchmark[Int] with VectorGeneratorType.IntGenerator

class VectorConcatenationStringBenchmark extends VectorAbstractConcatenationBenchmark[String] with VectorGeneratorType.StringGenerator


abstract class VectorAbstractConcatenation2Benchmark[A] extends Concatenation2Benchmarks[A] with VectorBenchmark[A]

class VectorConcatenation2IntBenchmark extends VectorAbstractConcatenation2Benchmark[Int] with VectorGeneratorType.IntGenerator

class VectorConcatenation2StringBenchmark extends VectorAbstractConcatenation2Benchmark[String] with VectorGeneratorType.StringGenerator