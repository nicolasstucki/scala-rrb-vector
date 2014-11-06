package codegen
package vector
package iterator

import vectorpointer.VectorPointerCodeGen

import scala.reflect.runtime.universe._

trait VectorIteratorClassGen {
    self: VectorIteratorMethodsGen with VectorProperties with VectorIteratorCodeGen with VectorPointerCodeGen =>

    def generateVectorIteratorClassDef(): Tree = {
        q"""
            class $vectorIteratorClassName[+$A]($it_iteratorStartIndex: Int, override private[immutable] val $endIndex: Int)
                extends AbstractIterator[$A]
                with Iterator[$A]
                with $vectorPointerClassName[$A @uncheckedVariance] {

                ..${generateVectorIteratorMethods()}
            }
         """
    }
}
