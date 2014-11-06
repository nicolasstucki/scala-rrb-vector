package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector {
            import scala.collection.immutable.vectorutils.generated.rrbvector._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVector_quick_directLevel_32_splitbalancedTest[A] extends VectorSpec[A] with RRBVector_quick_directLevel_32_splitbalancedGenerator[A]

            class IntRRBVector_quick_directLevel_32_splitbalancedTest extends RRBVector_quick_directLevel_32_splitbalancedTest[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_quick_directLevel_32_splitbalancedTest extends RRBVector_quick_directLevel_32_splitbalancedTest[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}