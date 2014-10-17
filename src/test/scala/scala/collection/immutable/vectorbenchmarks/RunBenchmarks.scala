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
    include[generated.rrbvector.closedblocks.fullrebalance.GenRRBVectorClosedBlocksFullRebalanceAppendIntBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.GenRRBVectorClosedBlocksQuickRebalanceAppendIntBenchmark]
}

class RunApplyBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorApplyIntBenchmark]
    include[rrbvector.RRBVectorApplyIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.GenRRBVectorClosedBlocksFullRebalanceApplyIntBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.GenRRBVectorClosedBlocksQuickRebalanceApplyIntBenchmark]
}

class RunBuilderBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorBuilderIntBenchmark]
    include[rrbvector.RRBVectorBuilderIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.GenRRBVectorClosedBlocksFullRebalanceBuilderIntBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.GenRRBVectorClosedBlocksQuickRebalanceBuilderIntBenchmark]
}

class RunConcatenationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorConcatenationIntBenchmark]
    include[rrbvector.RRBVectorConcatenationIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.GenRRBVectorClosedBlocksFullRebalanceConcatenationIntBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.GenRRBVectorClosedBlocksQuickRebalanceConcatenationIntBenchmark]
}

class RunIterationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIterationIntBenchmark]
    include[rrbvector.RRBVectorIterationIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.GenRRBVectorClosedBlocksFullRebalanceIterationIntBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.GenRRBVectorClosedBlocksQuickRebalanceIterationIntBenchmark]
}

class RunSplitBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorSplitIntBenchmark]
    include[rrbvector.RRBVectorSplitIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.GenRRBVectorClosedBlocksFullRebalanceSplitIntBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.GenRRBVectorClosedBlocksQuickRebalanceSplitIntBenchmark]
}

class RunMemoryAllocation extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIntMemoryAllocation]
    include[rrbvector.RRBVectorIntMemoryAllocation]
    include[generated.rrbvector.closedblocks.fullrebalance.GenRRBVectorClosedBlocksFullRebalanceIntMemoryAllocationBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.GenRRBVectorClosedBlocksQuickRebalanceIntMemoryAllocationBenchmark]
}