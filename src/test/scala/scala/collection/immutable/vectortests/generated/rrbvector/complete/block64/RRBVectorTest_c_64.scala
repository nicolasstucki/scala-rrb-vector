package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.complete.block64 {
            import scala.collection.immutable.vectorutils.generated.rrbvector.complete.block64._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVectorTest_c_64[A] extends VectorSpec[A] with RRBVectorGenerator_c_64[A]

            class IntRRBVector_c_64Test extends RRBVectorTest_c_64[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_c_64Test extends RRBVectorTest_c_64[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}