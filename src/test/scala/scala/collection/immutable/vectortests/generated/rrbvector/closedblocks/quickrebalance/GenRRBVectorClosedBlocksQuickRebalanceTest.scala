package scala {
  package collection {
    package immutable {
      package vectortests {
        package generated {
          package rrbvector.closedblocks.quickrebalance {
            import scala.collection.immutable.vectorutils.generated.rrbvector.closedblocks.quickrebalance._

            import scala.collection.immutable.vectortests._

            import scala.collection.immutable.vectorutils._

            abstract class GenRRBVectorClosedBlocksQuickRebalanceTest[A] extends VectorSpec[A] with GenRRBVectorClosedBlocksQuickRebalanceGenerator[A]

            class IntGenRRBVectorClosedBlocksQuickRebalanceTest extends GenRRBVectorClosedBlocksQuickRebalanceTest[Int] with VectorGeneratorType.IntGenerator

            class AnyRefGenRRBVectorClosedBlocksQuickRebalanceTest extends GenRRBVectorClosedBlocksQuickRebalanceTest[AnyRef] with VectorGeneratorType.AnyRefGenerator
          }
        }
      }
    }
  }
}