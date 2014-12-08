package scala.collection.immutable.vectorbenchmarks.mbrrbvector.balanced

import scala.collection.immutable.mbrrbvector.MbRRBVector
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.PrependBenchmarks
import scala.collection.immutable.vectorutils._


abstract class MbRRBVectorAbstractPrependBenchmark[@miniboxed A] extends PrependBenchmarks[A] with MbRRBVectorAbstractBenchmark[A]

class MbRRBVectorPrependIntBenchmark extends MbRRBVectorAbstractPrependBenchmark[Int] with VectorGeneratorType.IntGenerator {


    def prepend(vec: MbRRBVector[Int], n: Int, times: Int): Int = {
        var i = 0
        var v: MbRRBVector[Int] = vec
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

class MbRrbVectorPrependStringBenchmark extends MbRRBVectorAbstractPrependBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def prepend(vec: MbRRBVector[String], n: Int, times: Int): Int = {
        var i = 0
        var v: MbRRBVector[String] = null
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