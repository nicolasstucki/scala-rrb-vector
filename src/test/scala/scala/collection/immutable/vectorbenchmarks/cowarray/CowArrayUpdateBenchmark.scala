package scala.collection.immutable.vectorbenchmarks.cowarray

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.UpdateBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class CowArrayAbstractUpdateBenchmark[A] extends UpdateBenchmarks[A] with CowArrayBenchmark[A] {
    override def to(n: Int): Int = math.min(n, 92171)
}

class CowArrayUpdateIntBenchmark extends CowArrayAbstractUpdateBenchmark[Int] with VectorGeneratorType.IntGenerator

class CowArrayUpdateStringBenchmark extends CowArrayAbstractUpdateBenchmark[String] with VectorGeneratorType.StringGenerator