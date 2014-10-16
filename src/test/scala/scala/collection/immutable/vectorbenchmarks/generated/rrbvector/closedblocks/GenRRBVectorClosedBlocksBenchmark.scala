package scala {
  package collection {
    package immutable {
      package vectorbenchmarks {
        package generated {
          package rrbvector.closedblocks {
            import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

            import scala.collection.immutable.vectorutils.VectorGeneratorType

            import scala.collection.immutable.vectorutils.generated.rrbvector.closedblocks._

            import scala.collection.immutable.generated.rrbvector.closedblocks._

            trait GenRRBVectorClosedBlocksBenchmark[A] extends BaseVectorBenchmark[A] with GenRRBVectorClosedBlocksGenerator[A] {
              override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[GenRRBVectorClosedBlocks[A]] = sizes(from, to, by).map(((size) => GenRRBVectorClosedBlocks.tabulate(size)(element)))
            }

            abstract class GenRRBVectorClosedBlocksAppendBenchmark[A] extends AppendBenchmarks[A] with GenRRBVectorClosedBlocksBenchmark[A]

            class GenRRBVectorClosedBlocksAppendIntBenchmark extends GenRRBVectorClosedBlocksAppendBenchmark[Int] with VectorGeneratorType.IntGenerator {
              def sum32(vec: GenRRBVectorClosedBlocks[Int], times: Int): Int = {
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

            class GenRRBVectorClosedBlocksAppendAnyRefBenchmark extends GenRRBVectorClosedBlocksAppendBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator {
              val obj = new Object();
              def sum32(vec: GenRRBVectorClosedBlocks[AnyRef], times: Int): Int = {
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

            abstract class GenRRBVectorClosedBlocksApplyBenchmark[A] extends ApplyBenchmarks[A] with GenRRBVectorClosedBlocksBenchmark[A]

            class GenRRBVectorClosedBlocksApplyIntBenchmark extends GenRRBVectorClosedBlocksApplyBenchmark[Int] with VectorGeneratorType.IntGenerator

            class GenRRBVectorClosedBlocksApplyAnyRefBenchmark extends GenRRBVectorClosedBlocksApplyBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

            abstract class GenRRBVectorClosedBlocksBuilderBenchmark[A] extends BuilderBenchmarks[A] with GenRRBVectorClosedBlocksBenchmark[A] {
              def buildVector(n: Int, elems: Int): Int = {
                var i = 0;
                var sum = 0;
                var b = GenRRBVectorClosedBlocks.newBuilder[A];
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

            class GenRRBVectorClosedBlocksBuilderIntBenchmark extends GenRRBVectorClosedBlocksBuilderBenchmark[Int] with VectorGeneratorType.IntGenerator

            class GenRRBVectorClosedBlocksBuilderAnyRefBenchmark extends GenRRBVectorClosedBlocksBuilderBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

            abstract class GenRRBVectorClosedBlocksConcatenationBenchmark[A] extends ConcatenationBenchmarks[A] with GenRRBVectorClosedBlocksBenchmark[A]

            class GenRRBVectorClosedBlocksConcatenationIntBenchmark extends GenRRBVectorClosedBlocksConcatenationBenchmark[Int] with VectorGeneratorType.IntGenerator

            class GenRRBVectorClosedBlocksConcatenationAnyRefBenchmark extends GenRRBVectorClosedBlocksConcatenationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

            abstract class GenRRBVectorClosedBlocksIterationBenchmark[A] extends IterationBenchmarks[A] with GenRRBVectorClosedBlocksBenchmark[A]

            class GenRRBVectorClosedBlocksIterationIntBenchmark extends GenRRBVectorClosedBlocksIterationBenchmark[Int] with VectorGeneratorType.IntGenerator

            class GenRRBVectorClosedBlocksIterationAnyRefBenchmark extends GenRRBVectorClosedBlocksIterationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

            abstract class GenRRBVectorClosedBlocksMemoryAllocationBenchmark[A] extends MemoryAllocation[A] with GenRRBVectorClosedBlocksBenchmark[A]

            class GenRRBVectorClosedBlocksIntMemoryAllocationBenchmark extends GenRRBVectorClosedBlocksMemoryAllocationBenchmark[Int] with VectorGeneratorType.IntGenerator

            class GenRRBVectorClosedBlocksAnyRefMemoryAllocationBenchmark extends GenRRBVectorClosedBlocksMemoryAllocationBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator

            abstract class GenRRBVectorClosedBlocksSplitBenchmark[A] extends SplitBenchmarks[A] with GenRRBVectorClosedBlocksBenchmark[A]

            class GenRRBVectorClosedBlocksSplitIntBenchmark extends GenRRBVectorClosedBlocksSplitBenchmark[Int] with VectorGeneratorType.IntGenerator

            class GenRRBVectorClosedBlocksSplitAnyRefBenchmark extends GenRRBVectorClosedBlocksSplitBenchmark[AnyRef] with VectorGeneratorType.AnyRefGenerator
          }
        }
      }
    }
  }
}