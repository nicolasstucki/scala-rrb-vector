package scala.collection
package parallel
package immutable
package mbrrbvector

import scala.collection.immutable.mbrrbvector._

import generic.{CanCombineFrom, GenericParTemplate, ParFactory}
import scala.collection.mutable.ArrayBuffer

class ParMbRRBVector[@miniboxed +T](private[this] val vector: MbRRBVector[T])
  extends ParSeq[T]
  with GenericParTemplate[T, ParMbRRBVector]
  with ParSeqLike[T, ParMbRRBVector[T], MbRRBVector[T]]
  with Serializable {
    override def companion = ParMbRRBVector

    def this() = this(MbRRBVector())

    def apply(idx: Int) = vector.apply(idx)

    def length = vector.length

    def splitter: SeqSplitter[T] = {
        val pit = new ParMbRRBVectorSplitter[T](0, vector.length)
        pit.initIteratorFrom(vector)
        pit
    }

    override def seq: MbRRBVector[T] = vector

    override def toVector: Vector[T] = vector.toVector
}

class ParMbRRBVectorSplitter[@miniboxed +U](val _start: Int, val _end: Int) extends MbRRBVectorIterator[U](_start, _end) with SeqSplitter[U] {
    override final def remaining: Int = super.remaining

    def dup: SeqSplitter[U] = {
        val pit = new ParMbRRBVectorSplitter(_end - remaining, _end)
        pit.initIteratorFrom(this)
        pit
    }

    def split: Seq[ParMbRRBVectorSplitter[U]] = {
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
            return psplit(_halfAdjusted, rem - _halfAdjusted)
        } else {
            return Seq(this)
        }
    }

    def psplit(sizes: Int*): Seq[ParMbRRBVectorSplitter[U]] = {
        val splitted = new ArrayBuffer[ParMbRRBVectorSplitter[U]]
        var currentPos = _end - remaining
        for (sz <- sizes) {
            val pit = new ParMbRRBVectorSplitter[U](currentPos, currentPos + sz)
            pit.initIteratorFrom(this)
            splitted += pit
            currentPos += sz
        }
        splitted
    }
}

object ParMbRRBVector extends ParFactory[ParMbRRBVector] {
    implicit def canBuildFrom[@miniboxed T]: CanCombineFrom[Coll, T, ParMbRRBVector[T]] =
        new GenericCanCombineFrom[T]

    def newBuilder[@miniboxed T]: Combiner[T, ParMbRRBVector[T]] = newCombiner[T]

    def newCombiner[@miniboxed T]: Combiner[T, ParMbRRBVector[T]] = new ParMbRRBVectorCombiner[T]

}

private[immutable] class ParMbRRBVectorCombiner[@miniboxed T] extends Combiner[T, ParMbRRBVector[T]] {

    private[immutable] val builder: MbRRBVectorBuilder[T] = {
        val b = new MbRRBVectorBuilder[T]
        b.init()
        b
    }

    override def size = builder.endIndex

    override def result() = new ParMbRRBVector[T](builder.result())

    override def clear() = builder.clear()

    override def +=(elem: T) = {
        builder += elem
        this
    }

    override def ++=(xs: TraversableOnce[T]) = {
        builder ++= xs
        this
    }

    def combine[@miniboxed U <: T, NewTo >: ParMbRRBVector[T]](other: Combiner[U, NewTo]): Combiner[U, NewTo] = {
        if (this eq other)
            return this
        else {
            val newCombiner = new ParMbRRBVectorCombiner[T]
            newCombiner ++= this.builder.result()
            newCombiner ++= other.asInstanceOf[ParMbRRBVectorCombiner[T]].builder.result()
            return newCombiner
        }
    }
}
