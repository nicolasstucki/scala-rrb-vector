package scalameter

import org.scalameter.api._
import org.scalameter.{Measurer, Aggregator, Warmer, execution}

/**
 * Created by nicolasstucki on 15/09/2014.
 */
abstract class VectorIterationBenchmark extends PerformanceTest.OfflineReport {
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
                ("1 level", Gen.range("size")(1, 32, 2))
                , ("2 levels", Gen.range("size")(32, 1024, 32 * 2))
                , ("3 levels", Gen.range("size")(1024 + 1, 1024 * 32, 1024 * 2))
                , ("4 levels", Gen.range("size")(1024 * 32 + 1, 1024 * 1024, 1024 * 32 * 2))
                //                , ("5 levels", Gen.range("size")(1024 * 1024 + 1, 1024 * 1024 * 32, 1024 * 1024 * 2))
            )
        } {
            performance of "iteration" in {

                performance of "iterate with iterator" in {
                    performance of rangeName in {
                        using(vectors(sizes)) curve "Vector" in { v => val it = v.iterator; while (it.hasNext) it.next()}
                        using(rrbvector(sizes)) curve "rrb Vector" in { v => val it = v.iterator; while (it.hasNext) it.next()}
                    }
                }

                performance of "iterate by index" in {
                    performance of rangeName in {
                        using(vectors(sizes)) curve "Vector" in { v =>
                            var i = 0
                            val len = v.length
                            while (i < len) {
                                v(i)
                                i += 1
                            }
                        }
                        using(rrbvector(sizes)) curve "rrb Vector" in { v =>
                            var i = 0
                            val len = v.length
                            while (i < len) {
                                v(i)
                                i += 1
                            }
                        }
                    }
                }

                performance of "iterate with reverseIterator" in {
                    performance of rangeName in {
                        using(vectors(sizes)) curve "Vector" in { v => val it = v.reverseIterator; while (it.hasNext) it.next()}
                        using(rrbvector(sizes)) curve "rrb Vector" in { v => val it = v.iterator; while (it.hasNext) it.next()}
                    }
                }

                performance of "reverse iterate by index" in {
                    performance of rangeName in {
                        using(vectors(sizes)) curve "Vector" in { v =>
                            var i = v.length
                            while (i > 0) {
                                i -= 1
                                v(i)
                            }
                        }
                        using(rrbvector(sizes)) curve "rrb Vector" in { v =>
                            var i = v.length
                            while (i > 0) {
                                i -= 1
                                v(i)
                            }
                        }
                    }
                }

            }
        }
    }

}

object IntVectorIterationBenchmark extends VectorIterationBenchmark with IntVectorBenchmark

object StringVectorIterationBenchmark extends VectorIterationBenchmark with StringVectorBenchmark
