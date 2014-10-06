package codegen

import codegen.vectorbuilder.VectorBuilderClass
import codegen.vectorclass.VectorClass
import codegen.vectorobject.VectorObjectClass
import codegen.vectoriterator.VectorIteratorClass
import codegen.vectorpointer.VectorPointerClass
import codegen.vectorreverseiterator.VectorReverseIteratorClass

import scala.reflect.runtime.universe._

trait VectorPackage {
    self: VectorClass
      with VectorObjectClass
      with VectorIteratorClass
      with VectorReverseIteratorClass
      with VectorBuilderClass
      with VectorPointerClass =>

    def filePath = s"src/main/scala/scala/collection/immutable/${subPackageName.toString}/${vectorClassName.toString}.scala"

    def generateVectorClasses() = {
        val vectorObjectDef = generateVectorObjectClass()
        println(s"Generated $vectorObjectName")
        val vectorClassDef = generateVectorClass()
        println(s"Generated $vectorClassName")
        val vectorIteratorDef = generateVectorIteratorClass()
        println(s"Generated $vectorIteratorName")
        val vectorReverseIteratorDef = generateVectorReverseIteratorClass()
        println(s"Generated $vectorReverseIteratorName")
        val vectorBuilderDef = generateVectorBuilderClass()
        println(s"Generated $vectorBuilderName")
        val vectorPointerDef = generateVectorPointerClass()
        println(s"Generated $vectorPointerClassName")
        q"""
                package scala {
                package collection {
                package immutable {
                package $subPackageName {

                import scala.annotation.tailrec
                import scala.annotation.unchecked.uncheckedVariance
                import scala.collection.generic.{GenericCompanion, GenericTraversableTemplate, CanBuildFrom, IndexedSeqFactory}
                import scala.compat.Platform

                $vectorObjectDef
                $vectorClassDef

                $vectorIteratorDef
                $vectorReverseIteratorDef

                $vectorBuilderDef

                $vectorPointerDef

                }}}}
             """
    }

    val subPackageName = TermName("genrrbvector")

    val vectorObjectName = TermName("GenRRBVector")
    val vectorClassName = TypeName("GenRRBVector")

    val vectorPointerClassName = TypeName("GenRRBVectorPointer")
    val vectorBuilderName = TypeName("GenRRBVectorBuilder")

    val vectorIteratorName = TypeName("GenRRBVectorIterator")
    val vectorReverseIteratorName = TypeName("GenRRBVectorReverseIterator")

    val A = TypeName("A")

    val noAssertions = true

    def asserts(tree: Tree): Tree = if (noAssertions) q"{}" else tree

}
