package org.scalameter
package vectors


import collection._
import Key._


trait Collections extends PerformanceTest {

    /* data */

    def sizes(from: Int, to: Int, by: Int) = Gen.range("size")(from, to, by)

    def sized[T, Repr](g: Gen[Repr])(implicit ev: Repr <:< Traversable[T]): Gen[(Int, Repr)] = for (xs <- g) yield (xs.size, xs)

    /* sequences */


    def vectors(from: Int, to: Int, by: Int) = for {
        size <- sizes(from, to, by)
    } yield (0 until size).toVector

    def rbvectors(from: Int, to: Int, by: Int) = for {
        size <- sizes(from, to, by)
    } yield scala.collection.immutable.rrbvector.Vector.range(0, size)

}
