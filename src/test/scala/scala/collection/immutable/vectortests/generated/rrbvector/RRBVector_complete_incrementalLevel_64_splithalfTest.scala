package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector {
            import scala.collection.immutable.vectorutils.generated.rrbvector._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class RRBVector_complete_incrementalLevel_64_splithalfTest[A] extends VectorSpec[A] with RRBVector_complete_incrementalLevel_64_splithalfGenerator[A]

            class IntRRBVector_complete_incrementalLevel_64_splithalfTest extends RRBVector_complete_incrementalLevel_64_splithalfTest[Int] with VectorGeneratorType.IntGenerator

            class StringRRBVector_complete_incrementalLevel_64_splithalfTest extends RRBVector_complete_incrementalLevel_64_splithalfTest[String] with VectorGeneratorType.StringGenerator
          }
        }
      }
    }
  }
}