package scala.collection.immutable.rrbvectorx

import scala.collection.mutable.Builder


final class VectorBuilder[A]()
  extends Builder[A, Vector[A]] {

    var acc: Vector[A] = Vector.empty[A]

    def +=(elem: A): this.type = {
        acc = acc :+ elem
        this
    }

//    override def ++=(xs: TraversableOnce[A]): this.type = xs match {
//        case v: Vector[A] => acc = acc ++ v; this
//        case _ => super.++=(xs)
//    }

    def result: Vector[A] = acc

    def clear(): Unit = {
        acc = Vector.empty[A]
    }
}