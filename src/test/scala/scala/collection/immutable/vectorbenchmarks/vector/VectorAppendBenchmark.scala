package scala.collection.immutable.vectorbenchmarks.vector

import org.scalameter.{Gen, PerformanceTest}
import org.scalameter.PerformanceTest.OfflineRegressionReport

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.AppendBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class VectorAbstractAppendBenchmark[A] extends AppendBenchmarks[A] with VectorBenchmark[A]

class VectorAppendIntBenchmark extends VectorAbstractAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def append(vec: Vector[Int], n: Int): Int = {
        var v = vec
        var i = 0
        while (i < n) {
            v = vec :+ 0
            i += 1
        }
        v.length
    }

}

class VectorAppendStringBenchmark extends VectorAbstractAppendBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def append(vec: Vector[String], n: Int): Int = {
        var v = vec
        var i = 0
        while (i < n) {
            v = vec :+ ref
            i += 1
        }
        v.length
    }
}