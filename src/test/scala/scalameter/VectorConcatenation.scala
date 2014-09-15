package scalameter

import org.scalameter.api._
import org.scalameter.{Measurer, Aggregator, Warmer, execution}


abstract class VectorConcatenation extends PerformanceTest.OfflineReport {
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
                        using(rrbprototype(sizes)) curve "rrb prototype" in { v => v ++ v}
                    }
                }

            }
        }
    }

}

object IntVectorConcatenation extends VectorConcatenation with IntVectorBenchmark

object StringVectorConcatenation extends VectorConcatenation with StringVectorBenchmark
