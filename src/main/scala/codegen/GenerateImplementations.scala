package codegen

import codegen.vectorbuilder._
import codegen.vectorclass._
import codegen.vectoriterator._
import codegen.vectorobject._
import codegen.vectorpointer._
import codegen.vectorreverseiterator._

import scala.reflect.runtime.universe._

object GenerateImplementations extends App {

    def saveToFile(path: String, code: Tree) = {
        new java.io.File(path).getParentFile.mkdirs()
        val writer = new java.io.PrintWriter(path)
        try {
            writer.write(showCode(code))
            println("Exported to: " + path)
        }
        finally writer.close()
    }

    abstract class VectorImplementation
      extends VectorPackageGen with VectorProperties
      with VectorObjectClassGen with VectorObjectMethodsGen with VectorObjectCodeGen
      with VectorClassGen with VectorMethodsGen with VectorCodeGen
      with VectorBuilderClassGen with VectorBuilderMethodsGen with VectorBuilderCodeGen
      with VectorIteratorClassGen with VectorIteratorMethodsGen with VectorIteratorCodeGen
      with VectorReverseIteratorClassGen with VectorReverseIteratorMethodsGen with VectorReverseIteratorCodeGen
      with VectorPointerClassGen with VectorPointerMethodsGen with VectorPointerCodeGen

    val packageGenerator1 = new VectorImplementation {

        val useTailWritableOpt: Boolean = true

        def subpackage = TermName("rrbVector1")

        def vectorName = "Gen1Vector"
    }


    val packageGenerator2 = new VectorImplementation {

        val useTailWritableOpt: Boolean = false

        def subpackage = TermName("rrbVector2")

        def vectorName = "Gen2Vector"
    }


    saveToFile("./output-test-1.scala", packageGenerator1.generateVectorPackage())
    saveToFile("./output-test-2.scala", packageGenerator2.generateVectorPackage())
}