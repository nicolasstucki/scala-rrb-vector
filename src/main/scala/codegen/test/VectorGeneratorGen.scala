package codegen.test

import codegen.VectorProperties

import scala.reflect.runtime.universe._

trait VectorGeneratorGen {
    self: VectorProperties =>

    def generateVectorGeneratorClass() = {
        inPackages(
            s"scala.collection.immutable.vectorutils.generated.$subpackage".split("\\."),
            q"""
                trait $vectorGeneratorClassName[$A] extends BaseVectorGenerator[$A] {
                    import scala.collection.immutable.generated.$subpackage._

                    override type Vec = ${vectorClassName}[$A]

                    final def vectorClassName: String = $vectorName

                    override final def tabulatedVector(n: Int): Vec = $vectorObjectName.tabulate(n)(element)

                    override final def rangedVector(start: Int, end: Int): Vec = $vectorObjectName.range(start, end) map element

                    override final def emptyVector: Vec = $vectorObjectName.empty[A]

                    override final def plus(vec: Vec, elem: A): Vec = vec :+ elem

                    override final def plus(elem: A, vec: Vec): Vec = vec.+:(elem)

                    override final def plusPlus(vec1: Vec, vec2: Vec): Vec = vec1 ++ vec2

                    override final def take(vec: Vec, n: Int): Vec = vec.take(n)

                    override final def drop(vec: Vec, n: Int): Vec = vec.drop(n)
                }
            """)
    }


}
