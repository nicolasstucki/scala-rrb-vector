package scala
package collection
package parallel.immutable
package rrbvector.mb

import scala.collection.generic.{GenericParTemplate, CanCombineFrom, ParFactory}
import scala.collection.parallel.{ParSeqLike, Combiner, SeqSplitter}
import mutable.ArrayBuffer
import immutable.rrbvector.mb.MbRRBVector
import immutable.rrbvector.mb.MbRRBVectorBuilder
import immutable.rrbvector.mb.MbRRBVectorIterator

class ParMbRRBVector[+T](private[this] val vector: MbRRBVector[T])
  extends ParSeq[T]
  with GenericParTemplate[T, ParMbRRBVector]
  with ParSeqLike[T, ParMbRRBVector[T], MbRRBVector[T]]
  with Serializable {
    override def companion = ParMbRRBVector

    def this() = this(MbRRBVector())

    def apply(idx: Int) = vector.apply(idx)

    def length = vector.length

    def splitter: SeqSplitter[T] = {
        val pit = new ParMbRRBVectorSplitter(0, vector.length)
        pit.initIteratorFrom(vector)
        pit
    }

    override def seq: MbRRBVector[T] = vector

    override def toVector: Vector[T] = vector.toVector

    class ParMbRRBVectorSplitter(val _start: Int, val _end: Int) extends MbRRBVectorIterator[T](_start, _end) with SeqSplitter[T] {
        override def remaining: Int = super.remaining

        def dup: SeqSplitter[T] = {
            val pit = new ParMbRRBVectorSplitter(_end - remaining, _end)
            pit.initIteratorFrom(this)
            pit
        }

        def split: Seq[ParMbRRBVectorSplitter] = {
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

        def psplit(sizes: Int*): Seq[ParMbRRBVectorSplitter] = {
            val splitted = new ArrayBuffer[ParMbRRBVectorSplitter]
            var currentPos = _end - remaining
            for (sz <- sizes) {
                val pit = new ParMbRRBVectorSplitter(currentPos, currentPos + sz)
                pit.initIteratorFrom(this)
                splitted += pit
                currentPos += sz
            }
            splitted
        }
    }

}


object ParMbRRBVector extends ParFactory[ParMbRRBVector] {
    implicit def canBuildFrom[T]: CanCombineFrom[Coll, T, ParMbRRBVector[T]] =
        new GenericCanCombineFrom[T]

    def newBuilder[T]: Combiner[T, ParMbRRBVector[T]] = newCombiner[T]

    def newCombiner[T]: Combiner[T, ParMbRRBVector[T]] = new ParMbRRBVectorCombiner[T]

}

private[immutable] class ParMbRRBVectorCombiner[T] extends Combiner[T, ParMbRRBVector[T]] {

    private[immutable] val builder: MbRRBVectorBuilder[T] = new MbRRBVectorBuilder[T]

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

    def combine[U <: T, NewTo >: ParMbRRBVector[T]](other: Combiner[U, NewTo]): Combiner[U, NewTo] = {
        if (this eq other)
            return this
        else {
            val newCombiner = new ParMbRRBVectorCombiner[T]
            newCombiner ++= this.builder.result()
            newCombiner ++= other.asInstanceOf[ParMbRRBVectorCombiner[T]].builder.result()
            return newCombiner
            // builder ++= other.asInstanceOf[ParMbRRBVectorCombiner[T]].builder.result()
            // this
        }
    }
}
