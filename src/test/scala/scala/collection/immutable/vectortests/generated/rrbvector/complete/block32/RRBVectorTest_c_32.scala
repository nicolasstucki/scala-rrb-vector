package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.complete.block32 {
            import scala.collection.immutable.vectorutils.generated.rrbvector.complete.block32._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVectorTest_c_32[A] extends VectorSpec[A] with RRBVectorGenerator_c_32[A]

            class IntRRBVector_c_32Test extends RRBVectorTest_c_32[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_c_32Test extends RRBVectorTest_c_32[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}