package scala {
  package collection {
    package immutable {
      package vectorbenchmarks {
        package generated {
          package rrbvector.quick.block64 {
            package balanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector.quick.block64._

              import scala.collection.immutable.generated.rrbvector.quick.block64._

              trait RRBVector_q_64_Benchmark[A] extends BaseVectorBenchmark[A] with RRBVectorGenerator_q_64[A] {
                override def generateVectors(from: Int, to: Int, by: Int, sizesName: String): org.scalameter.Gen[RRBVector_q_64[A]] = sizes(from, to, by, sizesName).map(((size) => tabulatedVector(size)));
                def generateVectors2(from: Int, to: Int, by: Int): org.scalameter.Gen[scala.Tuple2[RRBVector_q_64[A], RRBVector_q_64[A]]] = sizes(from, to, by, "size1").flatMap(((size1) => sizes(from, to, by, "size2").map(((size2) => scala.Tuple2(tabulatedVector(size1), tabulatedVector(size2))))));
                override def vectorName: String = super.vectorName.+("Balanced")
              }

              abstract class RRBVector_q_64_Append_Benchmark[A] extends AppendBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_AppendInt_Benchmark extends RRBVector_q_64_Append_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def append(vec: RRBVector_q_64[Int], n: Int): Int = {
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

              class RRBVector_q_64_AppendString_Benchmark extends RRBVector_q_64_Append_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def append(vec: RRBVector_q_64[String], n: Int): Int = {
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

              abstract class RRBVector_q_64_Prepend_Benchmark[A] extends PrependBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_PrependInt_Benchmark extends RRBVector_q_64_Prepend_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def prepend(vec: RRBVector_q_64[Int], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = {
                            val x$65 = 0;
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

              class RRBVector_q_64_PrependString_Benchmark extends RRBVector_q_64_Prepend_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def prepend(vec: RRBVector_q_64[String], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = {
                            val x$66 = ref;
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

              abstract class RRBVector_q_64_Apply_Benchmark[A] extends ApplyBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_ApplyInt_Benchmark extends RRBVector_q_64_Apply_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_ApplyString_Benchmark extends RRBVector_q_64_Apply_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_64_Builder_Benchmark[A] extends BuilderBenchmarks[A] with RRBVector_q_64_Benchmark[A] {
                def buildVector(n: Int): Int = {
                  var i = 0;
                  var b = RRBVector_q_64.newBuilder[A];
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

              class RRBVector_q_64_BuilderInt_Benchmark extends RRBVector_q_64_Builder_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_BuilderString_Benchmark extends RRBVector_q_64_Builder_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_64_Concatenation_Benchmark[A] extends ConcatenationBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_ConcatenationInt_Benchmark extends RRBVector_q_64_Concatenation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_ConcatenationString_Benchmark extends RRBVector_q_64_Concatenation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_64_Iteration_Benchmark[A] extends IterationBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_IterationInt_Benchmark extends RRBVector_q_64_Iteration_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_IterationString_Benchmark extends RRBVector_q_64_Iteration_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_64_MemoryAllocation_Benchmark[A] extends MemoryAllocation[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_IntMemoryAllocation_Benchmark extends RRBVector_q_64_MemoryAllocation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_StringMemoryAllocation_Benchmark extends RRBVector_q_64_MemoryAllocation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_64_Split_Benchmark[A] extends SplitBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_SplitInt_Benchmark extends RRBVector_q_64_Split_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_SplitString_Benchmark extends RRBVector_q_64_Split_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_64_ParMap_Benchmark[A] extends ParMapBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_IntParMap_Benchmark extends RRBVector_q_64_ParMap_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_StringParMap_Benchmark extends RRBVector_q_64_ParMap_Benchmark[String] with VectorGeneratorType.StringGenerator
            }

            package xunbalanced {
              import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._

              import scala.collection.immutable.vectorutils.VectorGeneratorType

              import scala.collection.immutable.vectorutils.generated.rrbvector.quick.block64._

              import scala.collection.immutable.generated.rrbvector.quick.block64._

              trait RRBVector_q_64_Benchmark[A] extends BaseVectorBenchmark[A] with RRBVectorGenerator_q_64[A] {
                override def generateVectors(from: Int, to: Int, by: Int, sizesName: String): org.scalameter.Gen[RRBVector_q_64[A]] = sizes(from, to, by, sizesName).map(((size) => randomVectorOfSize(size)(defaultVectorConfig(111))));
                def generateVectors2(from: Int, to: Int, by: Int): org.scalameter.Gen[scala.Tuple2[RRBVector_q_64[A], RRBVector_q_64[A]]] = sizes(from, to, by, "size1").flatMap(((size1) => sizes(from, to, by, "size2").map(((size2) => scala.Tuple2(randomVectorOfSize(size1)(defaultVectorConfig(111)), randomVectorOfSize(size2)(defaultVectorConfig(111)))))));
                override def vectorName: String = super.vectorName.+("XUnbalanced")
              }

              abstract class RRBVector_q_64_Append_Benchmark[A] extends AppendBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_AppendInt_Benchmark extends RRBVector_q_64_Append_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def append(vec: RRBVector_q_64[Int], n: Int): Int = {
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

              class RRBVector_q_64_AppendString_Benchmark extends RRBVector_q_64_Append_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def append(vec: RRBVector_q_64[String], n: Int): Int = {
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

              abstract class RRBVector_q_64_Prepend_Benchmark[A] extends PrependBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_PrependInt_Benchmark extends RRBVector_q_64_Prepend_Benchmark[Int] with VectorGeneratorType.IntGenerator {
                def prepend(vec: RRBVector_q_64[Int], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = {
                            val x$67 = 0;
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

              class RRBVector_q_64_PrependString_Benchmark extends RRBVector_q_64_Prepend_Benchmark[String] with VectorGeneratorType.StringGenerator {
                val ref = "";
                def prepend(vec: RRBVector_q_64[String], n: Int, times: Int): Int = {
                  var i = 0;
                  var sum = 0;
                  while (i.<(times)) 
                    {
                      var v = vec;
                      var j = 0;
                      while (j.<(n)) 
                        {
                          v = {
                            val x$68 = ref;
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

              abstract class RRBVector_q_64_Apply_Benchmark[A] extends ApplyBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_ApplyInt_Benchmark extends RRBVector_q_64_Apply_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_ApplyString_Benchmark extends RRBVector_q_64_Apply_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_64_Builder_Benchmark[A] extends BuilderBenchmarks[A] with RRBVector_q_64_Benchmark[A] {
                def buildVector(n: Int): Int = {
                  var i = 0;
                  var b = RRBVector_q_64.newBuilder[A];
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

              class RRBVector_q_64_BuilderInt_Benchmark extends RRBVector_q_64_Builder_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_BuilderString_Benchmark extends RRBVector_q_64_Builder_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_64_Concatenation_Benchmark[A] extends ConcatenationBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_ConcatenationInt_Benchmark extends RRBVector_q_64_Concatenation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_ConcatenationString_Benchmark extends RRBVector_q_64_Concatenation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_64_Iteration_Benchmark[A] extends IterationBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_IterationInt_Benchmark extends RRBVector_q_64_Iteration_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_IterationString_Benchmark extends RRBVector_q_64_Iteration_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_64_MemoryAllocation_Benchmark[A] extends MemoryAllocation[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_IntMemoryAllocation_Benchmark extends RRBVector_q_64_MemoryAllocation_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_StringMemoryAllocation_Benchmark extends RRBVector_q_64_MemoryAllocation_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_64_Split_Benchmark[A] extends SplitBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_SplitInt_Benchmark extends RRBVector_q_64_Split_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_SplitString_Benchmark extends RRBVector_q_64_Split_Benchmark[String] with VectorGeneratorType.StringGenerator

              abstract class RRBVector_q_64_ParMap_Benchmark[A] extends ParMapBenchmarks[A] with RRBVector_q_64_Benchmark[A]

              class RRBVector_q_64_IntParMap_Benchmark extends RRBVector_q_64_ParMap_Benchmark[Int] with VectorGeneratorType.IntGenerator

              class RRBVector_q_64_StringParMap_Benchmark extends RRBVector_q_64_ParMap_Benchmark[String] with VectorGeneratorType.StringGenerator
            }
          }
        }
      }
    }
  }
}