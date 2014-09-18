package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.{Gen, Key, PerformanceTest}


trait BaseVectorBenchmark extends PerformanceTest {

    /* config */

    val minHeight = 1
    val maxHeight = 3
    val points = 64


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

    def fromToBy(height: Int) = (
      math.pow(32, height - 1).toInt + 1,
      math.pow(32, height).toInt,
      math.max(math.pow(32, height).toInt / points, 1)
      )

    def performanceOfVectors(benchmarks: Int => Unit): Unit = {
        performance of "vector benchmarks" config(
          Key.exec.benchRuns -> 12,
          Key.exec.independentSamples -> 1
          ) in {
            for (height <- minHeight to maxHeight) {
                benchmarks(height)
            }
        }
    }
}
