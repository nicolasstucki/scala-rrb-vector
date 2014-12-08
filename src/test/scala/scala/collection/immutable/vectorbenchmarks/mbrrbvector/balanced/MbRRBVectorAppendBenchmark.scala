package scala.collection.immutable.vectorbenchmarks.mbrrbvector.balanced

import scala.collection.immutable.mbrrbvector.MbRRBVector
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.AppendBenchmarks
import scala.collection.immutable.vectorutils._


abstract class MbRRBVectorAbstractAppendBenchmark[@miniboxed A] extends AppendBenchmarks[A] with MbRRBVectorAbstractBenchmark[A]

class MbRRBVectorAppendIntBenchmark extends MbRRBVectorAbstractAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def append(vec: MbRRBVector[Int], n: Int): Int = {
        var i = 0
        var v = vec
        while (i < n) {
            v = vec :+ 0
            i += 1
        }
        v.length
    }

}

class MbRRBVectorAppendStringBenchmark extends MbRRBVectorAbstractAppendBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def append(vec: MbRRBVector[String], n: Int): Int = {
        var v = vec
        var i = 0
        while (i < n) {
            v = vec :+ ref
            i += 1
        }
        v.length
    }
}