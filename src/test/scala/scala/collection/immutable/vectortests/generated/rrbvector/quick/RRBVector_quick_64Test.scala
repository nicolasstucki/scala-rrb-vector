package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.quick {
            import scala.collection.immutable.vectorutils.generated.rrbvector.quick._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVector_quick_64Test[A] extends VectorSpec[A] with RRBVector_quick_64Generator[A]

            class IntRRBVector_quick_64Test extends RRBVector_quick_64Test[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_quick_64Test extends RRBVector_quick_64Test[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}