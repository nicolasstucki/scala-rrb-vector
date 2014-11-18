package scala {
  package collection {
    package immutable {
      package vectorbenchmarks {
        package generated {
          package rrbvector.quick.block32 {
            package balanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector.quick.block32._

              import scala.collection.immutable.generated.rrbvector.quick.block32._

              trait RRBVector_q_32_Benchmark[A] extends BaseVectorBenchmark[A] with RRBVectorGenerator_q_32[A] {
                override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[RRBVector_q_32[A]] = sizes(from, to, by).map(((size) => tabulatedVector(size)));
                override def vectorName: String = super.vectorName.+("Balanced")
              }

              abstract class RRBVector_q_32_Append_Benchmark[A] extends AppendBenchmarks[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_AppendInt_Benchmark extends RRBVector_q_32_Append_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def append(vec: RRBVector_q_32[Int], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
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

              class RRBVector_q_32_AppendString_Benchmark extends RRBVector_q_32_Append_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def append(vec: RRBVector_q_32[String], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
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

              abstract class RRBVector_q_32_Prepend_Benchmark[A] extends PrependBenchmarks[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_PrependInt_Benchmark extends RRBVector_q_32_Prepend_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def prepend(vec: RRBVector_q_32[Int], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = {
                            val x$66 = 0;
                            vec.+:(x$66)
                          };
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

              class RRBVector_q_32_PrependString_Benchmark extends RRBVector_q_32_Prepend_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def prepend(vec: RRBVector_q_32[String], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = {
                            val x$65 = ref;
                            vec.+:(x$65)
                          };
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

              abstract class RRBVector_q_32_Apply_Benchmark[A] extends ApplyBenchmarks[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_ApplyInt_Benchmark extends RRBVector_q_32_Apply_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_32_ApplyString_Benchmark extends RRBVector_q_32_Apply_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_32_Builder_Benchmark[A] extends BuilderBenchmarks[A] with RRBVector_q_32_Benchmark[A] {
                def buildVector(n: Int, elems: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  var b = RRBVector_q_32.newBuilder[A];
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

              class RRBVector_q_32_BuilderInt_Benchmark extends RRBVector_q_32_Builder_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_32_BuilderString_Benchmark extends RRBVector_q_32_Builder_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_32_Concatenation_Benchmark[A] extends ConcatenationBenchmarks[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_ConcatenationInt_Benchmark extends RRBVector_q_32_Concatenation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_32_ConcatenationString_Benchmark extends RRBVector_q_32_Concatenation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_32_Iteration_Benchmark[A] extends IterationBenchmarks[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_IterationInt_Benchmark extends RRBVector_q_32_Iteration_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_32_IterationString_Benchmark extends RRBVector_q_32_Iteration_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_32_MemoryAllocation_Benchmark[A] extends MemoryAllocation[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_IntMemoryAllocation_Benchmark extends RRBVector_q_32_MemoryAllocation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_32_StringMemoryAllocation_Benchmark extends RRBVector_q_32_MemoryAllocation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_32_Split_Benchmark[A] extends SplitBenchmarks[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_SplitInt_Benchmark extends RRBVector_q_32_Split_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_32_SplitString_Benchmark extends RRBVector_q_32_Split_Benchmark[String] with VectorGeneratorType.StringGenerator
            }

            package xunbalanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector.quick.block32._

              import scala.collection.immutable.generated.rrbvector.quick.block32._

              trait RRBVector_q_32_Benchmark[A] extends BaseVectorBenchmark[A] with RRBVectorGenerator_q_32[A] {
                override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[RRBVector_q_32[A]] = sizes(from, to, by).map(((size) => randomVectorOfSize(size)(defaultVectorConfig(111))));
                override def vectorName: String = super.vectorName.+("XUnbalanced")
              }

              abstract class RRBVector_q_32_Append_Benchmark[A] extends AppendBenchmarks[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_AppendInt_Benchmark extends RRBVector_q_32_Append_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def append(vec: RRBVector_q_32[Int], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
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

              class RRBVector_q_32_AppendString_Benchmark extends RRBVector_q_32_Append_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def append(vec: RRBVector_q_32[String], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
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

              abstract class RRBVector_q_32_Prepend_Benchmark[A] extends PrependBenchmarks[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_PrependInt_Benchmark extends RRBVector_q_32_Prepend_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def prepend(vec: RRBVector_q_32[Int], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = {
                            val x$68 = 0;
                            vec.+:(x$68)
                          };
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

              class RRBVector_q_32_PrependString_Benchmark extends RRBVector_q_32_Prepend_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def prepend(vec: RRBVector_q_32[String], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = {
                            val x$67 = ref;
                            vec.+:(x$67)
                          };
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

              abstract class RRBVector_q_32_Apply_Benchmark[A] extends ApplyBenchmarks[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_ApplyInt_Benchmark extends RRBVector_q_32_Apply_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_32_ApplyString_Benchmark extends RRBVector_q_32_Apply_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_32_Builder_Benchmark[A] extends BuilderBenchmarks[A] with RRBVector_q_32_Benchmark[A] {
                def buildVector(n: Int, elems: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  var b = RRBVector_q_32.newBuilder[A];
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

              class RRBVector_q_32_BuilderInt_Benchmark extends RRBVector_q_32_Builder_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_32_BuilderString_Benchmark extends RRBVector_q_32_Builder_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_32_Concatenation_Benchmark[A] extends ConcatenationBenchmarks[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_ConcatenationInt_Benchmark extends RRBVector_q_32_Concatenation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_32_ConcatenationString_Benchmark extends RRBVector_q_32_Concatenation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_32_Iteration_Benchmark[A] extends IterationBenchmarks[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_IterationInt_Benchmark extends RRBVector_q_32_Iteration_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_32_IterationString_Benchmark extends RRBVector_q_32_Iteration_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_32_MemoryAllocation_Benchmark[A] extends MemoryAllocation[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_IntMemoryAllocation_Benchmark extends RRBVector_q_32_MemoryAllocation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_32_StringMemoryAllocation_Benchmark extends RRBVector_q_32_MemoryAllocation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_32_Split_Benchmark[A] extends SplitBenchmarks[A] with RRBVector_q_32_Benchmark[A]

              class RRBVector_q_32_SplitInt_Benchmark extends RRBVector_q_32_Split_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_32_SplitString_Benchmark extends RRBVector_q_32_Split_Benchmark[String] with VectorGeneratorType.StringGenerator
            }
          }
        }
      }
    }
  }
}