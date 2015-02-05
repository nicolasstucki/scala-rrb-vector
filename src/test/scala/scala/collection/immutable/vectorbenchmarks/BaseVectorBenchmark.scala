package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.PerformanceTest.OfflineRegressionReport
import org.scalameter._
import org.scalameter.utils.Tree

import scala.collection.immutable.vectorutils.BaseVectorGenerator


trait BaseVectorBenchmark[A] extends OfflineRegressionReport with BaseVectorGenerator[A] {

    /* config */

    def minHeight = 3

    def maxHeight = 3

    def points = 32

    def independentSamples = 16

    def benchRunsPerSample = 64

    def benchRuns = independentSamples * benchRunsPerSample

    def memoryInHeapSeq = Seq("16g") //, "512m")

    /* data */

    def sizes(from: Int, to: Int, by: Int, sizesName: String) = Gen.range(sizesName)(to - 1, from, -by)

    def sized[T, Repr](g: Gen[Repr])(implicit ev: Repr <:< Traversable[T]): Gen[(Int, Repr)] = for (xs <- g) yield (xs.size, xs)

    /* sequences */

    def generateVectors(from: Int, to: Int, by: Int, sizesName: String = "sizes"): Gen[Vec]

    def fromToBy(height: Int) = (
      math.pow(32, height - 1).toInt + 1,
      math.pow(32, height).toInt,
      math.max(math.pow(32, height).toInt / points, 1)
      )

    def performanceOfVectors(benchmarks: Int => Unit): Unit = {
        for (memoryInHeap <- memoryInHeapSeq) {
            performance of s"$vectorName benchmarks (-Xms$memoryInHeap -Xmx$memoryInHeap)" config(
              Key.exec.benchRuns -> benchRuns,
              // Key.verbose -> false,
              Key.exec.independentSamples -> independentSamples,
              Key.exec.jvmflags -> s"-Xms$memoryInHeap -Xmx$memoryInHeap" // "-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining" "-XX:+PrintCompilation",
              ) in {
                for (height <- maxHeight to minHeight by -1) {
                    benchmarks(height)
                }
            }
        }
    }
}

