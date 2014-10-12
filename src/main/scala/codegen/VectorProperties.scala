package codegen

import scala.reflect.runtime.universe._

trait VectorProperties {
    protected val blockIndexBits = 5

    protected def blockWidth = 1 << blockIndexBits

    protected def blockMask = (1 << blockIndexBits) - 1

    protected val blockInvariants = 1

    protected val useAssertions = false

    protected val vectorObjectName = TermName(vectorName)
    protected val vectorClassName = TypeName(vectorName)
    protected val vectorPointerClassName = TypeName(vectorName + "Pointer")
    protected val vectorBuilderClassName = TypeName(vectorName + "Builder")
    protected val vectorIteratorClassName = TypeName(vectorName + "Iterator")
    protected val vectorReverseIteratorClassName = TypeName(vectorName + "VectorReverseIterator")

    def subpackage: TermName

    def vectorName: String

    protected val A = TypeName("A")
    protected val B = TypeName("B")

    val CLOSED_BLOCKS: Boolean

}
