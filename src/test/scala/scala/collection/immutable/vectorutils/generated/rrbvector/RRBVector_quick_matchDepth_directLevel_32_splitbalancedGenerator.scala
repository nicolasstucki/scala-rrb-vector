package scala {
  package collection {
    package immutable {
      package vectorutils {
        package generated {
          package rrbvector {
            trait RRBVector_quick_matchDepth_directLevel_32_splitbalancedGenerator[A] extends BaseVectorGenerator[A] {
              import scala.collection.immutable.generated.rrbvector._;
              override type Vec = RRBVector_quick_matchDepth_directLevel_32_splitbalanced[A];
              final def vectorClassName: String = "RRBVector_quick_matchDepth_directLevel_32_splitbalanced";
              final override def tabulatedVector(n: Int): Vec = RRBVector_quick_matchDepth_directLevel_32_splitbalanced.tabulate(n)(element);
              final override def rangedVector(start: Int, end: Int): Vec = RRBVector_quick_matchDepth_directLevel_32_splitbalanced.range(start, end).map(element);
              final override def emptyVector: Vec = RRBVector_quick_matchDepth_directLevel_32_splitbalanced.empty[A];
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