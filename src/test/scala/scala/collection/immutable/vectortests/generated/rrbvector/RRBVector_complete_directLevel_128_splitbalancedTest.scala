package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector {
            import scala.collection.immutable.vectorutils.generated.rrbvector._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVector_complete_directLevel_128_splitbalancedTest[A] extends VectorSpec[A] with RRBVector_complete_directLevel_128_splitbalancedGenerator[A]

            class IntRRBVector_complete_directLevel_128_splitbalancedTest extends RRBVector_complete_directLevel_128_splitbalancedTest[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_complete_directLevel_128_splitbalancedTest extends RRBVector_complete_directLevel_128_splitbalancedTest[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}