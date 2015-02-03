package scala.collection.immutable.vectortests.redblack

import scala.collection.immutable.vectorbenchmarks.redblack.RedBlackSeqGenerator
import scala.collection.immutable.vectortests.VectorSpec
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class RedBlackSeqTest[A] extends VectorSpec[A] with RedBlackSeqGenerator[A]

class IntRedBlackSeqTest extends RedBlackSeqTest[Int] with VectorGeneratorType.IntGenerator

class StringRedBlackSeqTest extends RedBlackSeqTest[String] with VectorGeneratorType.StringGenerator