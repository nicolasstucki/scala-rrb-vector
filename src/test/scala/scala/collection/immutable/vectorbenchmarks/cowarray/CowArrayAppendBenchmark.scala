package scala.collection.immutable.vectorbenchmarks.cowarray

import scala.collection.immutable.cowarray.CowArray
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.AppendBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class CowArrayAbstractAppendBenchmark[A] extends AppendBenchmarks[A] with CowArrayBenchmark[A]

class CowArrayAppendIntBenchmark extends CowArrayAbstractAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def append(vec: CowArray[Int], n: Int): Int = {
        var v = vec
        var i = 0
        while (i < n) {
            v = vec :+ 0
            i += 1
        }
        v.length
    }

}

class CowArrayAppendStringBenchmark extends CowArrayAbstractAppendBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def append(vec: CowArray[String], n: Int): Int = {
        var v = vec
        var i = 0
        while (i < n) {
            v = vec :+ ref
            i += 1
        }
        v.length
    }
}