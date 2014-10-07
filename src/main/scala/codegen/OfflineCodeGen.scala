package codegen

import codegen.vectorbuilder._
import codegen.vectorclass._
import codegen.vectoriterator._
import codegen.vectorpointer._
import codegen.vectorreverseiterator._
import codegen.vectorobject._

object OfflineCodeGen extends App {
    val universe: reflect.runtime.universe.type = reflect.runtime.universe

    import codegen.OfflineCodeGen.universe._


    def saveToFile(path: String, code: Tree) = {
        new java.io.File(path).getParentFile.mkdirs()
        val writer = new java.io.PrintWriter(path)
        try {
            writer.write(showCode(code))
            println("Exported to: " + path)
        }
        finally writer.close()
    }

    val codeGenerator = new VectorPackage
      with VectorClass with VectorCode
      with VectorObjectClass with VectorObjectCode
      with VectorIteratorClass
      with VectorReverseIteratorClass
      with VectorBuilderClass with VectorBuilderCode
      with VectorPointerClass with VectorPointerCode {}

//    val codeGenerator = new VectorPackage
//      with VectorClass with VectorCode
//      with VectorObjectClass with VectorObjectCode
//      with VectorIteratorClass
//      with VectorReverseIteratorClass
//      with VectorBuilderClass with VectorBuilderCode
//      with VectorPointerClass with writabletail.VectorPointerCode {}


    saveToFile(codeGenerator.filePath, codeGenerator.generateVectorPackage())
//    println(showCode(codeGenerator.generateVectorClasses()))
}


