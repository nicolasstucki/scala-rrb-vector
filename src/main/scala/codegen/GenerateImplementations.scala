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
            saveToFile(outputFile, showCode(generateVectorPackage()) + "\n\n" + showCode(generateParVectorPackage()))
            saveToFile(outputGeneratorFile, showCode(generateVectorGeneratorClass()))
            saveToFile(outputTestFile, showCode(generateVectorTestClasses()))
            saveToFile(outputBenchmarkFile, showCode(generateVectorBenchmarkClasses()))
        }
    }

    val USE_ASSERTIONS = false

    for {
        useDirectLevel <- Iterator(true, false)
        depthMatch <- Seq(DEPTH_MATCH_METHOD.WITH_MATCH, DEPTH_MATCH_METHOD.WITH_IF_ELSE_IF)
        useCompleteRebalance <- Iterator(true, false)
        indexBits <- 5 to 7
        par_split_method <- Seq(PAR_SPLIT_METHOD.SPLIT_IN_COMPLETE_SUBTREES, PAR_SPLIT_METHOD.SPLIT_IN_HALF /*, PAR_SPLIT_METHOD.BLOCK_SPLIT */)
    } {


        val packageGenerator = new VectorImplementation {

            override protected val PAR_SPLIT = par_split_method
            override protected val DEPTH_MATCH = depthMatch
            protected val COMPLETE_REBALANCE: Boolean = useCompleteRebalance
            protected val DIRECT_LEVEL: Boolean = useDirectLevel
            override protected val blockIndexBits: Int = indexBits
            override protected val useAssertions: Boolean = USE_ASSERTIONS
        }
        packageGenerator.exportCodeToFiles()
    }

}