package codegen
package vector
package vectorobject

import vectorpointer.VectorPointerCodeGen

import scala.reflect.runtime.universe._

trait VectorObjectCodeGen {
    self: VectorProperties with VectorPointerCodeGen =>

    // Methods names
    val o_empty = TermName("empty")
    val o_emptyTransientBlock = TermName("emptyTransientBlock")

}
