package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.complete.block256 {
            import scala.collection.immutable.vectorutils.generated.rrbvector.complete.block256._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVectorTest_c_256_asserted[A] extends VectorSpec[A] with RRBVectorGenerator_c_256_asserted[A]

            class IntRRBVector_c_256_assertedTest extends RRBVectorTest_c_256_asserted[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_c_256_assertedTest extends RRBVectorTest_c_256_asserted[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}