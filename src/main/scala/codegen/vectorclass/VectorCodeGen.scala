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
    val v_appendedBack = TermName("appendedBack")
    val v_appendBackSetupCurrentBlock = TermName("appendBackSetupCurrentBlock")
    val v_appendBackSetupNewBlock = TermName("appendBackSetupNewBlock")
    val v_takeFront0 = TermName("takeFront0")

    val v_concatenated = TermName("concatenated")
    val v_rebalanced = TermName("rebalanced")
    val v_rebalancedLeafs = TermName("rebalancedLeafs")
    val v_computeNewSizes = TermName("computeNewSizes")
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
        q"$it.$initFrom(this); if ($depth > 0) $it.$it_resetIterator()"

    def initReverseIteratorCode(rit: Tree) =
        q"$rit.$initFrom(this); if ($depth > 0) $rit.$rit_resetIterator()"


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
                $gotoPosRelaxed($index, 0, $v_endIndex, $depth)
                $display0(($index - $focusStartLocal) & $blockMask).asInstanceOf[$A]
            } else {
                throw new IndexOutOfBoundsException($index.toString)
            }
         """
    }

    def collPlusCode(elem: Tree) = {
        q"""
            if (bf eq IndexedSeq.ReusableCBF) $v_appendedBack($elem).asInstanceOf[That] // just ignore bf
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
                if ($that.$v_isEmpty) this.asInstanceOf[That]
                else {
                    if($that.isInstanceOf[$vectorClassName[$B]]) {
                        val vec = $that.asInstanceOf[$vectorClassName[$B]]
                        if (this.isEmpty) vec.asInstanceOf[That]
                        else this.$v_concatenated[$B](vec).asInstanceOf[That]
                    } else super.++($that)
                }
            } else super.++(that.seq)
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

    protected def appendedBackCode(value: Tree) = {

        q"""
            if (${isEmptyCode()}) return $vectorObjectName.singleton[$B]($value)

            val _endIndex = this.$v_endIndex
            val vec = new $vectorClassName[$B](_endIndex + 1)
            vec.$initFrom(this)
            vec.$gotoIndex(_endIndex - 1, _endIndex - 1)

            val elemIndexInBlock = (_endIndex - vec.$focusStart) & $blockMask
            if( elemIndexInBlock != 0 ) vec.$v_appendBackSetupCurrentBlock()
            else vec.$v_appendBackSetupNewBlock()

            vec.$display0(elemIndexInBlock) = $value.asInstanceOf[AnyRef]

            vec
         """
    }

    protected def appendBackSetupCurrentBlockCode() = {
        q"""
            $focusEnd += 1
            ${
            if (CLOSED_BLOCKS)
                q"$display0 = $copyOf($display0, $display0.$v_length, $display0.$v_length + 1)"
            else
                q"$display0 = $copyOf($display0, $blockWidth, $blockWidth)"
        }
            val _depth = $depth
            if (_depth > 1) {
                val stabilizationIndex = $focus | $focusRelax
                val displaySizes = $allDisplaySizes()
                $copyDisplays(_depth, stabilizationIndex)
                $stabilize(_depth, stabilizationIndex)
                var i = $focusDepth
                while ( i < _depth ) {
                    val oldSizes = displaySizes(i - 1)
                    if (oldSizes != null) {
                        val newSizes = new Array[Int](${if (CLOSED_BLOCKS) q"oldSizes.length" else q"$blockWidth"})
                        val lastIndex = oldSizes.length - 1
                        Platform.arraycopy(oldSizes, 0, newSizes, 0, lastIndex)
                        newSizes(lastIndex) = oldSizes(lastIndex) + 1
                        displaySizes(i - 1) = newSizes
                    }
                    i += 1
                }
                $putDisplaySizes(displaySizes)
            }
         """
    }

    protected def appendBackSetupNewBlockCode() = {
        q"""
            ${if (useAssertions) q"assert($v_endIndex - 2 == $focus + $focusStart)" else q""}
            val _depth = $depth

            // TODO: should only copy the top displays, not the ones affected by setUpNextBlockStartTailWritable
            val displaySizes = $allDisplaySizes()
            $copyDisplays(_depth, $focus | $focusRelax)

            val newRelaxedIndex = ($v_endIndex - $focusStart - 1) + $focusRelax
            val xor = newRelaxedIndex ^ ($focus | $focusRelax)
            $setupNextBlockStartWritable(newRelaxedIndex, xor)
            $stabilize($depth, newRelaxedIndex)

            if (_depth != $depth) {
                if ($v_endIndex - 1 == (1 << ($blockIndexBits * ($depth - 1)))) {
                    displaySizes(depth - 1) = null
                } else {
                    val newSizes = new Array[Int](2)
                    newSizes(0) = endIndex - 1
                    newSizes(1) = endIndex
                    displaySizes(depth - 1) = newSizes
                }
            } else {
                val j = math.max(
                    if (xor.<(1024)) 1
                    else if (xor.<(32768)) 2
                    else if (xor.<(1048576)) 3
                    else if (xor.<(33554432)) 4
                    else 5,
                    $focusDepth)
                var i = $focusDepth
                while (i < j) {
                    displaySizes(i - 1) = null
                    i += 1
                }

                while (i < $depth) {
                    // TODO: load sizes and put them directly back from/to display
                    val oldSizes = displaySizes(i - 1)
                    val display = i match {
                        case 1 => display1
                        case 2 => display2
                        case 3 => display3
                        case 4 => display4
                        case 5 => display5
                    }
                    val newSizes = new Array[Int](display.length - $blockInvariants)
                    Platform.arraycopy(oldSizes, 0, newSizes, 0, oldSizes.length)
                    if (newSizes.length != oldSizes.length) {
                        newSizes(newSizes.length - 1) = newSizes(newSizes.length - 2) + 1
                    } else {
                        newSizes(newSizes.length - 1) += 1
                    }
                    displaySizes(i - 1) = newSizes
                    i += 1
                }
                putDisplaySizes(displaySizes)
            }
            if (_depth == focusDepth)
                initFocus(endIndex - 1, 0, endIndex, depth, 0)
            else {
                // TODO: find a wider/taller focus
                initFocus(0, endIndex - 1, endIndex, 1, newRelaxedIndex & ${~31})
            }

         """
    }

    def concatenatedCode(that: Tree) = {
        @inline def concatName(i: Int) = TermName("concat" + i)
        def concatenatedLevel(lvl: Int) = {
            var parameters: List[Tree] =
                q"this.${displayNameAt(lvl - 1)}" ::
                  q"${concatName(lvl - 1)}" ::
                  q"that.${displayNameAt(lvl - 1)}" :: Nil
            if (!CLOSED_BLOCKS) {
                parameters = parameters :::
                  q"(((this.$focus|this.$focusRelax) >> ${blockIndexBits * lvl}) & $blockMask)" ::
                  q"if(${concatName(lvl - 1)}(1) == null) 1 else 2" ::
                  q"if(that.focusDepth <= $lvl) (((that.$focusEnd >> ${blockIndexBits * (lvl - 1)}) & $blockMask) + 1) else ???" :: Nil
            }
            parameters = parameters ::: q"$lvl" :: Nil
            q"val ${concatName(lvl)} = $v_rebalanced(..$parameters)"
        }
        def concatenateVectors(maxLevel: Int) = {
            def initVector = {
                if (maxLevel == 1)
                    q"vec.initFromRoot(concat1, if(newSize <= $blockWidth) 1 else 2, newSize)"
                else {
                    def initVectorFromRoot(newRoot: Tree, rootDepth: Int) =
                        if (rootDepth == maxLevel || maxLevel == 6)
                            q"vec.initFromRoot($newRoot, $rootDepth, newSize)"
                        else if (CLOSED_BLOCKS)
                            q"vec.initFromRoot(withComputedSizes($newRoot, $rootDepth), $rootDepth, newSize)"
                        else
                            q"vec.initFromRoot(withComputedSizes($newRoot, $rootDepth, newSize), $rootDepth, newSize)"
                    if (maxLevel == 6)
                        initVectorFromRoot(q"${concatName(maxLevel)}", 6)
                    else
                        q"""
                            if(${concatName(maxLevel)}.length == 2) {
                                ${initVectorFromRoot(q"${concatName(maxLevel)}(0).asInstanceOf[Array[AnyRef]]", maxLevel)}
                            } else {
                                ${initVectorFromRoot(q"${concatName(maxLevel)}", maxLevel + 1)}
                            }
                        """
                }
            }

            q"""
                val ${concatName(1)} = $v_rebalancedLeafs(this.${displayNameAt(0)}, that.${displayNameAt(0)}, ..${if (CLOSED_BLOCKS) Nil else q"(this.$focus & $blockMask) + 1" :: q"math.min($blockWidth, that.$focusEnd)" :: Nil}, ${maxLevel == 1})
                ..${(2 to maxLevel) map concatenatedLevel}
                ${initVector}
             """
        }

        val concatOnVec = matchOnInt(q"math.max(this.$depth, that.$depth)", 1 to 6, concatenateVectors, Some(q"throw new IllegalStateException"))
        q"""
            this.$gotoIndex(this.$v_endIndex - 1, this.$v_endIndex)
            that.$gotoIndex(0, that.$v_endIndex)
            val newSize = this.$v_endIndex + that.$v_endIndex
            val vec = new $vectorClassName[B](newSize)
            $concatOnVec
            vec
        """
    }

    protected def rebalancedCode(displayLeft: Tree, concat: Tree, displayRight: Tree, leftLength: TermName, concatLength: TermName, rightLength: TermName, currentDepth: Tree) = {
        val lengthSizes = TermName("nalen")
        val sizes = TermName("sizes")

        q"""
            val tup = computeNewSizes(displayLeft, concat, displayRight, ..${
            if (CLOSED_BLOCKS) Nil else q"$leftLength" :: q"$concatLength" :: q"$rightLength" :: Nil
        }, currentDepth);
            val $sizes: Array[Int] = tup._1;
            val $lengthSizes: Int = tup._2;

            val topLen = ($lengthSizes >> $blockIndexBits) + (if (($lengthSizes & $blockMask) == 0) 1 else 2);
            val top = new Array[AnyRef](topLen);

            var mid = new Array[AnyRef](math.min($lengthSizes, $blockWidth) + 1)
            var bot: Array[AnyRef] = null
            top(0) = mid

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
                    if (iBot == 0 && j == 0 && displayValue.length - 1 == $sizes(iSizes)) {
                        mid(iMid) = displayValue
                        i += 1
                        iMid += 1
                        iSizes += 1
                    } else {
                        val numElementsToCopy = math.min(displayValue.length - j, $sizes(iSizes) - iBot)
                        if (iBot == 0) {
                            bot = new Array[AnyRef]($sizes(iSizes) + (if ($currentDepth == 2) 0 else 1))
                            mid(iMid) = bot
                        }
                        Platform.arraycopy(displayValue, j, bot, iBot, numElementsToCopy)
                        j += numElementsToCopy
                        iBot += numElementsToCopy
                        if (j == displayValue.length) {
                            i += 1
                            j = 0
                        }
                        if (iBot == $sizes(iSizes)) {
                            iMid += 1
                            iBot = 0
                            iSizes += 1
                        }
                    }
                    if (iMid == $blockWidth) {
                        top(iTop) = withComputedSizes(mid, $currentDepth - 1, ..${
            if (CLOSED_BLOCKS) Nil else q"???" :: Nil
        })
                        iTop += 1
                        iMid = 0
                        mid = new Array[AnyRef](math.min($lengthSizes - $blockWidth * iTop, $blockWidth) + 1)
                    }
                }
                d += 1
            } while (d < 3)

            top(iTop) = withComputedSizes(mid, $currentDepth - 1, ..${
            if (CLOSED_BLOCKS) Nil else q"???" :: Nil
        })

            top
         """
    }

    protected def rebalancedLeafsCode(displayLeft: Tree, displayRight: Tree, leftLength: TermName, rightLength: TermName, isTop: TermName) = {
        q"""
            if ($leftLength == $blockWidth) {
               val top = new Array[AnyRef](${if (CLOSED_BLOCKS) q"3" else q"${blockWidth + blockInvariants}"})
               top(0) = $displayLeft
               top(1) = $displayRight
               top
            } else if ($leftLength + $rightLength <= $blockWidth) {
                val mergedDisplay = new Array[AnyRef](${if (CLOSED_BLOCKS) q"$leftLength + $rightLength" else q"$blockWidth"})
                Platform.arraycopy($displayLeft, 0, mergedDisplay, 0, $leftLength)
                Platform.arraycopy($displayRight, 0, mergedDisplay, $leftLength, $rightLength)
                if ($isTop) {
                    mergedDisplay
                } else {
                    val top = new Array[AnyRef](${if (CLOSED_BLOCKS) q"2" else q"${blockWidth + blockInvariants}"})
                    top(0) = mergedDisplay
                    top
                }
            } else {
                val top = new Array[AnyRef](${if (CLOSED_BLOCKS) q"3" else q"${blockWidth + blockInvariants}"})
                val arr0 = new Array[AnyRef]($blockWidth)
                val arr1 = new Array[AnyRef](${if (CLOSED_BLOCKS) q"$leftLength + $rightLength - $blockWidth" else q"$blockWidth"})
                top(0) = arr0
                top(1) = arr1
                Platform.arraycopy($displayLeft, 0, arr0, 0, $leftLength)
                Platform.arraycopy($displayRight, 0, arr0, $leftLength, $blockWidth - $leftLength)
                Platform.arraycopy($displayRight, $blockWidth - $leftLength, arr1, 0,  $rightLength - $blockWidth + $leftLength)
                top
            }
         """
    }

    protected def computeNewSizesCode(displayLeft: Tree, concat: Tree, displayRight: Tree, leftLength: TermName, concatLength: TermName, rightLength: TermName, currentDepth: Tree) = {
        q"""
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

    protected def withComputedSizesCode(node: Tree, currentDepth: Tree, endIndex: Tree) = {
        q"""
            // TODO: Do not set sizes if the node is perfectly balanced
            if ($currentDepth > 1) {
                var i = 0
                var acc = 0
                val end = ${
            if (CLOSED_BLOCKS) q"$node.length - 1" else q"$blockWidth"
        }
                val sizes = new Array[Int](end)
                while (${
            if (CLOSED_BLOCKS) q"i < end" else q"acc < _endIndex"
        }) {
                    acc += treeSize($node(i).asInstanceOf[Array[AnyRef]], $currentDepth - 1)
                    sizes(i) = acc
                    i += 1
                }
                $node(end) = sizes
            } else {
                var i = 0
                var acc = 0
                val end = ${
            if (CLOSED_BLOCKS) q"$node.length - 1" else q"$blockWidth"
        }
                val sizes = new Array[Int](end)
                while (${
            if (CLOSED_BLOCKS) q"i < end" else q"acc < _endIndex"
        }) {
                    acc += $node(i).asInstanceOf[Array[AnyRef]].length
                    sizes(i) = acc
                    i += 1
                }
                $node(end) = sizes
            }
            $node
        """
    }


    protected def treeSizeCode(tree: Tree, currentDepth: Tree) = {
        q"""
            val treeSizes = tree(tree.length - 1).asInstanceOf[Array[Int]]
            if (treeSizes != null) {
                treeSizes(treeSizes.length - 1)
            } else {
                var _tree = $tree
                var _currentDepth = $currentDepth
                var acc = 0
                while (_currentDepth > 0) {
                    acc += (_tree.length - 2) * (1 << ($blockIndexBits * _currentDepth))
                    _currentDepth -= 1
                    _tree = _tree(_tree.length - 2).asInstanceOf[Array[AnyRef]]
                }
                acc + _tree.length
            }
        """
    }


    protected def takeFront0Code(n: Tree): Tree = {
        val d0len = TermName("d0len")
        q"""
            val vec = new $vectorClassName[$A]($n)
            vec.$initFrom(this)

            if ($depth > 1) {
                vec.$gotoIndex($n - 1, $n)

                val $d0len = (vec.$focus & $blockMask) + 1
                if ($d0len != $blockWidth) {
                    val d0 = new Array[AnyRef](${
            if (CLOSED_BLOCKS) q"$d0len" else q"$blockWidth"
        })
                    Platform.arraycopy(vec.$display0, 0, d0, 0, $d0len)
                    vec.$display0 = d0
                }

                val cutIndex = vec.$focus | vec.$focusRelax
                vec.$cleanTop(cutIndex)
                vec.$focusDepth = math.min(vec.$depth, vec.$focusDepth)

                // Note that cleanTop may change the depth
                if (vec.$depth > 1) {
                    val displaySizes = $allDisplaySizes()
                    vec.$copyDisplays(vec.$depth, cutIndex)
                    if (vec.$depth > 2 || $d0len != $blockWidth)
                        vec.$stabilize(vec.$depth, cutIndex)
                    if (vec.$focusDepth < vec.$depth) {
                        var offset = 0
                        var i = vec.$depth
                        while (i > vec.$focusDepth) {
                            i -= 1
                            val oldSizes = displaySizes(i - 1)
                            if (oldSizes != null) {
                                val newLen = (vec.$focusRelax >> ($blockIndexBits * i)) + 1
                                val newSizes = new Array[Int](newLen)
                                Platform.arraycopy(oldSizes, 0, newSizes, 0, newLen - 1)
                                newSizes(newLen - 1) = n - offset
                                offset += newSizes(newLen - 2)
                                displaySizes(i - 1) = newSizes
                            }
                        }
                        vec.$putDisplaySizes(displaySizes)
                    }


                }
            } else if ($n != $blockWidth) {
                val d0 = new Array[AnyRef](${
            if (CLOSED_BLOCKS) q"$n" else q"$blockWidth"
        })
                Platform.arraycopy(vec.$display0, 0, d0, 0, $n)
                vec.$display0 = d0
            }
            vec.$focusEnd = $n
            vec
         """
    }

    protected def assertVectorInvariantCode() = {
        def checkThatDisplayDefinedIffBelowDepth(lvl: Int) = {
            val p1 = q"($depth <= $lvl && ${
                displayAt(lvl)
            } == null)"
            val p2 = q"($depth > 0 && ${
                displayAt(lvl)
            } != null)"
            val str = s"<=$lvl <==> display$lvl==null "
            q"""assert($p1 || $p2, $depth.toString +: ${
                str
            } :+ ($depth, ${
                displayAt(lvl)
            }))"""
        }
        def checkDisplayIsCoherentWithTree(lvl: Int) =
            q"""
                if (${
                displayAt(lvl)
            } != null) {
                    assert(${
                displayAt(lvl - 1)
            }  != null)
                    if ($focusDepth <= $lvl) assert(${
                displayAt(lvl)
            }(($focusRelax >> ${
                lvl * blockIndexBits
            }) & $blockMask) == ${
                displayAt(lvl - 1)
            })
                    else assert(${
                displayAt(lvl)
            }(($focus >> ${
                lvl * blockIndexBits
            }) & $blockMask) == ${
                displayAt(lvl - 1)
            })
                }
             """

        q"""
            assert(0 <= $depth && $depth <= 6, $depth)

            assert($v_isEmpty == ($depth == 0), ($v_isEmpty, $depth))
            assert(isEmpty == (length == 0), ($v_isEmpty, $v_length))
            assert(length == endIndex, (length, endIndex))

            ..${
            (0 to 5) map checkThatDisplayDefinedIffBelowDepth
        }

            ..${
            (5 to 1 by -1) map checkDisplayIsCoherentWithTree
        }

            assert(0 <= $focusStart && $focusStart <= $focusEnd && $focusEnd <= $v_endIndex, ($focusStart, $focusEnd, $v_endIndex))
            assert($focusStart == $focusEnd || $focusEnd != 0, "focusStart==focusEnd ==> focusEnd==0" +($focusStart, $focusEnd))

            assert(0 <= $focusDepth && $focusDepth <= $depth, ($focusDepth, $depth))

            ${
            if (CLOSED_BLOCKS) checkSizesClosedDef() else checkSizesFullDef()
        }

            ${
            matchOnInt(q"$depth", 1 to 6, i => q"checkSizes(${
                displayAt(i - 1)
            }, $i, $v_endIndex)", Some(q"()"))
        }
        """


    }

    private def checkSizesClosedDef() = {
        q"""
            def checkSizes(node: Array[AnyRef], currentDepth: Int, _endIndex: Int): Unit = {
                if (currentDepth > 1) {
                    val sizes = node.last.asInstanceOf[Array[Int]]
                    if (sizes != null) {
                        assert(node.length == sizes.length + 1)
                        assert(sizes.last == _endIndex, (sizes.last, _endIndex))
                        for (i <- 0 until sizes.length - 1)
                            checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth - 1, sizes(i) - (if (i == 0) 0 else sizes(i - 1)))
                        checkSizes(node(node.length - 2).asInstanceOf[Array[AnyRef]], currentDepth - 1, if(sizes.length > 1) sizes.last -  sizes(sizes.length - 2) else sizes.last)
                    } else {
                        for (i <- 0 until node.length - 2)
                            checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth - 1, 1 << ($blockIndexBits * (currentDepth - 1)))
                        val expectedLast = _endIndex - (1 << (5 * currentDepth - 5)) * (node.length - 2)
                        assert(1 <= expectedLast && expectedLast <= (1 << (5 * currentDepth)))
                        checkSizes(node(node.length-2).asInstanceOf[Array[AnyRef]], currentDepth - 1, expectedLast)
                    }
                } else {
                    assert(node.length == _endIndex)
                }
            }
        """
    }

    private def checkSizesFullDef() = {
        q"""
            def checkSizes(node: Array[AnyRef], currentDepth: Int, _endIndex: Int): Unit = {
                if (currentDepth > 1) {
                    assert(node.length == ${
            blockWidth + blockInvariants
        })
                    val sizes = node.last.asInstanceOf[Array[Int]]
                    if (sizes != null) {
                        val _sizes = sizes.filter(_!=0)
                        assert(node.length == sizes.length + 1)
                        assert(_endIndex == _sizes.last)
                        var i = 0
                        while ( i < _sizes.length - 1 ) {
                            checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth - 1, sizes(i) - (if (i == 0) 0 else sizes(i - 1)))
                            i += 1
                        }
                        checkSizes(node(_sizes.length - 1).asInstanceOf[Array[AnyRef]], currentDepth - 1, _sizes.last - _sizes(_sizes.length - 2))
                    } else {
                        val fullTreeSize = 1 << ($blockIndexBits * (currentDepth - 1))
                        var i = 0
                        while (i < _endIndex / fullTreeSize) {
                            checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth - 1, fullTreeSize)
                            i += 1
                        }
                        val lastEndIndex = _endIndex - fullTreeSize * (_endIndex / fullTreeSize)
                        if((_endIndex % fullTreeSize) != 0) {
                            assert(1 <= lastEndIndex && lastEndIndex <= fullTreeSize)
                            checkSizes(node(_endIndex / fullTreeSize).asInstanceOf[Array[AnyRef]], currentDepth - 1, lastEndIndex)
                        }
                    }
                } else {
                    assert(node.length == $blockWidth)
                }
            }
        """
    }

}
