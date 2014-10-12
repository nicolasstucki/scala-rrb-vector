package codegen.vectorobject

import codegen.vectorpointer.VectorPointerCodeGen
import codegen.VectorProperties

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorObjectCodeGen {
    self: VectorProperties with VectorPointerCodeGen =>

    // Methods names
    val o_singleton = TermName("singleton")
    val o_empty = TermName("empty")

    def singletonCode(value: TermName, tp: TypeName) = {
        q"""
            val vec = new $vectorClassName[$tp](1)
            vec.$display0 = new Array[AnyRef](${if (CLOSED_BLOCKS) 1 else blockWidth})
            vec.$display0(0) = $value.asInstanceOf[AnyRef]
            vec.$depth = 1
            vec.$focusEnd = 1
            vec.$focusDepth = 1
            vec
        """
    }

}
