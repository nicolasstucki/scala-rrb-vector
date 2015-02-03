package scala.collection.immutable.vectorbenchmarks.redblack

import scala.collection.immutable.redblack.RedBlackSeq
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.PrependBenchmarks
import scala.collection.immutable.vectorutils._

abstract class RedBlackSeqAbstractPrependBenchmark[A] extends PrependBenchmarks[A] with RedBlackSeqBenchmark[A]

class RedBlackSeqPrependIntBenchmark extends RedBlackSeqAbstractPrependBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def prepend(vec: RedBlackSeq[Int], n: Int, times: Int): Int = {
        var i = 0
        var v: RedBlackSeq[Int] = vec
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

class RedBlackSeqPrependStringBenchmark extends RedBlackSeqAbstractPrependBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def prepend(vec: RedBlackSeq[String], n: Int, times: Int): Int = {
        var i = 0
        var v: RedBlackSeq[String] = null
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