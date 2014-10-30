package codegen
package vectorreverseiterator

import codegen.vectorpointer.VectorPointerCodeGen

import scala.annotation.tailrec
import scala.reflect.runtime.universe._


trait VectorReverseIteratorMethodsGen {
    self: VectorReverseIteratorCodeGen with VectorPointerCodeGen with VectorProperties =>

    def generateVectorReverseIteratorMethods() = {
        val fields = Seq(
            q"private var $rit_lastIndexOfBlock: Int = _",
            q"private var $rit_lo: Int = _",
            q"private var $rit_endLo: Int = _",
            q"private var $rit_hasNextVar: Boolean = $rit_startIndex < $endIndex")

        val methods = Seq(rit_resetIteratorDef, rit_hasNextDef, rit_nextDef)

        fields ++ methods
    }


    protected def rit_resetIteratorDef() = {
        val code = rit_resetIteratorCode()
        q"private[immutable] final def $rit_resetIterator(): Unit = $code"
    }

    protected def rit_hasNextDef() = q"def $rit_hasNext = $rit_hasNextVar"

    protected def rit_nextDef() = {
        val code = rit_nextCode()
        q"def next(): $A = $code"
    }


}

