package scala.collection.immutable.vectorbenchmarks.fingertree

import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.AppendBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType


abstract class FingerTreeAbstractAppendBenchmark[A] extends AppendBenchmarks[A] with FingerTreeBenchmark[A]

class FingerTreeAppendIntBenchmark extends FingerTreeAbstractAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def append(vec: FingerTree[Int], n: Int): Int = {
        var v = vec
        var i = 0
        while (i < n) {
            v = vec :+ 0
            i += 1
        }
        v.length
    }

}

class FingerTreeAppendStringBenchmark extends FingerTreeAbstractAppendBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def append(vec: FingerTree[String], n: Int): Int = {
        var v = vec
        var i = 0
        while (i < n) {
            v = vec :+ ref
            i += 1
        }
        v.length
    }
}