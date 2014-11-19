package scala
package collection
package parallel.immutable
package rrbvector

import scala.collection.generic.{GenericParTemplate, CanCombineFrom, ParFactory}
import scala.collection.immutable.VectorBuilder
import scala.collection.mutable
import scala.collection.parallel.immutable.LazyParVectorCombiner
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

    class ParRRBVectorSplitter(val _start: Int, val _end: Int) extends RRBVectorIterator[T](_start, _end) with SeqSplitter[T] {
        override def remaining: Int = super.remaining

        def dup: SeqSplitter[T] = {
            val pit = new ParRRBVectorSplitter(_end - remaining, _end)
            pit.initIteratorFrom(this)
            pit
        }

        def split: Seq[ParRRBVectorSplitter] = {
            val rem = remaining
            if (rem >= 2) {
                val _half = rem / 2
                val _splitModulo =
                    if (rem <= (1 << 5)) 1
                    else if (rem <= (1 << 10)) 1 << 5
                    else if (rem <= (1 << 15)) 1 << 10
                    else if (rem <= (1 << 20)) 1 << 15
                    else if (rem <= (1 << 25)) 1 << 20
                    else 1 << 25
                val _halfAdjusted = if (_half > _splitModulo) _half - _half % _splitModulo else if (_splitModulo < _end) _splitModulo else _half
                psplit(_halfAdjusted, rem - _halfAdjusted)
            } else {
                Seq(this)
            }
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

    //        def newCombiner[T]: Combiner[T, ParRRBVector[T]] = new LazyParRRBVectorCombiner[T]
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
        if (this eq other) this
        else {
            val newCombiner = new ParRRBVectorCombiner[T]
            newCombiner ++= this.builder.result()
            newCombiner ++= other.asInstanceOf[ParRRBVectorCombiner[T]].builder.result()
            newCombiner
            // builder ++= other.asInstanceOf[ParRRBVectorCombiner[T]].builder.result()
            // this
        }
    }
}


private[immutable] class LazyParRRBVectorCombiner[T] extends Combiner[T, ParRRBVector[T]] {
    var sz = 0
    val vectors = new ArrayBuffer[RRBVectorBuilder[T]] += new RRBVectorBuilder[T]

    def size: Int = sz

    def +=(elem: T): this.type = {
        vectors.last += elem
        sz += 1
        this
    }

    def clear() = {
        vectors.clear()
        vectors += new RRBVectorBuilder[T]
        sz = 0
    }

    def result(): ParRRBVector[T] = {
        val rvb = new RRBVectorBuilder[T]
        for (vb <- vectors) {
            rvb ++= vb.result
        }
        new ParRRBVector(rvb.result())
    }

    def combine[U <: T, NewTo >: ParRRBVector[T]](other: Combiner[U, NewTo]) = if (other eq this) this
    else {
        val that = other.asInstanceOf[LazyParRRBVectorCombiner[T]]
        sz += that.sz
        vectors ++= that.vectors
        this
    }
}
