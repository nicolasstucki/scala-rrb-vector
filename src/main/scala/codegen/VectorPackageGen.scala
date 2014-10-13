package codegen

import codegen.vectorbuilder.VectorBuilderClassGen
import codegen.vectoriterator.VectorIteratorClassGen
import codegen.vectorobject.VectorObjectClassGen
import codegen.vectorclass.VectorClassGen
import codegen.vectorpointer.VectorPointerClassGen
import codegen.vectorreverseiterator.VectorReverseIteratorClassGen

import scala.reflect.runtime.universe._

trait VectorPackageGen {
    self: VectorProperties
      with VectorObjectClassGen
      with VectorClassGen
      with VectorBuilderClassGen
      with VectorIteratorClassGen
      with VectorReverseIteratorClassGen
      with VectorPointerClassGen =>

    def generateVectorPackage() = {
        inPackages(s"scala.collection.immutable.generated".split('.'),
            q"""
                package $subpackage {
                    import scala.annotation.tailrec
                    import scala.compat.Platform
                    import scala.annotation.unchecked.uncheckedVariance
                    import scala.collection.generic.{GenericCompanion, GenericTraversableTemplate, CanBuildFrom, IndexedSeqFactory}

                    ..${generateClassesDef()}
                }
             """)
    }

    def generateClassesDef(): Seq[Tree] = {
        generateVectorObjectClassDef() :: generateVectorClassDef() :: generateVectorBuilderClassDef() ::
          generateVectorIteratorClassDef() :: generateVectorReverseIteratorClassDef() :: generateVectorPointerClassDef() :: Nil
    }
}
