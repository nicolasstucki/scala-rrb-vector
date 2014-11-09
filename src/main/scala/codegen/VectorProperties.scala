package codegen

import scala.reflect.runtime.universe._

trait VectorProperties {

    protected val PAR_SPLIT: PAR_SPLIT_METHOD

    protected val DEPTH_MATCH: DEPTH_MATCH_METHOD

    protected val COMPLETE_REBALANCE: Boolean

    protected val DIRECT_LEVEL: Boolean

    protected val blockIndexBits: Int

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


    protected def parVectorClassName = TypeName("Par" + vectorName)

    protected def parVectorObjectTypeName = TypeName("Par" + vectorName)

    protected def parVectorObjectTermName = TermName("Par" + vectorName)

    protected def parVectorIteratorClassName = TypeName(s"Par${vectorName}Iterator")

    protected def parVectorCombinerClassName = TypeName(s"Par${vectorName}Combinator")


    protected def vectorTestClassName = TypeName(vectorName + "Test")

    protected def vectorBaseBenchmarkClassName = vectorBenchmarkClassName("")

    protected def vectorBenchmarkClassName(method: String) = TypeName(vectorName + "_" + (if (method != "") method + "_" else "") + "Benchmark")


    protected val useAssertions: Boolean

    def subpackage: TermName = {
        TermName(s"rrbvector")
    }

    def vectorName: String = {
        val balanceType = if (COMPLETE_REBALANCE) "complete" else "quick"
        val levelIndirectionType = if (DIRECT_LEVEL) "directLevel" else "incrementalLevel"

        s"RRBVector_${balanceType}_${DEPTH_MATCH.shortName}_${levelIndirectionType}_${blockWidth}_${PAR_SPLIT.shortName}"
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

sealed trait PAR_SPLIT_METHOD {
    def shortName: String
}

object PAR_SPLIT_METHOD {

    object SPLIT_IN_HALF extends PAR_SPLIT_METHOD {
        override def shortName = "splithalf"
    }

    object SPLIT_IN_COMPLETE_SUBTREES extends PAR_SPLIT_METHOD {
        override def shortName = "splitbalanced"
    }

    object BLOCK_SPLIT extends PAR_SPLIT_METHOD {
        override def shortName = "splitsubtrees"
    }

}

sealed trait DEPTH_MATCH_METHOD {
    def shortName: String
}

object DEPTH_MATCH_METHOD {

    object WITH_MATCH extends DEPTH_MATCH_METHOD {
        override def shortName = "matchDepth"
    }

    object WITH_IF_ELSE_IF extends DEPTH_MATCH_METHOD {
        override def shortName = "ifElseDepth"
    }

}