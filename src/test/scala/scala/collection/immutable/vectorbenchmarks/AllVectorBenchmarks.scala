package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.PerformanceTest


class AllVectorBenchmarks extends PerformanceTest.OfflineReport {

        include[VectorApplyBenchmarks]
    //    include[VectorAppendBenchmarks]
    //    include[VectorConcatenatedBenchmarks]
    //    include[VectorForeachBenchmarks]
//    include[VectorIteratorBenchmarks]
    //    include[VectorMapBenchmarks]
    //    include[VectorPrependBenchmarks]
    //    include[VectorUpdatedBenchmarks]

}
