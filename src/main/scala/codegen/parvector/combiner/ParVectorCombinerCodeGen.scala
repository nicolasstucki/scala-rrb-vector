package codegen
package parvector
package combiner

import iterator.ParVectorIteratorClassGen
import vector.vectorpointer.VectorPointerCodeGen

import scala.reflect.runtime.universe._

trait ParVectorCombinerCodeGen {
    self: VectorProperties with VectorPointerCodeGen with ParVectorIteratorClassGen =>

    // Field names

    val comb_builder = TermName("builder")

    // Method definitions

    protected def plusEqCode(elem: TermName) = {
        q"""
            $comb_builder += $elem
            this
         """
    }

    protected def plusPlusCode(xs: TermName) = {
        q"""
            $comb_builder ++= $xs
            this
         """
    }

    protected def combineCode(other: TermName) = {
        q"""
            if ($other eq this) this
            else {
                val that = $other.asInstanceOf[$parVectorCombinerClassName[$B]]
                $comb_builder ++= that.$comb_builder.result()
                this
            }
         """
    }

}
