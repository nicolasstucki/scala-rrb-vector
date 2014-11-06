package codegen
package vector
package vectorclass

import iterator.VectorIteratorCodeGen
import vectorobject.VectorObjectCodeGen
import vectorpointer.VectorPointerCodeGen

import scala.reflect.runtime.universe._


trait VectorMethodsGen {
    self: VectorCodeGen with VectorProperties with VectorObjectCodeGen with VectorPointerCodeGen with VectorIteratorCodeGen =>

    def generateVectorMethods(): Seq[Tree] = {

        val methods =
            Seq[Tree](
                q"private[immutable] var $v_dirty: Boolean = false",
                companionDef,
                lengthDef,
                lengthCompareDef,
                parDef,
                iteratorDef,
                reverseIteratorDef,
                //SeqLike
                applyDef,
                collPlusDef,
                // IterableLike
                isEmptyDef,
                headDef,
                takeDef,
                dropRightDef,
                sliceDef,
                splitAtDef,
                plusPlusDef,
                tailDef,
                lastDef,
                initDef,
                // Private methods
                appendBackSetupNewBlockDef,
                concatenateDef,
                rebalancedDef,
                rebalancedLeafsDef,
                if (COMPLETE_REBALANCE) computeBranchingDef else computeNewSizesDef,
                withComputedSizesDef,
                treeSizeDef,
                takeFront0Def
            )

        if (useAssertions) methods :+ assertVectorInvariantDef
        else methods

    }


    private def companionDef = q"override def companion: scala.collection.generic.GenericCompanion[$vectorClassName] = $vectorObjectName"

    private def lengthDef = q"def $v_length(): Int = ${lengthCode()}"

    private def lengthCompareDef = {
        val len = TermName("len")
        val code = lengthCompareCode(q"$len")
        q"override def $v_lengthCompare($len: Int): Int = $code"
    }

    private def parDef = {
        q"override def $v_par = new $parVectorClassName[$A](this)"
    }

    private def iteratorDef = {
        val code = iteratorCode()
        q"override def $v_iterator: $vectorIteratorClassName[$A] = $code"
    }

    private def reverseIteratorDef = {
        val code = reverseIteratorCode()
        q"override def $v_reverseIterator: $vectorReverseIteratorClassName[$A] = $code"
    }

    //
    // SeqLike
    //

    private def applyDef = {
        val index = TermName("index")
        val code = applyCode(q"$index")
        q"def /*SeqLike*/ $v_apply(index: Int): A = $code"
    }

    private def collPlusDef = {
        val elem = TermName("elem")
        val code = collPlusCode(elem)
        q"override def /*SeqLike*/ :+[$B >: $A, That]($elem: B)(implicit bf: CanBuildFrom[$vectorClassName[$A], $B, That]): That = $code"
    }

    //
    // IterableLike
    //

    protected def isEmptyDef = {
        val code = isEmptyCode(q"this")
        q"override /*IterableLike*/ def $v_isEmpty: Boolean = $code"
    }


    protected def headDef = {
        val code = headCode()
        q"override /*IterableLike*/ def $v_head: $A = $code"

    }

    protected def takeDef: Tree = {
        val n = TermName("n")
        val code = takeCode(q"$n")
        q"override /*IterableLike*/ def $v_take($n: Int): $vectorClassName[$A] = $code"
    }

    protected def takeRightDef: Tree = {
        val n = TermName("n")
        val code = takeRightCode(q"$n")
        q"override /*IterableLike*/ def $v_takeRight($n: Int): $vectorClassName[$A] = $code"
    }

    protected def dropDef: Tree = {
        val n = TermName("n")
        val code = dropCode(q"$n")
        q"override /*IterableLike*/ def $v_drop($n: Int): $vectorClassName[$A] = $code"
    }

    protected def dropRightDef: Tree = {
        val n = TermName("n")
        val code = dropRightCode(q"$n")
        q"override /*IterableLike*/ def $v_dropRight($n: Int): $vectorClassName[$A] = $code"
    }

    protected def sliceDef: Tree = {
        val from = TermName("from")
        val until = TermName("until")
        val code = sliceCode(q"$from", q"$until")
        q"override /*IterableLike*/ def $v_slice($from: Int, $until: Int): $vectorClassName[A] = $code"
    }

    protected def splitAtDef: Tree = {
        val n = TermName("n")
        val code = q"($v_take($n), $v_drop($n))"
        q"override /*IterableLike*/ def $v_splitAt($n: Int): ($vectorClassName[$A], $vectorClassName[$A]) = $code"
    }


    //
    // TraversableLike
    //

    protected def plusPlusDef = {
        val that = TermName("that")
        val bf = TermName("bf")
        val code = plusPlusCode(q"$that", q"$bf")
        q"override /*TraversableLike*/ def ++[$B >: $A, That]($that: GenTraversableOnce[$B])(implicit $bf: CanBuildFrom[$vectorClassName[$A], $B, That]): That = $code"
    }

    protected def tailDef = {
        val code = tailCode(q"this")
        q"override /*TraversableLike*/ def $v_tail: $vectorClassName[$A] = $code"
    }

    protected def lastDef = {
        val code = lastCode(q"this")
        q"override /*TraversableLike*/ def $v_last: $A = $code"
    }

    protected def initDef = {
        val code = initCode(q"this")
        q"override /*TraversableLike*/ def $v_init: $vectorClassName[$A] = $code"
    }


    //
    // Private methods
    //

    protected def appendBackSetupNewBlockDef = {
        val code = appendBackSetupNewBlockCode()
        q"private[immutable] def $v_appendBackSetupNewBlock() = $code"
    }

    protected def concatenateDef = {
        val currentSize = TermName("currentSize")
        val that = TermName("that")
        val code = concatenateCode(currentSize, that)
        q"private[immutable] def $v_concatenate[$B >: $A]($currentSize: Int, $that: $vectorClassName[$B]) { $code }"
    }

    protected def rebalancedDef = {
        val displayLeft = TermName("displayLeft")
        val concat = TermName("concat")
        val displayRight = TermName("displayRight")
        val currentDepth = TermName("currentDepth")

        val code = rebalancedCode(q"$displayLeft", q"$concat", q"$displayRight", q"$currentDepth")
        q"private def $v_rebalanced($displayLeft: Array[AnyRef], $concat: Array[AnyRef], $displayRight: Array[AnyRef], $currentDepth: Int): Array[AnyRef] = $code"
    }

    protected def rebalancedLeafsDef = {
        val displayLeft = TermName("displayLeft")
        val displayRight = TermName("displayRight")

        val isTop = TermName("isTop")
        val code = rebalancedLeafsCode(q"$displayLeft", q"$displayRight", isTop)
        q"private def $v_rebalancedLeafs($displayLeft: Array[AnyRef], $displayRight: Array[AnyRef], $isTop: Boolean): Array[AnyRef] = $code"
    }


    protected def computeNewSizesDef = {
        val displayLeft = TermName("displayLeft")
        val concat = TermName("concat")
        val displayRight = TermName("displayRight")
        val currentDepth = TermName("currentDepth")
        val code = computeNewSizesCode(q"$displayLeft", q"$concat", q"$displayRight", q"$currentDepth")
        q"private def $v_computeNewSizes($displayLeft: Array[AnyRef], $concat: Array[AnyRef], $displayRight: Array[AnyRef], $currentDepth: Int) = $code"
    }

    protected def computeBranchingDef = {
        val displayLeft = TermName("displayLeft")
        val concat = TermName("concat")
        val displayRight = TermName("displayRight")
        val currentDepth = TermName("currentDepth")
        val code = computeBranchingCode(q"$displayLeft", q"$concat", q"$displayRight", q"$currentDepth")
        q"private def $v_computeBranching($displayLeft: Array[AnyRef], $concat: Array[AnyRef], $displayRight: Array[AnyRef], $currentDepth: Int) = $code"
    }

    protected def withComputedSizesDef = {
        val node = TermName("node")
        val currentDepth = TermName("currentDepth")
        val endIndex = TermName("_endIndex")
        val code = withComputedSizesCode(q"$node", q"$currentDepth", q"$endIndex")
        q"private def $v_withComputedSizes($node: Array[AnyRef], $currentDepth: Int): Array[AnyRef] = $code"
    }

    protected def treeSizeDef = {
        val tree = TermName("tree")
        val currentDepth = TermName("currentDepth")
        val code = treeSizeCode(q"$tree", q"$currentDepth")
        q"private def $v_treeSize($tree: Array[AnyRef], $currentDepth: Int): Int = $code"
    }

    protected def takeFront0Def: Tree = {
        val n = TermName("n")
        val code = takeFront0Code(q"$n")
        q"private def $v_takeFront0($n: Int): $vectorClassName[$A] = $code"
    }

    //
    // Invariant
    //

    protected def assertVectorInvariantDef = {
        val code = assertVectorInvariantCode()
        q"private[immutable] def $v_assertVectorInvariant(): Boolean = $code"
    }


}

