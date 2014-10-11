package codegen
package vectorobject


import scala.reflect.runtime.universe._


trait VectorObjectMethodsGen {
    self: VectorObjectCodeGen with VectorProperties =>

    def generateVectorObjectMethods() = {
        Seq(
            newBuilderDef(),
            canBuildFromDef(),
            q"private val NIL = new $vectorClassName[Nothing](0)",
            q"override def $o_empty[A]: $vectorClassName[A] = NIL",
            singletonDef()
        )

    }

    private def newBuilderDef() =
        q"def newBuilder[$A]: mutable.Builder[$A, $vectorClassName[$A]] = new $vectorBuilderClassName[$A]"

    private def canBuildFromDef() =
        q"implicit def canBuildFrom[$A]: scala.collection.generic.CanBuildFrom[Coll, $A, $vectorClassName[$A]] = ReusableCBF.asInstanceOf[GenericCanBuildFrom[$A]]"

    private def singletonDef() = {
        val value = TermName("value")
        val code = singletonCode(value, A)
        q"private[immutable] final def $o_singleton[$A]($value: $A): $vectorClassName[$A] = $code"
    }

}

