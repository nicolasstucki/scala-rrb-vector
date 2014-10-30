package scala
package collection
package parallel.immutable
package rrbvector

import scala.collection.generic.{CanBuildFrom, GenericParTemplate, CanCombineFrom, ParFactory}
import scala.collection.parallel.ParSeqLike
import scala.collection.parallel.Combiner
import scala.collection.parallel.SeqSplitter
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
        val pit = new ParRRBVectorIterator(0, vector.length)
        vector.initIterator(pit)
        pit
    }

    override def map[S, That](f: (T) => S)(implicit bf: CanBuildFrom[ParRRBVector[T], S, That]) = super.map(f)

    override def seq: RRBVector[T] = vector

    override def toVector: Vector[T] = vector.toVector

    class ParRRBVectorIterator(_start: Int, _end: Int) extends RRBVectorIterator[T](_start, _end) with SeqSplitter[T] {
        def remaining: Int = remainingElementCount

        def dup: SeqSplitter[T] = {
            // (new ParRRBVector(remainingVector)).splitter
            val pit = new ParRRBVectorIterator(_end - remaining, _end)
            pit.initIteratorFrom(this)
            pit
        }

        def split: Seq[ParRRBVectorIterator] = {
            val rem = remaining
            if (rem >= 2) psplit(rem / 2, rem - rem / 2)
            else Seq(this)
        }

        def psplit(sizes: Int*): Seq[ParRRBVectorIterator] = {
            //            var remvector = remainingVector
            //            val splitted = new ArrayBuffer[RRBVector[T]]
            //            for (sz <- sizes) {
            //                splitted += remvector.take(sz)
            //                remvector = remvector.drop(sz)
            //            }
            //            splitted.map(v => new ParRRBVector(v).splitter.asInstanceOf[ParRRBVectorIterator])
            val splitted = new ArrayBuffer[ParRRBVectorIterator]
            var currentPos = _end - remaining
            for (sz <- sizes) {
                val pit = new ParRRBVectorIterator(currentPos, currentPos + sz)
                pit.initIteratorFrom(this)
                splitted += pit
                currentPos += sz
            }
            splitted
        }
    }

}

/** $factoryInfo
  * @define Coll `immutable.ParRRBVector`
  * @define coll immutable parallel vector
  */
object ParRRBVector extends ParFactory[ParRRBVector] {
    implicit def canBuildFrom[T]: CanCombineFrom[Coll, T, ParRRBVector[T]] =
        new GenericCanCombineFrom[T]

    def newBuilder[T]: Combiner[T, ParRRBVector[T]] = newCombiner[T]

    def newCombiner[T]: Combiner[T, ParRRBVector[T]] = new LazyParRRBVectorCombiner[T] // was: with EPC[T, ParRRBVector[T]]
}

private[immutable] class LazyParRRBVectorCombiner[T] extends Combiner[T, ParRRBVector[T]] {
    //self: EnvironmentPassingCombiner[T, ParRRBVector[T]] =>
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

    def result: ParRRBVector[T] = {
        val rvb = new RRBVectorBuilder[T]
        for (vb <- vectors) {
            rvb ++= vb.result
        }
        new ParRRBVector(rvb.result)
    }

    def combine[U <: T, NewTo >: ParRRBVector[T]](other: Combiner[U, NewTo]) = if (other eq this) this
    else {
        val that = other.asInstanceOf[LazyParRRBVectorCombiner[T]]
        sz += that.sz
        vectors ++= that.vectors
        this
    }
}
