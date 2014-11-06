package codegen
package vector
package builder

import scala.reflect.runtime.universe._

trait VectorBuilderClassGen {
    self: VectorBuilderMethodsGen with VectorProperties =>

    def generateVectorBuilderClassDef(): Tree = {
        q"""
            final class $vectorBuilderClassName[$A]()
                extends mutable.Builder[$A, $vectorClassName[$A]]
                with $vectorPointerClassName[$A @uncheckedVariance] {
                    ..${generateVectorBuilderMethods()}
            }
         """
    }
}
