package codegen
package vectoriterator

import codegen.VectorPackage
import codegen.vectorpointer.VectorPointerCodeGen

import scala.annotation.tailrec
import scala.reflect.runtime.universe._


trait VectorIteratorMethodsGen {
    self: VectorIteratorCodeGen with VectorProperties =>

    def generateVectorIteratorMethods() = {
        val fields = Seq(
            q"private var $it_blockIndex: Int = _",
            q"private var $it_lo: Int = _",
            q"private var $it_endLo: Int = _",
            q"private var $it_hasNextVar: Boolean = $it_iteratorStartIndex < $it_endIndex"
        )
        val methods = Seq(resetIteratorDef, hasNextDef, nextDef)

        fields ++ methods
    }


    protected def resetIteratorDef() = {
        val code = resetIteratorCode()
        q"private[immutable] final def $it_resetIterator(): Unit = $code"
    }

    protected def hasNextDef() = q"def $it_hasNext = $it_hasNextVar"

    protected def nextDef() = {
        val code = nextCode()
        q"def next(): A = $code"
    }


}

