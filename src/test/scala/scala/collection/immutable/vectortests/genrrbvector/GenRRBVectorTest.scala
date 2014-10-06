package scala.collection.immutable.vectortests.genrrbvector

import scala.collection.immutable.genrrbvector.GenRRBVector
import scala.collection.immutable.vectortests.VectorSpec
import scala.collection.immutable.vectorutils.{VectorGeneratorType, BaseVectorGenerator}


abstract class GenRRBVectorTest[A] extends VectorSpec[A] with BaseVectorGenerator.GenRRBVectorGenerator[A]

class IntGenRRBVectorTest extends GenRRBVectorTest[Int] with VectorGeneratorType.IntGenerator

class AnyRefGenRRBVectorTest extends GenRRBVectorTest[AnyRef] with VectorGeneratorType.AnyRefGenerator

