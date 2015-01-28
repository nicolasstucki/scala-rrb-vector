package scala.collection.immutable.vectorbenchmarks.fingertree

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.ConcatenationBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class FingerTreeAbstractConcatenationBenchmark[A] extends ConcatenationBenchmarks[A] with FingerTreeBenchmark[A] {

//    override def to(n: Int): Int = math.min(n, 20000)
}

class FingerTreeConcatenationIntBenchmark extends FingerTreeAbstractConcatenationBenchmark[Int] with VectorGeneratorType.IntGenerator

class FingerTreeConcatenationStringBenchmark extends FingerTreeAbstractConcatenationBenchmark[String] with VectorGeneratorType.StringGenerator