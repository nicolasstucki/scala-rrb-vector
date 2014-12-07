package codegen

import scala.reflect.runtime.universe._

trait VectorProperties {

    protected val useCompleteRebalance: Boolean

    protected val useAssertions: Boolean

    protected val blockIndexBits: Int


    protected final def blockWidth = 1 << blockIndexBits

    protected final def blockMask = blockWidth - 1

    protected final def maxTreeLevel =  31 / blockIndexBits /* 31 for bits that are used to represent positive Int for indices*/

    protected final def maxTreeDepth = maxTreeLevel + 1

    protected final val blockInvariants = 1

    protected final val A = TypeName("A")

    protected final val B = TypeName("B")


    protected def vectorObjectName = TermName(vectorName())

    protected def vectorClassName = TypeName(vectorName())

    protected def vectorPointerClassName = TypeName(vectorName("Pointer"))

    protected def vectorBuilderClassName = TypeName(vectorName("Builder"))

    protected def vectorIteratorClassName = TypeName(vectorName("Iterator"))

    protected def vectorReverseIteratorClassName = TypeName(vectorName("ReverseIterator"))

    protected def vectorGeneratorClassName = TypeName(vectorName("Generator"))


    protected def parVectorClassName = TypeName("Par" + vectorName())

    protected def parVectorObjectTypeName = TypeName("Par" + vectorName())

    protected def parVectorObjectTermName = TermName("Par" + vectorName())

    protected def parVectorIteratorClassName = TypeName("Par" + vectorName("Iterator"))

    protected def parVectorCombinerClassName = TypeName("Par" + vectorName("Combinator"))


    protected def vectorTestClassName = TypeName(vectorName("Test"))

    protected def vectorBaseBenchmarkClassName = vectorBenchmarkClassName("")

    protected def vectorBenchmarkClassName(method: String) = TypeName(vectorName() + "_" + (if (method != "") method + "_" else "") + "Benchmark")

    def subpackage: TermName = {
        val balanceType = if (useCompleteRebalance) "complete" else "quick"

        TermName(s"rrbvector.$balanceType.block$blockWidth")
    }

    def vectorName(namePostfix: String = ""): String = {
        val balanceType = if (useCompleteRebalance) "_c" else "_q"
        val assertedStr = if (useAssertions) "_asserted" else ""
        val blockWidthStr = "_" + blockWidth
        s"RRBVector$namePostfix$balanceType$blockWidthStr$assertedStr"
    }

    protected def inPackages(packages: Seq[String], code: Tree): Tree = {
        if (packages.nonEmpty) {
            val packageName = TermName(packages.head)
            q"package $packageName { ${inPackages(packages.tail, code)} }"
        } else code
    }

    protected def assertions(asserts: Tree*): Seq[Tree] = {
        if (useAssertions) asserts map (a => q"assert($a)")
        else Nil
    }
}
