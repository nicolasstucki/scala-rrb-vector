package scala
package collection
package parallel.immutable
package rrbvector

import scala.collection.generic.{GenericParTemplate, CanCombineFrom, ParFactory}
import scala.collection.parallel.{ParSeqLike, Combiner, SeqSplitter}
import mutable.ArrayBuffer
import immutable.rrbvector.RRBVector
import immutable.rrbvector.RRBVectorBuilder
import immutable.rrbvector.RRBVectorIterator

class ParRRBVector[+T](private[this] val vector: RRBVector[T])
  extends ParSeq[T]
  with GenericParTemplate[T, ParRRBVector]
  with ParSeqLike[T, ParRRBVector[T], RRBVector[T]]
  with Serializable {
    override def companion = ParRRBVector

    def this() = this(RRBVector())

    def apply(idx: Int) = vector.apply(idx)

    def length = vector.length

    def splitter: SeqSplitter[T] = {
        val pit = new ParRRBVectorSplitter(0, vector.length)
        pit.initIteratorFrom(vector)
        pit
    }

    override def seq: RRBVector[T] = vector

    override def toVector: Vector[T] = vector.toVector

    class ParRRBVectorSplitter(_start: Int, _end: Int) extends RRBVectorIterator[T](_start, _end) with SeqSplitter[T] {
        override def remaining: Int = super.remaining

        def dup: SeqSplitter[T] = {
            val pit = new ParRRBVectorSplitter(_end - remaining, _end)
            pit.initIteratorFrom(this)
            pit
        }

        def split: Seq[ParRRBVectorSplitter] = {
            val rem = remaining
            if (rem >= 2) {
                val splitSize =
                    if (rem < 32) 1
                    else if (rem < 1024) 1 << 5
                    else if (rem < 32768) 1 << 10
                    else if (rem < 1048576) 1 << 15
                    else if (rem < 33554432) 1 << 20
                    else 1 << 25
                val splitted = new ArrayBuffer[ParRRBVectorSplitter]
                var currentPos = _end - remaining
                while (currentPos < rem) {
                    val pit = new ParRRBVectorSplitter(currentPos, math.min(currentPos + splitSize, _end))
                    pit.initIteratorFrom(this)
                    splitted += pit
                    currentPos += splitSize
                }
                splitted
            }
            else Seq(this)
        }


        def psplit(sizes: Int*): Seq[ParRRBVectorSplitter] = {
            val splitted = new ArrayBuffer[ParRRBVectorSplitter]
            var currentPos = _end - remaining
            for (sz <- sizes) {
                val pit = new ParRRBVectorSplitter(currentPos, currentPos + sz)
                pit.initIteratorFrom(this)
                splitted += pit
                currentPos += sz
            }
            splitted
        }
    }

}


object ParRRBVector extends ParFactory[ParRRBVector] {
    implicit def canBuildFrom[T]: CanCombineFrom[Coll, T, ParRRBVector[T]] =
        new GenericCanCombineFrom[T]

    def newBuilder[T]: Combiner[T, ParRRBVector[T]] = newCombiner[T]

    def newCombiner[T]: Combiner[T, ParRRBVector[T]] = new ParRRBVectorCombiner[T]
}

private[immutable] class ParRRBVectorCombiner[T] extends Combiner[T, ParRRBVector[T]] {

    private[immutable] val builder: RRBVectorBuilder[T] = new RRBVectorBuilder[T]

    override def size = builder.endIndex

    override def result() = new ParRRBVector[T](builder.result())

    override def clear() = builder.clear()

    override def +=(elem: T) = {
        builder += elem
        this
    }

    override def ++=(xs: TraversableOnce[T]) = {
        builder ++= xs
        this
    }

    def combine[U <: T, NewTo >: ParRRBVector[T]](other: Combiner[U, NewTo]) = {
        if (other eq this) this
        else {
            val that = other.asInstanceOf[ParRRBVectorCombiner[T]]
            builder ++= that.builder.result()
            this
        }
    }
}
