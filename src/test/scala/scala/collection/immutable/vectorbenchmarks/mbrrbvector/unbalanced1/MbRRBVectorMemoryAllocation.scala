package scala.collection.immutable.vectorbenchmarks.mbrrbvector.unbalanced1

import org.scalameter.Executor

import scala.collection.immutable.vectorutils.VectorGeneratorType


class MbRRBVectorIntMemoryAllocation extends MbRRBVectorAbstractBenchmark[Int] with VectorGeneratorType.IntGenerator{

    override def measurer = new Executor.Measurer.MemoryFootprint

    performanceOfVectors { height =>

        val (from, to, by) = fromToBy(height)

        var sideeffect = 0
        performance of "MemoryFootprint (KB not ms)" in {
            performance of s"Height $height" in {
                using(generateIntVectors(from, to, by)) curve vectorName in { vec => vec}
            }
        }
    }
}
