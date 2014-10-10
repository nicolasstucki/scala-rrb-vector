package codegen
package vectorpointer

import scala.reflect.runtime.universe._

trait VectorPointerClassGen extends ClassGen {
    self: MethodsGen with VectorPointerCodeGen with VectorProperties =>

    def generateClassDef(): Tree = {
        q"""
            import scala.annotation.tailrec
            import scala.compat.Platform
            private[immutable] trait $vectorPointerClassName[$A] {..${generateMethods()}}
         """
    }
}
