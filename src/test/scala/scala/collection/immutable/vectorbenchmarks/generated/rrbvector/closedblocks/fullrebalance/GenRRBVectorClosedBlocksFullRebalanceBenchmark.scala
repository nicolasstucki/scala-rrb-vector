package scala {
  package collection {
    package immutable {
      package vectorbenchmarks {
        package generated {
          package rrbvector.closedblocks.fullrebalance {
            import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

            import scala.collection.immutable.vectorutils.VectorGeneratorType

            import scala.collection.immutable.vectorutils.generated.rrbvector.closedblocks.fullrebalance._

            import scala.collection.immutable.generated.rrbvector.closedblocks.fullrebalance._

            trait GenRRBVectorClosedBlocksFullRebalanceBenchmark[A] extends BaseVectorBenchmark[A] with GenRRBVectorClosedBlocksFullRebalanceGenerator[A] {
              override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[GenRRBVectorClosedBlocksFullRebalance[A]] = sizes(from, to, by).map(((size) => GenRRBVectorClosedBlocksFullRebalance.tabulate(size)(element)))
            }

            abstract class GenRRBVectorClosedBlocksFullRebalanceAppendBenchmark[A] extends AppendBenchmarks[A] with GenRRBVectorClosedBlocksFullRebalanceBenchmark[A]

            class GenRRBVectorClosedBlocksFullRebalanceAppendIntBenchmark extends GenRRBVectorClosedBlocksFullRebalanceAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {
              def sum32(vec: GenRRBVectorClosedBlocksFullRebalance[Int], times: Int): Int = {
                var i = 0;
                var v = vec;
                var sum = 0;
                while (i.<(times)) 
                  {
                    v = vec.:+(0).:+(1).:+(2).:+(3).:+(4).:+(5).:+(6).:+(7).:+(0).:+(1).:+(2).:+(3).:+(4).:+(5).:+(6).:+(7).:+(0).:+(1).:+(2).:+(3).:+(4).:+(5).:+(6).:+(7).:+(0).:+(1).:+(2).:+(3).:+(4).:+(5).:+(6).:+(7);
                    sum.+=(v.length);
                    i.+=(1)
                  }
                ;
                sum
              }
            }

            class GenRRBVectorClosedBlocksFullRebalanceAppendAnyRefBenchmark extends GenRRBVectorClosedBlocksFullRebalanceAppendBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator {
              val obj = new Object();
              def sum32(vec: GenRRBVectorClosedBlocksFullRebalance[AnyRef], times: Int): Int = {
                var i = 0;
                var v = vec;
                var sum = 0;
                while (i.<(times)) 
                  {
                    v = vec.:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj).:+(obj);
                    sum.+=(v.length);
                    i.+=(1)
                  }
                ;
                sum
              }
            }

            abstract class GenRRBVectorClosedBlocksFullRebalanceApplyBenchmark[A] extends ApplyBenchmarks[A] with GenRRBVectorClosedBlocksFullRebalanceBenchmark[A]

            class GenRRBVectorClosedBlocksFullRebalanceApplyIntBenchmark extends GenRRBVectorClosedBlocksFullRebalanceApplyBenchmark[Int] with VectorGeneratorType.IntGenerator

            class GenRRBVectorClosedBlocksFullRebalanceApplyAnyRefBenchmark extends GenRRBVectorClosedBlocksFullRebalanceApplyBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

            abstract class GenRRBVectorClosedBlocksFullRebalanceBuilderBenchmark[A] extends BuilderBenchmarks[A] with GenRRBVectorClosedBlocksFullRebalanceBenchmark[A] {
              def buildVector(n: Int, elems: Int): Int = {
                var i = 0;
                var sum = 0;
                var b = GenRRBVectorClosedBlocksFullRebalance.newBuilder[A];
                val e = element(0);
                while (i.<(elems)) 
                  {
                    val m = math.min(n, elems.-(i));
                    var j = 0;
                    while (j.<(m)) 
                      {
                        b.+=(e);
                        i.+=(1);
                        j.+=(1)
                      }
                    ;
                    sum = b.result().length;
                    b.clear()
                  }
                ;
                sum
              }
            }

            class GenRRBVectorClosedBlocksFullRebalanceBuilderIntBenchmark extends GenRRBVectorClosedBlocksFullRebalanceBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

            class GenRRBVectorClosedBlocksFullRebalanceBuilderAnyRefBenchmark extends GenRRBVectorClosedBlocksFullRebalanceBuilderBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

            abstract class GenRRBVectorClosedBlocksFullRebalanceConcatenationBenchmark[A] extends ConcatenationBenchmarks[A] with GenRRBVectorClosedBlocksFullRebalanceBenchmark[A]

            class GenRRBVectorClosedBlocksFullRebalanceConcatenationIntBenchmark extends GenRRBVectorClosedBlocksFullRebalanceConcatenationBenchmark[Int] with VectorGeneratorType.IntGenerator

            class GenRRBVectorClosedBlocksFullRebalanceConcatenationAnyRefBenchmark extends GenRRBVectorClosedBlocksFullRebalanceConcatenationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

            abstract class GenRRBVectorClosedBlocksFullRebalanceIterationBenchmark[A] extends IterationBenchmarks[A] with GenRRBVectorClosedBlocksFullRebalanceBenchmark[A]

            class GenRRBVectorClosedBlocksFullRebalanceIterationIntBenchmark extends GenRRBVectorClosedBlocksFullRebalanceIterationBenchmark[Int] with VectorGeneratorType.IntGenerator

            class GenRRBVectorClosedBlocksFullRebalanceIterationAnyRefBenchmark extends GenRRBVectorClosedBlocksFullRebalanceIterationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

            abstract class GenRRBVectorClosedBlocksFullRebalanceMemoryAllocationBenchmark[A] extends MemoryAllocation[A] with GenRRBVectorClosedBlocksFullRebalanceBenchmark[A]

            class GenRRBVectorClosedBlocksFullRebalanceIntMemoryAllocationBenchmark extends GenRRBVectorClosedBlocksFullRebalanceMemoryAllocationBenchmark[Int] with VectorGeneratorType.IntGenerator

            class GenRRBVectorClosedBlocksFullRebalanceAnyRefMemoryAllocationBenchmark extends GenRRBVectorClosedBlocksFullRebalanceMemoryAllocationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

            abstract class GenRRBVectorClosedBlocksFullRebalanceSplitBenchmark[A] extends SplitBenchmarks[A] with GenRRBVectorClosedBlocksFullRebalanceBenchmark[A]

            class GenRRBVectorClosedBlocksFullRebalanceSplitIntBenchmark extends GenRRBVectorClosedBlocksFullRebalanceSplitBenchmark[Int] with VectorGeneratorType.IntGenerator

            class GenRRBVectorClosedBlocksFullRebalanceSplitAnyRefBenchmark extends GenRRBVectorClosedBlocksFullRebalanceSplitBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator
          }
        }
      }
    }
  }
}