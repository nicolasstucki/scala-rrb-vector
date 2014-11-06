package codegen
package parvector
package parvectorobject

import scala.reflect.runtime.universe._

trait ParVectorObjectMethodsGen {
    self: VectorProperties =>

    def generateParVectorObjectsMethods() = {
        val methods = Seq(
            canBuildFromDef,
            newBuilderDef,
            newCombinerDef)
        methods
    }


    private def canBuildFromDef =
        q"implicit def canBuildFrom[$A]: CanCombineFrom[Coll, $A, $parVectorClassName[$A]] = new GenericCanCombineFrom[$A]"

    private def newBuilderDef =
        q"def newBuilder[$A]: Combiner[$A, $parVectorClassName[$A]] = newCombiner[$A]"

    private def newCombinerDef =
        q"def newCombiner[$A]: Combiner[$A, $parVectorClassName[$A]] = new $parVectorCombinerClassName[$A]"

}

