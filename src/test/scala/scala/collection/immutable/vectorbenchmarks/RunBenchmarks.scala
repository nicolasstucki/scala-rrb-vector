package scala.collection.immutable.vectorbenchmarks

import org.scalameter.PerformanceTest

class RunAppendBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorAppendIntBenchmark]

    include[rrbvector.balanced.RRBVectorAppendIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorAppendIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorAppendIntBenchmark]

    include[mbrrbvector.balanced.MbRRBVectorAppendIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorAppendIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorAppendIntBenchmark]

}

class RunApplyBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorApplyIntBenchmark]

    include[rrbvector.balanced.RRBVectorApplyIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorApplyIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorApplyIntBenchmark]

    include[mbrrbvector.balanced.MbRRBVectorApplyIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorApplyIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorApplyIntBenchmark]


}

class RunBuilderBenchmarks extends PerformanceTest.OfflineRegressionReport {


    include[vector.VectorBuilderIntBenchmark]
    include[rrbvector.balanced.RRBVectorBuilderIntBenchmark]
    include[mbrrbvector.balanced.MbRRBVectorBuilderIntBenchmark]

}

class RunConcatenationBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorConcatenationIntBenchmark]

    include[rrbvector.balanced.RRBVectorConcatenationIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorConcatenationIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorConcatenationIntBenchmark]

    include[mbrrbvector.balanced.MbRRBVectorConcatenationIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorConcatenationIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorConcatenationIntBenchmark]

}

class RunIterationBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorIterationIntBenchmark]

    include[rrbvector.balanced.RRBVectorIterationIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorIterationIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorIterationIntBenchmark]

    include[mbrrbvector.balanced.MbRRBVectorIterationIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorIterationIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorIterationIntBenchmark]

}

class RunPrependBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorPrependIntBenchmark]

    include[rrbvector.balanced.RRBVectorPrependIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorPrependIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorPrependIntBenchmark]

    include[mbrrbvector.balanced.MbRRBVectorPrependIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorPrependIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorPrependIntBenchmark]

}

class RunSplitBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorSplitIntBenchmark]

    include[rrbvector.balanced.RRBVectorSplitIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorSplitIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorSplitIntBenchmark]

    include[mbrrbvector.balanced.MbRRBVectorSplitIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorSplitIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorSplitIntBenchmark]

}

class RunMemoryAllocation extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorIntMemoryAllocation]

    include[rrbvector.balanced.RRBVectorIntMemoryAllocation]
    include[rrbvector.unbalanced1.RRBVectorIntMemoryAllocation]
    include[rrbvector.xunbalanced.RRBVectorIntMemoryAllocation]

    include[mbrrbvector.balanced.MbRRBVectorIntMemoryAllocation]
    include[mbrrbvector.unbalanced1.MbRRBVectorIntMemoryAllocation]
    include[mbrrbvector.xunbalanced.MbRRBVectorIntMemoryAllocation]

}


class RunParMapBenchmarks extends PerformanceTest.OfflineRegressionReport {

    include[vector.VectorParMapIntBenchmark]

    include[rrbvector.balanced.RRBVectorParMapIntBenchmark]
    include[rrbvector.unbalanced1.RRBVectorParMapIntBenchmark]
    include[rrbvector.xunbalanced.RRBVectorParMapIntBenchmark]

    include[mbrrbvector.balanced.MbRRBVectorParMapIntBenchmark]
    include[mbrrbvector.unbalanced1.MbRRBVectorParMapIntBenchmark]
    include[mbrrbvector.xunbalanced.MbRRBVectorParMapIntBenchmark]

}