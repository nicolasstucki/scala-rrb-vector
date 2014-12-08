package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import org.scalameter._

import scala.collection.immutable.vectorbenchmarks.BaseVectorBenchmark


abstract class MemoryAllocation[@miniboxed A] extends BaseVectorBenchmark[A] {

    override def measurer = new Executor.Measurer.MemoryFootprint

    performanceOfVectors { height =>

        val (from, to, by) = fromToBy(height)

        var sideeffect = 0
        performance of "MemoryFootprint (KB not ms)" in {
            performance of s"Height $height" in {
                using(generateVectors(from, to, by)) curve vectorName in { vec => vec}
            }
        }
    }
}