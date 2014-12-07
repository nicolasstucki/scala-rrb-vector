package codegen.parvector.iterator

import codegen.VectorProperties
import codegen.vector.iterator.VectorIteratorCodeGen
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
        def splitModulo(lvl: Int): Tree = {
            q"""
                if (rem <= ${1 << (blockIndexBits * lvl)}) ${1 << (blockIndexBits * (lvl - 1))}
                else ${if (lvl < maxTreeLevel) splitModulo(lvl + 1) else q"${1 << (blockIndexBits * lvl)}"}
             """
        }
        q"""
            val rem = $pit_remaining
            if (rem >= 2) {
                val _half = rem / 2
                val _splitModulo = ${splitModulo(1)}
                val _halfAdjusted = if (_half > _splitModulo) _half - _half % _splitModulo else if (_splitModulo < _end) _splitModulo else _half
                psplit(_halfAdjusted, rem - _halfAdjusted)
            }
            else Seq(this)
         """
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
