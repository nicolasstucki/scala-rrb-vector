package codegen
package vectorpointer
package writabletail

import codegen.{vectorpointer, VectorPackage}

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorPointerCode extends vectorpointer.VectorPointerCode {
    self: VectorPackage =>


//    val hasWritableTail = TermName("hasWritableTail")


}
