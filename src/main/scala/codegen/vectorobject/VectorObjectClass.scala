package codegen.vectorobject

import codegen.VectorPackage
import codegen.vectorpointer.VectorPointerCode

import scala.reflect.runtime.universe._

trait VectorObjectClass {
    self: VectorPackage with VectorObjectCode with VectorPointerCode =>

    def generateVectorObjectClass() = {
        q"""
            object $vectorObjectName extends scala.collection.generic.IndexedSeqFactory[$vectorClassName] {
                ${newBuilderDef()}
                ${canBuildFromDef()}

                private val NIL = new $vectorClassName[Nothing](0)

                override def empty[A]: $vectorClassName[A] = NIL

                ${singletonDef()}

                private[immutable] final val useAssertions = false
            }
            """
    }


    private def newBuilderDef() = q"def newBuilder[$A]: mutable.Builder[$A, $vectorClassName[$A]] = new $vectorBuilderName[$A]"

    private def canBuildFromDef() = q"implicit def canBuildFrom[$A]: scala.collection.generic.CanBuildFrom[Coll, $A, $vectorClassName[$A]] = ReusableCBF.asInstanceOf[GenericCanBuildFrom[$A]]"

    private def singletonDef() = {
        val value = TermName("value")
        val code = singletonCode(value, A)
        q"private[immutable] final def $singleton[$A]($value: $A): $vectorClassName[$A] = $code"
    }


}



