package scalameter

import org.scalameter.api._
import org.scalameter.{Measurer, Aggregator, Warmer, execution}

import scala.collection.immutable

abstract class VectorConcatenationBenchmark extends PerformanceTest.OfflineReport {
    self: VectorBenchmark =>

//    override def executor = new execution.LocalExecutor(
//        Warmer.Default(),
//        Aggregator.median,
//        measurer
//    )
//
//    override def measurer = new Measurer.Default

    performance of s"/temp/$typedName" in {

        for {
            (rangeName, sizes) <- Seq(
                //                , ("5 levels", Gen.range("size")(1024 * 1024 + 1, 1024 * 1024 * 32, 1024 * 1024 * 2))
                ("4 levels", Gen.range("size")(1024 * 32 + 1, 1024 * 1024, 1024 * 32))
                , ("3 levels", Gen.range("size")(1024 + 1, 1024 * 32, 1024))
                , ("2 levels", Gen.range("size")(32 + 1, 1024, 32))
                , ("1 level", Gen.range("size")(1, 32, 1))
            )
        } {

            performance of "concatenation" in {

                performance of "concat v ++ v" in {
                    performance of rangeName in {
                        using(vectors(sizes)) curve "Vector" in { v => v ++ v}
                        using(rrbvector(sizes)) curve "rrb Vector" in { v => v ++ v}
                    }
                }

            }

            performance of "append" in {

                performance of "append v :+ e" in {
                    performance of rangeName in {
                        val e = element(-1)
                        using(vectors(sizes)) curve "Vector" in { v => v :+ e}
                        using(rrbvector(sizes)) curve "rrb Vector" in { v => v :+ e}
                    }
                }

                performance of "append n elements" in {
                    performance of rangeName in {
                        using(sizes) curve "Vector" in { n =>
                            var v = Vector.empty[A]
                            var i = 0
                            val e = element(0)
                            while (i < n) {
                                v = v :+ e
                                i += 1
                            }
                            v
                        }
                        using(sizes) curve "rrb Vector" in { n =>
                            var v = immutable.rrbvector.Vector.empty[A]
                            var i = 0
                            val e = element(0)
                            while (i < n) {
                                v = v :+ e
                                i += 1
                            }
                            v
                        }
                    }
                }

                performance of "prepend e +: v" in {
                    performance of rangeName in {
                        val e = element(-1)
                        using(vectors(sizes)) curve "Vector" in { v => v :+ e}
                        using(rrbvector(sizes)) curve "rrb Vector" in { v => v :+ e}
                    }
                }
            }
        }
    }

}

object IntVectorConcatenationBenchmark extends VectorConcatenationBenchmark with IntVectorBenchmark

object StringVectorConcatenationBenchmark extends VectorConcatenationBenchmark with StringVectorBenchmark
