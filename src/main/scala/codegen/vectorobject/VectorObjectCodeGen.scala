package codegen.vectorobject

import codegen.vectorpointer.VectorPointerCodeGen
import codegen.VectorProperties

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorObjectCodeGen {
    self: VectorProperties with VectorPointerCodeGen =>

    // Methods names
    val o_empty = TermName("empty")

}
