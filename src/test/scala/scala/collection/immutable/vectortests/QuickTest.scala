package scala.collection.immutable.vectortests

import scala.collection.immutable.rrbvector.{RRBVectorIterator, RRBVector}
import scala.collection.immutable.vectorutils.{BaseVectorGenerator, VectorGeneratorType}
import scala.collection.immutable.vectorutils.generated.rrbvector._


object QuickTest extends App {

        val n = 32769
        val seed = 111
        val vector = (new BaseVectorGenerator.RRBVectorGenerator[Int] with VectorGeneratorType.IntGenerator).randomVectorOfSize(n)(BaseVectorGenerator.defaultVectorConfig(seed))
        println(vector)

}