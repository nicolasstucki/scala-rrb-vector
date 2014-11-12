package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.quick.block256 {
            import scala.collection.immutable.vectorutils.generated.rrbvector.quick.block256._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVectorTest_q_256[A] extends VectorSpec[A] with RRBVectorGenerator_q_256[A]

            class IntRRBVector_q_256Test extends RRBVectorTest_q_256[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_q_256Test extends RRBVectorTest_q_256[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}