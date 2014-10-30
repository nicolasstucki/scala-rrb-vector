package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.complete {
            import scala.collection.immutable.vectorutils.generated.rrbvector.complete._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVector_complete_32Test[A] extends VectorSpec[A] with RRBVector_complete_32Generator[A]

            class IntRRBVector_complete_32Test extends RRBVector_complete_32Test[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_complete_32Test extends RRBVector_complete_32Test[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}