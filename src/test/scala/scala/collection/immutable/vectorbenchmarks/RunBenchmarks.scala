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
//    include[rrbvector.RRBVectorAppendIntBenchmark]

    include[generated.rrbvector.complete.balanced.RRBVector_complete_32_AppendInt_Benchmark]
    include[generated.rrbvector.complete.balanced.RRBVector_complete_64_AppendInt_Benchmark]

//    include[generated.rrbvector.complete.xunbalanced.RRBVector_complete_32_AppendInt_Benchmark]
//    include[generated.rrbvector.complete.xunbalanced.RRBVector_complete_64_AppendInt_Benchmark]

//    include[generated.rrbvector.quick.xunbalanced.RRBVector_quick_32_AppendInt_Benchmark]
//    include[generated.rrbvector.quick.xunbalanced.RRBVector_quick_64_AppendInt_Benchmark]
}

class RunApplyBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorApplyIntBenchmark]
//    include[rrbvector.RRBVectorApplyIntBenchmark]

    include[generated.rrbvector.complete.balanced.RRBVector_complete_32_ApplyInt_Benchmark]
    include[generated.rrbvector.complete.balanced.RRBVector_complete_64_ApplyInt_Benchmark]

//    include[generated.rrbvector.complete.xunbalanced.RRBVector_complete_32_ApplyInt_Benchmark]
//    include[generated.rrbvector.complete.xunbalanced.RRBVector_complete_64_ApplyInt_Benchmark]

//    include[generated.rrbvector.quick.xunbalanced.RRBVector_quick_32_ApplyInt_Benchmark]
//    include[generated.rrbvector.quick.xunbalanced.RRBVector_quick_64_ApplyInt_Benchmark]
}

class RunBuilderBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorBuilderIntBenchmark]
//    include[rrbvector.RRBVectorBuilderIntBenchmark]

    include[generated.rrbvector.complete.balanced.RRBVector_complete_32_BuilderInt_Benchmark]
    include[generated.rrbvector.complete.balanced.RRBVector_complete_64_BuilderInt_Benchmark]
}

class RunConcatenationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorConcatenationIntBenchmark]
//    include[rrbvector.RRBVectorConcatenationIntBenchmark]

    include[generated.rrbvector.complete.balanced.RRBVector_complete_32_ConcatenationInt_Benchmark]
    include[generated.rrbvector.complete.balanced.RRBVector_complete_64_ConcatenationInt_Benchmark]

//    include[generated.rrbvector.complete.xunbalanced.RRBVector_complete_32_ConcatenationInt_Benchmark]
//    include[generated.rrbvector.complete.xunbalanced.RRBVector_complete_64_ConcatenationInt_Benchmark]

    include[generated.rrbvector.quick.balanced.RRBVector_quick_32_ConcatenationInt_Benchmark]
    include[generated.rrbvector.quick.balanced.RRBVector_quick_64_ConcatenationInt_Benchmark]

//    include[generated.rrbvector.quick.xunbalanced.RRBVector_quick_32_ConcatenationInt_Benchmark]
//    include[generated.rrbvector.quick.xunbalanced.RRBVector_quick_64_ConcatenationInt_Benchmark]
}

class RunIterationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIterationIntBenchmark]
//    include[rrbvector.RRBVectorIterationIntBenchmark]

    include[generated.rrbvector.complete.balanced.RRBVector_complete_32_IterationInt_Benchmark]
    include[generated.rrbvector.complete.balanced.RRBVector_complete_64_IterationInt_Benchmark]

//    include[generated.rrbvector.complete.xunbalanced.RRBVector_complete_32_IterationInt_Benchmark]
//    include[generated.rrbvector.complete.xunbalanced.RRBVector_complete_64_IterationInt_Benchmark]

//    include[generated.rrbvector.quick.xunbalanced.RRBVector_quick_32_IterationInt_Benchmark]
//    include[generated.rrbvector.quick.xunbalanced.RRBVector_quick_64_IterationInt_Benchmark]
}

class RunSplitBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorSplitIntBenchmark]
//    include[rrbvector.RRBVectorSplitIntBenchmark]

    include[generated.rrbvector.complete.balanced.RRBVector_complete_32_SplitInt_Benchmark]
    include[generated.rrbvector.complete.balanced.RRBVector_complete_64_SplitInt_Benchmark]

//    include[generated.rrbvector.complete.xunbalanced.RRBVector_complete_32_SplitInt_Benchmark]
//    include[generated.rrbvector.complete.xunbalanced.RRBVector_complete_64_SplitInt_Benchmark]

//    include[generated.rrbvector.quick.xunbalanced.RRBVector_quick_32_SplitInt_Benchmark]
//    include[generated.rrbvector.quick.xunbalanced.RRBVector_quick_64_SplitInt_Benchmark]
}

class RunMemoryAllocation extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIntMemoryAllocation]
//    include[rrbvector.RRBVectorIntMemoryAllocation]

    include[generated.rrbvector.complete.balanced.RRBVector_complete_32_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.complete.balanced.RRBVector_complete_64_IntMemoryAllocation_Benchmark]

//    include[generated.rrbvector.complete.xunbalanced.RRBVector_complete_32_IntMemoryAllocation_Benchmark]
//    include[generated.rrbvector.complete.xunbalanced.RRBVector_complete_64_IntMemoryAllocation_Benchmark]

//    include[generated.rrbvector.quick.xunbalanced.RRBVector_quick_32_IntMemoryAllocation_Benchmark]
//    include[generated.rrbvector.quick.xunbalanced.RRBVector_quick_64_IntMemoryAllocation_Benchmark]
}