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
      with VectorPointerClassGen with VectorPointerMethodsGen with VectorPointerCodeGen {

        def outputFile = {
            val subpackagePath = subpackage.toString.replace('.', '/')
            s"./src/main/scala/scala/collection/immutable/generated/$subpackagePath/$vectorName.scala"
        }

        def exportCodeToFile() = {
            saveToFile(outputFile, generateVectorPackage())
        }
    }

    val packageGenerator1 = new VectorImplementation {

        def subpackage = TermName("rrbvector.closedblocks")

        def vectorName = "Vector"

        val CLOSED_BLOCKS: Boolean = true

    }


    val packageGenerator2 = new VectorImplementation {

        def subpackage = TermName("rrbvector.fullblocks")

        def vectorName = "Vector"

        val CLOSED_BLOCKS: Boolean = false
    }

    packageGenerator1.exportCodeToFile()
    packageGenerator2.exportCodeToFile()
}