package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.quick {
            import scala.collection.immutable.vectorutils.generated.rrbvector.quick._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVector_quick_32Test[A] extends VectorSpec[A] with RRBVector_quick_32Generator[A]

            class IntRRBVector_quick_32Test extends RRBVector_quick_32Test[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_quick_32Test extends RRBVector_quick_32Test[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}