package scala.collection.immutable.vectortests.vector

import scala.collection.immutable.vectortests.VectorSpec


abstract class VectorTest[A] extends VectorSpec[A, Vector[A]] {

    override def emptyVector: Vector[A] = Vector.empty[A]

    override def plus(vec: Vector[A], elem: A): Vector[A] = vec :+ elem

    override def plus(elem: A, vec: Vector[A]): Vector[A] = elem +: vec

    override def plusPlus(vec1: Vector[A], vec2: Vector[A]): Vector[A] = vec1 ++ vec2

    override def tabulatedVector(n: Int): Vector[A] = Vector.tabulate(n)(element)

}

class IntVectorTest extends VectorTest[Int] {
    def element(n: Int) = n
}

class StringVectorTest extends VectorTest[String] {
    def element(n: Int) = n.toString
}