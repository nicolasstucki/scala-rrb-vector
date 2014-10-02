package scala.collection.immutable.vectorbenchmarks

import org.scalameter._

import scala.collection.immutable.vectorutils.RRBVectorGenerator

class VectorMemoryAllocation extends PerformanceTest.OfflineReport with BaseVectorBenchmark {

    override def measurer = new Executor.Measurer.MemoryFootprint

    implicit val config = RRBVectorGenerator.defaultConfig[Int](x => x)

    performanceOfVectors { height =>

        val (from, to, by) = fromToBy(height)

        var sideeffect = 0
        performance of "MemoryFootprint (KB not ms)" config(
        Key.exec.minWarmupRuns -> 100,
        Key.exec.maxWarmupRuns -> 500
        ) in {
            performance of s"Height $height" in {
                using(vectors(from, to, by)) curve "Vector" in { vec => vec}
                using(rbVectors(from, to, by)) curve "rbVector" in { vec => vec}
                using(rrbVectors(from, to, by)) curve "rrbVector" in { vec => vec}
                // using(extremelyUnbalancedRrbVectors(from, to, by)) curve "extremely unbalanced rrbVector" in { vec => vec}
            }
        }
    }
}