package codegen
package vector
package iterator

import vectorpointer.VectorPointerCodeGen

import scala.reflect.runtime.universe._

trait VectorIteratorMethodsGen {
    self: VectorIteratorCodeGen with VectorPointerCodeGen with VectorProperties =>

    def generateVectorIteratorMethods() = {
        val fields = Seq(
            q"private var $it_blockIndex: Int = _",
            q"private var $it_lo: Int = _",
            q"private var $it_endLo: Int = _",
            q"private var $it_hasNextVar: Boolean = _"
        )
        val methods = Seq(initIteratorFromDef, hasNextDef, nextDef, remainingDef)

        fields ++ methods
    }


    protected def initIteratorFromDef = {
        val that = TermName("that")
        val code = initIteratorFromCode(that)
        q"private[collection] final def $it_initIteratorFrom[$B >: $A](that: $vectorPointerClassName[$B]): Unit = $code"
    }

    protected def hasNextDef = q"final def $it_hasNext = $it_hasNextVar"

    protected def nextDef = {
        val code = nextCode()
        q"def next(): A = $code"
    }

    private def remainingDef = {
        val code = remainingCode()
        q"private[collection] def $it_remaining: Int = $code"
    }

}

