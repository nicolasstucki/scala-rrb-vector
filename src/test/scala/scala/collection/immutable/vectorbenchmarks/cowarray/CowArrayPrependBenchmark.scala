package scala.collection.immutable.vectorbenchmarks.cowarray

import scala.collection.immutable.cowarray.CowArray
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.PrependBenchmarks
import scala.collection.immutable.vectorutils._

abstract class CowArrayAbstractPrependBenchmark[A] extends PrependBenchmarks[A] with CowArrayBenchmark[A]

class CowArrayPrependIntBenchmark extends CowArrayAbstractPrependBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def prepend(vec: CowArray[Int], n: Int, times: Int): Int = {
        var i = 0
        var v: CowArray[Int] = vec
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

class CowArrayPrependStringBenchmark extends CowArrayAbstractPrependBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def prepend(vec: CowArray[String], n: Int, times: Int): Int = {
        var i = 0
        var v: CowArray[String] = null
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