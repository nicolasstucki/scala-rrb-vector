package scala.collection.immutable
package vectorutils


import scala.collection.immutable.IndexedSeq
import scala.util.Random
import rrbvector._

trait BaseVectorGenerator[A] extends VectorOps[A] with VectorGeneratorType[A] {

    def vectorClassName: String

    def vectorName: String = s"$vectorClassName[$vectorTypeName]"

    def tabulatedVector(n: Int): Vec

    def rangedVector(start: Int, end: Int): Vec

    final def defaultVectorConfig(seed: Int) = BaseVectorGenerator.defaultVectorConfig(seed)

    final def randomVectorOfSize[A](n: Int)(implicit config: BaseVectorGenerator.Config): Vec = {

        def randomVectorFromRange(start: Int, end: Int): Vec = end - start match {
            case 0 => emptyVector
            case n if n > 0 && config.maxSplitSize < n =>
                val mid = start + config.rnd.nextInt(n) + 1
                val v1 = randomVectorFromRange(start, mid)
                val v2 = randomVectorFromRange(mid, end)
                val v3 = plusPlus(v1, v2)
                v3
            case n if n > 0 && config.maxSplitSize >= n => rangedVector(start, end)
            case _ => throw new IllegalArgumentException()
        }

        randomVectorFromRange(0, n)
    }


}

object BaseVectorGenerator {

    final def defaultVectorConfig(seed: Int) = BaseVectorGenerator.Config(new scala.util.Random(seed), 6)

    case class Config(rnd: Random, maxSplitSize: Int)

    trait VectorGenerator[A] extends BaseVectorGenerator[A] {
        override type Vec = Vector[A]

        final def vectorClassName: String = "Vector"

        override final def tabulatedVector(n: Int): Vec = Vector.tabulate(n)(element)

        override final def rangedVector(start: Int, end: Int): Vec = Vector.range(start, end) map element

        override final def emptyVector: Vec = Vector.empty[A]

        override def plus(vec: Vec, elem: A): Vec = vec :+ elem

        override def plus(elem: A, vec: Vec): Vec = elem +: vec

        override final def plusPlus(vec1: Vec, vec2: Vec): Vec = vec1 ++ vec2

        override final def take(vec: Vec, n: Int): Vec = vec.take(n)

        override final def drop(vec: Vec, n: Int): Vec = vec.drop(n)
    }

    trait RRBVectorGenerator[A] extends BaseVectorGenerator[A] {
        override type Vec = RRBVector[A]

        final def vectorClassName: String = "RRBVector"

        override final def tabulatedVector(n: Int): Vec = RRBVector.tabulate(n)(element)

        override final def rangedVector(start: Int, end: Int): Vec = RRBVector.range(start, end) map element

        override final def emptyVector: Vec = RRBVector.empty[A]

        override final def plus(vec: Vec, elem: A): Vec = vec :+ elem

        override final def plus(elem: A, vec: Vec): Vec = elem +: vec

        override final def plusPlus(vec1: Vec, vec2: Vec): Vec = vec1 ++ vec2

        override final def take(vec: Vec, n: Int): Vec = vec.take(n)

        override final def drop(vec: Vec, n: Int): Vec = vec.drop(n)
    }

}



