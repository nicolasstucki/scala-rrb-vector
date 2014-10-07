package scala.collection.immutable.vectorbenchmarks.vector

import scala.collection.immutable.vectorbenchmarks.VectorBenchmark
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.SplitBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class VectorAbstractSplitBenchmark[A] extends SplitBenchmarks[A] with VectorBenchmark[A]

class VectorSplitIntBenchmark extends VectorAbstractSplitBenchmark[Int] with VectorGeneratorType.IntGenerator

class VectorSplitAnyRefBenchmark extends VectorAbstractSplitBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

