package scala {
  package collection {
    package immutable {
      package vectorutils {
        package generated {
          package rrbvector {
            package quick {
              trait RRBVector_quick_32Generator[A] extends BaseVectorGenerator[A] {
                import scala.collection.immutable.generated.rrbvector.quick._;
                override type Vec = RRBVector_quick_32[A];
                final def vectorClassName: String = "RRBVector_quick_32";
                final override def tabulatedVector(n: Int): Vec = RRBVector_quick_32.tabulate(n)(element);
                final override def rangedVector(start: Int, end: Int): Vec = RRBVector_quick_32.range(start, end).map(element);
                final override def emptyVector: Vec = RRBVector_quick_32.empty[A];
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