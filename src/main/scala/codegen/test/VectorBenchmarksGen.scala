package codegen.test

import codegen.VectorProperties

import scala.reflect.runtime.universe._

trait VectorBenchmarksGen {
    self: VectorProperties =>

    def generateVectorBenchmarkClasses() = {
        def testSet(vecGen: Tree, pack: TermName, variant: String) ={
            q"""
                package $pack {
                    import scala.collection.immutable.vectorbenchmarks.genericbenchmarks._
                    import scala.collection.immutable.vectorutils.VectorGeneratorType
                    import scala.collection.immutable.vectorutils.generated.$subpackage._
                    import scala.collection.immutable.generated.$subpackage._

                    trait $vectorBaseBenchmarkClassName[A] extends BaseVectorBenchmark[A] with $vectorGeneratorClassName[A] {
                        override def generateVectors(from: Int, to: Int, by: Int): org.scalameter.Gen[$vectorClassName[A]] = for {
                            size <- sizes(from, to, by)
                        } yield $vecGen
                        override def vectorName: String = super.vectorName + $variant
                    }

                    abstract class ${vectorBenchmarkClassName("Append")}[A] extends AppendBenchmarks[A] with $vectorBaseBenchmarkClassName[A]
                    class ${vectorBenchmarkClassName("AppendInt")} extends ${vectorBenchmarkClassName("Append")}[Int] with VectorGeneratorType.IntGenerator {

                        def sum1(vec: $vectorClassName[Int], times: Int): Int = {
                            var i = 0
                            var v = vec
                            var sum = 0
                            while (i < times) {
                                v = vec :+ 0
                                sum += v.length
                                i += 1
                            }
                            sum
                        }

                        def sum8(vec: $vectorClassName[Int], times: Int): Int = {
                            var i = 0
                            var v = vec
                            var sum = 0
                            while (i < times) {
                                v = vec :+ 0 :+ 0 :+ 0 :+ 0 :+ 0 :+ 0 :+ 0 :+ 0
                                sum += v.length
                                i += 1
                            }
                            sum
                        }

                        def sum32(vec: $vectorClassName[Int], times: Int): Int = {
                            var i = 0
                            var v = vec
                            var sum = 0
                            while (i < times) {
                                v = vec :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7 :+ 0 :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7
                                sum += v.length
                                i += 1
                            }
                            sum
                        }
                    }
                    class ${vectorBenchmarkClassName("AppendAnyRef")} extends ${vectorBenchmarkClassName("Append")}[AnyRef] with VectorGeneratorType.AnyRefGenerator {
                        val obj = new Object
                        def sum1(vec: $vectorClassName[AnyRef], times: Int): Int = {
                            var i = 0
                            var v = vec
                            var sum = 0
                            while (i < times) {
                                v = vec :+ obj
                                sum += v.length
                                i += 1
                            }
                            sum
                        }
                        def sum8(vec: $vectorClassName[AnyRef], times: Int): Int = {
                            var i = 0
                            var v = vec
                            var sum = 0
                            while (i < times) {
                                v = vec :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj
                                sum += v.length
                                i += 1
                            }
                            sum
                        }
                        def sum32(vec: $vectorClassName[AnyRef], times: Int): Int = {
                            var i = 0
                            var v = vec
                            var sum = 0
                            while (i < times) {
                                v = vec :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj :+ obj
                                sum += v.length
                                i += 1
                            }
                            sum
                        }
                    }

                    abstract class ${vectorBenchmarkClassName("Apply")}[A] extends ApplyBenchmarks[A] with $vectorBaseBenchmarkClassName[A]
                    class ${vectorBenchmarkClassName("ApplyInt")} extends ${vectorBenchmarkClassName("Apply")}[Int] with VectorGeneratorType.IntGenerator
                    class ${vectorBenchmarkClassName("ApplyAnyRef")} extends ${vectorBenchmarkClassName("Apply")}[AnyRef] with VectorGeneratorType.AnyRefGenerator

                    abstract class ${vectorBenchmarkClassName("Builder")}[A] extends BuilderBenchmarks[A] with $vectorBaseBenchmarkClassName[A] {
                        def buildVector(n: Int, elems: Int): Int = {
                            var i = 0
                            var sum = 0
                            var b = $vectorObjectName.newBuilder[A]
                            val e = element(0)
                            while (i < elems) {
                                val m = math.min(n, elems - i)
                                var j = 0
                                while (j < m) {
                                    b += e
                                    i += 1
                                    j += 1
                                }
                                sum = b.result().length
                                b.clear()
                            }
                            sum
                        }
                    }
                    class ${vectorBenchmarkClassName("BuilderInt")} extends ${vectorBenchmarkClassName("Builder")}[Int] with VectorGeneratorType.IntGenerator
                    class ${vectorBenchmarkClassName("BuilderAnyRef")} extends ${vectorBenchmarkClassName("Builder")}[AnyRef] with VectorGeneratorType.AnyRefGenerator


                    abstract class ${vectorBenchmarkClassName("Concatenation")}[A] extends ConcatenationBenchmarks[A] with $vectorBaseBenchmarkClassName[A]
                    class ${vectorBenchmarkClassName("ConcatenationInt")} extends ${vectorBenchmarkClassName("Concatenation")}[Int] with VectorGeneratorType.IntGenerator
                    class ${vectorBenchmarkClassName("ConcatenationAnyRef")} extends ${vectorBenchmarkClassName("Concatenation")}[AnyRef] with VectorGeneratorType.AnyRefGenerator


                    abstract class ${vectorBenchmarkClassName("Iteration")}[A] extends IterationBenchmarks[A] with $vectorBaseBenchmarkClassName[A]
                    class ${vectorBenchmarkClassName("IterationInt")} extends ${vectorBenchmarkClassName("Iteration")}[Int] with VectorGeneratorType.IntGenerator
                    class ${vectorBenchmarkClassName("IterationAnyRef")} extends ${vectorBenchmarkClassName("Iteration")}[AnyRef] with VectorGeneratorType.AnyRefGenerator

                    abstract class ${vectorBenchmarkClassName("MemoryAllocation")}[A] extends MemoryAllocation[A] with $vectorBaseBenchmarkClassName[A]
                    class ${vectorBenchmarkClassName("IntMemoryAllocation")} extends ${vectorBenchmarkClassName("MemoryAllocation")}[Int] with VectorGeneratorType.IntGenerator
                    class ${vectorBenchmarkClassName("AnyRefMemoryAllocation")} extends ${vectorBenchmarkClassName("MemoryAllocation")}[AnyRef] with VectorGeneratorType.AnyRefGenerator

                    abstract class ${vectorBenchmarkClassName("Split")}[A] extends SplitBenchmarks[A] with $vectorBaseBenchmarkClassName[A]
                    class ${vectorBenchmarkClassName("SplitInt")} extends ${vectorBenchmarkClassName("Split")}[Int] with VectorGeneratorType.IntGenerator
                    class ${vectorBenchmarkClassName("SplitAnyRef")} extends ${vectorBenchmarkClassName("Split")}[AnyRef] with VectorGeneratorType.AnyRefGenerator
                }
             """
        }

        inPackages(
            s"scala.collection.immutable.vectorbenchmarks.generated".split('.'),
            q"""
                package $subpackage {
                    ${testSet(q"tabulatedVector(size)", TermName("balanced"), "Balanced")}
                    ${testSet(q"randomVectorOfSize(size)(defaultVectorConfig())", TermName("xunbalanced"), "XUnbalanced")}
                }
             """)

    }
}
