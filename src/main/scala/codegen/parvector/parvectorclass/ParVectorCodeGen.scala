package codegen
package parvector
package parvectorclass

import iterator.ParVectorIteratorClassGen
import vector.vectorpointer.VectorPointerCodeGen

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait ParVectorCodeGen {
    self: VectorProperties with VectorPointerCodeGen with ParVectorIteratorClassGen =>

    // Field names

    val par_vector = TermName("vector")

    // Method definitions

    protected def splitterCode() = {
        q"""
            val pit = new $parVectorIteratorClassName(0, $par_vector.length)
            pit.initIteratorFrom($par_vector)
            pit
         """
    }

}
