package scala.collection.immutable.vectorbenchmarks.rrbvector.balanced

import org.scalameter.Executor
import scala.collection.immutable.vectorutils.VectorGeneratorType

class RRBVectorIntMemoryAllocation extends RRBVectorAbstractBenchmark[Int] with VectorGeneratorType.IntGenerator {

    override def measurer = new Executor.Measurer.MemoryFootprint

    performanceOfVectors { height =>

        val (from, to, by) = fromToBy(height)

        performance of "MemoryFootprint (KB not ms)" in {
            performance of s"Height $height" in {
                using(generateVectors(from, to, by)) curve vectorName in { vec => vec}
            }
        }
    }
}
