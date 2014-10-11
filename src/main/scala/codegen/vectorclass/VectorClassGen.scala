package codegen
package vectorclass

import scala.reflect.runtime.universe._

trait VectorClassGen {
    self: VectorMethodsGen with VectorProperties with VectorCodeGen =>

    def generateVectorClassDef(): Tree = {
        q"""
            final class $vectorClassName[+$A] private[immutable](val $v_endIndex: Int)
                extends scala.collection.AbstractSeq[$A]
                with scala.collection.immutable.IndexedSeq[$A]
                with scala.collection.generic.GenericTraversableTemplate[$A, $vectorClassName]
                with scala.collection.IndexedSeqLike[$A, $vectorClassName[$A]]
                with $vectorPointerClassName[$A @uncheckedVariance]
                with Serializable {
                    self =>
                    ..${generateVectorMethods()}
            }
         """
    }
}
