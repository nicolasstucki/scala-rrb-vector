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
                q"private[immutable] var $v_transient: Boolean = false",
                companionDef,
                lengthDef,
                lengthCompareDef,
                parDef,
                iteratorDef,
                reverseIteratorDef,
                //SeqLike
                applyDef,
                collPlusDef,
                plusCollDef,
                // IterableLike
                isEmptyDef,
                headDef,
                takeDef,
                dropDef,
                dropRightDef,
                takeRightDef,
                sliceDef,
                splitAtDef,
                plusPlusDef,
                tailDef,
                lastDef,
                initDef,
                // Private methods
                appendDef,
                appendOnCurrentBlockDef,
                appendBackNewBlockDef,
                prependDef,
                prependOnCurrentBlockDef,
                prependFrontNewBlockDef,
                createSingletonVectorDef,
                normalizeAndFocusOnDef,
                makeTransientIfNeededDef,
                concatenateDef,
                rebalancedDef,
                rebalancedLeafsDef,
                if (useCompleteRebalance) computeBranchingDef else computeNewSizesDef,
                takeFront0Def,
                dropFront0Def
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

    private def plusCollDef = {
        val elem = TermName("elem")
        val code = plusCollCode(elem)
        q"override def /*SeqLike*/ +:[$B >: $A, That]($elem: B)(implicit bf: CanBuildFrom[$vectorClassName[$A], $B, That]): That = $code"
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

    protected def appendDef = {
        val elemParam = TermName("elem")
        val endIndexParam = TermName("_endIndex")
        val code = appendCode(elemParam, endIndexParam)
        q"private[immutable] def $v_append[$B]($elemParam: $B, $endIndexParam: Int) { $code }"
    }

    protected def appendOnCurrentBlockDef = {
        val elemParam = TermName("elem")
        val elemIndexInBlockParam = TermName("elemIndexInBlock")
        val code = appendOnCurrentBlockCode(elemParam, elemIndexInBlockParam)
        q"private def $v_appendOnCurrentBlock[$B]($elemParam: $B, $elemIndexInBlockParam: Int) { $code }"
    }

    protected def appendBackNewBlockDef = {
        val elemParam = TermName("elem")
        val elemIndexInBlockParam = TermName("elemIndexInBlock")
        val code = appendBackNewBlockCode(elemParam, elemIndexInBlockParam)
        q"private def $v_appendBackNewBlock[$B]($elemParam: $B, $elemIndexInBlockParam: Int) { $code }"
    }

    protected def prependDef = {
        val elemParam = TermName("elem")
        val code = prependCode(elemParam)
        q"private[immutable] def $v_prepend[$B]($elemParam: $B) { $code }"
    }

    protected def prependOnCurrentBlockDef = {
        val elemParam = TermName("elem")
        val oldD0 = TermName("oldD0")
        val code = prependOnCurrentBlockCode(elemParam, oldD0)
        q"private def $v_prependOnCurrentBlock[$B]($elemParam: $B, $oldD0: Array[AnyRef]) { $code }"
    }

    protected def prependFrontNewBlockDef = {
        val elemParam = TermName("elem")
        val code = prependFrontNewBlockCode(elemParam)
        q"private def $v_prependFrontNewBlock[$B]($elemParam: $B) { $code }"
    }

    protected def createSingletonVectorDef = {
        val elemParam = TermName("elem")
        val code = createSingletonVectorCode(elemParam)
        q"private def $v_createSingletonVector[$B]($elemParam: $B) = $code"
    }

    protected def normalizeAndFocusOnDef = {
        val indexParam = TermName("index")
        val code = normalizeAndFocusOnCode(indexParam)
        q"private[immutable] def $v_normalizeAndFocusOn($indexParam: Int) = $code"
    }

    protected def makeTransientIfNeededDef= {
        val code = makeTransientIfNeededCode()
        q"private[immutable] def $v_makeTransientIfNeeded() = $code"
    }

    protected def takeFront0Def: Tree = {
        val n = TermName("n")
        val code = takeFront0Code(n)
        q"private def $v_takeFront0($n: Int): $vectorClassName[$A] = $code"
    }

    protected def dropFront0Def: Tree = {
        val n = TermName("n")
        val code = dropFront0Code(n)
        q"private def $v_dropFront0($n: Int): $vectorClassName[$A] = $code"
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

    //
    // Debug
    //

    protected def assertVectorInvariantDef = {
        val code = assertVectorInvariantCode()
        q"private[immutable] def $v_assertVectorInvariant(): Boolean = $code"
    }

    private def debugToStringDef = {
        val code = debugToStringCode(q""""\ttransient = " + $v_transient + "\n"""")
        q"override private[immutable] def $debugToString(): String = $code"
    }

}

