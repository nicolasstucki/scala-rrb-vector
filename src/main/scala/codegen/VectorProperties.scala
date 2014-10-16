package codegen

import scala.reflect.runtime.universe._

trait VectorProperties {
    protected val blockIndexBits = 5

    protected final def blockWidth = 1 << blockIndexBits

    protected final def blockMask = blockWidth - 1

    protected val blockInvariants = 1

    protected val A = TypeName("A")

    protected val B = TypeName("B")

    protected def vectorObjectName = TermName(vectorName)

    protected def vectorClassName = TypeName(vectorName)

    protected def vectorPointerClassName = TypeName(vectorName + "Pointer")

    protected def vectorBuilderClassName = TypeName(vectorName + "Builder")

    protected def vectorIteratorClassName = TypeName(vectorName + "Iterator")

    protected def vectorReverseIteratorClassName = TypeName(vectorName + "ReverseIterator")

    protected def vectorGeneratorClassName = TypeName(vectorName + "Generator")

    protected def vectorTestClassName = TypeName(vectorName + "Test")

    protected def vectorBaseBenchmarkClassName = vectorBenchmarkClassName("")

    protected def vectorBenchmarkClassName(method: String) = TypeName(vectorName + method + "Benchmark")


    protected val useAssertions = true

    def subpackage: TermName

    val CLOSED_BLOCKS: Boolean

    def vectorName: String


    protected def inPackages(packages: Seq[String], code: Tree): Tree = {
        if (packages.nonEmpty) {
            val packageName = TermName(packages.head)
            q"package $packageName { ${inPackages(packages.tail, code)} }"
        } else code
    }
}
