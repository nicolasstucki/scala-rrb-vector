package scala {
  package collection {
    package immutable {
      package vectorbenchmarks {
        package generated {
          package rrbvector.quick {
            package balanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector.quick._

              import scala.collection.immutable.generated.rrbvector.quick._

              trait RRBVector_quick_64_Benchmark[A] extends BaseVectorBenchmark[A] with RRBVector_quick_64Generator[A] {
                override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[RRBVector_quick_64[A]] = sizes(from, to, by).map(((size) => tabulatedVector(size)));
                override def vectorName: String = super.vectorName.+("Balanced")
              }

              abstract class RRBVector_quick_64_Append_Benchmark[A] extends AppendBenchmarks[A] with RRBVector_quick_64_Benchmark[A]

              class RRBVector_quick_64_AppendInt_Benchmark extends RRBVector_quick_64_Append_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def sum1(vec: RRBVector_quick_64[Int], times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      v = vec.:+(0);
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                };
                def sum8(vec: RRBVector_quick_64[Int], times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      v = vec.:+(0).:+(0).:+(0).:+(0).:+(0).:+(0).:+(0).:+(0);
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                };
                def sum32(vec: RRBVector_quick_64[Int], times: Int): Int = {
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

              class RRBVector_quick_64_AppendString_Benchmark extends RRBVector_quick_64_Append_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def sum1(vec: RRBVector_quick_64[String], times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      v = vec.:+(ref);
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                };
                def sum8(vec: RRBVector_quick_64[String], times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      v = vec.:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref);
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                };
                def sum32(vec: RRBVector_quick_64[String], times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      v = vec.:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref);
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                }
              }

              abstract class RRBVector_quick_64_Apply_Benchmark[A] extends ApplyBenchmarks[A] with RRBVector_quick_64_Benchmark[A]

              class RRBVector_quick_64_ApplyInt_Benchmark extends RRBVector_quick_64_Apply_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_quick_64_ApplyString_Benchmark extends RRBVector_quick_64_Apply_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_quick_64_Builder_Benchmark[A] extends BuilderBenchmarks[A] with RRBVector_quick_64_Benchmark[A] {
                def buildVector(n: Int, elems: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  var b = RRBVector_quick_64.newBuilder[A];
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

              class RRBVector_quick_64_BuilderInt_Benchmark extends RRBVector_quick_64_Builder_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_quick_64_BuilderString_Benchmark extends RRBVector_quick_64_Builder_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_quick_64_Concatenation_Benchmark[A] extends ConcatenationBenchmarks[A] with RRBVector_quick_64_Benchmark[A]

              class RRBVector_quick_64_ConcatenationInt_Benchmark extends RRBVector_quick_64_Concatenation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_quick_64_ConcatenationString_Benchmark extends RRBVector_quick_64_Concatenation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_quick_64_Iteration_Benchmark[A] extends IterationBenchmarks[A] with RRBVector_quick_64_Benchmark[A]

              class RRBVector_quick_64_IterationInt_Benchmark extends RRBVector_quick_64_Iteration_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_quick_64_IterationString_Benchmark extends RRBVector_quick_64_Iteration_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_quick_64_MemoryAllocation_Benchmark[A] extends MemoryAllocation[A] with RRBVector_quick_64_Benchmark[A]

              class RRBVector_quick_64_IntMemoryAllocation_Benchmark extends RRBVector_quick_64_MemoryAllocation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_quick_64_StringMemoryAllocation_Benchmark extends RRBVector_quick_64_MemoryAllocation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_quick_64_Split_Benchmark[A] extends SplitBenchmarks[A] with RRBVector_quick_64_Benchmark[A]

              class RRBVector_quick_64_SplitInt_Benchmark extends RRBVector_quick_64_Split_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_quick_64_SplitString_Benchmark extends RRBVector_quick_64_Split_Benchmark[String] with VectorGeneratorType.StringGenerator
            }

            package xunbalanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector.quick._

              import scala.collection.immutable.generated.rrbvector.quick._

              trait RRBVector_quick_64_Benchmark[A] extends BaseVectorBenchmark[A] with RRBVector_quick_64Generator[A] {
                override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[RRBVector_quick_64[A]] = sizes(from, to, by).map(((size) => randomVectorOfSize(size)(defaultVectorConfig())));
                override def vectorName: String = super.vectorName.+("XUnbalanced")
              }

              abstract class RRBVector_quick_64_Append_Benchmark[A] extends AppendBenchmarks[A] with RRBVector_quick_64_Benchmark[A]

              class RRBVector_quick_64_AppendInt_Benchmark extends RRBVector_quick_64_Append_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def sum1(vec: RRBVector_quick_64[Int], times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      v = vec.:+(0);
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                };
                def sum8(vec: RRBVector_quick_64[Int], times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      v = vec.:+(0).:+(0).:+(0).:+(0).:+(0).:+(0).:+(0).:+(0);
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                };
                def sum32(vec: RRBVector_quick_64[Int], times: Int): Int = {
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

              class RRBVector_quick_64_AppendString_Benchmark extends RRBVector_quick_64_Append_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def sum1(vec: RRBVector_quick_64[String], times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      v = vec.:+(ref);
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                };
                def sum8(vec: RRBVector_quick_64[String], times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      v = vec.:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref);
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                };
                def sum32(vec: RRBVector_quick_64[String], times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      v = vec.:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref).:+(ref);
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                }
              }

              abstract class RRBVector_quick_64_Apply_Benchmark[A] extends ApplyBenchmarks[A] with RRBVector_quick_64_Benchmark[A]

              class RRBVector_quick_64_ApplyInt_Benchmark extends RRBVector_quick_64_Apply_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_quick_64_ApplyString_Benchmark extends RRBVector_quick_64_Apply_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_quick_64_Builder_Benchmark[A] extends BuilderBenchmarks[A] with RRBVector_quick_64_Benchmark[A] {
                def buildVector(n: Int, elems: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  var b = RRBVector_quick_64.newBuilder[A];
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

              class RRBVector_quick_64_BuilderInt_Benchmark extends RRBVector_quick_64_Builder_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_quick_64_BuilderString_Benchmark extends RRBVector_quick_64_Builder_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_quick_64_Concatenation_Benchmark[A] extends ConcatenationBenchmarks[A] with RRBVector_quick_64_Benchmark[A]

              class RRBVector_quick_64_ConcatenationInt_Benchmark extends RRBVector_quick_64_Concatenation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_quick_64_ConcatenationString_Benchmark extends RRBVector_quick_64_Concatenation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_quick_64_Iteration_Benchmark[A] extends IterationBenchmarks[A] with RRBVector_quick_64_Benchmark[A]

              class RRBVector_quick_64_IterationInt_Benchmark extends RRBVector_quick_64_Iteration_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_quick_64_IterationString_Benchmark extends RRBVector_quick_64_Iteration_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_quick_64_MemoryAllocation_Benchmark[A] extends MemoryAllocation[A] with RRBVector_quick_64_Benchmark[A]

              class RRBVector_quick_64_IntMemoryAllocation_Benchmark extends RRBVector_quick_64_MemoryAllocation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_quick_64_StringMemoryAllocation_Benchmark extends RRBVector_quick_64_MemoryAllocation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_quick_64_Split_Benchmark[A] extends SplitBenchmarks[A] with RRBVector_quick_64_Benchmark[A]

              class RRBVector_quick_64_SplitInt_Benchmark extends RRBVector_quick_64_Split_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_quick_64_SplitString_Benchmark extends RRBVector_quick_64_Split_Benchmark[String] with VectorGeneratorType.StringGenerator
            }
          }
        }
      }
    }
  }
}