package scala {
  package collection {
    package immutable {
      package vectorutils {
        package generated {
          package rrbvector {
            package fullblocks {
              trait GenRRBVectorFullBlocksGenerator[A] extends BaseVectorGenerator[A] {
                import scala.collection.immutable.generated.rrbvector.fullblocks._;
                override type Vec = GenRRBVectorFullBlocks[A];
                final def vectorClassName: String = "GenRRBVectorFullBlocks";
                final override def tabulatedVector(n: Int): Vec = GenRRBVectorFullBlocks.tabulate(n)(element);
                final override def emptyVector: Vec = GenRRBVectorFullBlocks.empty[A];
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