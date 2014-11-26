package scala.collection.immutable.vectorbenchmarks.rrbvector.unbalanced1

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.AppendBenchmarks
import scala.collection.immutable.vectorutils._


abstract class RRBVectorAbstractAppendBenchmark[A] extends AppendBenchmarks[A] with RRBVectorAbstractBenchmark[A]

class RRBVectorAppendIntBenchmark extends RRBVectorAbstractAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {

    def append(vec: RRBVector[Int], n: Int): Int = {
        var v = vec
        var i = 0
        while (i < n) {
            v = vec :+ 0
            i += 1
        }
        v.length
    }

}

class RRBVectorAppendStringBenchmark extends RRBVectorAbstractAppendBenchmark[String] with VectorGeneratorType.StringGenerator {
    val ref = ""

    def append(vec: RRBVector[String], n: Int): Int = {
        var v = vec
        var i = 0
        while (i < n) {
            v = vec :+ ref
            i += 1
        }
        v.length
    }
}