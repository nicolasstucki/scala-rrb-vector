package scala.collection.immutable.vectorbenchmarks.vector

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.ParMapBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType


class VectorParMapIntBenchmark extends ParMapBenchmarks with VectorBenchmark[Int] with VectorGeneratorType.IntGenerator

