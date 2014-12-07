package scala.collection.immutable.vectortests

import scala.collection.immutable.rrbvector.{RRBVectorIterator, RRBVector}
import scala.collection.immutable.vectorutils.{BaseVectorGenerator, VectorGeneratorType}
//import scala.collection.immutable.vectorutils.generated.rrbvector._


object QuickTest extends App {

//    val n = 32768
//    val seed = 111
//
//    def f(x: Int) = -x
//
//    val vector = (new BaseVectorGenerator.RRBVectorGenerator[Int] with VectorGeneratorType.IntGenerator).randomVectorOfSize(n)(BaseVectorGenerator.defaultVectorConfig(seed))
//    val parVec = vector.par map f
//
//    println(vector)
//    println(parVec)

    val vec = RRBVector.range(0, 32767)
    println(vec)

}