package scala.collection.immutable.rrbvector

import org.scalatest._

class Issue2 extends FlatSpec with Matchers {
    "Code in issue #2" should "not throw a NPE" in {
        import scala.util.Random

        object Mutate {
            def apply(rnd : Random, xs : IndexedSeq[Boolean] ): IndexedSeq[Boolean] = {
                val point = rnd.nextInt(xs.length)
                xs.updated(point, !xs(point))
            }

            def apply2(rnd : Random, xs : IndexedSeq[Boolean] ): IndexedSeq[Boolean] = {
                val point = rnd.nextInt(xs.length)
                val (ys,zs) = xs.splitAt(point)
                ys ++ (!zs.head +: zs.tail)
            }
        }

        val iterations = 100000
        val top_length = 4096
        val repeats = 1

        def test(descr : String, init : (Random,Int) => IndexedSeq[Boolean]) {
            var len = 16
            val rnd = new Random(0)
            do {
                val indi = init(rnd,len)

                var times = 0.0
                for(i <- 0 until repeats) {
                    val initialTime = System.nanoTime()
                    for (i <- 1 until iterations) {
                        val newIndi = Mutate.apply2(rnd,indi)
                    }
                    val finalTime = System.nanoTime()
                    times += finalTime - initialTime
                }
                val avg = times/repeats
                println( descr+", "+len+", "+avg/1e9 )
                len = len * 2
            } while ( len <= top_length )
        }

        test("Scala-Vector[Boolean]", (rnd, len) => Vector.fill(len)(rnd.nextDouble > 0.5))

        test("Scala-RRBVector[Boolean]", (rnd,len) => RRBVector.fill(len)(rnd.nextDouble > 0.5))
    }
}
