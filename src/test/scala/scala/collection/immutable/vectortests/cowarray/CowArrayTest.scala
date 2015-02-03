package scala.collection.immutable.vectortests.cowarray

import scala.collection.immutable.vectorbenchmarks.cowarray.CowArrayGenerator
import scala.collection.immutable.vectortests.VectorSpec
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class CowArrayTest[A] extends VectorSpec[A] with CowArrayGenerator[A]

class IntCowArrayTest extends CowArrayTest[Int] with VectorGeneratorType.IntGenerator

class StringCowArrayTest extends CowArrayTest[String] with VectorGeneratorType.StringGenerator