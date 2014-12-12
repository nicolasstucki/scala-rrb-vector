package scala.collection.immutable.vectorbenchmarks

import org.scalameter.PerformanceTest

class RunAppendBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[mbrrbvector.balanced.MbRRBVectorAppendIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorAppendIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorAppendIntBenchmark]

    include[rrbvector.balanced.RRBVectorAppendIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorAppendIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorAppendIntBenchmark]

    include[vector.VectorAppendIntBenchmark]
}

class RunApplyBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[mbrrbvector.balanced.MbRRBVectorApplyIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorApplyIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorApplyIntBenchmark]

    include[rrbvector.balanced.RRBVectorApplyIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorApplyIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorApplyIntBenchmark]

    include[vector.VectorApplyIntBenchmark]

}

class RunBuilderBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[mbrrbvector.balanced.MbRRBVectorBuilderIntBenchmark]
    include[rrbvector.balanced.RRBVectorBuilderIntBenchmark]
    include[vector.VectorBuilderIntBenchmark]

}

class RunConcatenationBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[mbrrbvector.balanced.MbRRBVectorConcatenationIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorConcatenationIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorConcatenationIntBenchmark]

    include[rrbvector.balanced.RRBVectorConcatenationIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorConcatenationIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorConcatenationIntBenchmark]

    include[vector.VectorConcatenationIntBenchmark]
}

class RunIterationBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[mbrrbvector.balanced.MbRRBVectorIterationIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorIterationIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorIterationIntBenchmark]

    include[rrbvector.balanced.RRBVectorIterationIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorIterationIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorIterationIntBenchmark]

    include[vector.VectorIterationIntBenchmark]
}

class RunPrependBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[mbrrbvector.balanced.MbRRBVectorPrependIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorPrependIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorPrependIntBenchmark]

    include[rrbvector.balanced.RRBVectorPrependIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorPrependIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorPrependIntBenchmark]

    include[vector.VectorPrependIntBenchmark]
}

class RunSplitBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[mbrrbvector.balanced.MbRRBVectorSplitIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorSplitIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorSplitIntBenchmark]

    include[rrbvector.balanced.RRBVectorSplitIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorSplitIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorSplitIntBenchmark]

    include[vector.VectorSplitIntBenchmark]
}

class RunMemoryAllocation extends PerformanceTest.OfflineRegressionReport {

    include[mbrrbvector.balanced.MbRRBVectorIntMemoryAllocation]
    include[mbrrbvector.unbalanced1.MbRRBVectorIntMemoryAllocation]
    include[mbrrbvector.xunbalanced.MbRRBVectorIntMemoryAllocation]

    include[rrbvector.balanced.RRBVectorIntMemoryAllocation]
    include[rrbvector.unbalanced1.RRBVectorIntMemoryAllocation]
    include[rrbvector.xunbalanced.RRBVectorIntMemoryAllocation]

    include[vector.VectorIntMemoryAllocation]
}


class RunParMapBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[mbrrbvector.balanced.MbRRBVectorParMapIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorParMapIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorParMapIntBenchmark]

    include[rrbvector.balanced.RRBVectorParMapIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorParMapIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorParMapIntBenchmark]

    include[vector.VectorParMapIntBenchmark]
}