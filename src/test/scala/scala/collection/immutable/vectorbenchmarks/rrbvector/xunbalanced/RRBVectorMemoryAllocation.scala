package scala.collection.immutable.vectorbenchmarks.rrbvector.xunbalanced

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.MemoryAllocation
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class RRBVectorAbstractMemoryAllocation[A] extends MemoryAllocation[A] with RRBVectorAbstractBenchmark[A]

class RRBVectorIntMemoryAllocation extends RRBVectorAbstractMemoryAllocation[Int] with VectorGeneratorType.IntGenerator

class RRBVectorStringMemoryAllocation extends RRBVectorAbstractMemoryAllocation[String] with VectorGeneratorType.StringGenerator