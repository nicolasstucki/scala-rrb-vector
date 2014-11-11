package scala.collection.immutable.vectorbenchmarks

import org.scalameter.PerformanceTest

import scala.collection.immutable.vectorbenchmarks.rrbvector.balanced.RRBVectorStringMemoryAllocation


class RunAllBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[RunAppendBenchmarks]
    include[RunApplyBenchmarks]
    include[RunBuilderBenchmarks]
    include[RunConcatenationBenchmarks]
    include[RunIterationBenchmarks]
    include[RunPrependBenchmarks]
    include[RunSplitBenchmarks]
    include[RunMemoryAllocation]
}


class RunAppendBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorAppendIntBenchmark]

    include[rrbvector.balanced.RRBVectorAppendIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorAppendIntBenchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_AppendInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_AppendInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_AppendInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_AppendInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_AppendInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_AppendInt_Benchmark]

    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_AppendInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_AppendInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_AppendInt_Benchmark]
}

class RunApplyBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorApplyIntBenchmark]
    include[vector.VectorApplyStringBenchmark]

    include[rrbvector.balanced.RRBVectorApplyIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorApplyIntBenchmark]

    include[rrbvector.balanced.RRBVectorApplyStringBenchmark]
    include[rrbvector.xunbalanced.RRBVectorApplyStringBenchmark]

    // Benchmarks showed that directLevel is slower than incrementalLevel
    //    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_ApplyInt_Benchmark]
    //    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_ApplyInt_Benchmark]
    //    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_ApplyInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_ApplyInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_ApplyInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_ApplyInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_ifElseDepth_incrementalLevel_32_splitbalanced_ApplyInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_ifElseDepth_incrementalLevel_64_splitbalanced_ApplyInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_ifElseDepth_incrementalLevel_128_splitbalanced_ApplyInt_Benchmark]

    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_ApplyInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_ApplyInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_ApplyInt_Benchmark]

    include[generated.rrbvector.xunbalanced.RRBVector_complete_ifElseDepth_incrementalLevel_32_splitbalanced_ApplyInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_ifElseDepth_incrementalLevel_64_splitbalanced_ApplyInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_ifElseDepth_incrementalLevel_128_splitbalanced_ApplyInt_Benchmark]
}

class RunBuilderBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorBuilderIntBenchmark]

    include[rrbvector.balanced.RRBVectorBuilderIntBenchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_BuilderInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_BuilderInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_BuilderInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_BuilderInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_BuilderInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_BuilderInt_Benchmark]

}

class RunConcatenationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorConcatenationIntBenchmark]

    include[rrbvector.balanced.RRBVectorConcatenationIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorConcatenationIntBenchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_ConcatenationInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_ConcatenationInt_Benchmark]

    //    include[generated.rrbvector.balanced.RRBVector_quick_matchDepth_directLevel_32_splitbalanced_ConcatenationInt_Benchmark]
    //    include[generated.rrbvector.balanced.RRBVector_quick_matchDepth_directLevel_64_splitbalanced_ConcatenationInt_Benchmark]
    //    include[generated.rrbvector.balanced.RRBVector_quick_matchDepth_directLevel_128_splitbalanced_ConcatenationInt_Benchmark]

    //    include[generated.rrbvector.balanced.RRBVector_quick_matchDepth_incrementalLevel_32_splitbalanced_ConcatenationInt_Benchmark]
    //    include[generated.rrbvector.balanced.RRBVector_quick_matchDepth_incrementalLevel_64_splitbalanced_ConcatenationInt_Benchmark]
    //    include[generated.rrbvector.balanced.RRBVector_quick_matchDepth_incrementalLevel_128_splitbalanced_ConcatenationInt_Benchmark]


    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_ConcatenationInt_Benchmark]

    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_ConcatenationInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_ConcatenationInt_Benchmark]

    //    include[generated.rrbvector.xunbalanced.RRBVector_quick_matchDepth_directLevel_32_splitbalanced_ConcatenationInt_Benchmark]
    //    include[generated.rrbvector.xunbalanced.RRBVector_quick_matchDepth_directLevel_64_splitbalanced_ConcatenationInt_Benchmark]
    //    include[generated.rrbvector.xunbalanced.RRBVector_quick_matchDepth_directLevel_128_splitbalanced_ConcatenationInt_Benchmark]

    //    include[generated.rrbvector.xunbalanced.RRBVector_quick_matchDepth_incrementalLevel_32_splitbalanced_ConcatenationInt_Benchmark]
    //    include[generated.rrbvector.xunbalanced.RRBVector_quick_matchDepth_incrementalLevel_64_splitbalanced_ConcatenationInt_Benchmark]
    //    include[generated.rrbvector.xunbalanced.RRBVector_quick_matchDepth_incrementalLevel_128_splitbalanced_ConcatenationInt_Benchmark]

}

class RunIterationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIterationIntBenchmark]

    include[rrbvector.balanced.RRBVectorIterationIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorIterationIntBenchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_IterationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_IterationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_IterationInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_IterationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_IterationInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_IterationInt_Benchmark]

    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_IterationInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_IterationInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_IterationInt_Benchmark]

    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_IterationInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_IterationInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_IterationInt_Benchmark]
}

class RunPrependBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorPrependIntBenchmark]

    include[rrbvector.balanced.RRBVectorPrependIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorPrependIntBenchmark]

    //    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_PrependInt_Benchmark]
    //    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_PrependInt_Benchmark]
    //    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_PrependInt_Benchmark]

    //    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_PrependInt_Benchmark]
    //    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_PrependInt_Benchmark]
    //    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_PrependInt_Benchmark]

    //    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_PrependInt_Benchmark]
    //    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_PrependInt_Benchmark]
    //    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_PrependInt_Benchmark]
}

class RunSplitBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorSplitIntBenchmark]

    include[rrbvector.balanced.RRBVectorSplitIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorSplitIntBenchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_SplitInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_SplitInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_SplitInt_Benchmark]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_SplitInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_SplitInt_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_SplitInt_Benchmark]

    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_SplitInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_SplitInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_SplitInt_Benchmark]

    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_32_splitbalanced_SplitInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_64_splitbalanced_SplitInt_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_incrementalLevel_128_splitbalanced_SplitInt_Benchmark]
}

class RunMemoryAllocation extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIntMemoryAllocation]

    include[rrbvector.balanced.RRBVectorIntMemoryAllocation]
    include[rrbvector.xunbalanced.RRBVectorIntMemoryAllocation]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_IntMemoryAllocation_Benchmark]

    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_IntMemoryAllocation_Benchmark]

    include[vector.VectorStringMemoryAllocation]
    include[RRBVectorStringMemoryAllocation]

    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_StringMemoryAllocation_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_StringMemoryAllocation_Benchmark]
    include[generated.rrbvector.balanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_StringMemoryAllocation_Benchmark]

    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_32_splitbalanced_StringMemoryAllocation_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_64_splitbalanced_StringMemoryAllocation_Benchmark]
    include[generated.rrbvector.xunbalanced.RRBVector_complete_matchDepth_directLevel_128_splitbalanced_StringMemoryAllocation_Benchmark]

}


class RunParMapBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorParMapIntBenchmark]

    include[rrbvector.balanced.RRBVectorParMapIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorParMapIntBenchmark]


    // TODO Add generated parvectors
}