package codegen
package parvector
package iterator

import vector.iterator.VectorIteratorCodeGen

import codegen.VectorProperties

import scala.reflect.runtime.universe._

trait ParVectorIteratorMethodsGen {
    self: ParVectorIteratorCodeGen with VectorProperties with VectorIteratorCodeGen =>

    def generateParVectorIteratorMethods() = {
        val methods = Seq(
            remainingDef,
            dupDef,
            splitDef,
            psplitDef)
        methods
    }


    private def remainingDef =
        q"override final def $it_remaining: Int = super.$it_remaining"

    private def dupDef = {
        val code = dupCode()
        q"def $pit_dup: SeqSplitter[$A] = $code"
    }

    private def splitDef = {
        val code = splitCode()
        q"def $pit_split: Seq[$parVectorIteratorClassName] = $code"
    }

    private def psplitDef = {
        val sizes = TermName("sizes")
        val code = splitterCode(sizes)
        q"def $pit_psplit($sizes: Int*): Seq[$parVectorIteratorClassName] = $code"
    }

}

