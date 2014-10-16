package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.fullblocks {
            import scala.collection.immutable.vectorutils.generated.rrbvector.fullblocks._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class GenRRBVectorFullBlocksTest[A] extends VectorSpec[A] with GenRRBVectorFullBlocksGenerator[A]

            class IntGenRRBVectorFullBlocksTest extends GenRRBVectorFullBlocksTest[Int] with VectorGeneratorType.IntGenerator

            class AnyRefGenRRBVectorFullBlocksTest extends GenRRBVectorFullBlocksTest[AnyRef] with VectorGeneratorType.AnyRefGenerator
          }
        }
      }
    }
  }
}