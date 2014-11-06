package codegen
package parvector
package parvectorclass


import scala.reflect.runtime.universe._

trait ParVectorClassGen {
    self: ParVectorMethodsGen with VectorProperties with ParVectorCodeGen =>

    def generateParVectorClassDef(): Tree = {
        q"""
            class $parVectorClassName[+$A](private[this] val $par_vector: $vectorClassName[$A])
              extends ParSeq[$A]
              with GenericParTemplate[$A, $parVectorObjectTypeName]
              with ParSeqLike[$A, $parVectorClassName[$A], $vectorClassName[$A]]
              with Serializable {

              ..${generateParVectorMethods()}
            }
         """
    }
}
