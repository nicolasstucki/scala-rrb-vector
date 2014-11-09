package scala.collection.immutable.vectorbenchmarks.rrbvector
import org.scalameter.Gen

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.AppendBenchmarks
import scala.collection.immutable.vectorbenchmarks.RRBVectorBenchmark
import scala.collection.immutable.vectorutils._


abstract class RRBVectorAbstractAppendBenchmark[A] extends AppendBenchmarks[A] with RRBVectorBenchmark[A]

class RRBVectorAppendIntBenchmark extends RRBVectorAbstractAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {
    def sum1(vec: RRBVector[Int], times: Int): Int = {
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

    def sum8(vec: RRBVector[Int], times: Int): Int = {
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

    def sum(vec: RRBVector[Int], n: Int, times: Int): Int = {
        var i = 0
        var v = vec
        var sum = 0
        while (i < times) {
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

class RRBVectorAppendStringBenchmark extends RRBVectorAbstractAppendBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""
    def sum1(vec: Vec, times: Int): Int = {
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

    def sum8(vec: RRBVector[String], times: Int): Int = {
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

    def sum(vec: RRBVector[String], n: Int, times: Int): Int = {
        var i = 0
        var v = vec
        var sum = 0
        while (i < times) {
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