package scala {
  package collection {
    package immutable {
      package vectorutils {
        package generated {
          package rrbvector {
            package closedblocks {
              package quickrebalance {
                trait GenRRBVectorClosedBlocksQuickRebalanceGenerator[A] extends BaseVectorGenerator[A] {
                  import scala.collection.immutable.generated.rrbvector.closedblocks.quickrebalance._;
                  override type Vec = GenRRBVectorClosedBlocksQuickRebalance[A];
                  final def vectorClassName: String = "GenRRBVectorClosedBlocksQuickRebalance";
                  final override def tabulatedVector(n: Int): Vec = GenRRBVectorClosedBlocksQuickRebalance.tabulate(n)(element);
                  final override def rangedVector(start: Int, end: Int): Vec = GenRRBVectorClosedBlocksQuickRebalance.range(start, end).map(element);
                  final override def emptyVector: Vec = GenRRBVectorClosedBlocksQuickRebalance.empty[A];
                  final override def plus(vec: Vec, elem: A): Vec = vec.:+(elem);
                  final override def plus(elem: A, vec: Vec): Vec = vec.+:(elem);
                  final override def plusPlus(vec1: Vec, vec2: Vec): Vec = vec1.++(vec2);
                  final override def take(vec: Vec, n: Int): Vec = vec.take(n);
                  final override def drop(vec: Vec, n: Int): Vec = vec.drop(n)
                }
              }
            }
          }
        }
      }
    }
  }
}