package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.closedblocks.fullrebalance {
            import scala.collection.immutable.vectorutils.generated.rrbvector.closedblocks.fullrebalance._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class GenRRBVectorClosedBlocksFullRebalanceTest[A] extends VectorSpec[A] with GenRRBVectorClosedBlocksFullRebalanceGenerator[A]

            class IntGenRRBVectorClosedBlocksFullRebalanceTest extends GenRRBVectorClosedBlocksFullRebalanceTest[Int] with VectorGeneratorType.IntGenerator

            class AnyRefGenRRBVectorClosedBlocksFullRebalanceTest extends GenRRBVectorClosedBlocksFullRebalanceTest[AnyRef] with VectorGeneratorType.AnyRefGenerator
          }
        }
      }
    }
  }
}