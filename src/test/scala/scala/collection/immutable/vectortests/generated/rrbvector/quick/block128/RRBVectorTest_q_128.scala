package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.quick.block128 {
            import scala.collection.immutable.vectorutils.generated.rrbvector.quick.block128._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVectorTest_q_128[A] extends VectorSpec[A] with RRBVectorGenerator_q_128[A]

            class IntRRBVector_q_128Test extends RRBVectorTest_q_128[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_q_128Test extends RRBVectorTest_q_128[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}