package codegen

import codegen.parvector.combiner.ParVectorCombinerClassGen
import codegen.parvector.iterator.ParVectorIteratorClassGen
import codegen.parvector.parvectorclass.ParVectorClassGen
import codegen.parvector.parvectorobject.ParVectorObjectClassGen
import vector.builder.VectorBuilderClassGen
import vector.iterator.VectorIteratorClassGen
import vector.vectorobject.VectorObjectClassGen
import vector.vectorclass.VectorClassGen
import vector.vectorpointer.VectorPointerClassGen
import vector.reverseiterator.VectorReverseIteratorClassGen

import scala.reflect.runtime.universe._

trait VectorPackageGen {
    self: VectorProperties
      with VectorObjectClassGen
      with VectorClassGen
      with VectorBuilderClassGen
      with VectorIteratorClassGen
      with VectorReverseIteratorClassGen
      with VectorPointerClassGen
      with ParVectorClassGen
      with ParVectorObjectClassGen
      with ParVectorIteratorClassGen
      with ParVectorCombinerClassGen =>

    def generateVectorPackage() = {
        inPackages(s"scala.collection.immutable.generated".split('.'),
            q"""
                package $subpackage {
                    import scala.annotation.tailrec
                    import scala.compat.Platform
                    import scala.annotation.unchecked.uncheckedVariance
                    import scala.collection.generic.{GenericCompanion, GenericTraversableTemplate, CanBuildFrom, IndexedSeqFactory}
                    import scala.collection.parallel.immutable.generated.$subpackage.$parVectorClassName
                    ..${generateClassesDef()}
                }
             """)
    }

    def generateParVectorPackage() = {
        inPackages(s"scala.collection.parallel.immutable.generated".split('.'),
            q"""
                package $subpackage {
                    import scala.collection.immutable.generated.$subpackage._
                    import scala.collection.generic.{GenericParTemplate, CanCombineFrom, ParFactory}
                    import scala.collection.parallel.{ParSeqLike, Combiner, SeqSplitter}
                    import scala.collection.mutable.ArrayBuffer

                    ..${generateParClassesDef()}
                }
             """)
    }

    def generateClassesDef(): Seq[Tree] = {
        generateVectorObjectClassDef() :: generateVectorClassDef() :: generateVectorBuilderClassDef() ::
          generateVectorIteratorClassDef() :: generateVectorReverseIteratorClassDef() :: generateVectorPointerClassDef() :: Nil
    }

    def generateParClassesDef(): Seq[Tree] = {
        generateParVectorClassDef() :: generateParVectorObjectDef() :: generateParVectorCombinerClassDef() :: Nil
    }
}
