package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.closedblocks {
            import scala.collection.immutable.vectorutils.generated.rrbvector.closedblocks._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class GenRRBVectorClosedBlocksTest[A] extends VectorSpec[A] with GenRRBVectorClosedBlocksGenerator[A]

            class IntGenRRBVectorClosedBlocksTest extends GenRRBVectorClosedBlocksTest[Int] with VectorGeneratorType.IntGenerator

            class AnyRefGenRRBVectorClosedBlocksTest extends GenRRBVectorClosedBlocksTest[AnyRef] with VectorGeneratorType.AnyRefGenerator
          }
        }
      }
    }
  }
}