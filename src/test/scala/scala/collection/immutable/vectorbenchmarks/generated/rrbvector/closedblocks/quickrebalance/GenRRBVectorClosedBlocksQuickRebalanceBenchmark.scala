package scala {
  package collection {
    package immutable {
      package vectorbenchmarks {
        package generated {
          package rrbvector.closedblocks.quickrebalance {
            package balanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector.closedblocks.quickrebalance._

              import scala.collection.immutable.generated.rrbvector.closedblocks.quickrebalance._

              trait GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A] extends BaseVectorBenchmark[A] with GenRRBVectorClosedBlocksQuickRebalanceGenerator[A] {
                override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[GenRRBVectorClosedBlocksQuickRebalance[A]] = sizes(from, to, by).map(((size) => tabulatedVector(size)));
                override def vectorName: String = super.vectorName.+("Balanced")
              }

              abstract class GenRRBVectorClosedBlocksQuickRebalanceAppendBenchmark[A] extends AppendBenchmarks[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A]

              class GenRRBVectorClosedBlocksQuickRebalanceAppendIntBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {
                def sum32(vec: GenRRBVectorClosedBlocksQuickRebalance[Int], times: Int): Int = {
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

              class GenRRBVectorClosedBlocksQuickRebalanceAppendAnyRefBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceAppendBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator {
                val obj = new Object();
                def sum32(vec: GenRRBVectorClosedBlocksQuickRebalance[AnyRef], times: Int): Int = {
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

              abstract class GenRRBVectorClosedBlocksQuickRebalanceApplyBenchmark[A] extends ApplyBenchmarks[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A]

              class GenRRBVectorClosedBlocksQuickRebalanceApplyIntBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceApplyBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorClosedBlocksQuickRebalanceApplyAnyRefBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceApplyBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorClosedBlocksQuickRebalanceBuilderBenchmark[A] extends BuilderBenchmarks[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A] {
                def buildVector(n: Int, elems: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  var b = GenRRBVectorClosedBlocksQuickRebalance.newBuilder[A];
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

              class GenRRBVectorClosedBlocksQuickRebalanceBuilderIntBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorClosedBlocksQuickRebalanceBuilderAnyRefBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceBuilderBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorClosedBlocksQuickRebalanceConcatenationBenchmark[A] extends ConcatenationBenchmarks[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A]

              class GenRRBVectorClosedBlocksQuickRebalanceConcatenationIntBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceConcatenationBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorClosedBlocksQuickRebalanceConcatenationAnyRefBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceConcatenationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorClosedBlocksQuickRebalanceIterationBenchmark[A] extends IterationBenchmarks[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A]

              class GenRRBVectorClosedBlocksQuickRebalanceIterationIntBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceIterationBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorClosedBlocksQuickRebalanceIterationAnyRefBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceIterationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorClosedBlocksQuickRebalanceMemoryAllocationBenchmark[A] extends MemoryAllocation[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A]

              class GenRRBVectorClosedBlocksQuickRebalanceIntMemoryAllocationBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceMemoryAllocationBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorClosedBlocksQuickRebalanceAnyRefMemoryAllocationBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceMemoryAllocationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorClosedBlocksQuickRebalanceSplitBenchmark[A] extends SplitBenchmarks[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A]

              class GenRRBVectorClosedBlocksQuickRebalanceSplitIntBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceSplitBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorClosedBlocksQuickRebalanceSplitAnyRefBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceSplitBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator
            }

            package xunbalanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector.closedblocks.quickrebalance._

              import scala.collection.immutable.generated.rrbvector.closedblocks.quickrebalance._

              trait GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A] extends BaseVectorBenchmark[A] with GenRRBVectorClosedBlocksQuickRebalanceGenerator[A] {
                override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[GenRRBVectorClosedBlocksQuickRebalance[A]] = sizes(from, to, by).map(((size) => randomVectorOfSize(size)(defaultVectorConfig())));
                override def vectorName: String = super.vectorName.+("XUnbalanced")
              }

              abstract class GenRRBVectorClosedBlocksQuickRebalanceAppendBenchmark[A] extends AppendBenchmarks[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A]

              class GenRRBVectorClosedBlocksQuickRebalanceAppendIntBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {
                def sum32(vec: GenRRBVectorClosedBlocksQuickRebalance[Int], times: Int): Int = {
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

              class GenRRBVectorClosedBlocksQuickRebalanceAppendAnyRefBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceAppendBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator {
                val obj = new Object();
                def sum32(vec: GenRRBVectorClosedBlocksQuickRebalance[AnyRef], times: Int): Int = {
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

              abstract class GenRRBVectorClosedBlocksQuickRebalanceApplyBenchmark[A] extends ApplyBenchmarks[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A]

              class GenRRBVectorClosedBlocksQuickRebalanceApplyIntBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceApplyBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorClosedBlocksQuickRebalanceApplyAnyRefBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceApplyBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorClosedBlocksQuickRebalanceBuilderBenchmark[A] extends BuilderBenchmarks[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A] {
                def buildVector(n: Int, elems: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  var b = GenRRBVectorClosedBlocksQuickRebalance.newBuilder[A];
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

              class GenRRBVectorClosedBlocksQuickRebalanceBuilderIntBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorClosedBlocksQuickRebalanceBuilderAnyRefBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceBuilderBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorClosedBlocksQuickRebalanceConcatenationBenchmark[A] extends ConcatenationBenchmarks[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A]

              class GenRRBVectorClosedBlocksQuickRebalanceConcatenationIntBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceConcatenationBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorClosedBlocksQuickRebalanceConcatenationAnyRefBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceConcatenationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorClosedBlocksQuickRebalanceIterationBenchmark[A] extends IterationBenchmarks[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A]

              class GenRRBVectorClosedBlocksQuickRebalanceIterationIntBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceIterationBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorClosedBlocksQuickRebalanceIterationAnyRefBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceIterationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorClosedBlocksQuickRebalanceMemoryAllocationBenchmark[A] extends MemoryAllocation[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A]

              class GenRRBVectorClosedBlocksQuickRebalanceIntMemoryAllocationBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceMemoryAllocationBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorClosedBlocksQuickRebalanceAnyRefMemoryAllocationBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceMemoryAllocationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorClosedBlocksQuickRebalanceSplitBenchmark[A] extends SplitBenchmarks[A] with GenRRBVectorClosedBlocksQuickRebalanceBenchmark[A]

              class GenRRBVectorClosedBlocksQuickRebalanceSplitIntBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceSplitBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorClosedBlocksQuickRebalanceSplitAnyRefBenchmark extends GenRRBVectorClosedBlocksQuickRebalanceSplitBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator
            }
          }
        }
      }
    }
  }
}