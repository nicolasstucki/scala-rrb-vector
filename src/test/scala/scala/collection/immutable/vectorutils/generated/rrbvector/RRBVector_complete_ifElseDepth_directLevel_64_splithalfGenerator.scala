package scala {
  package collection {
    package immutable {
      package vectorutils {
        package generated {
          package rrbvector {
            trait RRBVector_complete_ifElseDepth_directLevel_64_splithalfGenerator[A] extends BaseVectorGenerator[A] {
              import scala.collection.immutable.generated.rrbvector._;
              override type Vec = RRBVector_complete_ifElseDepth_directLevel_64_splithalf[A];
              final def vectorClassName: String = "RRBVector_complete_ifElseDepth_directLevel_64_splithalf";
              final override def tabulatedVector(n: Int): Vec = RRBVector_complete_ifElseDepth_directLevel_64_splithalf.tabulate(n)(element);
              final override def rangedVector(start: Int, end: Int): Vec = RRBVector_complete_ifElseDepth_directLevel_64_splithalf.range(start, end).map(element);
              final override def emptyVector: Vec = RRBVector_complete_ifElseDepth_directLevel_64_splithalf.empty[A];
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