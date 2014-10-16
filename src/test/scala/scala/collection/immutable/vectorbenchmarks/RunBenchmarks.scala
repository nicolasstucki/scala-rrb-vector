package scala.collection.immutable.vectorbenchmarks

import org.scalameter.PerformanceTest


class RunAllBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[RunAppendBenchmarks]
    include[RunApplyBenchmarks]
    include[RunBuilderBenchmarks]
    include[RunConcatenationBenchmarks]
    include[RunIterationBenchmarks]
    include[RunMemoryAllocation]

}


class RunAppendBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorAppendIntBenchmark]
    include[rrbvector.RRBVectorAppendIntBenchmark]
    include[generated.rrbvector.closedblocks.GenRRBVector1AppendIntBenchmark]

}

class RunApplyBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorApplyIntBenchmark]
    include[rrbvector.RRBVectorApplyIntBenchmark]
    include[generated.rrbvector.closedblocks.GenRRBVector1ApplyIntBenchmark]
//    include[generated.rrbvector.fullblocks.GenRRBVector2ApplyIntBenchmark]
}

class RunBuilderBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorBuilderIntBenchmark]
    include[rrbvector.RRBVectorBuilderIntBenchmark]
    include[generated.rrbvector.closedblocks.GenRRBVector1BuilderIntBenchmark]
}

class RunConcatenationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorConcatenationIntBenchmark]
    include[rrbvector.RRBVectorConcatenationIntBenchmark]
}

class RunIterationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIterationIntBenchmark]
    include[rrbvector.RRBVectorIterationIntBenchmark]
    include[generated.rrbvector.closedblocks.GenRRBVector1IterationIntBenchmark]
}

class RunSplitBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorSplitIntBenchmark]
    include[rrbvector.RRBVectorSplitIntBenchmark]
    include[generated.rrbvector.closedblocks.GenRRBVector1SplitIntBenchmark]
}

class RunMemoryAllocation extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIntMemoryAllocation]
    include[rrbvector.RRBVectorIntMemoryAllocation]
    include[generated.rrbvector.closedblocks.GenRRBVector1IntMemoryAllocationBenchmark]
}