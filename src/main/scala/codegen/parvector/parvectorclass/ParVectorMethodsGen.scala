package codegen
package parvector
package parvectorclass

import iterator.ParVectorIteratorClassGen

import scala.reflect.runtime.universe._

trait ParVectorMethodsGen {
    self: ParVectorCodeGen with ParVectorIteratorClassGen with VectorProperties =>

    def generateParVectorMethods() = {
        val methods = Seq(
            companionDef,
            thisDef,
            applyDef,
            lengthDef,
            splitterDef,
            seqDef,
            toVectorDef,
            generateParVectorIteratorClassDef())
        methods
    }


    private def companionDef =
        q"override def companion = $parVectorObjectTermName"

    private def thisDef =
        q"def this() = this($vectorObjectName.empty[$A])"

    private def applyDef =
        q"def apply(idx: Int) = $par_vector.apply(idx)"

    private def lengthDef = q"def length = $par_vector.length"

    private def splitterDef = {
        val code = splitterCode()
        q"def splitter: SeqSplitter[$A] = $code"
    }

    private def seqDef =
        q"override def seq: $vectorClassName[$A] = $par_vector"


    private def toVectorDef =
        q"override def toVector: Vector[$A] = $par_vector.toVector"

}

