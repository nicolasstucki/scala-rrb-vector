package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.complete {
            import scala.collection.immutable.vectorutils.generated.rrbvector.complete._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVector_complete_64Test[A] extends VectorSpec[A] with RRBVector_complete_64Generator[A]

            class IntRRBVector_complete_64Test extends RRBVector_complete_64Test[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_complete_64Test extends RRBVector_complete_64Test[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}