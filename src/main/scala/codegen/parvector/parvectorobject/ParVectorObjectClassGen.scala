package codegen
package parvector
package parvectorobject

import scala.reflect.runtime.universe._

trait ParVectorObjectClassGen {
    self: ParVectorObjectMethodsGen with VectorProperties =>

    def generateParVectorObjectDef(): Tree = {
        q"""
            object $parVectorObjectTermName extends ParFactory[$parVectorClassName] {
                ..${generateParVectorObjectsMethods()}
            }
         """
    }
}
