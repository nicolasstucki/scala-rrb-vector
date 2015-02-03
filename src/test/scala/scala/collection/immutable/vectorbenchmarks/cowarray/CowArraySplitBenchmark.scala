package scala.collection.immutable.vectorbenchmarks.cowarray

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.SplitBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class CowArrayAbstractSplitBenchmark[A] extends SplitBenchmarks[A] with CowArrayBenchmark[A]

class CowArraySplitIntBenchmark extends CowArrayAbstractSplitBenchmark[Int] with VectorGeneratorType.IntGenerator

class CowArraySplitStringBenchmark extends CowArrayAbstractSplitBenchmark[String] with VectorGeneratorType.StringGenerator

