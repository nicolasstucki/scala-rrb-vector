package scala.collection.immutable.vectorbenchmarks.fingertree

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.PrependBenchmarks
import scala.collection.immutable.vectorutils._


abstract class FingerTreeAbstractPrependBenchmark[A] extends PrependBenchmarks[A] with FingerTreeBenchmark[A]

class FingerTreePrependIntBenchmark extends FingerTreeAbstractPrependBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def prepend(vec: FingerTree[Int], n: Int, times: Int): Int = {
        var i = 0
        var v: FingerTree[Int] = vec
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

class FingerTreePrependStringBenchmark extends FingerTreeAbstractPrependBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def prepend(vec: FingerTree[String], n: Int, times: Int): Int = {
        var i = 0
        var v: FingerTree[String] = null
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