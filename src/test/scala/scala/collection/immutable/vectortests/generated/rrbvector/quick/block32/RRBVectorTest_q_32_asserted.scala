package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.quick.block32 {
            import scala.collection.immutable.vectorutils.generated.rrbvector.quick.block32._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVectorTest_q_32_asserted[A] extends VectorSpec[A] with RRBVectorGenerator_q_32_asserted[A]

            class IntRRBVector_q_32_assertedTest extends RRBVectorTest_q_32_asserted[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_q_32_assertedTest extends RRBVectorTest_q_32_asserted[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}