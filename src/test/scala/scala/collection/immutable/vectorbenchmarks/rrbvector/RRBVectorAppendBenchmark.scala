package scala.collection.immutable.vectorbenchmarks.rrbvector
import org.scalameter.Gen

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.AppendBenchmarks
import scala.collection.immutable.vectorbenchmarks.RRBVectorBenchmark
import scala.collection.immutable.vectorutils._


abstract class RRBVectorAbstractAppendBenchmark[A] extends AppendBenchmarks[A] with RRBVectorBenchmark[A]

class RRBVectorAppendIntBenchmark extends RRBVectorAbstractAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {
    def sum32(vec: RRBVector[Int], times: Int): Int = {
        var i = 0
        var v = vec
        var sum = 0
        while (i < times) {
            v = vec :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7
            sum += v.length
            i += 1
        }
        sum
    }

}

class RRBVectorAppendAnyRefBenchmark extends RRBVectorAbstractAppendBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator {
    val obj = new Object

    def sum32(vec: RRBVector[AnyRef], times: Int): Int = {
        var i = 0
        var v = vec
        var sum = 0
        while (i < times) {
            v = vec :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj
            sum += v.length
            i += 1
        }
        sum
    }

}