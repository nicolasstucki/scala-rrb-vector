package codegen
package vectorpointer
package writabletail

import codegen.VectorPackage

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorPointerClass extends vectorpointer.VectorPointerClass {
    self: VectorPackage with VectorPointerCode =>


}

