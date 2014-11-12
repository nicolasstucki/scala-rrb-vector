package codegen

import vector.builder._
import vector.vectorclass._
import vector.iterator._
import vector.vectorobject._
import vector.vectorpointer._
import vector.reverseiterator._

import parvector.parvectorclass._
import parvector.parvectorobject._
import parvector.iterator._
import parvector.combiner._

import test._

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
      with ParVectorClassGen with ParVectorMethodsGen with ParVectorCodeGen
      with ParVectorObjectClassGen with ParVectorObjectMethodsGen
      with ParVectorIteratorClassGen with ParVectorIteratorMethodsGen with ParVectorIteratorCodeGen
      with ParVectorCombinerClassGen with ParVectorCombinerMethodsGen with ParVectorCombinerCodeGen

      with VectorGeneratorGen with VectorTestGen with VectorBenchmarksGen {

        def outputFile = {
            val subpackagePath = subpackage.toString.replace('.', '/')
            s"./src/main/scala/scala/collection/immutable/generated/$subpackagePath/${vectorName()}.scala"
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
            saveToFile(outputFile, showCode(generateVectorPackage()) + "\n\n" + showCode(generateParVectorPackage()))
            saveToFile(outputGeneratorFile, showCode(generateVectorGeneratorClass()))
            saveToFile(outputTestFile, showCode(generateVectorTestClasses()))
            if (!useAssertions) {
                saveToFile(outputBenchmarkFile, showCode(generateVectorBenchmarkClasses()))
            }
        }
    }

    val USE_ASSERTIONS = false

    for {
        completeRebalanceOn <- Iterator(true, false)
        assertionsOn <- Iterator(true, false)
        indexBits <- 5 to 8
    } {
        val packageGenerator = new VectorImplementation {
            protected val useCompleteRebalance: Boolean = completeRebalanceOn
            protected val useAssertions: Boolean = assertionsOn
            override protected val blockIndexBits: Int = indexBits
        }
        packageGenerator.exportCodeToFiles()
    }

}