package codegen
package parvector
package combiner

import codegen.vector.vectorpointer.VectorPointerCodeGen

import scala.reflect.runtime.universe._

trait ParVectorCombinerMethodsGen {
    self: ParVectorCombinerCodeGen with VectorPointerCodeGen with VectorProperties =>

    def generateParVectorCombinerMethods() = {
        val fields = Seq(
            q"val builder: $vectorBuilderClassName[$A] = new $vectorBuilderClassName[$A]"
        )
        val methods = Seq(
            sizeDef,
            resultDef,
            clearDef,
            plusEqDef,
            plusPlusDef,
            combineDef)

        fields ++ methods
    }

    private def sizeDef =
        q"override def size = $comb_builder.$endIndex"

    private def resultDef =
        q"override def result() = new $parVectorClassName[$A]($comb_builder.result())"

    private def clearDef =
        q"override def clear() = $comb_builder.clear()"

    private def plusEqDef = {
        val elem = TermName("elem")
        val code = plusEqCode(elem)
        q"override def +=($elem: $A) = $code"
}

    private def plusPlusDef = {
        val xs = TermName("xs")
        val code = plusPlusCode(xs)
        q"override def ++=($xs: TraversableOnce[$A]) = $code"
    }

    private def combineDef = {
        val other = TermName("other")
        val code = combineCode(other)
        q"def combine[$B <: $A, NewTo >: $parVectorClassName[$A]]($other: Combiner[$B, NewTo]) = $code"
    }
}

