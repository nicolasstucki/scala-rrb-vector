package scala.collection.immutable.vectortests

import scala.collection.immutable.rrbvector.RRBVector
import scala.collection.immutable.vectorutils.VectorGeneratorType
import scala.collection.immutable.vectorutils.generated.rrbvector._

/**
* Created by nicolasstucki on 13/10/2014.
*/
object QuickTest extends App {
//    val i = 35065
//    val j = 1

//    val generator = new RRBVector_complete_ifElseDepth_directLevel_32_splitbalancedGenerator[Int] with VectorGeneratorType.IntGenerator
//    generator.randomVectorOfSize(i)(generator.defaultVectorConfig(i + j))

    //    val v = RRBVector_complete_directLevel_32(1)
    //    val v2 = 0 +: v
    //    println(v2)

//        val n = 1
//        val m = 65
//        val a = RRBVector.range(0, n)
//        val b = RRBVector.range(n, n + m)
//     var c = a ++ b
//    println(c)

    //    val d = a :+ 99
    //    println(d)
    val b = RRBVector.newBuilder[Int]
    var i = 0
    while(i<65) {
        b+=i
        i+=1
    }
    val vec = b.result()
    println(vec)
}