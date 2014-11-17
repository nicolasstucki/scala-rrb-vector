package scala.collection.immutable.vectorbenchmarks.rrbvector.unbalanced1

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.PrependBenchmarks
import scala.collection.immutable.vectorutils._


abstract class RRBVectorAbstractPrependBenchmark[A] extends PrependBenchmarks[A] with RRBVectorAbstractBenchmark[A]

class RRBVectorPrependIntBenchmark extends RRBVectorAbstractPrependBenchmark[Int] with VectorGeneratorType.IntGenerator {


    def prepend(vec: RRBVector[Int], n: Int, times: Int): Int = {
        var i = 0
        var v: RRBVector[Int] = vec
        var sum = 0
        while (i < times) {
            v = vec
            var j = 0
            while (j < n) {
                v = 0 +: vec
                j += 1
            }
            sum += v.length
            i += 1
        }
        sum
    }

}

class RRBVectorPrependStringBenchmark extends RRBVectorAbstractPrependBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def prepend(vec: RRBVector[String], n: Int, times: Int): Int = {
        var i = 0
        var v: RRBVector[String] = null
        var sum = 0
        while (i < times) {
            v = vec
            var j = 0
            while (j < n) {
                v = ref +: vec
                j += 1
            }
            sum += v.length
            i += 1
        }
        sum
    }
}