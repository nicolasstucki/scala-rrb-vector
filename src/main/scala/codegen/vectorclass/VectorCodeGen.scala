package codegen.vectorclass

import codegen.vectoriterator.VectorIteratorCodeGen
import codegen.vectorobject.VectorObjectCodeGen
import codegen.vectorpointer.VectorPointerCodeGen
import codegen.VectorProperties
import codegen.vectorreverseiterator.VectorReverseIteratorCodeGen


import scala.reflect.runtime.universe._

trait VectorCodeGen {
    self: VectorProperties
      with VectorPointerCodeGen
      with VectorIteratorCodeGen
      with VectorReverseIteratorCodeGen
      with VectorObjectCodeGen =>

    //
    // Field names
    //

    val v_endIndex = TermName("endIndex")
    val v_dirty = TermName("dirty")

    //
    // Method names
    //

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
    val v_appendBackSetupNewBlock = TermName("appendBackSetupNewBlock")
    val v_takeFront0 = TermName("takeFront0")

    val v_concatenate = TermName("concatenate")
    val v_rebalanced = TermName("rebalanced")
    val v_rebalancedLeafs = TermName("rebalancedLeafs")
    val v_computeNewSizes = TermName("computeNewSizes")
    val v_computeBranching = TermName("computeBranching")
    val v_withComputedSizes = TermName("withComputedSizes")
    val v_treeSize = TermName("treeSize")

    val v_assertVectorInvariant = TermName("assertVectorInvariant")

    // Private[immutable]
    val v_initIterator = TermName("initIterator")

    //
    // Method definitions
    //

    def lengthCode() = q"$v_endIndex"

    def lengthCompareCode(len: Tree) = q"${lengthCode()} - $len"

    def initIteratorCode(it: Tree) =
        q"""
            if (this.$v_dirty) {
                this.$stabilize()
                this.$v_dirty = false
            }
            $it.$initWithFocusFrom(this)
            $it.$it_resetIterator()
         """

    def initReverseIteratorCode(rit: Tree) =
        q"""
            if (this.$v_dirty) {
                this.$stabilize()
                this.$v_dirty = false
            }
            $rit.$initWithFocusFrom(this)
            $rit.$rit_resetIterator()
         """

    def iteratorCode() = {
        val it = TermName("it")
        q"""
            val $it = new $vectorIteratorClassName[$A](0, $v_endIndex)
            this.$v_initIterator($it)
            $it
         """
    }

    def reverseIteratorCode() = {
        val it = TermName("it")
        q"""
            val $it = new $vectorReverseIteratorClassName[$A](0, $v_endIndex)
            this.$v_initIterator($it)
            $it
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
            if (bf eq IndexedSeq.ReusableCBF) ${appendedBackCode(elem)}.asInstanceOf[That] // just ignore bf
            else super.:+($elem)(bf)
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
        q"super.$v_takeRight($n)"

    }

    protected def dropCode(n: Tree): Tree = {
        q"super.$v_drop($n)"
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
                    if (this.$v_isEmpty)
                        thatVec.asInstanceOf[That]
                    else {
                        val newVec = new $vectorClassName(this.$v_endIndex + thatVec.$v_endIndex)
                        newVec.$initWithFocusFrom(this)
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

    protected def appendedBackCode(value: TermName) = {
        val resultVector = TermName("resultVector")
        val localEndIndex = TermName("_endIndex")
        val elemIndexInBlock = TermName("elemIndexInBlock")
        q"""
            if (this.$v_endIndex == 0) {
                val $resultVector = new $vectorClassName[$B](1)
                $resultVector.$initSingleton($value)
                $resultVector
            } else {
                val $localEndIndex = this.$endIndex
                val $resultVector = new $vectorClassName[$B]($localEndIndex + 1)
                $resultVector.$initWithFocusFrom(this)
                $resultVector.$v_dirty = this.$v_dirty
                if ((($localEndIndex - 1 - this.$focusStart) ^ this.$focus) >= $blockWidth) {
                    if ($resultVector.$v_dirty) {
                        $resultVector.$stabilize()
                        $resultVector.$v_dirty = false
                    }
                    $resultVector.$focusOn($localEndIndex - 1)
                }
                val $elemIndexInBlock = ($localEndIndex - $resultVector.$focusStart) & $blockMask
                if ($elemIndexInBlock == 0) {
                    if ($resultVector.$v_dirty) {
                        $resultVector.$stabilize()
                        $resultVector.$v_dirty = false
                    }
                    $resultVector.$v_appendBackSetupNewBlock()
                    $resultVector.${displayNameAt(0)}($elemIndexInBlock) = $value.asInstanceOf[AnyRef]
                    ..${assertions(q"$resultVector.$v_assertVectorInvariant()")}
                    $resultVector
                } else {
                    $resultVector.$v_dirty = true
                    $resultVector.$focusEnd = $resultVector.$endIndex
                    val d0 = new Array[AnyRef]($elemIndexInBlock + 1)
                    Platform.arraycopy($resultVector.${displayNameAt(0)}, 0, d0, 0, $elemIndexInBlock)
                    d0($elemIndexInBlock) = $value.asInstanceOf[AnyRef]
                    $resultVector.${displayNameAt(0)} = d0
                    ..${assertions(q"$resultVector.$v_assertVectorInvariant()")}
                    $resultVector
                }
            }
         """
    }

    protected def appendBackSetupNewBlockCode() = {
        val oldDepth = TermName("oldDepth")
        val newRelaxedIndex = TermName("newRelaxedIndex")
        val xor = TermName("xor")
        q"""
            ..${assertions(q"!$v_dirty")}

            val $oldDepth = $depth
            val $newRelaxedIndex = (($endIndex - 1) - $focusStart) + $focusRelax
            val $xor = $newRelaxedIndex ^ ($focus | $focusRelax)

            $setupNewBlockInNextBranch($newRelaxedIndex, $xor)

            if ($oldDepth == $depth) {
                var i = ${ifInLevel(q"$xor", 1 to 4, lvl => q"${lvl + 1}", q"6")}
                val _focusDepth = $focusDepth

                while (i < oldDepth) {
                    var display: Array[AnyRef] = null
                    var newDisplay: Array[AnyRef] = null
                    var newSizes: Array[Int] = null
                    ${matchOnInt(q"i", 2 to 5, lvl => q"display = ${displayAt(lvl)}")}
                    val displayLen = display.length - 1
                    if (i >= _focusDepth) {
                        val oldSizes = display(displayLen).asInstanceOf[Array[Int]]
                        newSizes = new Array[Int](displayLen)
                        Platform.arraycopy(oldSizes, 0, newSizes, 0, displayLen - 1)
                        newSizes(displayLen - 1) = oldSizes(displayLen - 1) + 1
                    }
                    newDisplay = new Array[AnyRef](display.length)
                    Platform.arraycopy(display, 0, newDisplay, 0, displayLen)
                    if (i >= _focusDepth)
                        newDisplay(displayLen) = newSizes
                    ${matchOnInt(q"i", 2 to 5, lvl => q"newDisplay((newRelaxedIndex >> ${blockIndexBits * lvl}) & $blockMask) = ${displayAt(lvl - 1)}; ${displayAt(lvl)} = newDisplay")}
                    i += 1
                }
            }

            if ($oldDepth == $focusDepth) $initFocus($endIndex - 1, 0, $endIndex, $depth, 0)
            else $initFocus($endIndex - 1, $endIndex - 1, $endIndex, 1, $newRelaxedIndex & ${~blockMask})
         """
    }

    def concatenateCode(currentSize: TermName, that: TermName) = {
        def concatenateOn(maxDepth: Int): Tree = {
            if (maxDepth == 1) {
                q"""
                    val concat = $v_rebalancedLeafs(${displayAt(0)}, $that.${displayNameAt(0)}, isTop = true)
                    $initFromRoot(concat, if ($endIndex <= $blockWidth) 1 else 2)
                    ..${assertions(q"this.$v_assertVectorInvariant()")}
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
                        $initFromRoot($v_withComputedSizes(concat, ${maxDepth + 1}), ${maxDepth + 1})
                    ..${assertions(q"this.$v_assertVectorInvariant()")}
                 """
            }
        }
        q"""
            ..${assertions(q"$that.$v_assertVectorInvariant()", q"0 < $that.length")}
            if (this.$v_dirty) {
                this.$stabilize()
                this.$v_dirty = false
            }
            if ($that.$v_dirty) {
                $that.$stabilize()
                $that.$v_dirty = false
            }
            ..${assertions(q"$that.assertVectorInvariant()")}
            this.$focusOn($currentSize - 1)
            ${matchOnInt(q"math.max(this.$depth, $that.$depth)", 1 to 6, concatenateOn, Some(q"throw new IllegalStateException"))}
         """
    }

    protected def rebalancedCode(displayLeft: Tree, concat: Tree, displayRight: Tree, currentDepth: Tree) = {
        val leftLength = TermName("leftLength")
        val concatLength = TermName("concatLength")
        val rightLength = TermName("rightLength")

        val lengthSizes = TermName("nalen")
        val sizes = TermName("sizes")
        val branching = TermName("branching")

        def computeSizes: Seq[Tree] = {
            if (COMPLETE_REBALANCE)
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

            val top = new Array[AnyRef](${if (COMPLETE_REBALANCE) q"($branching >> ${2 * blockIndexBits}) + (if(($branching & ${blockMask | (blockMask << blockIndexBits)}) == 0) $blockInvariants else ${blockInvariants + 1})" else q"($lengthSizes >> $blockIndexBits) + (if (($lengthSizes & $blockMask) == 0) 1 else 2)"})

            var mid = new Array[AnyRef](${if (COMPLETE_REBALANCE) q"(if (($branching >> ${2 * blockIndexBits}) == 0) (($branching + ${blockWidth - 1}) >> $blockIndexBits) + $blockInvariants else ${blockWidth + blockInvariants})" else q"(if($lengthSizes <= $blockWidth) $lengthSizes else $blockWidth) + $blockInvariants"})
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
                            if ($concat == null) displayEnd = $leftLength
                            else displayEnd = $leftLength - 1
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
                            if ($concat == null) i = 0
                            else i = 1
                        }
                }

                while (i < displayEnd) {
                    val displayValue = currentDisplay(i).asInstanceOf[Array[AnyRef]]
                    val displayValueEnd = displayValue.length - (if (currentDepth==2) 0 else 1)
                    if ( /* iBot==0 && j==0 */ (iBot | j) == 0 && displayValueEnd == ${if (COMPLETE_REBALANCE) q"$blockWidth" else q"$sizes(iSizes)"}) {
                        if ($currentDepth!=2 && bot!=null) {
                            $v_withComputedSizes(bot, currentDepth - 1)
                            bot = null
                        }
                        mid(iMid) = displayValue
                        i += 1
                        iMid += 1
                        iSizes += 1
                    } else {
                        val numElementsToCopy = math.min(displayValueEnd - j, ${if (COMPLETE_REBALANCE) q"$blockWidth" else q"$sizes(iSizes)"} - iBot)
                        if (iBot == 0) {
                            if ($currentDepth!=2 && bot!=null)
                                $v_withComputedSizes(bot, currentDepth - 1)
                            bot = new Array[AnyRef](${if (COMPLETE_REBALANCE) q"math.min($branching - (iTop << ${2 * blockIndexBits}) - (iMid << $blockIndexBits), $blockWidth)" else q"$sizes(iSizes)"} + (if ($currentDepth == 2) 0 else $blockInvariants))
                            mid(iMid) = bot
                        }
                        Platform.arraycopy(displayValue, j, bot, iBot, numElementsToCopy)
                        j += numElementsToCopy
                        iBot += numElementsToCopy
                        if (j == displayValueEnd) {
                            i += 1
                            j = 0
                        }
                        if (iBot == ${if (COMPLETE_REBALANCE) q"$blockWidth" else q"$sizes(iSizes)"}) {
                            iMid += 1
                            iBot = 0
                            iSizes += 1
                        }
                    }
                    if (iMid == $blockWidth) {
                        top(iTop) = withComputedSizes(mid, $currentDepth)
                        iTop += 1
                        iMid = 0
                        ..${
            if (COMPLETE_REBALANCE) Seq(
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
                $v_withComputedSizes(bot, currentDepth - 1)

            if(mid != null)
                top(iTop) = $v_withComputedSizes(mid, $currentDepth)
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
                Platform.arraycopy($displayLeft, 0, mergedDisplay, 0, $leftLength)
                Platform.arraycopy($displayRight, 0, mergedDisplay, $leftLength, $rightLength)
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
                Platform.arraycopy($displayLeft, 0, arr0, 0, $leftLength)
                Platform.arraycopy($displayRight, 0, arr0, $leftLength, $blockWidth - $leftLength)
                Platform.arraycopy($displayRight, $blockWidth - $leftLength, arr1, 0,  $rightLength - $blockWidth + $leftLength)
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
                val offset1 = i
                i = 0
                while (i < $concatLength) {
                    $branching += $concat(i).asInstanceOf[Array[AnyRef]].length
                    i += 1
                }
                val offset2 = offset1 + i - 1
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

    protected def withComputedSizesCode(node: Tree, currentDepth: Tree, endIndex: Tree) = {
        q"""
            ..${assertions(q"$node != null", q"0 <= $currentDepth", q"$currentDepth <= 6")}
            var i = 0
            var acc = 0
            val end = $node.length - 1
            val sizes = new Array[Int](end)
            if ($currentDepth > 1) {
                while (i < end) {
                    acc += treeSize($node(i).asInstanceOf[Array[AnyRef]], $currentDepth - 1)
                    sizes(i) = acc
                    i += 1
                }
                val last = $node(end-1).asInstanceOf[Array[AnyRef]]
                if( ( end > 1 && sizes(end-2) != ((end-1) << ($blockIndexBits * ($currentDepth-1))) ) || ( $currentDepth>2 && last(last.length - 1) != null) )
                    $node(end) = sizes
            } else {
                while (i < end) {
                    acc += $node(i).asInstanceOf[Array[AnyRef]].length
                    sizes(i) = acc
                    i += 1
                }
                if( end > 1  && sizes(end-2) != ((end-1) << $blockIndexBits) ) {
                    $node(end) = sizes
                }
            }
            $node
        """
    }


    protected def treeSizeCode(tree: Tree, currentDepth: Tree) = {
        q"""
            ..${assertions(q"tree != null", q"0 <= $currentDepth", q"$currentDepth <= 6")}
            if($currentDepth == 1) {
                tree.length
            } else {
                val treeSizes = tree(tree.length - 1).asInstanceOf[Array[Int]]
                if (treeSizes != null) {
                    treeSizes(treeSizes.length - 1)
                } else {
                    var _tree = $tree
                    var _currentDepth = $currentDepth
                    var acc = 0
                    while (_currentDepth > 1) {
                        acc += (_tree.length - 2) * (1 << ($blockIndexBits * (_currentDepth-1)))
                        _currentDepth -= 1
                        _tree = _tree(_tree.length - 2).asInstanceOf[Array[AnyRef]]
                    }
                    acc + _tree.length
                }
            }
        """
    }


    protected def takeFront0Code(n: Tree): Tree = {
        val d0len = TermName("d0len")
        q"""
            if ($v_dirty) {
                $stabilize()
                $v_dirty = false
            }

            val vec = new $vectorClassName[$A]($n)
            vec.$initWithFocusFrom(this)

            if ($depth > 1) {
                vec.$focusOn($n - 1)
                val $d0len = (vec.$focus & $blockMask) + 1
                if ($d0len != $blockWidth) {
                    val d0 = new Array[AnyRef]($d0len)
                    Platform.arraycopy(vec.${displayNameAt(0)}, 0, d0, 0, $d0len)
                    vec.${displayNameAt(0)} = d0
                }
                val cutIndex = vec.$focus | vec.$focusRelax
                vec.$cleanTop(cutIndex)
                vec.$focusDepth = math.min(vec.$depth, vec.$focusDepth)
                if (vec.$depth > 1) {
                    vec.$copyDisplays(vec.$focusDepth, cutIndex)
                    var i = vec.$depth
                    var offset = 0
                    while (i > vec.$focusDepth) {
                        val display = ${matchOnInt(q"i", 2 to 6, d => q"vec.${displayNameAt(d - 1)}")}
                        val oldSizes = ${getBlockSizes(q"display")}
                        val newLen = ((vec.$focusRelax >> ($blockIndexBits * (i - 1))) & $blockMask) + 1
                        val newSizes = new Array[Int](newLen)
                        Platform.arraycopy(oldSizes, 0, newSizes, 0, newLen - 1)
                        newSizes(newLen - 1) = $n - offset
                        if (newLen > 1)
                            offset += newSizes(newLen - 2)

                        val newDisplay = new Array[AnyRef](newLen + 1)
                        Platform.arraycopy(display, 0, newDisplay, 0, newLen)
                        newDisplay(newLen - 1) = null
                        newDisplay(newLen) = newSizes

                        ${matchOnInt(q"i", 2 to 6, d => q"vec.${displayNameAt(d - 1)} = newDisplay")}
                        i -= 1
                    }
                    vec.$stabilizeDisplayPath(vec.$depth, cutIndex)
                    vec.$focusEnd = $n
                } else {
                    vec.$focusEnd = $n
                }
            } else if ($n != $blockWidth) {
                val d0 = new Array[AnyRef]($n)
                Platform.arraycopy(vec.${displayNameAt(0)}, 0, d0, 0, $n)
                vec.${displayNameAt(0)} = d0
                vec.initFocus(0, 0, $n, 1, 0)
            }

            ..${assertions(q"vec.$v_assertVectorInvariant()")}
            vec
         """
    }

    protected def assertVectorInvariantCode() = {
        def checkThatDisplayDefinedIffBelowDepth(lvl: Int) = {
            val p1 = q"($depth <= $lvl && ${displayAt(lvl)} == null)"
            val p2 = q"($depth > 0 && ${displayAt(lvl)} != null)"
            val str = s"<=$lvl <==> display$lvl==null "
            q"""assert($p1 || $p2, $depth.toString +: $str :+ ($depth, ${displayAt(lvl)}))"""
        }
        def checkDisplayIsCoherentWithTree(lvl: Int) = {
            val checkCoherence =
                q"""
                    if ($focusDepth <= $lvl)
                        assert(${displayAt(lvl)}(($focusRelax >> ${lvl * blockIndexBits}) & $blockMask) == ${displayAt(lvl - 1)})
                    else
                       assert(${displayAt(lvl)}(($focus >> ${lvl * blockIndexBits}) & $blockMask) == ${displayAt(lvl - 1)})
                 """
            q"""
                if (${displayAt(lvl)} != null) {
                    assert(${displayAt(lvl - 1)}  != null)
                    ${if (lvl == 1) q"if(!$v_dirty) $checkCoherence" else checkCoherence}
                }
             """
        }

        q"""
            assert(0 <= $depth && $depth <= 6, $depth)

            assert($v_isEmpty == ($depth == 0), ($v_isEmpty, $depth))
            assert($v_isEmpty == ($v_length == 0), ($v_isEmpty, $v_length))
            assert($v_length == $endIndex, ($v_length, $endIndex))

            ..${(0 to 5) map checkThatDisplayDefinedIffBelowDepth}

            ..${(5 to 1 by -1) map checkDisplayIsCoherentWithTree}

            assert(0 <= $focusStart && $focusStart <= $focusEnd && $focusEnd <= $v_endIndex, ($focusStart, $focusEnd, $v_endIndex))
            assert($focusStart == $focusEnd || $focusEnd != 0, "focusStart==focusEnd ==> focusEnd==0" +($focusStart, $focusEnd))

            assert(0 <= $focusDepth && $focusDepth <= $depth, ($focusDepth, $depth))

            ${checkSizesDef()}

            ${matchOnInt(q"$depth", 1 to 6, i => q"checkSizes(${displayAt(i - 1)}, $i, $v_endIndex)", Some(q"()"))}

            true
        """


    }

    private def checkSizesDef() = {
        q"""
            def checkSizes(node: Array[AnyRef], currentDepth: Int, _endIndex: Int): Unit = {
                if (currentDepth > 1) {
                    val sizes = node.last.asInstanceOf[Array[Int]]
                    if (sizes != null) {
                        assert(node.length == sizes.length + 1)
                        if(!$v_dirty)
                            assert(sizes.last == _endIndex, (sizes.last, _endIndex))
                        for (i <- 0 until sizes.length - 1)
                            checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth - 1, sizes(i) - (if (i == 0) 0 else sizes(i - 1)))
                        checkSizes(node(node.length - 2).asInstanceOf[Array[AnyRef]], currentDepth - 1, if(sizes.length > 1) sizes.last -  sizes(sizes.length - 2) else sizes.last)
                    } else {
                        for (i <- 0 until node.length - 2)
                            checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth - 1, 1 << ($blockIndexBits * (currentDepth - 1)))
                        val expectedLast = _endIndex - (1 << ($blockIndexBits * (currentDepth - 1))) * (node.length - 2)
                        assert(1 <= expectedLast && expectedLast <= (1 << ($blockIndexBits * currentDepth)))
                        checkSizes(node(node.length-2).asInstanceOf[Array[AnyRef]], currentDepth - 1, expectedLast)
                    }
                } else if (!$v_dirty) {
                    assert(node.length == _endIndex)
                }
            }
        """
    }


}
