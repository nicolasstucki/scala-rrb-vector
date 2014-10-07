package codegen.vectorobject


import codegen.VectorPackage
import codegen.vectorpointer.VectorPointerCode

import scala.reflect.runtime.universe._

trait VectorObjectCode {
    self: VectorPackage with VectorPointerCode =>

    val o_singleton = TermName("singleton")
    val o_empty = TermName("empty")

    def singletonCode(value: TermName, tp: TypeName) = {
        q"""
            val vec = new $vectorClassName[$tp](1)
            vec.$display0 = new Array[AnyRef]($treeBranchWidth)
            vec.$display0(0) = $value.asInstanceOf[AnyRef]
            vec.$depth = 1
            vec.$focusEnd = 1
            vec.$focusDepth = 1
            vec.$hasWritableTail = true
            vec
        """
    }


}
