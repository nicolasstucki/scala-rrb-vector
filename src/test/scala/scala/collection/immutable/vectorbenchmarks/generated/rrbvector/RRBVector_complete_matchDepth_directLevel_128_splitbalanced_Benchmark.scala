package scala {
  package collection {
    package immutable {
      package vectorbenchmarks {
        package generated {
          package rrbvector {
            package balanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector._

              import scala.collection.immutable.generated.rrbvector._

              trait RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A] extends BaseVectorBenchmark[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalancedGenerator[A] {
                override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[RRBVector_complete_matchDepth_directLevel_128_splitbalanced[A]] = sizes(from, to, by).map(((size) => tabulatedVector(size)));
                override def vectorName: String = super.vectorName.+("Balanced")
              }

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Append_Benchmark[A] extends AppendBenchmarks[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A]

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_AppendInt_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Append_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def sum1(vec: RRBVector_complete_matchDepth_directLevel_128_splitbalanced[Int], times: Int): Int = {
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
                def sum8(vec: RRBVector_complete_matchDepth_directLevel_128_splitbalanced[Int], times: Int): Int = {
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
                def sum(vec: RRBVector_complete_matchDepth_directLevel_128_splitbalanced[Int], n: Int, times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = vec.:+(0);
                          j.+=(1)
                        }
                      ;
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                }
              }

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_AppendString_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Append_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def sum1(vec: RRBVector_complete_matchDepth_directLevel_128_splitbalanced[String], times: Int): Int = {
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
                def sum8(vec: RRBVector_complete_matchDepth_directLevel_128_splitbalanced[String], times: Int): Int = {
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
                def sum(vec: RRBVector_complete_matchDepth_directLevel_128_splitbalanced[String], n: Int, times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = vec.:+(ref);
                          j.+=(1)
                        }
                      ;
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                }
              }

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Apply_Benchmark[A] extends ApplyBenchmarks[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A]

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_ApplyInt_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Apply_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_ApplyString_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Apply_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Builder_Benchmark[A] extends BuilderBenchmarks[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A] {
                def buildVector(n: Int, elems: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  var b = RRBVector_complete_matchDepth_directLevel_128_splitbalanced.newBuilder[A];
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

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_BuilderInt_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Builder_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_BuilderString_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Builder_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Concatenation_Benchmark[A] extends ConcatenationBenchmarks[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A]

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_ConcatenationInt_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Concatenation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_ConcatenationString_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Concatenation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Iteration_Benchmark[A] extends IterationBenchmarks[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A]

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_IterationInt_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Iteration_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_IterationString_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Iteration_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_MemoryAllocation_Benchmark[A] extends MemoryAllocation[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A]

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_IntMemoryAllocation_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_MemoryAllocation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_StringMemoryAllocation_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_MemoryAllocation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Split_Benchmark[A] extends SplitBenchmarks[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A]

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_SplitInt_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Split_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_SplitString_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Split_Benchmark[String] with VectorGeneratorType.StringGenerator
            }

            package xunbalanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector._

              import scala.collection.immutable.generated.rrbvector._

              trait RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A] extends BaseVectorBenchmark[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalancedGenerator[A] {
                override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[RRBVector_complete_matchDepth_directLevel_128_splitbalanced[A]] = sizes(from, to, by).map(((size) => randomVectorOfSize(size)(defaultVectorConfig(111))));
                override def vectorName: String = super.vectorName.+("XUnbalanced")
              }

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Append_Benchmark[A] extends AppendBenchmarks[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A]

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_AppendInt_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Append_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def sum1(vec: RRBVector_complete_matchDepth_directLevel_128_splitbalanced[Int], times: Int): Int = {
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
                def sum8(vec: RRBVector_complete_matchDepth_directLevel_128_splitbalanced[Int], times: Int): Int = {
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
                def sum(vec: RRBVector_complete_matchDepth_directLevel_128_splitbalanced[Int], n: Int, times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = vec.:+(0);
                          j.+=(1)
                        }
                      ;
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                }
              }

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_AppendString_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Append_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def sum1(vec: RRBVector_complete_matchDepth_directLevel_128_splitbalanced[String], times: Int): Int = {
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
                def sum8(vec: RRBVector_complete_matchDepth_directLevel_128_splitbalanced[String], times: Int): Int = {
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
                def sum(vec: RRBVector_complete_matchDepth_directLevel_128_splitbalanced[String], n: Int, times: Int): Int = {
                  var i = 0;
                  var v = vec;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = vec.:+(ref);
                          j.+=(1)
                        }
                      ;
                      sum.+=(v.length);
                      i.+=(1)
                    }
                  ;
                  sum
                }
              }

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Apply_Benchmark[A] extends ApplyBenchmarks[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A]

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_ApplyInt_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Apply_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_ApplyString_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Apply_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Builder_Benchmark[A] extends BuilderBenchmarks[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A] {
                def buildVector(n: Int, elems: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  var b = RRBVector_complete_matchDepth_directLevel_128_splitbalanced.newBuilder[A];
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

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_BuilderInt_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Builder_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_BuilderString_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Builder_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Concatenation_Benchmark[A] extends ConcatenationBenchmarks[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A]

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_ConcatenationInt_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Concatenation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_ConcatenationString_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Concatenation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Iteration_Benchmark[A] extends IterationBenchmarks[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A]

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_IterationInt_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Iteration_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_IterationString_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Iteration_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_MemoryAllocation_Benchmark[A] extends MemoryAllocation[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A]

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_IntMemoryAllocation_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_MemoryAllocation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_StringMemoryAllocation_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_MemoryAllocation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Split_Benchmark[A] extends SplitBenchmarks[A] with RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Benchmark[A]

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_SplitInt_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Split_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_complete_matchDepth_directLevel_128_splitbalanced_SplitString_Benchmark extends RRBVector_complete_matchDepth_directLevel_128_splitbalanced_Split_Benchmark[String] with VectorGeneratorType.StringGenerator
            }
          }
        }
      }
    }
  }
}