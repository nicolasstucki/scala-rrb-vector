package scala.collection.immutable.vectorbenchmarks

import org.scalameter.PerformanceTest


class RunAllBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[RunAppendBenchmarks]
    include[RunApplyBenchmarks]
    include[RunBuilderBenchmarks]
    include[RunConcatenationBenchmarks]
    include[RunIterationBenchmarks]
    include[RunSplitBenchmarks]
    include[RunMemoryAllocation]

}


class RunAppendBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorAppendIntBenchmark]

    include[rrbvector.RRBVectorAppendIntBenchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_32_splitbalanced_AppendInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_64_splitbalanced_AppendInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_128_splitbalanced_AppendInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_32_splitbalanced_AppendInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_64_splitbalanced_AppendInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_128_splitbalanced_AppendInt_Benchmark]

}

class RunApplyBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorApplyIntBenchmark]
    include[rrbvector.RRBVectorApplyIntBenchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_32_splitbalanced_ApplyInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_64_splitbalanced_ApplyInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_128_splitbalanced_ApplyInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_32_splitbalanced_ApplyInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_64_splitbalanced_ApplyInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_128_splitbalanced_ApplyInt_Benchmark]

}

class RunBuilderBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorBuilderIntBenchmark]
    include[rrbvector.RRBVectorBuilderIntBenchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_32_splitbalanced_BuilderInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_64_splitbalanced_BuilderInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_128_splitbalanced_BuilderInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_32_splitbalanced_BuilderInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_64_splitbalanced_BuilderInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_128_splitbalanced_BuilderInt_Benchmark]

}

class RunConcatenationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorConcatenationIntBenchmark]
    include[rrbvector.RRBVectorConcatenationIntBenchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_32_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_64_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_128_splitbalanced_ConcatenationInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_32_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_64_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_128_splitbalanced_ConcatenationInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_quick_directLevel_32_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_quick_directLevel_64_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_quick_directLevel_128_splitbalanced_ConcatenationInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_quick_incrementalLevel_32_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_quick_incrementalLevel_64_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_quick_incrementalLevel_128_splitbalanced_ConcatenationInt_Benchmark]

}

class RunIterationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIterationIntBenchmark]
    include[rrbvector.RRBVectorIterationIntBenchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_32_splitbalanced_IterationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_64_splitbalanced_IterationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_128_splitbalanced_IterationInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_32_splitbalanced_IterationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_64_splitbalanced_IterationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_128_splitbalanced_IterationInt_Benchmark]

}

class RunSplitBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorSplitIntBenchmark]
    include[rrbvector.RRBVectorSplitIntBenchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_32_splitbalanced_SplitInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_64_splitbalanced_SplitInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_128_splitbalanced_SplitInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_32_splitbalanced_SplitInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_64_splitbalanced_SplitInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_128_splitbalanced_SplitInt_Benchmark]

}

class RunMemoryAllocation extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIntMemoryAllocation]
    include[rrbvector.RRBVectorIntMemoryAllocation]

    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_32_splitbalanced_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_64_splitbalanced_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_directLevel_128_splitbalanced_IntMemoryAllocation_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_32_splitbalanced_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_64_splitbalanced_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_incrementalLevel_128_splitbalanced_IntMemoryAllocation_Benchmark]

}


class RunParMapBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorParMapIntBenchmark]
    include[rrbvector.RRBVectorParMapIntBenchmark]

    // TODO Add generated parvectors
}