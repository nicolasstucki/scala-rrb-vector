package scala.collection.immutable.vectorbenchmarks

import org.scalameter.PerformanceTest

class RunAppendBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorAppendIntBenchmark]

    include[rrbvector.balanced.RRBVectorAppendIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorAppendIntBenchmark]

    include[generated.rrbvector.complete.block32.balanced.RRBVector_c_32_AppendInt_Benchmark]
    include[generated.rrbvector.complete.block64.balanced.RRBVector_c_64_AppendInt_Benchmark]
    include[generated.rrbvector.complete.block128.balanced.RRBVector_c_128_AppendInt_Benchmark]
    include[generated.rrbvector.complete.block256.balanced.RRBVector_c_256_AppendInt_Benchmark]

    include[generated.rrbvector.complete.block32.xunbalanced.RRBVector_c_32_AppendInt_Benchmark]
    include[generated.rrbvector.complete.block64.xunbalanced.RRBVector_c_64_AppendInt_Benchmark]
    include[generated.rrbvector.complete.block128.xunbalanced.RRBVector_c_128_AppendInt_Benchmark]
    include[generated.rrbvector.complete.block256.xunbalanced.RRBVector_c_256_AppendInt_Benchmark]

    include[generated.rrbvector.quick.block32.xunbalanced.RRBVector_q_32_AppendInt_Benchmark]
    include[generated.rrbvector.quick.block64.xunbalanced.RRBVector_q_64_AppendInt_Benchmark]
    include[generated.rrbvector.quick.block128.xunbalanced.RRBVector_q_128_AppendInt_Benchmark]
    include[generated.rrbvector.quick.block256.xunbalanced.RRBVector_q_256_AppendInt_Benchmark]

}

class RunApplyBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorApplyIntBenchmark]

    include[rrbvector.balanced.RRBVectorApplyIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorApplyIntBenchmark]

    include[generated.rrbvector.complete.block32.balanced.RRBVector_c_32_ApplyInt_Benchmark]
    include[generated.rrbvector.complete.block64.balanced.RRBVector_c_64_ApplyInt_Benchmark]
    include[generated.rrbvector.complete.block128.balanced.RRBVector_c_128_ApplyInt_Benchmark]
    include[generated.rrbvector.complete.block256.balanced.RRBVector_c_256_ApplyInt_Benchmark]

    include[generated.rrbvector.complete.block32.xunbalanced.RRBVector_c_32_ApplyInt_Benchmark]
    include[generated.rrbvector.complete.block64.xunbalanced.RRBVector_c_64_ApplyInt_Benchmark]
    include[generated.rrbvector.complete.block128.xunbalanced.RRBVector_c_128_ApplyInt_Benchmark]
    include[generated.rrbvector.complete.block256.xunbalanced.RRBVector_c_256_ApplyInt_Benchmark]

    include[generated.rrbvector.quick.block32.xunbalanced.RRBVector_q_32_ApplyInt_Benchmark]
    include[generated.rrbvector.quick.block64.xunbalanced.RRBVector_q_64_ApplyInt_Benchmark]
    include[generated.rrbvector.quick.block128.xunbalanced.RRBVector_q_128_ApplyInt_Benchmark]
    include[generated.rrbvector.quick.block256.xunbalanced.RRBVector_q_256_ApplyInt_Benchmark]

}

class RunBuilderBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorBuilderIntBenchmark]

    include[rrbvector.balanced.RRBVectorBuilderIntBenchmark]

    include[generated.rrbvector.complete.block32.balanced.RRBVector_c_32_BuilderInt_Benchmark]
    include[generated.rrbvector.complete.block64.balanced.RRBVector_c_64_BuilderInt_Benchmark]
    include[generated.rrbvector.complete.block128.balanced.RRBVector_c_128_BuilderInt_Benchmark]

}

class RunConcatenationBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorConcatenationIntBenchmark]

    include[rrbvector.balanced.RRBVectorConcatenationIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorConcatenationIntBenchmark]

    include[generated.rrbvector.complete.block32.balanced.RRBVector_c_32_ConcatenationInt_Benchmark]
    include[generated.rrbvector.complete.block64.balanced.RRBVector_c_64_ConcatenationInt_Benchmark]
    include[generated.rrbvector.complete.block128.balanced.RRBVector_c_128_ConcatenationInt_Benchmark]
    include[generated.rrbvector.complete.block256.balanced.RRBVector_c_256_ConcatenationInt_Benchmark]

    include[generated.rrbvector.quick.block32.balanced.RRBVector_q_32_ConcatenationInt_Benchmark]
    include[generated.rrbvector.quick.block64.balanced.RRBVector_q_64_ConcatenationInt_Benchmark]
    include[generated.rrbvector.quick.block128.balanced.RRBVector_q_128_ConcatenationInt_Benchmark]
    include[generated.rrbvector.quick.block256.balanced.RRBVector_q_256_ConcatenationInt_Benchmark]

    include[generated.rrbvector.complete.block32.xunbalanced.RRBVector_c_32_ConcatenationInt_Benchmark]
    include[generated.rrbvector.complete.block64.xunbalanced.RRBVector_c_64_ConcatenationInt_Benchmark]
    include[generated.rrbvector.complete.block128.xunbalanced.RRBVector_c_128_ConcatenationInt_Benchmark]
    include[generated.rrbvector.complete.block256.xunbalanced.RRBVector_c_256_ConcatenationInt_Benchmark]

    include[generated.rrbvector.quick.block32.xunbalanced.RRBVector_q_32_ConcatenationInt_Benchmark]
    include[generated.rrbvector.quick.block64.xunbalanced.RRBVector_q_64_ConcatenationInt_Benchmark]
    include[generated.rrbvector.quick.block128.xunbalanced.RRBVector_q_128_ConcatenationInt_Benchmark]
    include[generated.rrbvector.quick.block256.xunbalanced.RRBVector_q_256_ConcatenationInt_Benchmark]

}

class RunIterationBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorIterationIntBenchmark]

    include[rrbvector.balanced.RRBVectorIterationIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorIterationIntBenchmark]

    include[generated.rrbvector.complete.block32.balanced.RRBVector_c_32_IterationInt_Benchmark]
    include[generated.rrbvector.complete.block64.balanced.RRBVector_c_64_IterationInt_Benchmark]
    include[generated.rrbvector.complete.block128.balanced.RRBVector_c_128_IterationInt_Benchmark]
    include[generated.rrbvector.complete.block256.balanced.RRBVector_c_256_IterationInt_Benchmark]

    include[generated.rrbvector.complete.block32.xunbalanced.RRBVector_c_32_IterationInt_Benchmark]
    include[generated.rrbvector.complete.block64.xunbalanced.RRBVector_c_64_IterationInt_Benchmark]
    include[generated.rrbvector.complete.block128.xunbalanced.RRBVector_c_128_IterationInt_Benchmark]
    include[generated.rrbvector.complete.block256.xunbalanced.RRBVector_c_256_IterationInt_Benchmark]

    include[generated.rrbvector.quick.block32.xunbalanced.RRBVector_q_32_IterationInt_Benchmark]
    include[generated.rrbvector.quick.block64.xunbalanced.RRBVector_q_64_IterationInt_Benchmark]
    include[generated.rrbvector.quick.block128.xunbalanced.RRBVector_q_128_IterationInt_Benchmark]
    include[generated.rrbvector.quick.block256.xunbalanced.RRBVector_q_256_IterationInt_Benchmark]

}

class RunPrependBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorPrependIntBenchmark]

//    include[rrbvector.balanced.RRBVectorPrependIntBenchmark]
//    include[rrbvector.xunbalanced.RRBVectorPrependIntBenchmark]
//
//    include[generated.rrbvector.complete.block32.balanced.RRBVector_c_32_PrependInt_Benchmark]
//    include[generated.rrbvector.complete.block64.balanced.RRBVector_c_64_PrependInt_Benchmark]
//    include[generated.rrbvector.complete.block128.balanced.RRBVector_c_128_PrependInt_Benchmark]
//    include[generated.rrbvector.complete.block256.balanced.RRBVector_c_256_PrependInt_Benchmark]
//
//    include[generated.rrbvector.complete.block32.xunbalanced.RRBVector_c_32_PrependInt_Benchmark]
//    include[generated.rrbvector.complete.block64.xunbalanced.RRBVector_c_64_PrependInt_Benchmark]
//    include[generated.rrbvector.complete.block128.xunbalanced.RRBVector_c_128_PrependInt_Benchmark]
//    include[generated.rrbvector.complete.block256.xunbalanced.RRBVector_c_256_PrependInt_Benchmark]
//
//    include[generated.rrbvector.quick.block32.xunbalanced.RRBVector_q_32_PrependInt_Benchmark]
//    include[generated.rrbvector.quick.block64.xunbalanced.RRBVector_q_64_PrependInt_Benchmark]
//    include[generated.rrbvector.quick.block128.xunbalanced.RRBVector_q_128_PrependInt_Benchmark]
//    include[generated.rrbvector.quick.block256.xunbalanced.RRBVector_q_256_PrependInt_Benchmark]

}

class RunSplitBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorSplitIntBenchmark]

    include[rrbvector.balanced.RRBVectorSplitIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorSplitIntBenchmark]

    include[generated.rrbvector.complete.block32.balanced.RRBVector_c_32_SplitInt_Benchmark]
    include[generated.rrbvector.complete.block64.balanced.RRBVector_c_64_SplitInt_Benchmark]
    include[generated.rrbvector.complete.block128.balanced.RRBVector_c_128_SplitInt_Benchmark]
    include[generated.rrbvector.complete.block256.balanced.RRBVector_c_256_SplitInt_Benchmark]

    include[generated.rrbvector.complete.block32.xunbalanced.RRBVector_c_32_SplitInt_Benchmark]
    include[generated.rrbvector.complete.block64.xunbalanced.RRBVector_c_64_SplitInt_Benchmark]
    include[generated.rrbvector.complete.block128.xunbalanced.RRBVector_c_128_SplitInt_Benchmark]
    include[generated.rrbvector.complete.block256.xunbalanced.RRBVector_c_256_SplitInt_Benchmark]

    include[generated.rrbvector.quick.block32.xunbalanced.RRBVector_q_32_SplitInt_Benchmark]
    include[generated.rrbvector.quick.block64.xunbalanced.RRBVector_q_64_SplitInt_Benchmark]
    include[generated.rrbvector.quick.block128.xunbalanced.RRBVector_q_128_SplitInt_Benchmark]
    include[generated.rrbvector.quick.block256.xunbalanced.RRBVector_q_256_SplitInt_Benchmark]

}

class RunMemoryAllocation extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorIntMemoryAllocation]

    include[rrbvector.balanced.RRBVectorIntMemoryAllocation]
    include[rrbvector.xunbalanced.RRBVectorIntMemoryAllocation]

    include[generated.rrbvector.complete.block32.balanced.RRBVector_c_32_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.complete.block64.balanced.RRBVector_c_64_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.complete.block128.balanced.RRBVector_c_128_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.complete.block256.balanced.RRBVector_c_256_IntMemoryAllocation_Benchmark]

    include[generated.rrbvector.complete.block32.xunbalanced.RRBVector_c_32_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.complete.block64.xunbalanced.RRBVector_c_64_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.complete.block128.xunbalanced.RRBVector_c_128_IntMemoryAllocation_Benchmark]
    include[generated.rrbvector.complete.block256.xunbalanced.RRBVector_c_256_IntMemoryAllocation_Benchmark]

}


class RunParMapBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorParMapIntBenchmark]

    include[rrbvector.balanced.RRBVectorParMapIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorParMapIntBenchmark]

    // TODO Add generated parrrbvectors
}