package scala.collection.immutable.vectortests.rrbvector

import scala.collection.immutable.vectortests.VectorSpec
import scala.collection.immutable.vectorutils.{VectorGeneratorType, BaseVectorGenerator}


abstract class RRBVectorTest[A] extends VectorSpec[A] with BaseVectorGenerator.RRBVectorGenerator[A]

class IntRRBVectorTest extends RRBVectorTest[Int] with VectorGeneratorType.IntGenerator

class StringRRBVectorTest extends RRBVectorTest[String] with VectorGeneratorType.StringGenerator