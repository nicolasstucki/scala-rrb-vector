package scala.collection.immutable
package vectorutils


import scala.collection.immutable.mbrrbvector.{MbRRBVectorIterator, MbRRBVector}
import scala.util.Random
import scala.collection.immutable.rrbvector._
import scala.collection.immutable.mbrrbvector._

trait BaseVectorGenerator[@miniboxed A] extends VectorOps[A] with VectorGeneratorType[A] {

    def vectorClassName: String

    def vectorName: String = s"$vectorClassName[$vectorTypeName]"

    def tabulatedVector(n: Int): Vec

    def mapFun1(n: A): A

    def mapFun2(n: A): A

    def mapFun3(n: A): A

    def rangedVector(start: Int, end: Int): Vec

    def iterator(vec: Vec, start: Int, end: Int): Iterator[A]

    final def defaultVectorConfig(seed: Int) = BaseVectorGenerator.defaultVectorConfig(seed)

    final def randomVectorOfSize(n: Int)(implicit config: BaseVectorGenerator.Config): Vec = {

        def randomVectorFromRange(start: Int, end: Int): Vec = end - start match {
            case 0 => emptyVector
            case m if m > 0 && config.maxSplitSize < m =>
                val mid = start + config.rnd.nextInt(m) + 1
                val v1 = randomVectorFromRange(start, mid)
                val v2 = randomVectorFromRange(mid, end)
                val v3 = plusPlus(v1, v2)
                v3
            case m if m > 0 && config.maxSplitSize >= m => rangedVector(start, end)
            case _ => throw new IllegalArgumentException()
        }

        randomVectorFromRange(0, n)
    }


}

object BaseVectorGenerator {

    final def defaultVectorConfig(seed: Int) = BaseVectorGenerator.Config(new scala.util.Random(seed), 6)

    case class Config(rnd: Random, maxSplitSize: Int)

    trait VectorGenerator[@miniboxed A] extends BaseVectorGenerator[A] {
        override final type Vec = Vector[A]

        final def vectorClassName: String = "Vector"

        override final def newBuilder() = Vector.newBuilder[A]

        override final def tabulatedVector(n: Int): Vector[A] = Vector.tabulate(n)(element)

        override final def rangedVector(start: Int, end: Int): Vector[A] = Vector.range(start, end) map element

        override final def emptyVector: Vector[A] = Vector.empty[A]

        override def iterator(vec: Vector[A], start: Int, end: Int) = {
            val it = new VectorIterator[A](start, end)
            vec.initIterator(it)
            it
        }

        override def plus(vec: Vec, elem: A): Vec = vec :+ elem

        override def plus(elem: A, vec: Vec): Vec = elem +: vec

        override final def plusPlus(vec1: Vec, vec2: Vec): Vec = vec1 ++ vec2

        override final def take(vec: Vec, n: Int): Vec = vec.take(n)

        override final def drop(vec: Vec, n: Int): Vec = vec.drop(n)
    }

    trait RRBVectorGenerator[@miniboxed A] extends BaseVectorGenerator[A] {
        override type Vec = RRBVector[A]

        final def vectorClassName: String = "RRBVector"

        override final def newBuilder() = RRBVector.newBuilder[A]

        override final def tabulatedVector(n: Int): RRBVector[A] = RRBVector.tabulate(n)(element)

        override final def rangedVector(start: Int, end: Int): RRBVector[A] = RRBVector.range(start, end) map element

        override final def emptyVector: RRBVector[A] = RRBVector.empty[A]

        override def iterator(vec: RRBVector[A], start: Int, end: Int) = {
            val it = new RRBVectorIterator[A](start, end)
            it.initIteratorFrom(vec)
            it
        }

        override final def plus(vec: RRBVector[A], elem: A): RRBVector[A] = vec :+ elem

        override final def plus(elem: A, vec: Vec): RRBVector[A] = elem +: vec

        override final def plusPlus(vec1: RRBVector[A], vec2: RRBVector[A]): RRBVector[A] = vec1 ++ vec2

        override final def take(vec: RRBVector[A], n: Int): RRBVector[A] = vec.take(n)

        override final def drop(vec: RRBVector[A], n: Int): RRBVector[A] = vec.drop(n)
    }

    trait MbRRBVectorGenerator[@miniboxed A] extends BaseVectorGenerator[A] {
        override type Vec = MbRRBVector[A]

        final def vectorClassName: String = "MbRRBVector"

        override final def newBuilder() = MbRRBVector.newBuilder[A]

        override final def tabulatedVector(n: Int): MbRRBVector[A] = MbRRBVector.tabulate(n)(element)

        override final def rangedVector(start: Int, end: Int): MbRRBVector[A] = MbRRBVector.range(start, end) map element

        override final def emptyVector: MbRRBVector[A] = MbRRBVector.empty[A]

        override def iterator(vec: MbRRBVector[A], start: Int, end: Int) = {
            val it = new MbRRBVectorIterator[A](start, end)
            it.initIteratorFrom(vec)
            it
        }

        override final def plus(vec: MbRRBVector[A], elem: A): MbRRBVector[A] = vec :+ elem

        override final def plus(elem: A, vec: Vec): MbRRBVector[A] = elem +: vec

        override final def plusPlus(vec1: MbRRBVector[A], vec2: MbRRBVector[A]): MbRRBVector[A] = vec1 ++ vec2

        override final def take(vec: MbRRBVector[A], n: Int): MbRRBVector[A] = vec.take(n)

        override final def drop(vec: MbRRBVector[A], n: Int): MbRRBVector[A] = vec.drop(n)
    }
}



