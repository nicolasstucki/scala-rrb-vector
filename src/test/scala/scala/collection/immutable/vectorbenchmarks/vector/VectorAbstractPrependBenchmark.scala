package scala.collection.immutable.vectorbenchmarks.vector

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.PrependBenchmarks
import scala.collection.immutable.vectorutils._


abstract class VectorAbstractPrependBenchmark[A] extends PrependBenchmarks[A] with VectorBenchmark[A]

class VectorPrependIntBenchmark extends VectorAbstractPrependBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def prepend(vec: Vector[Int], n: Int, times: Int): Int = {
        var i = 0
        var v: Vector[Int] = vec
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

class VectorPrependStringBenchmark extends VectorAbstractPrependBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def prepend(vec: Vector[String], n: Int, times: Int): Int = {
        var i = 0
        var v: Vector[String] = null
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