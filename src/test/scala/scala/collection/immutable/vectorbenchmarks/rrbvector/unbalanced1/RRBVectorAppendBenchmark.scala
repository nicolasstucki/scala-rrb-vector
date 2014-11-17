package scala.collection.immutable.vectorbenchmarks.rrbvector.unbalanced1

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.AppendBenchmarks
import scala.collection.immutable.vectorutils._


abstract class RRBVectorAbstractAppendBenchmark[A] extends AppendBenchmarks[A] with RRBVectorAbstractBenchmark[A]

class RRBVectorAppendIntBenchmark extends RRBVectorAbstractAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def append(vec: RRBVector[Int], n: Int, times: Int): Int = {
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

class RRBVectorAppendStringBenchmark extends RRBVectorAbstractAppendBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def append(vec: RRBVector[String], n: Int, times: Int): Int = {
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