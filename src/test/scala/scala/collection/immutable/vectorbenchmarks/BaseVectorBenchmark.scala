package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.PerformanceTest.OfflineRegressionReport
import org.scalameter._
import org.scalameter.utils.Tree

import scala.collection.immutable.vectorutils.BaseVectorGenerator


trait BaseVectorBenchmark[A] extends OfflineRegressionReport with BaseVectorGenerator[A] {

    /* config */

    def minHeight = 4

    def maxHeight = 4

    def points = 8

    def independentSamples = 8

    def benchRunsPerSample = 64

    def benchRuns = independentSamples * benchRunsPerSample

    def memoryInHeapSeq = Seq("16g") //, "512m")

    def generateVectorsFixedSum(sum: Int): Gen[(Vec, Vec)] = {
        val min = sum / points
        val max = sum - min
        for {
            size <- sizes(min, max, (max - min) / points, "size")
        } yield (tabulatedVector(size), tabulatedVector(sum - size))
    }

    def generateVectorsFixedLHS(fixedSize: Int, from: Int, to: Int, by: Int): Gen[(Vec, Vec)] = {
        for {
            size <- sizes(from, to, by, "size")
        } yield (tabulatedVector(fixedSize), tabulatedVector(size))
    }

    def generateVectorsFixedRHS(fixedSize: Int, from: Int, to: Int, by: Int): Gen[(Vec, Vec)] = {
        for {
            size <- sizes(from, to, by, "size")
        } yield (tabulatedVector(size), tabulatedVector(fixedSize))
    }

    /* data */

    def sizes(from: Int, to: Int, by: Int, sizesName: String) = {
        def expSeq(current: Int, seq: List[Int] = Nil): List[Int] =
            if (current <= to) {
                val newVal = (1.5 * current).toInt
                expSeq(newVal, newVal :: seq)
            } else (1.5 * current).toInt :: seq
        Gen.enumeration(sizesName)(expSeq(4): _*)
//        Gen.range(sizesName)(to - 1, from, -by)
    }

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

