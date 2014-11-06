package codegen.parvector.iterator

import codegen.vector.iterator.VectorIteratorCodeGen
import codegen.{PAR_SPLIT_METHOD, VectorProperties}
import codegen.vector.vectorpointer.VectorPointerCodeGen

import scala.reflect.runtime.universe._

trait ParVectorIteratorCodeGen {
    self: VectorProperties with VectorIteratorCodeGen with VectorPointerCodeGen =>

    // Field names

    val pit_start = TermName("_start")
    val pit_end = TermName("_end")

    val pit_remaining = TermName("remaining")
    val pit_dup = TermName("dup")
    val pit_split = TermName("split")
    val pit_psplit = TermName("psplit")

    // Method definitions

    protected def dupCode() = {
        q"""
            val pit = new $parVectorIteratorClassName($pit_end - $pit_remaining, $pit_end)
            pit.$it_initIteratorFrom(this)
            pit
         """
    }

    protected def splitCode(): Tree = {
        PAR_SPLIT match {
            case PAR_SPLIT_METHOD.SPLIT_IN_HALF =>
                q"""
                    val rem = $pit_remaining
                    if (rem >= 2) $pit_psplit(rem / 2, rem - rem / 2)
                    else Seq(this)
                """
            case PAR_SPLIT_METHOD.SPLIT_IN_COMPLETE_SUBTREES =>
                q"""
                    val rem = $pit_remaining
                    if (rem >= 2) {
                        val splitSize = 1 << (5*(${getIndexLevel(q"rem")}-1))
                        val splitted = new ArrayBuffer[$parVectorIteratorClassName]
                        var currentPos = $pit_end - $pit_remaining
                        while (currentPos < rem) {
                            val pit = new $parVectorIteratorClassName(currentPos, math.min(currentPos + splitSize, $pit_end))
                            pit.$it_initIteratorFrom(this)
                            splitted += pit
                            currentPos += splitSize
                        }
                        splitted
                    }
                    else Seq(this)
                 """
            case PAR_SPLIT_METHOD.BLOCK_SPLIT =>
                ??? // TODO: split into each natural subtree of the root
        }
    }

    protected def splitterCode(sizes: TermName) = {
        q"""
            val splitted = new ArrayBuffer[$parVectorIteratorClassName]
            var currentPos = $pit_end - $pit_remaining
            for (sz <- $sizes) {
                val pit = new $parVectorIteratorClassName(currentPos, currentPos + sz)
                pit.$it_initIteratorFrom(this)
                splitted += pit
                currentPos += sz
            }
            splitted
         """
    }

}
