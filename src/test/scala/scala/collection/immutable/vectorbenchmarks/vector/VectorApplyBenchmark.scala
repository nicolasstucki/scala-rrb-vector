package scala.collection.immutable.vectorbenchmarks.vector

import org.scalameter.Gen

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.ApplyBenchmarks
import scala.collection.immutable.vectorbenchmarks.VectorBenchmark
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class VectorAbstractApplyBenchmark[A] extends ApplyBenchmarks[A] with VectorBenchmark[A]

class VectorApplyIntBenchmark extends VectorAbstractApplyBenchmark[Int] with VectorGeneratorType.IntGenerator

class VectorApplyAnyRefBenchmark extends VectorAbstractApplyBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator