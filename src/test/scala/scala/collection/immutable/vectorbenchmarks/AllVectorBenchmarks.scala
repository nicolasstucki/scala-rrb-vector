package scala.collection.immutable
package vectorbenchmarks

import org.scalameter.PerformanceTest


class AllVectorBenchmarks extends PerformanceTest.OfflineReport {

    include[VectorAppendBenchmarks]
    include[VectorApplyBenchmarks]
    include[VectorBuilderBenchmarks]
    include[VectorConcatBenchmarks]
    include[VectorIterationBenchmarks]

}
