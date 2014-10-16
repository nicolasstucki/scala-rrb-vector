package codegen

import codegen.test.{VectorBenchmarksGen, VectorTestGen, VectorGeneratorGen}
import codegen.vectorbuilder._
import codegen.vectorclass._
import codegen.vectoriterator._
import codegen.vectorobject._
import codegen.vectorpointer._
import codegen.vectorreverseiterator._

import scala.reflect.runtime.universe._

object GenerateImplementations extends App {

    def saveToFile(path: String, code: String) = {
        new java.io.File(path).getParentFile.mkdirs()
        val writer = new java.io.PrintWriter(path)
        try {
            writer.write(code)
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
      with VectorGeneratorGen with VectorTestGen with VectorBenchmarksGen {

        def outputFile = {
            val subpackagePath = subpackage.toString.replace('.', '/')
            s"./src/main/scala/scala/collection/immutable/generated/$subpackagePath/$vectorName.scala"
        }

        def outputGeneratorFile = {
            val subpackagePath = subpackage.toString.replace('.', '/')
            s"./src/test/scala/scala/collection/immutable/vectorutils/generated/$subpackagePath/$vectorGeneratorClassName.scala"
        }

        def outputTestFile = {
            val subpackagePath = subpackage.toString.replace('.', '/')
            s"./src/test/scala/scala/collection/immutable/vectortests/generated/$subpackagePath/$vectorTestClassName.scala"
        }

        def outputBenchmarkFile = {
            val subpackagePath = subpackage.toString.replace('.', '/')
            s"./src/test/scala/scala/collection/immutable/vectorbenchmarks/generated/$subpackagePath/$vectorBaseBenchmarkClassName.scala"
        }

        def exportCodeToFiles() = {
            saveToFile(outputFile, showCode(generateVectorPackage()))
            saveToFile(outputGeneratorFile, showCode(generateVectorGeneratorClass()))
            saveToFile(outputTestFile, showCode(generateVectorTestClasses()))
            saveToFile(outputBenchmarkFile, showCode(generateVectorBenchmarkClasses()))
        }
    }

    val packageGenerator1 = new VectorImplementation {

        def subpackage = TermName("rrbvector.closedblocks")

        def vectorName = "GenRRBVectorClosedBlocks"

        val CLOSED_BLOCKS: Boolean = true

        override protected val useAssertions: Boolean = false
    }


    val packageGenerator2 = new VectorImplementation {

        def subpackage = TermName("rrbvector.fullblocks")

        def vectorName = "GenRRBVectorFullBlocks"

        val CLOSED_BLOCKS: Boolean = false

        override protected val useAssertions: Boolean = false

    }

    packageGenerator1.exportCodeToFiles()
    packageGenerator2.exportCodeToFiles()
}