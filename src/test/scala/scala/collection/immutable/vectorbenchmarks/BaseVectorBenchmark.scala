package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.PerformanceTest.OfflineRegressionReport
import org.scalameter.{Gen, Key}

import scala.collection.immutable.vectorutils.BaseVectorGenerator


trait BaseVectorBenchmark[A] extends OfflineRegressionReport with BaseVectorGenerator[A] {

    /* config */

    val minHeight = 1
    val maxHeight = 3
    val points = 16
    val benchRuns = 32
    val independentSamples = 1


    /* data */

    def sizes(from: Int, to: Int, by: Int) = Gen.range("size")(from, to, by)

    def sized[T, Repr](g: Gen[Repr])(implicit ev: Repr <:< Traversable[T]): Gen[(Int, Repr)] = for (xs <- g) yield (xs.size, xs)

    /* sequences */

    def generateVectors(from: Int, to: Int, by: Int): Gen[Vec]

    def fromToBy(height: Int) = (
      math.pow(32, height - 1).toInt + 1,
      math.pow(32, height).toInt,
      math.max(math.pow(32, height).toInt / points, 1)
      )

    def performanceOfVectors(benchmarks: Int => Unit): Unit = {
        performance of "vector benchmarks" config(
          Key.exec.benchRuns -> benchRuns,
          Key.exec.independentSamples -> independentSamples,
          Key.exec.jvmflags -> "-XX:+PrintCompilation"
          ) in {
            for (height <- minHeight to maxHeight) {
                benchmarks(height)
            }
        }
    }
}
