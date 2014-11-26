package scala {
  package collection {
    package immutable {
      package vectorbenchmarks {
        package generated {
          package rrbvector.complete.block256 {
            package balanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector.complete.block256._

              import scala.collection.immutable.generated.rrbvector.complete.block256._

              trait RRBVector_c_256_Benchmark[A] extends BaseVectorBenchmark[A] with RRBVectorGenerator_c_256[A] {
                override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[RRBVector_c_256[A]] = sizes(from, to, by).map(((size) => tabulatedVector(size)));
                override def vectorName: String = super.vectorName.+("Balanced")
              }

              abstract class RRBVector_c_256_Append_Benchmark[A] extends AppendBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_AppendInt_Benchmark extends RRBVector_c_256_Append_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def append(vec: RRBVector_c_256[Int], n: Int): Int = {
                  var v = vec;
                  var i = 0;
                  while (i.<(n)) 
                    {
                      v = vec.:+(0);
                      i.+=(1)
                    }
                  ;
                  v.length
                }
              }

              class RRBVector_c_256_AppendString_Benchmark extends RRBVector_c_256_Append_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def append(vec: RRBVector_c_256[String], n: Int): Int = {
                  var v = vec;
                  var i = 0;
                  while (i.<(n)) 
                    {
                      v = vec.:+(ref);
                      i.+=(1)
                    }
                  ;
                  v.length
                }
              }

              abstract class RRBVector_c_256_Prepend_Benchmark[A] extends PrependBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_PrependInt_Benchmark extends RRBVector_c_256_Prepend_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def prepend(vec: RRBVector_c_256[Int], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = {
                            val x$36 = 0;
                            vec.+:(x$36)
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

              class RRBVector_c_256_PrependString_Benchmark extends RRBVector_c_256_Prepend_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def prepend(vec: RRBVector_c_256[String], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = {
                            val x$35 = ref;
                            vec.+:(x$35)
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

              abstract class RRBVector_c_256_Apply_Benchmark[A] extends ApplyBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_ApplyInt_Benchmark extends RRBVector_c_256_Apply_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_ApplyString_Benchmark extends RRBVector_c_256_Apply_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_c_256_Builder_Benchmark[A] extends BuilderBenchmarks[A] with RRBVector_c_256_Benchmark[A] {
                def buildVector(n: Int): Int = {
                  var i = 0;
                  var b = RRBVector_c_256.newBuilder[A];
                  val e = element(0);
                  while (i.<(n)) 
                    {
                      b.+=(e);
                      i.+=(1)
                    }
                  ;
                  b.result().length
                }
              }

              class RRBVector_c_256_BuilderInt_Benchmark extends RRBVector_c_256_Builder_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_BuilderString_Benchmark extends RRBVector_c_256_Builder_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_c_256_Concatenation_Benchmark[A] extends ConcatenationBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_ConcatenationInt_Benchmark extends RRBVector_c_256_Concatenation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_ConcatenationString_Benchmark extends RRBVector_c_256_Concatenation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_c_256_Iteration_Benchmark[A] extends IterationBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_IterationInt_Benchmark extends RRBVector_c_256_Iteration_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_IterationString_Benchmark extends RRBVector_c_256_Iteration_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_c_256_MemoryAllocation_Benchmark[A] extends MemoryAllocation[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_IntMemoryAllocation_Benchmark extends RRBVector_c_256_MemoryAllocation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_StringMemoryAllocation_Benchmark extends RRBVector_c_256_MemoryAllocation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_c_256_Split_Benchmark[A] extends SplitBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_SplitInt_Benchmark extends RRBVector_c_256_Split_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_SplitString_Benchmark extends RRBVector_c_256_Split_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_c_256_ParMap_Benchmark[A] extends ParMapBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_IntParMap_Benchmark extends RRBVector_c_256_ParMap_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_StringParMap_Benchmark extends RRBVector_c_256_ParMap_Benchmark[String] with VectorGeneratorType.StringGenerator
            }

            package xunbalanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector.complete.block256._

              import scala.collection.immutable.generated.rrbvector.complete.block256._

              trait RRBVector_c_256_Benchmark[A] extends BaseVectorBenchmark[A] with RRBVectorGenerator_c_256[A] {
                override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[RRBVector_c_256[A]] = sizes(from, to, by).map(((size) => randomVectorOfSize(size)(defaultVectorConfig(111))));
                override def vectorName: String = super.vectorName.+("XUnbalanced")
              }

              abstract class RRBVector_c_256_Append_Benchmark[A] extends AppendBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_AppendInt_Benchmark extends RRBVector_c_256_Append_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def append(vec: RRBVector_c_256[Int], n: Int): Int = {
                  var v = vec;
                  var i = 0;
                  while (i.<(n)) 
                    {
                      v = vec.:+(0);
                      i.+=(1)
                    }
                  ;
                  v.length
                }
              }

              class RRBVector_c_256_AppendString_Benchmark extends RRBVector_c_256_Append_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def append(vec: RRBVector_c_256[String], n: Int): Int = {
                  var v = vec;
                  var i = 0;
                  while (i.<(n)) 
                    {
                      v = vec.:+(ref);
                      i.+=(1)
                    }
                  ;
                  v.length
                }
              }

              abstract class RRBVector_c_256_Prepend_Benchmark[A] extends PrependBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_PrependInt_Benchmark extends RRBVector_c_256_Prepend_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def prepend(vec: RRBVector_c_256[Int], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = {
                            val x$38 = 0;
                            vec.+:(x$38)
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

              class RRBVector_c_256_PrependString_Benchmark extends RRBVector_c_256_Prepend_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def prepend(vec: RRBVector_c_256[String], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = {
                            val x$37 = ref;
                            vec.+:(x$37)
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

              abstract class RRBVector_c_256_Apply_Benchmark[A] extends ApplyBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_ApplyInt_Benchmark extends RRBVector_c_256_Apply_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_ApplyString_Benchmark extends RRBVector_c_256_Apply_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_c_256_Builder_Benchmark[A] extends BuilderBenchmarks[A] with RRBVector_c_256_Benchmark[A] {
                def buildVector(n: Int): Int = {
                  var i = 0;
                  var b = RRBVector_c_256.newBuilder[A];
                  val e = element(0);
                  while (i.<(n)) 
                    {
                      b.+=(e);
                      i.+=(1)
                    }
                  ;
                  b.result().length
                }
              }

              class RRBVector_c_256_BuilderInt_Benchmark extends RRBVector_c_256_Builder_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_BuilderString_Benchmark extends RRBVector_c_256_Builder_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_c_256_Concatenation_Benchmark[A] extends ConcatenationBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_ConcatenationInt_Benchmark extends RRBVector_c_256_Concatenation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_ConcatenationString_Benchmark extends RRBVector_c_256_Concatenation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_c_256_Iteration_Benchmark[A] extends IterationBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_IterationInt_Benchmark extends RRBVector_c_256_Iteration_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_IterationString_Benchmark extends RRBVector_c_256_Iteration_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_c_256_MemoryAllocation_Benchmark[A] extends MemoryAllocation[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_IntMemoryAllocation_Benchmark extends RRBVector_c_256_MemoryAllocation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_StringMemoryAllocation_Benchmark extends RRBVector_c_256_MemoryAllocation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_c_256_Split_Benchmark[A] extends SplitBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_SplitInt_Benchmark extends RRBVector_c_256_Split_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_SplitString_Benchmark extends RRBVector_c_256_Split_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_c_256_ParMap_Benchmark[A] extends ParMapBenchmarks[A] with RRBVector_c_256_Benchmark[A]

              class RRBVector_c_256_IntParMap_Benchmark extends RRBVector_c_256_ParMap_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_c_256_StringParMap_Benchmark extends RRBVector_c_256_ParMap_Benchmark[String] with VectorGeneratorType.StringGenerator
            }
          }
        }
      }
    }
  }
}