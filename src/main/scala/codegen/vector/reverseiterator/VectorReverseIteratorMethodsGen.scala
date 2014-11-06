package codegen
package vector
package reverseiterator

import vectorpointer.VectorPointerCodeGen

import scala.reflect.runtime.universe._

trait VectorReverseIteratorMethodsGen {
    self: VectorReverseIteratorCodeGen with VectorPointerCodeGen with VectorProperties =>

    def generateVectorReverseIteratorMethods() = {
        val fields = Seq(
            q"private var $rit_lastIndexOfBlock: Int = _",
            q"private var $rit_lo: Int = _",
            q"private var $rit_endLo: Int = _",
            q"private var $rit_hasNextVar: Boolean = $rit_startIndex < $endIndex")

        val methods = Seq(rit_initIteratorFromDef, rit_hasNextDef, rit_nextDef)

        fields ++ methods
    }


    protected def rit_initIteratorFromDef = {
        val that = TermName("that")
        val code = rit_initIteratorFromCode(that)
        q"private[collection] final def $rit_initIteratorFrom[$B >: $A]($that: $vectorPointerClassName[$B]): Unit  = $code"
    }

    protected def rit_hasNextDef = q"final def $rit_hasNext = $rit_hasNextVar"

    protected def rit_nextDef = {
        val code = rit_nextCode()
        q"def next(): $A = $code"
    }


}

