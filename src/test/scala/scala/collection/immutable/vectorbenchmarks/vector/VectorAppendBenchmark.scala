package scala.collection.immutable.vectorbenchmarks.vector

import org.scalameter.{Gen, PerformanceTest}
import org.scalameter.PerformanceTest.OfflineRegressionReport

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.AppendBenchmarks
import scala.collection.immutable.vectorbenchmarks.VectorBenchmark
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class VectorAbstractAppendBenchmark[A] extends AppendBenchmarks[A] with VectorBenchmark[A]

class VectorAppendIntBenchmark extends VectorAbstractAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def sum1(vec: Vector[Int], times: Int): Int = {
        var i = 0
        var v = vec
        var sum = 0
        while (i < times) {
            v = vec :+ 0
            sum += v.length
            i += 1
        }
        sum
    }

    def sum8(vec: Vector[Int], times: Int): Int = {
        var i = 0
        var v = vec
        var sum = 0
        while (i < times) {
            v = vec :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7
            sum += v.length
            i += 1
        }
        sum
    }

    def sum32(vec: Vector[Int], times: Int): Int = {
        var i = 0
        var v = vec
        var sum = 0
        while (i < times) {
            v = vec :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7
            sum += v.length
            i += 1
        }
        sum
    }


}

class VectorAppendStringBenchmark extends VectorAbstractAppendBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def sum1(vec: Vector[String], times: Int): Int = {
        var i = 0
        var v = vec
        var sum = 0
        while (i < times) {
            v = vec :+ ref
            sum += v.length
            i += 1
        }
        sum
    }
    def sum8(vec: Vector[String], times: Int): Int = {
        var i = 0
        var v = vec
        var sum = 0
        while (i < times) {
            v = vec :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref
            sum += v.length
            i += 1
        }
        sum
    }

    def sum32(vec: Vector[String], times: Int): Int = {
        var i = 0
        var v = vec
        var sum = 0
        while (i < times) {
            v = vec :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref
            sum += v.length
            i += 1
        }
        sum
    }

}