package codegen
package test

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
                    class ${vectorBenchmarkClassName("AppendString")} extends ${vectorBenchmarkClassName("Append")}[String] with VectorGeneratorType.StringGenerator {
                        val ref = ""
                        def sum1(vec: $vectorClassName[String], times: Int): Int = {
                            var i = 0
                            var v = vec
                            var sum = 0
                            while (i < times) {
                                v = vec :+ ref
                                sum += v.length
                                i += 1
                            }
                            sum
                        }
                        def sum8(vec: $vectorClassName[String], times: Int): Int = {
                            var i = 0
                            var v = vec
                            var sum = 0
                            while (i < times) {
                                v = vec :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref
                                sum += v.length
                                i += 1
                            }
                            sum
                        }
                        def sum32(vec: $vectorClassName[String], times: Int): Int = {
                            var i = 0
                            var v = vec
                            var sum = 0
                            while (i < times) {
                                v = vec :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref :+ ref
                                sum += v.length
                                i += 1
                            }
                            sum
                        }
                    }

                    abstract class ${vectorBenchmarkClassName("Apply")}[A] extends ApplyBenchmarks[A] with $vectorBaseBenchmarkClassName[A]
                    class ${vectorBenchmarkClassName("ApplyInt")} extends ${vectorBenchmarkClassName("Apply")}[Int] with VectorGeneratorType.IntGenerator
                    class ${vectorBenchmarkClassName("ApplyString")} extends ${vectorBenchmarkClassName("Apply")}[String] with VectorGeneratorType.StringGenerator

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
                    class ${vectorBenchmarkClassName("BuilderString")} extends ${vectorBenchmarkClassName("Builder")}[String] with VectorGeneratorType.StringGenerator


                    abstract class ${vectorBenchmarkClassName("Concatenation")}[A] extends ConcatenationBenchmarks[A] with $vectorBaseBenchmarkClassName[A]
                    class ${vectorBenchmarkClassName("ConcatenationInt")} extends ${vectorBenchmarkClassName("Concatenation")}[Int] with VectorGeneratorType.IntGenerator
                    class ${vectorBenchmarkClassName("ConcatenationString")} extends ${vectorBenchmarkClassName("Concatenation")}[String] with VectorGeneratorType.StringGenerator


                    abstract class ${vectorBenchmarkClassName("Iteration")}[A] extends IterationBenchmarks[A] with $vectorBaseBenchmarkClassName[A]
                    class ${vectorBenchmarkClassName("IterationInt")} extends ${vectorBenchmarkClassName("Iteration")}[Int] with VectorGeneratorType.IntGenerator
                    class ${vectorBenchmarkClassName("IterationString")} extends ${vectorBenchmarkClassName("Iteration")}[String] with VectorGeneratorType.StringGenerator

                    abstract class ${vectorBenchmarkClassName("MemoryAllocation")}[A] extends MemoryAllocation[A] with $vectorBaseBenchmarkClassName[A]
                    class ${vectorBenchmarkClassName("IntMemoryAllocation")} extends ${vectorBenchmarkClassName("MemoryAllocation")}[Int] with VectorGeneratorType.IntGenerator
                    class ${vectorBenchmarkClassName("StringMemoryAllocation")} extends ${vectorBenchmarkClassName("MemoryAllocation")}[String] with VectorGeneratorType.StringGenerator

                    abstract class ${vectorBenchmarkClassName("Split")}[A] extends SplitBenchmarks[A] with $vectorBaseBenchmarkClassName[A]
                    class ${vectorBenchmarkClassName("SplitInt")} extends ${vectorBenchmarkClassName("Split")}[Int] with VectorGeneratorType.IntGenerator
                    class ${vectorBenchmarkClassName("SplitString")} extends ${vectorBenchmarkClassName("Split")}[String] with VectorGeneratorType.StringGenerator
                }
             """
        }

        inPackages(
            s"scala.collection.immutable.vectorbenchmarks.generated".split('.'),
            q"""
                package $subpackage {
                    ${testSet(q"tabulatedVector(size)", TermName("balanced"), "Balanced")}
                    ${testSet(q"randomVectorOfSize(size)(defaultVectorConfig(111))", TermName("xunbalanced"), "XUnbalanced")}
                }
             """)

    }
}
