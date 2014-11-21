package codegen
package vector
package vectorclass

import scala.reflect.runtime.universe._

trait VectorCodeGen {
    self: VectorProperties
      with vectorpointer.VectorPointerCodeGen
      with iterator.VectorIteratorCodeGen
      with reverseiterator.VectorReverseIteratorCodeGen
      with vectorobject.VectorObjectCodeGen =>

    //
    // Field names
    //

    final val v_endIndex = TermName("endIndex")
    final val v_transient = TermName("transient")

    //
    // Method names
    //

    final val v_par = TermName("par")


    // SeqLike
    final val v_apply = TermName("apply")

    // IterableLike
    final val v_isEmpty = TermName("isEmpty")
    final val v_head = TermName("head")
    final val v_take = TermName("take")
    final val v_takeRight = TermName("takeRight")
    final val v_drop = TermName("drop")
    final val v_dropRight = TermName("dropRight")
    final val v_slice = TermName("slice")
    final val v_splitAt = TermName("splitAt")
    final val v_iterator = TermName("iterator")
    final val v_reverseIterator = TermName("reverseIterator")

    final val v_length = TermName("length")
    final val v_lengthCompare = TermName("lengthCompare")

    // TraversableLike
    final val v_tail = TermName("tail")
    final val v_last = TermName("last")
    final val v_init = TermName("init")

    // Private
    private[vectorclass] val v_append = TermName("append")
    private[vectorclass] val v_appendOnCurrentBlock = TermName("appendOnCurrentBlock")
    private[vectorclass] val v_appendBackNewBlock = TermName("appendBackNewBlock")
    private[vectorclass] val v_prepend = TermName("prepend")
    private[vectorclass] val v_prependOnCurrentBlock = TermName("prependOnCurrentBlock")
    private[vectorclass] val v_prependFrontNewBlock = TermName("prependFrontNewBlock")
    private[vectorclass] val v_createSingletonVector = TermName("createSingletonVector")

    val v_normalizeAndFocusOn = TermName("normalizeAndFocusOn")
    val v_makeTransientIfNeeded = TermName("makeTransientIfNeeded")

    private[vectorclass] val v_takeFront0 = TermName("takeFront0")
    private[vectorclass] val v_dropFront0 = TermName("dropFront0")

    val v_concatenate = TermName("concatenate")
    private[vectorclass] val v_rebalanced = TermName("rebalanced")
    private[vectorclass] val v_rebalancedLeafs = TermName("rebalancedLeafs")
    private[vectorclass] val v_computeBranching = TermName("computeBranching")
    private[vectorclass] val v_computeNewSizes = TermName("computeNewSizes")

    // Debug
    val v_assertVectorInvariant = TermName("assertVectorInvariant")
    val v_debugToString = TermName("debugToString")

    //
    // Method definitions
    //

    def lengthCode() = q"$v_endIndex"

    def lengthCompareCode(len: Tree) = q"${lengthCode()} - $len"

    def iteratorCode() = {
        val it = TermName("it")
        q"""
            if (this.$v_transient) {
                this.$normalize(this.$depth)
                this.$v_transient = false
                ..${assertions(q"this.$v_assertVectorInvariant()")}
            }
            val $it = new $vectorIteratorClassName[$A](0, $endIndex)
            $it.$it_initIteratorFrom(this)
            $it
         """
    }

    def reverseIteratorCode() = {
        val rit = TermName("rit")
        q"""
            if (this.$v_transient) {
                this.$normalize(this.$depth)
                this.$v_transient = false
                ..${assertions(q"this.$v_assertVectorInvariant()")}
            }
            val $rit = new $vectorReverseIteratorClassName[$A](0, $endIndex)
            $rit.$rit_initIteratorFrom(this)
            $rit
         """
    }

    // SeqLike

    def applyCode(index: Tree) = {
        val focusStartLocal = TermName("_focusStart")
        val indexInFocus = TermName("indexInFocus")
        q"""
            val $focusStartLocal = this.$focusStart
            if ($focusStartLocal <= $index && $index < $focusEnd) {
                val $indexInFocus = $index - $focusStartLocal
                $getElement($indexInFocus, $indexInFocus ^ $focus).asInstanceOf[$A]
            } else if (0 <= $index && $index < $v_endIndex) {
                $getElementFromRoot($index).asInstanceOf[$A]
            } else {
                throw new IndexOutOfBoundsException($index.toString)
            }
         """
    }

    def collPlusCode(elem: TermName) = {
        q"""
            if (bf.eq(IndexedSeq.ReusableCBF)) {
                val _endIndex = this.$endIndex
                if (_endIndex != 0) {
                    val resultVector = new $vectorClassName[$B](_endIndex + 1)
                    resultVector.$v_transient = this.$v_transient
                    resultVector.$initWithFocusFrom(this)
                    resultVector.$v_append($elem, _endIndex)
                    ..${assertions(q"resultVector.assertVectorInvariant()")}
                    resultVector.asInstanceOf[That]
                } else {
                    $v_createSingletonVector($elem).asInstanceOf[That]
                }
            } else {
                super.:+(elem)(bf)
            }
         """
    }

    def plusCollCode(elem: TermName) = {
        q"""
            if (bf.eq(IndexedSeq.ReusableCBF)) {
                val _endIndex = this.$endIndex
                if (_endIndex != 0) {
                    val resultVector = new $vectorClassName[$B](_endIndex + 1)
                    resultVector.$v_transient = this.$v_transient
                    resultVector.$initWithFocusFrom(this)
                    resultVector.$v_prepend($elem)
                    ..${assertions(q"resultVector.assertVectorInvariant()")}
                    resultVector.asInstanceOf[That]
                } else {
                    $v_createSingletonVector($elem).asInstanceOf[That]
                }
            } else {
                super.:+(elem)(bf)
            }
         """
    }

    // IterableLike
    protected def isEmptyCode(self: Tree = q"this") = q"$self.$v_endIndex == 0"

    protected def nonEmptyCode(self: Tree = q"this") = q"$self.$v_endIndex != 0"

    protected def headCode() = {
        q"""
            if (${nonEmptyCode(q"this")}) $v_apply(0)
            else throw new UnsupportedOperationException("empty.head")
         """
    }

    protected def takeCode(n: Tree): Tree = {
        q"""
            if ($n <= 0) $vectorObjectName.$o_empty
            else if ($n < $v_endIndex) $v_takeFront0($n)
            else this
        """
    }

    protected def takeRightCode(n: Tree): Tree = {
        q"""
            if ($n <= 0) $vectorObjectName.empty
            else if ($n < $endIndex) $v_dropFront0($endIndex - $n)
            else this
         """
    }

    protected def dropCode(n: Tree): Tree = {
        q"""
            if ($n <= 0)
                this
            else if ($n < $endIndex)
                $v_dropFront0($n)
            else
                $vectorObjectName.empty
         """
    }

    protected def dropRightCode(n: Tree): Tree = {
        q"""
            if ($n <= 0) this
            else if ($n < $v_endIndex) $v_takeFront0($v_endIndex - $n)
            else $vectorObjectName.$o_empty
         """
    }

    protected def sliceCode(from: Tree, until: Tree): Tree =
        q"$v_take($until).$v_drop($from)"


    // TraversableLike

    protected def plusPlusCode(that: Tree, bf: Tree) = {
        q"""
            if ($bf eq IndexedSeq.ReusableCBF) {
                if (that.$v_isEmpty)
                    this.asInstanceOf[That]
                else if (that.isInstanceOf[$vectorClassName[$B]]) {
                    val thatVec = $that.asInstanceOf[$vectorClassName[$B]]
                    if (this.endIndex == 0)
                        thatVec.asInstanceOf[That]
                    else {
                        val newVec = new $vectorClassName(this.$v_endIndex + thatVec.$v_endIndex)
                        newVec.$initWithFocusFrom(this)
                        newVec.$v_transient = this.$v_transient
                        newVec.$v_concatenate(this.$endIndex, thatVec)
                        newVec.asInstanceOf[That]
                    }
                }
                else
                    super.++($that.seq)
            } else super.++($that.seq)
        """
    }

    protected def tailCode(self: Tree) = {
        q"""
            if (${nonEmptyCode(self)}) $self.$v_drop(1)
            else throw new UnsupportedOperationException("empty.tail")
         """
    }

    protected def lastCode(self: Tree) = {
        q"""
            if (${nonEmptyCode(self)}) $self.$v_apply($self.$v_length - 1)
            else throw new UnsupportedOperationException("empty.last")
         """
    }

    protected def initCode(self: Tree) = {
        q"""
            if (${nonEmptyCode(self)}) $v_dropRight(1)
            else throw new UnsupportedOperationException("empty.init")
         """
    }

    // Private methods

    protected def appendCode(elemParam: TermName, endIndexParam: TermName) = {
        q"""
            if /* vector focus is not focused block of the last element */ ((($focusStart + $focus) ^ ($endIndexParam - 1)) >= 32) {
                $v_normalizeAndFocusOn($endIndexParam - 1)
            }

            val elemIndexInBlock = ($endIndexParam - $focusStart) & $blockMask
            if /* if next element will go in current block position */ (elemIndexInBlock != 0) {
                $v_appendOnCurrentBlock($elemParam, elemIndexInBlock)
            } else /* next element will go in a new block position */ {
                $v_appendBackNewBlock($elemParam, elemIndexInBlock)
            }
         """
    }

    protected def appendOnCurrentBlockCode(elemParam: TermName, elemIndexInBlockParam: TermName) = {
        q"""
            $focusEnd = $endIndex
            val d0 = new Array[AnyRef]($elemIndexInBlockParam + 1)
            System.arraycopy(${displayAt(0)}, 0, d0, 0, $elemIndexInBlockParam)
            d0($elemIndexInBlockParam) = elem.asInstanceOf[AnyRef]
            ${displayAt(0)} = d0
            $v_makeTransientIfNeeded()
            ..${assertions(q"$v_assertVectorInvariant()")}
         """
    }

    protected def appendBackNewBlockCode(elemParam: TermName, elemIndexInBlockParam: TermName) = {
        q"""
            val oldDepth = $depth
            val newRelaxedIndex = ($endIndex - 1) - $focusStart + $focusRelax
            val focusJoined = $focus | $focusRelax
            val xor = newRelaxedIndex ^ focusJoined
            val _transient = $v_transient
            $setupNewBlockInNextBranch(xor, _transient)
            if /* setupNewBlockInNextBranch(...) increased the depth of the tree */ (oldDepth == $depth) {
                var i = ${ifInLevel(q"xor", 1 to maxTreeLevel, lvl => q"${lvl + 1}", q"${maxTreeLevel + 1}")}
                if (i < oldDepth) {
                    val _focusDepth = focusDepth
                    var display: Array[AnyRef] = ${matchOnInt(q"i", 2 to maxTreeLevel, lvl => displayAt(lvl))}
                    do {
                        val displayLen = display.length - 1
                        val newSizes: Array[Int] =
                            if (i >= _focusDepth) {
                                $makeTransientSizes(display(displayLen).asInstanceOf[Array[Int]], displayLen - 1)
                            } else null

                        val newDisplay = new Array[AnyRef](display.length)
                        System.arraycopy(display, 0, newDisplay, 0, displayLen - 1)
                        if (i >= _focusDepth)
                            newDisplay(displayLen) = newSizes

                        ${
            matchOnInt(q"i", 2 to maxTreeLevel, lvl =>
                q"""
              ${displayAt(lvl)} = newDisplay
              ..${if (lvl == maxTreeLevel) Nil else q"display = ${displayAt(lvl + 1)}" :: Nil}
           """
            )
        }
                        i += 1
                    } while (i < oldDepth)
                }
            }

            if (oldDepth == $focusDepth)
                $initFocus($endIndex - 1, 0, $endIndex, $depth, 0)
            else
                $initFocus($endIndex - 1, $endIndex - 1, $endIndex, 1, newRelaxedIndex & ${~blockMask})

            ${displayAt(0)}($elemIndexInBlockParam) = $elemParam.asInstanceOf[AnyRef]
            $v_transient = true
            ..${assertions(q"this.$v_assertVectorInvariant()")}
         """
    }

    protected def prependCode(elemParam: TermName) = {
        q"""
            if (focusStart != 0 || (focus & ${~blockMask}) != 0) {
                /* the current focused block is not on the left most leaf block of the vector */
                $v_normalizeAndFocusOn(0)
            }
            val d0 = ${displayAt(0)}
            if /* element fits in current block */ (d0.length < $blockWidth) {
                $v_prependOnCurrentBlock($elemParam, d0)
            } else {
                $v_prependFrontNewBlock($elemParam)
            }
            ..${assertions(q"this.$v_assertVectorInvariant()")}
         """
    }

    protected def prependOnCurrentBlockCode(elemParam: TermName, oldD0: TermName) = {
        q"""
            val newLen = $oldD0.length + 1
            $focusEnd = newLen
            val newD0 = new Array[AnyRef](newLen)
            newD0(0) = $elemParam.asInstanceOf[AnyRef]
            System.arraycopy($oldD0, 0, newD0, 1, newLen - 1)
            ${displayAt(0)} = newD0
            $v_makeTransientIfNeeded()
            ..${assertions(q"this.$v_assertVectorInvariant()")}
         """
    }

    protected def prependFrontNewBlockCode(elemParam: TermName) = {
        q"""
            ..${assertions(q"${displayAt(0)}.length == $blockWidth")}

            var currentDepth = $focusDepth
            if (currentDepth == 1)
                currentDepth += 1
            var display = ${
            matchOnInt(q"currentDepth", 1 to maxTreeDepth, dep =>
                if (dep == 1) q"currentDepth = 2; ${displayAt(1)}"
                else displayAt(dep - 1)
            )
        }

            while /* the insertion depth has not been found */ (display != null && display.length == ${blockWidth + 1}) {
                currentDepth += 1
                ${matchOnInt(q"currentDepth", 2 to maxTreeDepth, dep => q"display = ${displayAt(dep - 1)}", Some(q"throw new IllegalStateException"))}
            }

            val oldDepth = $depth
            val _transient = $v_transient

            // create new node at this depth and all singleton nodes under it on left most branch
            setupNewBlockInInitBranch(currentDepth, _transient)

            // update sizes of nodes above the insertion depth
            if /* setupNewBlockInNextBranch(...) increased the depth of the tree */ (oldDepth == $depth) {
                var i = currentDepth
                if (i < oldDepth) {
                    val _focusDepth = $focusDepth
                    var display: Array[AnyRef] = ${matchOnInt(q"i", 2 to maxTreeLevel, lvl => displayAt(lvl))}
                    do {
                        val displayLen = display.length - 1
                        val newSizes: Array[Int] =
                            if (i >= _focusDepth) {
                                $makeTransientSizes(display(displayLen).asInstanceOf[Array[Int]], 1)
                            } else null

                        val newDisplay = new Array[AnyRef](display.length)
                        System.arraycopy(display, 0, newDisplay, 0, displayLen - 1)
                        if (i >= _focusDepth)
                            newDisplay(displayLen) = newSizes

                        ${
            matchOnInt(q"i", 2 to maxTreeLevel, lvl =>
                q"""
                    ${displayAt(lvl)} = newDisplay
                    ..${if (lvl < maxTreeLevel) q"display = ${displayAt(lvl + 1)}" :: Nil else Nil}
                 """
            )
        }
                        i += 1
                    } while (i < oldDepth)
                }
            }
            $initFocus(0, 0, 1, 1, 0)
            ${displayAt(0)}(0) = $elemParam.asInstanceOf[AnyRef]
            $v_transient = true
         """
    }

    protected def createSingletonVectorCode(elemParam: TermName) = {
        q"""
            val resultVector = new $vectorClassName[$B](1)
            resultVector.$initSingleton($elemParam)
            ..${assertions(q"resultVector.$v_assertVectorInvariant()")}
            resultVector
         """
    }

    protected def normalizeAndFocusOnCode(index: TermName) = {
        q"""
            if ($v_transient) {
                $normalize($depth)
                $v_transient = false
            }
            $focusOn($index)
         """
    }

    protected def makeTransientIfNeededCode() = {
        q"""
            val _depth = $depth
            if (_depth > 1 && !$v_transient) {
                $copyDisplaysAndNullFocusedBranch(_depth, $focus | $focusRelax)
                $v_transient = true
            }
         """
    }

    protected def takeFront0Code(n: TermName) = {
        val d0len = TermName("d0len")
        q"""
            if ($v_transient) {
                $normalize($depth)
                $v_transient = false
            }

            val vec = new $vectorClassName[$A]($n)
            vec.$initWithFocusFrom(this)

            if ($depth > 1) {
                vec.$focusOn($n - 1)
                val $d0len = (vec.$focus & $blockMask) + 1
                if ($d0len != $blockWidth) {
                    val d0 = new Array[AnyRef]($d0len)
                    System.arraycopy(vec.${displayNameAt(0)}, 0, d0, 0, $d0len)
                    vec.${displayNameAt(0)} = d0
                }
                val cutIndex = vec.$focus | vec.$focusRelax
                vec.$cleanTopTake(cutIndex)
                vec.$focusDepth = math.min(vec.$depth, vec.$focusDepth)
                if (vec.$depth > 1) {
                    vec.$copyDisplays(vec.$focusDepth, cutIndex)
                    var i = vec.$depth
                    var offset = 0
                    var display: Array[AnyRef] = null
                    while (i > vec.$focusDepth) {
                        ${matchOnInt(q"i", 2 to maxTreeDepth, d => q"display = vec.${displayNameAt(d - 1)}")}
                        val oldSizes = ${getBlockSizes(q"display")}
                        val newLen = ((vec.$focusRelax >> ($blockIndexBits * (i - 1))) & $blockMask) + 1
                        val newSizes = new Array[Int](newLen)
                        System.arraycopy(oldSizes, 0, newSizes, 0, newLen - 1)
                        newSizes(newLen - 1) = $n - offset
                        if (newLen > 1)
                            offset += newSizes(newLen - 2)

                        val newDisplay = new Array[AnyRef](newLen + 1)
                        System.arraycopy(display, 0, newDisplay, 0, newLen)
                        newDisplay(newLen - 1) = null
                        newDisplay(newLen) = newSizes

                        ${matchOnInt(q"i", 2 to maxTreeDepth, d => q"vec.${displayNameAt(d - 1)} = newDisplay")}
                        i -= 1
                    }
                    vec.$stabilizeDisplayPath(vec.$depth, cutIndex)
                    vec.$focusEnd = $n
                } else {
                    vec.$focusEnd = $n
                }
            } else if ($n != $blockWidth) {
                val d0 = new Array[AnyRef]($n)
                System.arraycopy(vec.${displayNameAt(0)}, 0, d0, 0, $n)
                vec.${displayNameAt(0)} = d0
                vec.$initFocus(0, 0, $n, 1, 0)
            }

            ..${assertions(q"vec.$v_assertVectorInvariant()")}
            vec
         """
    }

    protected def dropFront0Code(n: TermName) = {
        q"""
            if ($v_transient) {
                $normalize($depth)
                $v_transient = false
            }

            val vec = new $vectorClassName[$A](this.$endIndex - $n)
            vec.$initWithFocusFrom(this)
            if (vec.$depth > 1) {
                vec.$focusOn($n)
                val cutIndex = vec.$focus | vec.$focusRelax
                val d0Start = cutIndex & $blockMask
                if (d0Start != 0) {
                    val d0len = vec.${displayNameAt(0)}.length - d0Start
                    val d0 = new Array[AnyRef](d0len)
                    System.arraycopy(vec.${displayNameAt(0)}, d0Start, d0, 0, d0len)
                    vec.${displayNameAt(0)} = d0
                }

                vec.$cleanTopDrop(cutIndex)
                if (vec.$depth > 1) {
                    var i = 2
                    var display = vec.${displayNameAt(1)}
                    while (i <= vec.$depth) {
                        val splitStart = (cutIndex >> ($blockIndexBits * (i - 1))) & $blockMask
                        val newLen = display.length - splitStart - 1
                        val newDisplay = new Array[AnyRef](newLen + 1)
                        System.arraycopy(display, splitStart + 1, newDisplay, 1, newLen - 1)
                        ${
            matchOnInt(q"i", 2 to maxTreeDepth, dep =>
                q"""
                    newDisplay(0) = vec.${displayNameAt(dep - 2)}
                    vec.${displayNameAt(dep - 1)} = $withComputedSizes(newDisplay, $dep)
                    ..${if (dep < maxTreeDepth) q"display = vec.${displayNameAt(dep)}" :: Nil else Nil}
                 """
            )
        }
                        i += 1
                    }
                }
                // May not be optimal, but most of the time it will be
                vec.$initFocus(0, 0, vec.${displayNameAt(0)}.length, 1, 0)
            } else {
                val newLen = vec.${displayNameAt(0)}.length - $n
                val d0 = new Array[AnyRef](newLen)
                System.arraycopy(vec.${displayNameAt(0)}, $n, d0, 0, newLen)
                vec.${displayNameAt(0)} = d0
                vec.$initFocus(0, 0, newLen, 1, 0)
            }
            ..${assertions(q"vec.$v_assertVectorInvariant()")}
            vec
         """
    }


    def concatenateCode(currentSize: TermName, that: TermName) = {
        def concatenateOn(maxDepth: Int): Tree = {
            if (maxDepth == 1) {
                q"""
                    val concat = $v_rebalancedLeafs(${displayAt(0)}, $that.${displayNameAt(0)}, isTop = true)
                    $initFromRoot(concat, if ($endIndex <= $blockWidth) 1 else 2)
                 """
            } else {
                def localDisplay(i: Int) = TermName("d" + i)
                q"""
                    ..${(0 until maxDepth) map (i => q"var ${localDisplay(i)}: Array[AnyRef] = null")}
                    if (($that.$focus & ${~blockMask}) == 0) {
                        ..${(maxDepth - 1 to 0 by -1) map (i => q"${localDisplay(i)} = that.${displayNameAt(i)}")}
                    } else {
                        if ($that.${displayNameAt(maxDepth - 1)} != null)
                            ${localDisplay(maxDepth - 1)} = $that.${displayNameAt(maxDepth - 1)}
                        ..${(maxDepth - 2 to 0 by -1) map (i => q"if (${localDisplay(i + 1)} == null) ${localDisplay(i)} = $that.${displayNameAt(i)} else ${localDisplay(i)} = ${localDisplay(i + 1)}(0).asInstanceOf[Array[AnyRef]]")}
                    }
                    var concat: Array[AnyRef] = rebalancedLeafs(this.${displayNameAt(0)}, ${localDisplay(0)}, isTop = false)
                    ..${(2 until maxDepth) map (i => q"concat = $v_rebalanced(this.${displayNameAt(i - 1)}, concat, ${localDisplay(i - 1)}, $i)")}
                    concat = rebalanced(this.${displayNameAt(maxDepth - 1)}, concat, $that.${displayNameAt(maxDepth - 1)}, $maxDepth)
                    if (concat.length == ${1 + blockInvariants})
                        $initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], $maxDepth)
                    else
                        $initFromRoot($withComputedSizes(concat, ${maxDepth + 1}), ${maxDepth + 1})
                 """
            }
        }
        q"""
            ..${assertions(q"$that.$v_assertVectorInvariant()", q"0 < $that.length")}
            if (this.$v_transient) {
                this.$normalize(this.$depth)
                this.$v_transient = false
            }
            if ($that.$v_transient) {
                $that.$normalize(that.$depth)
                $that.$v_transient = false
            }
            ..${assertions(q"$that.assertVectorInvariant()")}
            this.$focusOn($currentSize - 1)
            ${matchOnInt(q"math.max(this.$depth, $that.$depth)", 1 to maxTreeDepth, concatenateOn, Some(q"throw new IllegalStateException"))}
            ..${assertions(q"this.$v_assertVectorInvariant()")}
         """
    }

    protected def rebalancedCode(displayLeft: Tree, concat: Tree, displayRight: Tree, currentDepth: Tree) = {
        // TODO Check: has changes
        val leftLength = TermName("leftLength")
        val concatLength = TermName("concatLength")
        val rightLength = TermName("rightLength")

        val lengthSizes = TermName("nalen")
        val sizes = TermName("sizes")
        val branching = TermName("branching")

        def computeSizes: Seq[Tree] = {
            if (useCompleteRebalance)
                Seq(q"val $branching = $v_computeBranching($displayLeft, $concat, $displayRight, $currentDepth)")
            else {
                Seq(q"val tup = $v_computeNewSizes($displayLeft, $concat, $displayRight, $currentDepth)",
                    q"val $sizes: Array[Int] = tup._1",
                    q"val $lengthSizes: Int = tup._2")
            }
        }
        q"""
            val $leftLength = if($displayLeft==null) 0 else ($displayLeft.length - $blockInvariants)
            val $concatLength = if($concat==null) 0 else ($concat.length - $blockInvariants)
            val $rightLength = if($displayRight==null) 0 else ($displayRight.length - $blockInvariants)

            ..$computeSizes

            val top = new Array[AnyRef](${if (useCompleteRebalance) q"($branching >> ${2 * blockIndexBits}) + (if(($branching & ${blockMask | (blockMask << blockIndexBits)}) == 0) $blockInvariants else ${blockInvariants + 1})" else q"($lengthSizes >> $blockIndexBits) + (if (($lengthSizes & $blockMask) == 0) 1 else 2)"})

            var mid = new Array[AnyRef](${if (useCompleteRebalance) q"(if (($branching >> ${2 * blockIndexBits}) == 0) (($branching + ${blockWidth - 1}) >> $blockIndexBits) + $blockInvariants else ${blockWidth + blockInvariants})" else q"(if($lengthSizes <= $blockWidth) $lengthSizes else $blockWidth) + $blockInvariants"})
            var bot: Array[AnyRef] = null

            var iSizes = 0
            var iTop = 0
            var iMid = 0
            var iBot = 0
            var i = 0
            var j = 0
            var d = 0

            var currentDisplay: Array[AnyRef] = null
            var displayEnd = 0

            do {
                d match {
                    case 0 =>
                        if ($displayLeft != null) {
                            currentDisplay = $displayLeft
                            if ($concat == null) displayEnd = $leftLength else displayEnd = $leftLength - 1
                        }
                    case 1 =>
                        if ($concat == null) {
                            displayEnd = 0
                        } else {
                            currentDisplay = concat
                            displayEnd = $concatLength
                        }
                        i = 0
                    case 2 =>
                        if ($displayRight != null) {
                            currentDisplay = $displayRight
                            displayEnd = $rightLength
                            if ($concat == null) i = 0 else i = 1
                        }
                }

                while (i < displayEnd) {
                    val displayValue = currentDisplay(i).asInstanceOf[Array[AnyRef]]
                    val displayValueEnd = if (currentDepth == 2) displayValue.length else displayValue.length - $blockInvariants
                    if ( /* iBot==0 && j==0 */ (iBot | j) == 0 && displayValueEnd == ${if (useCompleteRebalance) q"$blockWidth" else q"$sizes(iSizes)"}) {
                        if ($currentDepth != 2 && bot != null) {
                            $withComputedSizes(bot, currentDepth - 1)
                            bot = null
                        }
                        mid(iMid) = displayValue
                        i += 1
                        iMid += 1
                        iSizes += 1
                    } else {
                        val numElementsToCopy = math.min(displayValueEnd - j, ${if (useCompleteRebalance) q"$blockWidth" else q"$sizes(iSizes)"} - iBot)
                        if (iBot == 0) {
                            if ($currentDepth!=2 && bot!=null)
                                $withComputedSizes(bot, currentDepth - 1)
                            bot = new Array[AnyRef](${if (useCompleteRebalance) q"math.min($branching - (iTop << ${2 * blockIndexBits}) - (iMid << $blockIndexBits), $blockWidth)" else q"$sizes(iSizes)"} + (if ($currentDepth == 2) 0 else $blockInvariants))
                            mid(iMid) = bot
                        }
                        System.arraycopy(displayValue, j, bot, iBot, numElementsToCopy)
                        j += numElementsToCopy
                        iBot += numElementsToCopy
                        if (j == displayValueEnd) {
                            i += 1
                            j = 0
                        }
                        if (iBot == ${if (useCompleteRebalance) q"$blockWidth" else q"$sizes(iSizes)"}) {
                            iMid += 1
                            iBot = 0
                            iSizes += 1
                            if ($currentDepth!=2 && bot!=null)
                                $withComputedSizes(bot, currentDepth - 1)
                        }
                    }
                    if (iMid == $blockWidth) {
                        top(iTop) = if (currentDepth == 1) $withComputedSizes1(mid) else $withComputedSizes(mid, currentDepth)
                        iTop += 1
                        iMid = 0
                        ..${
            if (useCompleteRebalance) Seq(
                q"val remainingBranches = $branching - ((((iTop << $blockIndexBits) | iMid) << $blockIndexBits) | iBot)",
                q"""
                    if (remainingBranches > 0) {
                        mid = new Array[AnyRef](${q"if ((remainingBranches >> ${2 * blockIndexBits}) == 0) ((remainingBranches + ${blockWidth - 1 + (blockInvariants << blockIndexBits)}) >> $blockIndexBits) else ${blockWidth + blockInvariants}"} )
                    } else {
                        mid = null
                    }
                 """
            )
            else Seq(
                q"""
                    if ( $lengthSizes - (iTop << $blockIndexBits) != 0 ) {
                        mid = new Array[AnyRef](math.min($lengthSizes - (iTop << $blockIndexBits) + $blockInvariants, ${blockWidth + blockInvariants}) )
                    } else {
                        mid = null
                    }
                 """
            )
        }
                    }
                }
                d += 1
            } while (d < 3)
            if ($currentDepth!=2 && bot!=null)
                $withComputedSizes(bot, currentDepth - 1)

            if(mid != null) {
                top(iTop) = if (currentDepth == 1) $withComputedSizes1(mid) else $withComputedSizes(mid, currentDepth)
            }
            top
         """
    }

    protected def rebalancedLeafsCode(displayLeft: Tree, displayRight: Tree, isTop: TermName) = {
        val leftLength = TermName("leftLength")
        val rightLength = TermName("rightLength")
        q"""
            val $leftLength = $displayLeft.length
            val $rightLength = $displayRight.length
            if ($leftLength == $blockWidth) {
               val top = new Array[AnyRef](${2 + blockInvariants})
               top(0) = $displayLeft
               top(1) = $displayRight
               top
            } else if ($leftLength + $rightLength <= $blockWidth) {
                val mergedDisplay = new Array[AnyRef]($leftLength + $rightLength)
                System.arraycopy($displayLeft, 0, mergedDisplay, 0, $leftLength)
                System.arraycopy($displayRight, 0, mergedDisplay, $leftLength, $rightLength)
                if ($isTop) {
                    mergedDisplay
                } else {
                    val top = new Array[AnyRef](${1 + blockInvariants})
                    top(0) = mergedDisplay
                    top
                }
            } else {
                val top = new Array[AnyRef](${2 + blockInvariants})
                val arr0 = new Array[AnyRef]($blockWidth)
                val arr1 = new Array[AnyRef]($leftLength + $rightLength - $blockWidth)
                top(0) = arr0
                top(1) = arr1
                System.arraycopy($displayLeft, 0, arr0, 0, $leftLength)
                System.arraycopy($displayRight, 0, arr0, $leftLength, $blockWidth - $leftLength)
                System.arraycopy($displayRight, $blockWidth - $leftLength, arr1, 0,  $rightLength - $blockWidth + $leftLength)
                top
            }
         """
    }

    protected def computeNewSizesCode(displayLeft: Tree, concat: Tree, displayRight: Tree, currentDepth: Tree) = {
        val leftLength = TermName("leftLength")
        val concatLength = TermName("concatLength")
        val rightLength = TermName("rightLength")
        q"""
            val $leftLength = if ($displayLeft == null) 0 else ($displayLeft.length - $blockInvariants)
            val $concatLength = if ($concat == null) 0 else $concat.length - $blockInvariants
            val $rightLength = if ($displayRight == null) 0 else ($displayRight.length - $blockInvariants)
            var szsLength = $leftLength + $concatLength + $rightLength
            if ($leftLength != 0) szsLength -= 1
            if ($rightLength != 0) szsLength -= 1
            val szs = new Array[Int](szsLength)
            var totalCount = 0
            var i = 0
            while (i < $leftLength - 1) {
                val sz = if ($currentDepth == 1) 1
                         else if ($currentDepth == 2) $displayLeft(i).asInstanceOf[Array[AnyRef]].length
                         else $displayLeft(i).asInstanceOf[Array[AnyRef]].length - 1
                szs(i) = sz
                totalCount += sz
                i += 1
            }
            val offset1 = i
            i = 0
            while (i < $concatLength) {
                val sz = if ($currentDepth == 1) 1
                         else if ($currentDepth == 2) $concat(i).asInstanceOf[Array[AnyRef]].length
                         else $concat(i).asInstanceOf[Array[AnyRef]].length - 1
                szs(offset1 + i) = sz
                totalCount += sz
                i += 1
            }
            val offset2 = offset1 + i - 1
            i = 1
            while (i < $rightLength) {
                val sz = if ($currentDepth == 1) 1
                         else if ($currentDepth == 2) $displayRight(i).asInstanceOf[Array[AnyRef]].length
                         else $displayRight(i).asInstanceOf[Array[AnyRef]].length - 1
                szs(offset2 + i) = sz
                totalCount += sz
                i += 1
            }

            // COMPUTE NEW SIZES
            // Calculate the ideal or effective number of slots
            // used to limit number of extra slots.
            val effectiveNumberOfSlots = totalCount / 32 + 1 // <-- "desired" number of slots???

            val MinWidth = ${
            blockWidth - 1
        } // min number of slots allowed...

            // note - this makes multiple passes, can be done in one.
            // redistribute the smallest slots until only the allowed extras remain
            val EXTRAS = 2
            while (szsLength > effectiveNumberOfSlots + EXTRAS) {
                // TR each loop iteration removes the first short block
                // TR what if no small one is found? doesn't check ix < szs.length,
                // TR we know there are small ones. what if the small ones are all at the right?
                // TR how do we know there is (enough) stuff right of them to balance?

                var ix = 0
                // skip over any blocks large enough
                while (szs(ix) > MinWidth) ix += 1

                // Found a short one so redistribute over following ones
                var el = szs(ix) // current size <= MinWidth
                do {
                    val msz = math.min(el + szs(ix + 1), $blockWidth)
                    szs(ix) = msz
                    el = el + szs(ix + 1) - msz

                    ix += 1
                } while (el > 0)

                // shuffle up remaining slot sizes
                while (ix < szsLength - 1) {
                    szs(ix) = szs(ix + 1)
                    ix += 1
                }
                szsLength -= 1
            }

            (szs, szsLength)
        """
    }

    protected def computeBranchingCode(displayLeft: Tree, concat: Tree, displayRight: Tree, currentDepth: Tree) = {
        val leftLength = TermName("leftLength")
        val concatLength = TermName("concatLength")
        val rightLength = TermName("rightLength")
        val branching = TermName("branching")
        q"""
            val $leftLength = if ($displayLeft == null) 0 else ($displayLeft.length - $blockInvariants)
            val $concatLength = if ($concat == null) 0 else $concat.length - $blockInvariants
            val $rightLength = if ($displayRight == null) 0 else ($displayRight.length - $blockInvariants)
            var $branching = 0
            if ($currentDepth == 1) {
                $branching = $leftLength + $concatLength + $rightLength
                if ($leftLength != 0) $branching -= 1
                if ($rightLength != 0) $branching -= 1
            } else {
                var i = 0
                while (i < $leftLength - 1) {
                    $branching += $displayLeft(i).asInstanceOf[Array[AnyRef]].length
                    i += 1
                }
                i = 0
                while (i < $concatLength) {
                    $branching += $concat(i).asInstanceOf[Array[AnyRef]].length
                    i += 1
                }
                i = 1
                while (i < $rightLength) {
                    $branching += $displayRight(i).asInstanceOf[Array[AnyRef]].length
                    i += 1
                }
                if ($currentDepth != 2) {
                    $branching -= $leftLength + $concatLength + $rightLength
                    if ($leftLength != 0) $branching += 1
                    if ($rightLength != 0) $branching += 1
                }
            }
            $branching
        """
    }

    protected def assertVectorInvariantCode() = {
        def checkThatDisplayDefinedIffBelowDepth(lvl: Int) = {
            val p1 = q"($depth <= $lvl && ${displayAt(lvl)} == null)"
            val p2 = q"($depth > 0 && ${displayAt(lvl)} != null)"
            val str = s"<=$lvl <==> display$lvl==null "
            q"""assert($p1 || $p2, $depth.toString +: $str :+ ($depth, ${displayAt(lvl)}))"""
        }
        def checkDisplayIsCoherentWithTree(lvl: Int, transient: Boolean) = {
            q"""
                if (${displayAt(lvl)} != null) {
                    assert(${displayAt(lvl - 1)} != null)
                    if ($focusDepth <= $lvl)
                        assert(${displayAt(lvl)}(($focusRelax >> ${lvl * blockIndexBits}) & $blockMask) == ${if (!transient) displayAt(lvl - 1) else q"null"})
                    else
                        assert(${displayAt(lvl)}(($focus >> ${lvl * blockIndexBits}) & $blockMask) == ${if (!transient) displayAt(lvl - 1) else q"null"})
                }
             """
        }

        q"""
            assert(0 <= $depth && $depth <= $maxTreeDepth, $depth)

            assert($v_isEmpty == ($depth == 0), ($v_isEmpty, $depth))
            assert($v_isEmpty == ($v_length == 0), ($v_isEmpty, $v_length))
            assert($v_length == $endIndex, ($v_length, $endIndex))

            ..${(0 to maxTreeLevel) map checkThatDisplayDefinedIffBelowDepth}

            if(!$v_transient) {
                ..${(maxTreeLevel to 1 by -1) map (lvl => checkDisplayIsCoherentWithTree(lvl, transient = false))}
            } else {
                assert($depth > 1)
                ..${(maxTreeLevel to 1 by -1) map (lvl => checkDisplayIsCoherentWithTree(lvl, transient = true))}
            }

            assert(0 <= $focusStart && $focusStart <= $focusEnd && $focusEnd <= $v_endIndex, ($focusStart, $focusEnd, $v_endIndex))
            assert($focusStart == $focusEnd || $focusEnd != 0, "focusStart==focusEnd ==> focusEnd==0" +($focusStart, $focusEnd))
            assert(0 <= $focusDepth && $focusDepth <= $depth, ($focusDepth, $depth))

            ${checkSizesDef()}

            ${matchOnInt(q"$depth", 1 to maxTreeDepth, i => q"checkSizes(${displayAt(i - 1)}, $i, $v_endIndex)", Some(q"()"))}

            true
        """


    }

    private def checkSizesDef() = {
        q"""
            def checkSizes(node: Array[AnyRef], currentDepth: Int, _endIndex: Int): Unit = {
                if (currentDepth > 1) {
                    if (node != null) {
                        val sizes = node(node.length - 1).asInstanceOf[Array[Int]]
                        if (sizes != null) {
                            assert(node.length == sizes.length + 1)
                            if (!$v_transient)
                                assert(sizes(sizes.length - 1) == _endIndex, (sizes(sizes.length - 1), _endIndex))

                            var i = 0
                            while (i < sizes.length - 1) {
                                checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth - 1, sizes(i) - (if (i == 0) 0 else sizes(i - 1)))
                                i += 1
                            }
                            checkSizes(node(node.length - 2).asInstanceOf[Array[AnyRef]], currentDepth - 1, if (sizes.length > 1) sizes(sizes.length - 1) - sizes(sizes.length - 2) else sizes(sizes.length - 1))
                        } else {
                            var i = 0
                            while (i < node.length - 2) {
                                checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth - 1, 1 << ($blockIndexBits * (currentDepth - 1)))
                                i += 1
                            }
                            val expectedLast = _endIndex - (1 << ($blockIndexBits * (currentDepth - 1))) * (node.length - ${1 + blockInvariants})
                            assert(1 <= expectedLast && expectedLast <= (1 << ($blockIndexBits * currentDepth)))
                            checkSizes(node(node.length - ${1 + blockInvariants}).asInstanceOf[Array[AnyRef]], currentDepth - 1, expectedLast)
                        }
                    } else {
                        assert($v_transient)
                    }
                } else if (node != null) {
                    assert(node.length == _endIndex)
                } else {
                    assert($v_transient)
                }
            }
        """
    }

    protected def debugToStringCode() = {
        // TODO Check
        q"""
            val sb = new StringBuilder
            sb append "RRBVector ("
            ..${0 to maxTreeLevel map (lvl=> q"""sb append ("\t" + ${"display"+lvl} + " = " + ${displayAt(lvl)} + (if(${displayAt(lvl)} != null) ${displayAt(lvl)}.mkString("[", ", ", "]") else "") + "\n")""")}
            sb append ("\tdepth = " + $depth + "\n")
            sb append ("\tendIndex = " + $endIndex + "\n")
            sb append ("\tfocus = " + $focus + "\n")
            sb append ("\tfocusStart = " + $focusStart + "\n")
            sb append ("\tfocusEnd = " + $focusEnd + "\n")
            sb append ("\tfocusRelax = " + $focusRelax + "\n")
            sb append ("\ttransient = " + $v_transient + "\n")
            sb append ")"
            sb.toString
         """
    }

}
