package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.complete.block64 {
            import scala.collection.immutable.vectorutils.generated.rrbvector.complete.block64._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVectorTest_c_64_asserted[A] extends VectorSpec[A] with RRBVectorGenerator_c_64_asserted[A]

            class IntRRBVector_c_64_assertedTest extends RRBVectorTest_c_64_asserted[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_c_64_assertedTest extends RRBVectorTest_c_64_asserted[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}