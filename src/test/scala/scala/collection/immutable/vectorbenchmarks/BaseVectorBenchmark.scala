package scala.collection.immutable
package vectorbenchmarks

import org.scalameter
import org.scalameter.PerformanceTest.OfflineRegressionReport
import org.scalameter.{Gen, Key}

import scala.collection.immutable.vectorutils.BaseVectorGenerator


trait BaseVectorBenchmark[A] extends OfflineRegressionReport with BaseVectorGenerator[A] {

    /* config */

    def minHeight = 1
    def maxHeight = 3
    def points = 8
    def independentSamples = 32
    def benchRunsPerSample = 32
    def benchRuns = independentSamples * benchRunsPerSample
    def memoryInHeapSeq = Seq("16g", "512m")

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
        for (memoryInHeap <- memoryInHeapSeq) {
            performance of s"vector benchmarks (-Xms$memoryInHeap -Xmx$memoryInHeap)" config(
              Key.exec.benchRuns -> benchRuns,
              Key.exec.independentSamples -> independentSamples,
              Key.exec.jvmflags -> s"-Xms$memoryInHeap -Xmx$memoryInHeap" // "-XX:+PrintCompilation",
              ) in {
                for (height <- maxHeight to minHeight by -1) {
                    benchmarks(height)
                }
            }
        }
    }
}
