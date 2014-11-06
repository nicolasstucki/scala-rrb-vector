package codegen
package vector
package vectorobject

import scala.reflect.runtime.universe._

trait VectorObjectClassGen {
    self: VectorObjectMethodsGen with VectorProperties =>

    def generateVectorObjectClassDef() = {
        q"""
            object $vectorObjectName extends scala.collection.generic.IndexedSeqFactory[$vectorClassName] {
                ..${generateVectorObjectMethods()}
            }
            """
    }

}
