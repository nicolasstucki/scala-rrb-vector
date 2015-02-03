package scala.collection.immutable.vectorbenchmarks.redblack

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.ConcatenationBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class RedBlackSeqAbstractConcatenationBenchmark[A] extends ConcatenationBenchmarks[A] with RedBlackSeqBenchmark[A] {

//    override def to(n: Int): Int = math.min(n, 20000)
}

class RedBlackSeqConcatenationIntBenchmark extends RedBlackSeqAbstractConcatenationBenchmark[Int] with VectorGeneratorType.IntGenerator

class RedBlackSeqConcatenationStringBenchmark extends RedBlackSeqAbstractConcatenationBenchmark[String] with VectorGeneratorType.StringGenerator