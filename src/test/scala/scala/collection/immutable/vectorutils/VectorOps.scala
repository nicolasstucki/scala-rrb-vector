package scala.collection
package immutable.vectorutils

/**
 * Created by nicolasstucki on 06/10/2014.
 */
trait VectorOps[A] {
    type Vec <: IndexedSeq[A]

    def element(n: Int): A

    def newBuilder(): mutable.Builder[A, Vec]

    def emptyVector: Vec

    def plus(vec: Vec, elem: A): Vec

    def plus(elem: A, vec: Vec): Vec

    def plusPlus(vec1: Vec, vec2: Vec): Vec

    def take(vec: Vec, n: Int): Vec

    def drop(vec: Vec, n: Int): Vec
}
