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
    include[generated.rrbvector.closedblocks.fullrebalance.balanced.GenRRBVectorClosedBlocksFullRebalanceAppendIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.xunbalanced.GenRRBVectorClosedBlocksFullRebalanceAppendIntBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.xunbalanced.GenRRBVectorClosedBlocksQuickRebalanceAppendIntBenchmark]
}

class RunApplyBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorApplyIntBenchmark]
    include[rrbvector.RRBVectorApplyIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.balanced.GenRRBVectorClosedBlocksFullRebalanceApplyIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.xunbalanced.GenRRBVectorClosedBlocksFullRebalanceApplyIntBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.xunbalanced.GenRRBVectorClosedBlocksQuickRebalanceApplyIntBenchmark]
}

class RunBuilderBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorBuilderIntBenchmark]
    include[rrbvector.RRBVectorBuilderIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.balanced.GenRRBVectorClosedBlocksFullRebalanceBuilderIntBenchmark]
}

class RunConcatenationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorConcatenationIntBenchmark]
    include[rrbvector.RRBVectorConcatenationIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.balanced.GenRRBVectorClosedBlocksFullRebalanceConcatenationIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.xunbalanced.GenRRBVectorClosedBlocksFullRebalanceConcatenationIntBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.balanced.GenRRBVectorClosedBlocksQuickRebalanceConcatenationIntBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.xunbalanced.GenRRBVectorClosedBlocksQuickRebalanceConcatenationIntBenchmark]
}

class RunIterationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIterationIntBenchmark]
    include[rrbvector.RRBVectorIterationIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.balanced.GenRRBVectorClosedBlocksFullRebalanceIterationIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.xunbalanced.GenRRBVectorClosedBlocksFullRebalanceIterationIntBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.xunbalanced.GenRRBVectorClosedBlocksQuickRebalanceIterationIntBenchmark]
}

class RunSplitBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorSplitIntBenchmark]
    include[rrbvector.RRBVectorSplitIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.balanced.GenRRBVectorClosedBlocksFullRebalanceSplitIntBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.xunbalanced.GenRRBVectorClosedBlocksFullRebalanceSplitIntBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.xunbalanced.GenRRBVectorClosedBlocksQuickRebalanceSplitIntBenchmark]
}

class RunMemoryAllocation extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIntMemoryAllocation]
    include[rrbvector.RRBVectorIntMemoryAllocation]
    include[generated.rrbvector.closedblocks.fullrebalance.balanced.GenRRBVectorClosedBlocksFullRebalanceIntMemoryAllocationBenchmark]
    include[generated.rrbvector.closedblocks.fullrebalance.xunbalanced.GenRRBVectorClosedBlocksFullRebalanceIntMemoryAllocationBenchmark]
    include[generated.rrbvector.closedblocks.quickrebalance.xunbalanced.GenRRBVectorClosedBlocksQuickRebalanceIntMemoryAllocationBenchmark]
}