package codegen
package vectorclass

import codegen.vectoriterator.VectorIteratorCodeGen
import codegen.vectorobject.VectorObjectCodeGen
import codegen.vectorpointer.VectorPointerCodeGen

import scala.annotation.tailrec
import scala.reflect.runtime.universe._


trait VectorMethodsGen {
    self: VectorCodeGen with VectorProperties with VectorObjectCodeGen with VectorPointerCodeGen with VectorIteratorCodeGen =>

    def generateVectorMethods(): Seq[Tree] = {

        val methods =
            Seq[Tree](
                companionDef(),
                lengthDef(),
                lengthCompareDef(),
                initIteratorDef(),
                initReverseIteratorDef(),
                iteratorDef(),
                reverseIteratorDef(),
                //SeqLike
                applyDef(),
                collPlusDef(),
                // IterableLike
                isEmptyDef(),
                headDef(),
                takeDef(),
                dropRightDef(),
                sliceDef(),
                splitAtDef(),
                plusPlusDef(),
                tailDef(),
                lastDef(),
                initDef(),
                // Private methods
                appendedBackDef(),
                appendBackSetupCurrentBlockDef(),
                appendBackSetupNewBlockDef(),
                concatenatedDef(),
                rebalancedDef(),
                computeNewSizesDef(),
                withComputedSizesDef(),
                treeSizeDef(),
                copiedAcrossDef(),
                takeFront0Def()
            )

        if (useAssertions) methods :+ assertVectorInvariantDef()
        else methods

    }


    private def companionDef() = q"override def companion: scala.collection.generic.GenericCompanion[$vectorClassName] = $vectorObjectName"

    private def lengthDef() = q"def $v_length(): Int = ${lengthCode()}"

    private def lengthCompareDef() = {
        val len = TermName("len")
        val code = lengthCompareCode(q"$len")
        q"override def $v_lengthCompare($len: Int): Int = $code"
    }

    private def initIteratorDef() = {
        val it = TermName("it")
        val code = initIteratorCode(q"$it")
        q"private[collection] def $v_initIterator[$B >: $A]($it: $vectorIteratorClassName[$B]) { $code }"
    }

    private def initReverseIteratorDef() = {
        val rit = TermName("it")
        val code = initIteratorCode(q"$rit")
        q"private[collection] def $v_initIterator[$B >: $A]($rit: $vectorReverseIteratorClassName[$B]) { $code }"
    }

    private def iteratorDef() = {
        val code = iteratorCode()
        q"override def $v_iterator: $vectorIteratorClassName[$A] = $code"
    }

    private def reverseIteratorDef() = {
        val code = reverseIteratorCode()
        q"override def $v_reverseIterator: $vectorReverseIteratorClassName[$A] = $code"
    }

    //
    // SeqLike
    //

    private def applyDef() = {
        val index = TermName("index")
        val code = applyCode(q"$index")
        q"def /*SeqLike*/ $v_apply(index: Int): A = $code"
    }

    private def collPlusDef() = {
        val elem = TermName("elem")
        val code = collPlusCode(q"$elem")
        q"override def /*SeqLike*/ :+[$B >: $A, That]($elem: B)(implicit bf: CanBuildFrom[$vectorClassName[$A], $B, That]): That = $code"
    }

    //
    // IterableLike
    //

    protected def isEmptyDef() = {
        val code = isEmptyCode(q"this")
        q"override /*IterableLike*/ def $v_isEmpty: Boolean = $code"
    }


    protected def headDef() = {
        val code = headCode()
        q"override /*IterableLike*/ def $v_head: $A = $code"

    }

    protected def takeDef(): Tree = {
        val n = TermName("n")
        val code = takeCode(q"$n")
        q"override /*IterableLike*/ def $v_take($n: Int): $vectorClassName[$A] = $code"
    }

    protected def takeRightDef(): Tree = {
        val n = TermName("n")
        val code = takeRightCode(q"$n")
        q"override /*IterableLike*/ def $v_takeRight($n: Int): $vectorClassName[$A] = $code"
    }

    protected def dropDef(): Tree = {
        val n = TermName("n")
        val code = dropCode(q"$n")
        q"override /*IterableLike*/ def $v_drop($n: Int): $vectorClassName[$A] = $code"
    }

    protected def dropRightDef(): Tree = {
        val n = TermName("n")
        val code = dropRightCode(q"$n")
        q"override /*IterableLike*/ def $v_dropRight($n: Int): $vectorClassName[$A] = $code"
    }

    protected def sliceDef(): Tree = {
        val from = TermName("from")
        val until = TermName("until")
        val code = sliceCode(q"$from", q"$until")
        q"override /*IterableLike*/ def $v_slice($from: Int, $until: Int): $vectorClassName[A] = $code"
    }

    protected def splitAtDef(): Tree = {
        val n = TermName("n")
        val code = q"($v_take($n), $v_drop($n))"
        q"override /*IterableLike*/ def $v_splitAt($n: Int): ($vectorClassName[$A], $vectorClassName[$A]) = $code"
    }


    //
    // TraversableLike
    //

    protected def plusPlusDef() = {
        val that = TermName("that")
        val bf = TermName("bf")
        val code = plusPlusCode(q"$that", q"$bf")
        q"override /*TraversableLike*/ def ++[$B >: $A, That]($that: GenTraversableOnce[$B])(implicit $bf: CanBuildFrom[$vectorClassName[$A], $B, That]): That = $code"
    }

    protected def tailDef() = {
        val code = tailCode(q"this")
        q"override /*TraversableLike*/ def $v_tail: $vectorClassName[$A] = $code"
    }

    protected def lastDef() = {
        val code = lastCode(q"this")
        q"override /*TraversableLike*/ def $v_last: $A = $code"
    }

    protected def initDef() = {
        val code = initCode(q"this")
        q"override /*TraversableLike*/ def $v_init: $vectorClassName[$A] = $code"
    }


    //
    // Private methods
    //

    protected def appendedBackDef() = {
        val value = TermName("value")
        val code = appendedBackCode(q"$value")
        def assertedCode = q"val res = $code; res.$v_assertVectorInvariant(); res"
        q"private def $v_appendedBack[$B >: $A]($value: $B): $vectorClassName[$B] = ${if (useAssertions) assertedCode else code}"
    }

    protected def appendBackSetupCurrentBlockDef() = {
        val code = appendBackSetupCurrentBlockCode()
        q"private def $v_appendBackSetupCurrentBlock() = $code"
    }

    protected def appendBackSetupNewBlockDef() = {
        val code = appendBackSetupNewBlockCode()
        q"private def $v_appendBackSetupNewBlock() = $code"
    }

    protected def concatenatedDef() = {
        val that = TermName("that")
        val asserts = Seq(
            q"this.$v_assertVectorInvariant()",
            q"that.$v_assertVectorInvariant()",
            q"assert(this.$v_length > 0)",
            q"assert($that.$v_length > 0)"
        )
        val code = concatenatedCode(q"$that")
        def assertedCode = q"..$asserts; val res = $code; res.$v_assertVectorInvariant(); res"
        q"""
            private[immutable] def $v_concatenated[$B >: $A]($that: $vectorClassName[$B]): $vectorClassName[$B] = {
                ${if (useAssertions) assertedCode else code}
            }
        """
    }

    protected def rebalancedDef() = {
        val displayLeft = TermName("displayLeft")
        val concat = TermName("concat")
        val displayRight = TermName("displayRight")
        val lengthLeft = TermName("lengthLeft")
        val lengthConcat = TermName("lengthConcat")
        val lengthRight = TermName("lengthRight")
        val currentDepth = TermName("currentDepth")
        val code = rebalancedCode(q"$displayLeft", q"$concat", q"$displayRight", q"$lengthLeft", q"$lengthConcat", q"$lengthRight", q"$currentDepth")
        q"private def $v_rebalanced($displayLeft: Array[AnyRef], $concat: Array[AnyRef], $displayRight: Array[AnyRef], $lengthLeft: Int, $lengthConcat: Int, $lengthRight: Int, $currentDepth: Int): Array[AnyRef] = $code"
    }


    protected def computeNewSizesDef() = {
        val all = TermName("all")
        val alen = TermName("alen")
        val currentDepth = TermName("currentDepth")
        val code = computeNewSizesCode(q"$all", q"$alen", q"$currentDepth")
        q"private def $v_computeNewSizes($all: Array[AnyRef], $alen: Int, $currentDepth: Int) = $code"
    }

    protected def withComputedSizesDef() = {
        val node = TermName("node")
        val currentDepth = TermName("currentDepth")
        val asserts = Seq(
            q"assert($node != null)",
            q"assert(0 <= $currentDepth && $currentDepth <= 6)"
        )
        val code = withComputedSizesCode(q"$node", q"$currentDepth")
        q"""
            private def $v_withComputedSizes($node: Array[AnyRef], $currentDepth: Int): Array[AnyRef] = {
                ..${if (useAssertions) asserts else Nil}
                $code
            }
        """
    }

    protected def treeSizeDef() = {
        val tree = TermName("tree")
        val currentDepth = TermName("currentDepth")
        val asserts = Seq(
            q"assert(tree != null)",
            q"assert(0 <= $currentDepth && $currentDepth <= 6)"
        )
        val code = treeSizeCode(q"$tree", q"$currentDepth")
        q"""
            private def $v_treeSize($tree: Array[AnyRef], $currentDepth: Int): Int = {
                ..${if (useAssertions) asserts else Nil}
                $code
            }
        """
    }


    protected def copiedAcrossDef() = {
        val all = TermName("all")
        val sizes = TermName("sizes")
        val lengthSizes = TermName("lengthSizes")
        val currentDepth = TermName("currentDepth")
        val asserts = Seq(
            q"assert($all != null)",
            q"assert($lengthSizes <= $sizes.length)",
            q"assert(0 <= $currentDepth && $currentDepth <= 6)"
        )
        val code = copiedAcrossCode(q"$all", q"$sizes", q"$lengthSizes", q"$currentDepth")
        q"""
            private def $v_copiedAcross($all: Array[AnyRef], $sizes: Array[Int], $lengthSizes: Int, $currentDepth: Int): Array[AnyRef] = {
                ..${if (useAssertions) asserts else Nil}
                $code
            }
        """
    }

    //
    // Invariant
    //

    protected def assertVectorInvariantDef() = {
        val code = assertVectorInvariantCode()
        q"private[immutable] def $v_assertVectorInvariant(): Unit = $code"
    }

    protected def takeFront0Def(): Tree = {
        val n = TermName("n")
        val code = takeFront0Code(q"$n")
        val assertedCode = q"val res = $code; res.$v_assertVectorInvariant(); res"
        q"""
            private def $v_takeFront0($n: Int): $vectorClassName[$A] = {
                ${if (useAssertions) assertedCode else code}
            }
         """
    }


}

