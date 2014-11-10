package scala.collection.immutable.vectorbenchmarks.vector

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.ParMapBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class VectorAbstractParMapBenchmark[A] extends ParMapBenchmarks[A] with VectorBenchmark[A]

class VectorParMapIntBenchmark extends VectorAbstractParMapBenchmark[Int] with VectorGeneratorType.IntGenerator

class VectorParMapStringBenchmark extends VectorAbstractParMapBenchmark[String] with VectorGeneratorType.StringGenerator

