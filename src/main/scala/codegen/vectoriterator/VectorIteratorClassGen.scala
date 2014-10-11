package codegen
package vectoriterator

import scala.reflect.runtime.universe._

trait VectorIteratorClassGen {
    self: VectorIteratorMethodsGen with VectorProperties with VectorIteratorCodeGen =>

    def generateVectorIteratorClassDef(): Tree = {
        q"""
            class $vectorIteratorClassName[+$A]($it_iteratorStartIndex: Int, $it_endIndex: Int)
                extends AbstractIterator[$A]
                with Iterator[$A]
                with $vectorPointerClassName[$A @uncheckedVariance] {

                ..${generateVectorIteratorMethods()}
            }
         """
    }
}
