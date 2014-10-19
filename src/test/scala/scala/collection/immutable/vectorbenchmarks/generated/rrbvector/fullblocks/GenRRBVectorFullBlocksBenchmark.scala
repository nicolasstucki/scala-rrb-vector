package scala {
  package collection {
    package immutable {
      package vectorbenchmarks {
        package generated {
          package rrbvector.fullblocks {
            package balanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector.fullblocks._

              import scala.collection.immutable.generated.rrbvector.fullblocks._

              trait GenRRBVectorFullBlocksBenchmark[A] extends BaseVectorBenchmark[A] with GenRRBVectorFullBlocksGenerator[A] {
                override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[GenRRBVectorFullBlocks[A]] = sizes(from, to, by).map(((size) => tabulatedVector(size)));
                override def vectorName: String = super.vectorName.+("Balanced")
              }

              abstract class GenRRBVectorFullBlocksAppendBenchmark[A] extends AppendBenchmarks[A] with GenRRBVectorFullBlocksBenchmark[A]

              class GenRRBVectorFullBlocksAppendIntBenchmark extends GenRRBVectorFullBlocksAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {
                def sum32(vec: GenRRBVectorFullBlocks[Int], times: Int): Int = {
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

              class GenRRBVectorFullBlocksAppendAnyRefBenchmark extends GenRRBVectorFullBlocksAppendBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator {
                val obj = new Object();
                def sum32(vec: GenRRBVectorFullBlocks[AnyRef], times: Int): Int = {
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

              abstract class GenRRBVectorFullBlocksApplyBenchmark[A] extends ApplyBenchmarks[A] with GenRRBVectorFullBlocksBenchmark[A]

              class GenRRBVectorFullBlocksApplyIntBenchmark extends GenRRBVectorFullBlocksApplyBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorFullBlocksApplyAnyRefBenchmark extends GenRRBVectorFullBlocksApplyBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorFullBlocksBuilderBenchmark[A] extends BuilderBenchmarks[A] with GenRRBVectorFullBlocksBenchmark[A] {
                def buildVector(n: Int, elems: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  var b = GenRRBVectorFullBlocks.newBuilder[A];
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

              class GenRRBVectorFullBlocksBuilderIntBenchmark extends GenRRBVectorFullBlocksBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorFullBlocksBuilderAnyRefBenchmark extends GenRRBVectorFullBlocksBuilderBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorFullBlocksConcatenationBenchmark[A] extends ConcatenationBenchmarks[A] with GenRRBVectorFullBlocksBenchmark[A]

              class GenRRBVectorFullBlocksConcatenationIntBenchmark extends GenRRBVectorFullBlocksConcatenationBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorFullBlocksConcatenationAnyRefBenchmark extends GenRRBVectorFullBlocksConcatenationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorFullBlocksIterationBenchmark[A] extends IterationBenchmarks[A] with GenRRBVectorFullBlocksBenchmark[A]

              class GenRRBVectorFullBlocksIterationIntBenchmark extends GenRRBVectorFullBlocksIterationBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorFullBlocksIterationAnyRefBenchmark extends GenRRBVectorFullBlocksIterationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorFullBlocksMemoryAllocationBenchmark[A] extends MemoryAllocation[A] with GenRRBVectorFullBlocksBenchmark[A]

              class GenRRBVectorFullBlocksIntMemoryAllocationBenchmark extends GenRRBVectorFullBlocksMemoryAllocationBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorFullBlocksAnyRefMemoryAllocationBenchmark extends GenRRBVectorFullBlocksMemoryAllocationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorFullBlocksSplitBenchmark[A] extends SplitBenchmarks[A] with GenRRBVectorFullBlocksBenchmark[A]

              class GenRRBVectorFullBlocksSplitIntBenchmark extends GenRRBVectorFullBlocksSplitBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorFullBlocksSplitAnyRefBenchmark extends GenRRBVectorFullBlocksSplitBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator
            }

            package xunbalanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector.fullblocks._

              import scala.collection.immutable.generated.rrbvector.fullblocks._

              trait GenRRBVectorFullBlocksBenchmark[A] extends BaseVectorBenchmark[A] with GenRRBVectorFullBlocksGenerator[A] {
                override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[GenRRBVectorFullBlocks[A]] = sizes(from, to, by).map(((size) => randomVectorOfSize(size)(defaultVectorConfig())));
                override def vectorName: String = super.vectorName.+("XUnbalanced")
              }

              abstract class GenRRBVectorFullBlocksAppendBenchmark[A] extends AppendBenchmarks[A] with GenRRBVectorFullBlocksBenchmark[A]

              class GenRRBVectorFullBlocksAppendIntBenchmark extends GenRRBVectorFullBlocksAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {
                def sum32(vec: GenRRBVectorFullBlocks[Int], times: Int): Int = {
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

              class GenRRBVectorFullBlocksAppendAnyRefBenchmark extends GenRRBVectorFullBlocksAppendBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator {
                val obj = new Object();
                def sum32(vec: GenRRBVectorFullBlocks[AnyRef], times: Int): Int = {
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

              abstract class GenRRBVectorFullBlocksApplyBenchmark[A] extends ApplyBenchmarks[A] with GenRRBVectorFullBlocksBenchmark[A]

              class GenRRBVectorFullBlocksApplyIntBenchmark extends GenRRBVectorFullBlocksApplyBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorFullBlocksApplyAnyRefBenchmark extends GenRRBVectorFullBlocksApplyBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorFullBlocksBuilderBenchmark[A] extends BuilderBenchmarks[A] with GenRRBVectorFullBlocksBenchmark[A] {
                def buildVector(n: Int, elems: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  var b = GenRRBVectorFullBlocks.newBuilder[A];
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

              class GenRRBVectorFullBlocksBuilderIntBenchmark extends GenRRBVectorFullBlocksBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorFullBlocksBuilderAnyRefBenchmark extends GenRRBVectorFullBlocksBuilderBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorFullBlocksConcatenationBenchmark[A] extends ConcatenationBenchmarks[A] with GenRRBVectorFullBlocksBenchmark[A]

              class GenRRBVectorFullBlocksConcatenationIntBenchmark extends GenRRBVectorFullBlocksConcatenationBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorFullBlocksConcatenationAnyRefBenchmark extends GenRRBVectorFullBlocksConcatenationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorFullBlocksIterationBenchmark[A] extends IterationBenchmarks[A] with GenRRBVectorFullBlocksBenchmark[A]

              class GenRRBVectorFullBlocksIterationIntBenchmark extends GenRRBVectorFullBlocksIterationBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorFullBlocksIterationAnyRefBenchmark extends GenRRBVectorFullBlocksIterationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorFullBlocksMemoryAllocationBenchmark[A] extends MemoryAllocation[A] with GenRRBVectorFullBlocksBenchmark[A]

              class GenRRBVectorFullBlocksIntMemoryAllocationBenchmark extends GenRRBVectorFullBlocksMemoryAllocationBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorFullBlocksAnyRefMemoryAllocationBenchmark extends GenRRBVectorFullBlocksMemoryAllocationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

              abstract class GenRRBVectorFullBlocksSplitBenchmark[A] extends SplitBenchmarks[A] with GenRRBVectorFullBlocksBenchmark[A]

              class GenRRBVectorFullBlocksSplitIntBenchmark extends GenRRBVectorFullBlocksSplitBenchmark[Int] with VectorGeneratorType.IntGenerator

              class GenRRBVectorFullBlocksSplitAnyRefBenchmark extends GenRRBVectorFullBlocksSplitBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator
            }
          }
        }
      }
    }
  }
}