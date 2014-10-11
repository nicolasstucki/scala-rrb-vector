package codegen
package vectorpointer

import scala.reflect.runtime.universe._

trait VectorPointerClassGen {
    self: VectorPointerMethodsGen with VectorProperties =>

    def generateVectorPointerClassDef(): Tree = {
        q"""
            private[immutable] trait $vectorPointerClassName[$A] {
                ..${generateVectorPointerMethods()}
            }
         """
    }
}
