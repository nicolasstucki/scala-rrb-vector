package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.complete.block128 {
            import scala.collection.immutable.vectorutils.generated.rrbvector.complete.block128._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVectorTest_c_128[A] extends VectorSpec[A] with RRBVectorGenerator_c_128[A]

            class IntRRBVector_c_128Test extends RRBVectorTest_c_128[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_c_128Test extends RRBVectorTest_c_128[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}