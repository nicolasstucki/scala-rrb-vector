package codegen
package vectorreverseiterator

import scala.reflect.runtime.universe._

trait VectorReverseIteratorClassGen {
    self: VectorReverseIteratorMethodsGen with VectorProperties with VectorReverseIteratorCodeGen =>

    def generateVectorReverseIteratorClassDef(): Tree = {
        q"""
            class $vectorReverseIteratorClassName[+$A]($rit_startIndex: Int, $rit_endIndex: Int)
                extends AbstractIterator[$A]
                with Iterator[$A]
                with $vectorPointerClassName[$A @uncheckedVariance] {

                ..${generateVectorReverseIteratorMethods()}
            }
         """
    }
}
