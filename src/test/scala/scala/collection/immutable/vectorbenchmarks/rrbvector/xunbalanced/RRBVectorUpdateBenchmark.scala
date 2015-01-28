package scala.collection.immutable.vectorbenchmarks.rrbvector.xunbalanced

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.UpdateBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class RRBVectorAbstractUpdateBenchmark[A] extends UpdateBenchmarks[A] with RRBVectorAbstractBenchmark[A]

class RRBVectorUpdateIntBenchmark extends RRBVectorAbstractUpdateBenchmark[Int] with VectorGeneratorType.IntGenerator

class RRBVectorUpdateStringBenchmark extends RRBVectorAbstractUpdateBenchmark[String] with VectorGeneratorType.StringGenerator