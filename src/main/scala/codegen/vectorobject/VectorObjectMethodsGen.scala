package codegen
package vectorobject


import scala.reflect.runtime.universe._


trait VectorObjectMethodsGen {
    self: VectorObjectCodeGen with VectorProperties =>

    def generateVectorObjectMethods() = {
        Seq(
            newBuilderDef(),
            canBuildFromDef(),
            q"private lazy val EMPTY_VECTOR = new $vectorClassName[Nothing](0)",
            q"override def $o_empty[$A]: $vectorClassName[$A] = EMPTY_VECTOR"
        )
    }

    private def newBuilderDef() =
        q"def newBuilder[$A]: mutable.Builder[$A, $vectorClassName[$A]] = new $vectorBuilderClassName[$A]"

    private def canBuildFromDef() =
        q"implicit def canBuildFrom[$A]: scala.collection.generic.CanBuildFrom[Coll, $A, $vectorClassName[$A]] = ReusableCBF.asInstanceOf[GenericCanBuildFrom[$A]]"
}

