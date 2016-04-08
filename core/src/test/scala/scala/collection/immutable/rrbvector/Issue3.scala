package scala.collection.immutable.rrbvector

import org.scalatest._

class Issue3 extends FlatSpec with Matchers {
    "Code in issue #3" should "not throw" in {
        RRBVector.fill(/*32^5 + 1*/ 33554433)(0).apply(1024) // NPE
        RRBVector.fill(/*32^5 + 1*/33554433)(0).foreach(_ => ())
    }

    "Code in issue #3" should " should be correctly filled" in {
        val vector = RRBVector.fill(/*32^5 + 1*/ 33554433)(0)
        for (i <- 0 until 33554433)
            assert(vector(i) == 0, "failed at index " + i)
        println()
    }
}
