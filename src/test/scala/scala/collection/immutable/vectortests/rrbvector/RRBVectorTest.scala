package scala.collection.immutable.vectortests.rrbvector

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectortests.VectorSpec


abstract class RRBVectorTest[A] extends VectorSpec[A, RRBVector[A]] {

    override def emptyVector: RRBVector[A] = RRBVector.empty[A]

    override def plus(vec: RRBVector[A], elem: A): RRBVector[A] = vec :+ elem

    override def plus(elem: A, vec: RRBVector[A]): RRBVector[A] = elem +: vec

    override def plusPlus(vec1: RRBVector[A], vec2: RRBVector[A]): RRBVector[A] = vec1 ++ vec2

    override def tabulatedVector(n: Int): RRBVector[A] = RRBVector.tabulate(n)(element)

}

class IntRRBVectorTest extends RRBVectorTest[Int] {
    def element(n: Int) = n
}

class StringRRBVectorTest extends RRBVectorTest[String] {
    def element(n: Int) = n.toString
}