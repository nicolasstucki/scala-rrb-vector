package scala.collection.immutable.vectortests.rrbvector.mb

import scala.collection.immutable.vectortests.VectorSpec
import scala.collection.immutable.vectorutils.{BaseVectorGenerator, VectorGeneratorType}


abstract class MbRRBVectorTest[@miniboxed A] extends VectorSpec[A] with BaseVectorGenerator.RRBVectorGenerator[A]

class IntMbRRBVectorTest extends MbRRBVectorTest[Int] with VectorGeneratorType.IntGenerator

class StringMbRRBVectorTest extends MbRRBVectorTest[String] with VectorGeneratorType.StringGenerator