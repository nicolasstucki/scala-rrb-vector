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
    include[genrrbvector.GenRRBVectorAppendIntBenchmark]
}

class RunApplyBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorApplyIntBenchmark]
    include[rrbvector.RRBVectorApplyIntBenchmark]
    include[genrrbvector.GenRRBVectorApplyIntBenchmark]
}

class RunBuilderBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorBuilderIntBenchmark]
    include[rrbvector.RRBVectorBuilderIntBenchmark]
    include[genrrbvector.GenRRBVectorBuilderIntBenchmark]
}

class RunConcatenationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorConcatenationIntBenchmark]
    include[rrbvector.RRBVectorConcatenationIntBenchmark]
    include[genrrbvector.GenRRBVectorConcatenationIntBenchmark]
}

class RunIterationBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIterationIntBenchmark]
    include[rrbvector.RRBVectorIterationIntBenchmark]
    include[genrrbvector.GenRRBVectorIterationIntBenchmark]
}

class RunSplitBenchmarks extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorSplitIntBenchmark]
    include[rrbvector.RRBVectorSplitIntBenchmark]
//    include[genrrbvector.GenRRBVectorSplitIntBenchmark]
}

class RunMemoryAllocation extends PerformanceTest.OfflineRegressionReport {
    include[vector.VectorIntMemoryAllocation]
    include[rrbvector.RRBVectorIntMemoryAllocation]
    include[genrrbvector.GenRRBVectorIntMemoryAllocation]
}