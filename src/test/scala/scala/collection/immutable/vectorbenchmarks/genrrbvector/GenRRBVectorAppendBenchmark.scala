package scala.collection.immutable.vectorbenchmarks.genrrbvector

import scala.collection.immutable.genrrbvector.GenRRBVector
import scala.collection.immutable.vectorbenchmarks._
import scala.collection.immutable.vectorbenchmarks.genericbenchmarks.AppendBenchmarks
import scala.collection.immutable.vectorutils._


abstract class GenRRBVectorAbstractAppendBenchmark[A] extends AppendBenchmarks[A] with GenRRBVectorBenchmark[A]

class GenRRBVectorAppendIntBenchmark extends GenRRBVectorAbstractAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {
    def sum32(vec: GenRRBVector[Int], times: Int): Int = {
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

class GenRRBVectorAppendAnyRefBenchmark extends GenRRBVectorAbstractAppendBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator {
    val obj = new Object

    def sum32(vec: GenRRBVector[AnyRef], times: Int): Int = {
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