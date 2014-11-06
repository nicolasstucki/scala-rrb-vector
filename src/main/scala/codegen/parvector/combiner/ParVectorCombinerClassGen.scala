package codegen
package parvector
package combiner

import scala.reflect.runtime.universe._

trait ParVectorCombinerClassGen {
    self: ParVectorCombinerMethodsGen with VectorProperties with ParVectorCombinerCodeGen =>

    def generateParVectorCombinerClassDef(): Tree = {
        q"""
            private[immutable] class $parVectorCombinerClassName[$A] extends Combiner[$A, $parVectorClassName[$A]] {
              ..${generateParVectorCombinerMethods()}
            }
         """
    }
}
