package scala.collection.immutable.vectortests.vector

import scala.collection.immutable.vectortests.VectorSpec
import scala.collection.immutable.vectorutils.{VectorGeneratorType, BaseVectorGenerator}


abstract class VectorTest[A] extends VectorSpec[A] with BaseVectorGenerator.VectorGenerator[A] {
    override def isRRBVectorImplementation = false
}

class IntVectorTest extends VectorTest[Int] with VectorGeneratorType.IntGenerator

class StringVectorTest extends VectorTest[String] with VectorGeneratorType.StringGenerator