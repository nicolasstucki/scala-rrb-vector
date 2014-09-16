package scalameter

import org.scalameter.api._

import scala.collection.immutable
import scala.reflect.ClassTag

trait VectorBenchmark {
    type A

    def element(n: Int): A

    def typedName: String

    def vectors(sizes: Gen[Int]) = for (size <- sizes) yield Vector.tabulate[A](size)(element _)

    def rrbprototype(sizes: Gen[Int]) = for (size <- sizes) yield immutable.rrbprototype.Vector.tabulate[A](size)(element _)

    def rrbvector(sizes: Gen[Int]) = for (size <- sizes) yield immutable.rrbvector.Vector.tabulate[A](size)(element _)

    def rrbvector1(sizes: Gen[Int]) = for (size <- sizes) yield immutable.rrbvector1.Vector.tabulate[A](size)(element _)

}

trait IntVectorBenchmark extends VectorBenchmark {
    final type A = Int

    override final def element(n: Int): Int = n

    def typedName = "Vector[Int]"
}

trait StringVectorBenchmark extends VectorBenchmark {
    final type A = String

    override final def element(n: Int): String = n.toString

    def typedName = "Vector[String]"
}
