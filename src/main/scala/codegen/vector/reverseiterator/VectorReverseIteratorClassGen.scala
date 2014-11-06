package codegen
package vector
package reverseiterator

import vectorpointer.VectorPointerCodeGen

import scala.reflect.runtime.universe._

trait VectorReverseIteratorClassGen {
    self: VectorReverseIteratorMethodsGen with VectorProperties with VectorReverseIteratorCodeGen with VectorPointerCodeGen =>

    def generateVectorReverseIteratorClassDef(): Tree = {
        q"""
            class $vectorReverseIteratorClassName[+$A]($rit_startIndex: Int, override private[immutable] final val $endIndex: Int)
                extends AbstractIterator[$A]
                with Iterator[$A]
                with $vectorPointerClassName[$A @uncheckedVariance] {

                ..${generateVectorReverseIteratorMethods()}
            }
         """
    }
}
