package scala {
  package collection {
    package immutable {
      package vectorutils {
        package generated {
          package rrbvector {
            package closedblocks {
              package fullrebalance {
                trait GenRRBVectorClosedBlocksFullRebalanceGenerator[A] extends BaseVectorGenerator[A] {
                  import scala.collection.immutable.generated.rrbvector.closedblocks.fullrebalance._;
                  override type Vec = GenRRBVectorClosedBlocksFullRebalance[A];
                  final def vectorClassName: String = "GenRRBVectorClosedBlocksFullRebalance";
                  final override def tabulatedVector(n: Int): Vec = GenRRBVectorClosedBlocksFullRebalance.tabulate(n)(element);
                  final override def emptyVector: Vec = GenRRBVectorClosedBlocksFullRebalance.empty[A];
                  final override def plus(vec: Vec, elem: A): Vec = vec.:+(elem);
                  final override def plus(elem: A, vec: Vec): Vec = vec.+:(elem);
                  final override def plusPlus(vec1: Vec, vec2: Vec): Vec = vec1.++(vec2);
                  override def take(vec: Vec, n: Int): Vec = vec.take(n);
                  override def drop(vec: Vec, n: Int): Vec = vec.drop(n)
                }
              }
            }
          }
        }
      }
    }
  }
}