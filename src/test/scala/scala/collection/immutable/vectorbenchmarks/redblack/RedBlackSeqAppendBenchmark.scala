package scala.collection.immutable.vectorbenchmarks.redblack

import scala.collection.immutable.redblack.RedBlackSeq
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.AppendBenchmarks
import scala.collection.immutable.vectorutils.VectorGeneratorType

abstract class RedBlackSeqAbstractAppendBenchmark[A] extends AppendBenchmarks[A] with RedBlackSeqBenchmark[A]

class RedBlackSeqAppendIntBenchmark extends RedBlackSeqAbstractAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def append(vec: RedBlackSeq[Int], n: Int): Int = {
        var v = vec
        var i = 0
        while (i < n) {
            v = vec :+ 0
            i += 1
        }
        v.length
    }

}

class RedBlackSeqAppendStringBenchmark extends RedBlackSeqAbstractAppendBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def append(vec: RedBlackSeq[String], n: Int): Int = {
        var v = vec
        var i = 0
        while (i < n) {
            v = vec :+ ref
            i += 1
        }
        v.length
    }
}