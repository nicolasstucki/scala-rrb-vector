package scala.collection.immutable.vectortests.fingertree


import scala.collection.immutable.vectorbenchmarks.fingertree.FingerTreeSeqGenerator
import scala.collection.immutable.vectortests.VectorSpec
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class FingerTreeSeqTest[A] extends VectorSpec[A] with FingerTreeSeqGenerator[A]

class IntFingerTreeSeqTest extends FingerTreeSeqTest[Int] with VectorGeneratorType.IntGenerator

class StringFingerTreeSeqTest extends FingerTreeSeqTest[String] with VectorGeneratorType.StringGenerator