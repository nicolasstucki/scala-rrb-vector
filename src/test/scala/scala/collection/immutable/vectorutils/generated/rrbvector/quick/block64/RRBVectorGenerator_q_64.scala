package scala {
  package collection {
    package immutable {
      package vectorutils {
        package generated {
          package rrbvector {
            package quick {
              package block64 {
                trait RRBVectorGenerator_q_64[A] extends BaseVectorGenerator[A] {
                  import scala.collection.immutable.generated.rrbvector.quick.block64._;
                  override type Vec = RRBVector_q_64[A];
                  final def vectorClassName: String = "RRBVector_q_64";
                  final override def newBuilder() = RRBVector_q_64.newBuilder[A];
                  final override def tabulatedVector(n: Int): Vec = RRBVector_q_64.tabulate(n)(element);
                  final override def rangedVector(start: Int, end: Int): Vec = RRBVector_q_64.range(start, end).map(element);
                  final override def emptyVector: Vec = RRBVector_q_64.empty[A];
                  override def iterator(vec: Vec, start: Int, end: Int) = {
                    val it = new RRBVectorIterator_q_64[A](start, end);
                    it.initIteratorFrom(vec);
                    it
                  };
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