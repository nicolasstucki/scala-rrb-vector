package scala.collection.immutable.vectorbenchmarks.vector

import org.scalameter.{Gen, PerformanceTest}
import org.scalameter.PerformanceTest.OfflineRegressionReport

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.AppendBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class VectorAbstractAppendBenchmark[A] extends AppendBenchmarks[A] with VectorBenchmark[A]

class VectorAppendIntBenchmark extends VectorAbstractAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def append(vec: Vector[Int], n: Int, times: Int): Int = {
        var i = 0
        var sum = 0
        while (i < times) {
            var v = vec
            var j = 0
            while (j<n) {
                v = vec :+ 0
                j += 1
            }
            sum += v.length
            i += 1
        }
        sum
    }

}

class VectorAppendStringBenchmark extends VectorAbstractAppendBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def append(vec: Vector[String], n: Int, times: Int): Int = {
        var i = 0
        var sum = 0
        while (i < times) {
            var v = vec
            var j = 0
            while (j<n) {
                v = vec :+ ref
                j += 1
            }
            sum += v.length
            i += 1
        }
        sum
    }
}