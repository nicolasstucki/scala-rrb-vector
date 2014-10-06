package codegen.vectorclass

import codegen.VectorPackage
import codegen.vectorpointer.VectorPointerCode

import scala.reflect.runtime.universe._

private[codegen] trait VectorClass {
    self: VectorPackage with VectorPointerCode with VectorCode =>

    def generateVectorClass() =
        q"""
                final class $vectorClassName[+$A] private[immutable](val $endIndex: Int)
                    extends scala.collection.AbstractSeq[$A]
                    with scala.collection.immutable.IndexedSeq[$A]
                    with scala.collection.generic.GenericTraversableTemplate[$A, $vectorClassName]
                    with scala.collection.IndexedSeqLike[$A, $vectorClassName[$A]]
                    with $vectorPointerClassName[$A @uncheckedVariance]
                    with Serializable {
                        self =>

                        ${companionDef()}
                        ${lengthDef()}
                        ${lengthCompareDef()}

                        ${initIteratorDef()}
                        ${initReverseIteratorDef()}
                        ${iteratorDef()}
                        ${reverseIteratorDef()}

                        // SeqLike
                        ${applyDef()}
                        ${collPlusDef()}

                        // IterableLike
                        override /*IterableLike*/ def isEmpty: Boolean = endIndex == 0

                        override /*IterableLike*/ def head: A = {
                            if (isEmpty) throw new UnsupportedOperationException("empty.head")
                            apply(0)
                        }

                        override /*IterableLike*/ def slice(from: Int, until: Int): $vectorClassName[A] = take(until).drop(from)

                        override /*IterableLike*/ def splitAt(n: Int): ($vectorClassName[A], $vectorClassName[A]) = (take(n), drop(n))

                        ${plusPlusDef()}

                        override /*TraversableLike*/ def tail: $vectorClassName[A] = {
                            if (isEmpty) throw new UnsupportedOperationException("empty.tail")
                            drop(1)
                        }

                        override /*TraversableLike*/ def last: A = {
                            if (isEmpty) throw new UnsupportedOperationException("empty.last")
                            apply(length - 1)
                        }

                        override /*TraversableLike*/ def init: $vectorClassName[A] = {
                            if (isEmpty) throw new UnsupportedOperationException("empty.init")
                            dropRight(1)
                        }

                        // Private methods
                        ${appendedBackDef()}
                        ${appendBackNewTailDef()}
                        ${concatenatedDef()}
                        ${rebalancedDef()}
                        ${computeNewSizesDef()}
                        ${withComputedSizesDef()}
                        ${treeSize()}
                        ${copiedAcross()}

                        // Invariant
                        ${asserts(assertVectorInvariantDef())}
                }
            """



    private def companionDef() = q"override def companion: scala.collection.generic.GenericCompanion[$vectorClassName] = $vectorObjectName"

    private def lengthDef() = q"def length(): Int = $endIndex"

    private def lengthCompareDef() = q"override def lengthCompare(len: Int): Int = length - len"


    private def initIteratorDef() = {
        q"""
            private[collection] def initIterator[B >: A](s: $vectorIteratorName[B]) {
                s.initFrom(this)
                if (depth > 0) s.resetIterator()
            }
        """
    }

    private def initReverseIteratorDef() = {
        q"""
            private[collection] def initIterator[B >: A](s: $vectorReverseIteratorName[B]) {
                s.initFrom(this)
                if ($depth > 0) s.initIterator()
            }
        """
    }

    private def iteratorDef() = {
        q"""
            override def iterator: $vectorIteratorName[A] =
            {
                val s = new $vectorIteratorName[A](0, endIndex)
                initIterator(s)
                s
            }
        """
    }

    private def reverseIteratorDef() = {
        q"""
            override def reverseIterator: $vectorReverseIteratorName[A] =
            {
                val s = new $vectorReverseIteratorName[A](0, $endIndex)
                initIterator(s)
                s
            }
        """
    }

    //
    // SeqLike
    //

    private def applyDef() = {
        q"""
            def /*SeqLike*/ apply(index: Int): A = {
                val _focusStart = this.$focusStart
                if (_focusStart <= index && index < $focusEnd) {
                    val indexInFocus = index - _focusStart
                    getElement(indexInFocus, indexInFocus ^ $focus).asInstanceOf[A]
                } else if (0 <= index && index < $endIndex) {
                    $gotoPosRelaxed(index, 0, $endIndex, $depth)
                    $display0((index - _focusStart) & 31).asInstanceOf[A]
                } else {
                    throw new IndexOutOfBoundsException(index.toString)
                }
            }
         """
    }

    private def collPlusDef() = {
        q"""
            override def /*SeqLike*/ :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[$vectorClassName[A], B, That]): That =
                if (bf eq IndexedSeq.ReusableCBF) $appendedBack(elem).asInstanceOf[That] // just ignore bf
                else super.:+(elem)(bf)
         """
    }

    //
    // IterableLike
    //


    //
    // TraversableLike
    //
    private def plusPlusDef() = {
        val op1 =
            q"""
               that match {
                            case vec: $vectorClassName[B] => {
                                if (this.isEmpty) vec.asInstanceOf[That]
                                else this.concatenated[B](vec).asInstanceOf[That]
                            }
                            case _ => super.++(that)
                        }
             """
        val op2 = q"""
                        if(that.isInstanceOf[$vectorClassName[B]]) {
                            val vec = that.asInstanceOf[$vectorClassName[B]]
                            if (this.isEmpty) vec.asInstanceOf[That]
                            else this.concatenated[B](vec).asInstanceOf[That]
                        } else super.++(that)

                   """
        q"""
            override /*TraversableLike*/ def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[$vectorClassName[A], B, That]): That =
            {
                if (bf eq IndexedSeq.ReusableCBF) {
                    if (that.isEmpty) this.asInstanceOf[That]
                    else {
                        $op2
                    }
                } else super.++(that.seq)
            }
        """
    }

    //
    // Private methods
    //

    private def appendedBackDef() = {
        q"""
            private def $appendedBack[B >: A](value: B): $vectorClassName[B] = {
                val _endIndex = this.endIndex
                if (_endIndex == 0) return $vectorObjectName.singleton[B](value)

                val vec = new $vectorClassName[B](_endIndex + 1)
                vec.initFrom(this)
                vec.gotoIndex(_endIndex - 1, _endIndex - 1)
                if (this.hasWritableTail) {
                    this.hasWritableTail = false
                    val elemIndexInBlock = (_endIndex - vec.focusStart) & 31
                    vec.display0(elemIndexInBlock) = value.asInstanceOf[AnyRef]
                    vec.focusEnd += 1
                    if (elemIndexInBlock < ${treeBranchWidth - 1})
                        vec.hasWritableTail = true
                } else vec.appendBackNewTail(value)

                vec
            }
         """
    }

    private def appendBackNewTailDef() = {
        q"""
            private def appendBackNewTail[B >: A](value: B): Unit = {
                ${asserts(q"assert(endIndex - 2 == focus + focusStart)")}

                val elemIndexInBlock = (endIndex - focusStart - 1) & 31
                val _depth = depth
                if (elemIndexInBlock != 0) {
                    val deltaSize = $treeBranchWidth - display0.length
                    display0 = copyOf(display0, display0.length, $treeBranchWidth)
                    if (_depth > 1) {
                        val stabilizationIndex = focus | focusRelax
                        val displaySizes = allDisplaySizes()
                        copyDisplays(_depth, stabilizationIndex)
                        stabilize(_depth, stabilizationIndex)
                        if (deltaSize == 0) {
                            putDisplaySizes(displaySizes)
                        } else {
                            for (i <- focusDepth until depth) {
                                val oldSizes = displaySizes(i - 1)
                                if (oldSizes != null) {
                                    val newSizes = new Array[Int](oldSizes.length)
                                    val lastIndex = oldSizes.length - 1
                                    Platform.arraycopy(oldSizes, 0, newSizes, 0, lastIndex)
                                    newSizes(lastIndex) = oldSizes(lastIndex) + deltaSize
                                    displaySizes(i - 1) = newSizes
                                }
                            }
                            putDisplaySizes(displaySizes)
                        }
                    }
                    display0(elemIndexInBlock) = value.asInstanceOf[AnyRef]
                    focusEnd += 1
                    if (elemIndexInBlock < ${treeBranchWidth - 1})
                        hasWritableTail = true
                } else {
                    // TODO: should only copy the top displays, not the ones affected by setUpNextBlockStartTailWritable
                    val displaySizes = allDisplaySizes()
                    copyDisplays(_depth, focus | focusRelax)

                    val newRelaxedIndex = (endIndex - focusStart - 1) + focusRelax
                    val xor = newRelaxedIndex ^ (focus | focusRelax)
                    setUpNextBlockStartTailWritable(newRelaxedIndex, xor)
                    stabilize(depth, newRelaxedIndex)

                    if (_depth != depth) {
                        if (endIndex - 1 == (1 << (5 * depth - 5))) {
                            displaySizes(depth - 1) = null
                        } else {
                            val newSizes = new Array[Int](2)
                            newSizes(0) = endIndex - 1
                            newSizes(1) = endIndex + 31
                            displaySizes(depth - 1) = newSizes
                        }
                    } else {
                        for (i <- focusDepth until depth) {
                            val oldSizes = displaySizes(i - 1)
                            val display = i match {
                                case 1 => display1
                                case 2 => display2
                                case 3 => display3
                                case 4 => display4
                                case 5 => display5
                            }
                            val newSizes = new Array[Int](display.length - 1)
                            Platform.arraycopy(oldSizes, 0, newSizes, 0, oldSizes.length)
                            if (newSizes.length != oldSizes.length) {
                                newSizes(newSizes.length - 1) = newSizes(newSizes.length - 2) + $treeBranchWidth
                            } else {
                                newSizes(newSizes.length - 1) += $treeBranchWidth
                            }
                            displaySizes(i - 1) = newSizes
                        }
                        putDisplaySizes(displaySizes)
                    }

                    display0(0) = value.asInstanceOf[AnyRef]
                    if (_depth == focusDepth)
                        initFocus(endIndex - 1, 0, endIndex, depth, 0)
                    else {
                        // TODO: find a wider/taller focus
                        initFocus(0, endIndex - 1, endIndex, 1, newRelaxedIndex & ${~31})
                    }
                    hasWritableTail = true
                }
            }
         """
    }

    private def concatenatedDef() = {
        q"""
            private[immutable] def $concatenated[B >: A](that: $vectorClassName[B]): $vectorClassName[B] = {

                ${asserts(q"this.assertVectorInvariant()")}
                ${asserts(q"that.assertVectorInvariant()")}
                ${asserts(q"assert(this.length > 0)")}
                ${asserts(q"assert(that.length > 0)")}

                this.gotoIndex(this.endIndex - 1, this.endIndex)
                that.gotoIndex(0, that.endIndex)

                val newSize = this.length + that.length

                def initVector(vec: $vectorClassName[B], concat: Array[AnyRef], depth: Int): Unit = {
                    if (concat.length == 2) vec.initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], depth, newSize)
                    else {
                        vec.initFromRoot(withComputedSizes(concat, depth), depth + 1, newSize)
                    }
                }

                val vec = new $vectorClassName[B](newSize)

                @inline def displayLength(display: Array[AnyRef]): Int = if (display != null) display.length else 0

                math.max(this.depth, that.depth) match {
                    case 1 =>
                        val concat1 = $rebalanced(this.display0, null, that.display0, this.endIndex, 0, that.endIndex, 1)
                        initVector(vec, concat1, 1)
                    case 2 =>
                        val concat1 = $rebalanced(this.display0, null, that.display0, (this.focus & 31) + 1, 0, if (that.depth == 1) that.endIndex else that.display0.length, 1)
                        val concat2 = $rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2)
                        initVector(vec, concat2, 2)
                    case 3 =>
                        val concat1 = $rebalanced(this.display0, null, that.display0, (this.focus & 31) + 1, 0, if (that.depth == 1) that.endIndex else that.display0.length, 1)
                        val concat2 = $rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2)
                        val concat3 = $rebalanced(this.display2, concat2, that.display2, displayLength(this.display2), concat2.length, displayLength(that.display2), 3)
                        initVector(vec, concat3, 3)
                    case 4 =>
                        val concat1 = $rebalanced(this.display0, null, that.display0, (this.focus & 31) + 1, 0, if (that.depth == 1) that.endIndex else that.display0.length, 1)
                        val concat2 = $rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2)
                        val concat3 = $rebalanced(this.display2, concat2, that.display2, displayLength(this.display2), concat2.length, displayLength(that.display2), 3)
                        val concat4 = $rebalanced(this.display3, concat3, that.display3, displayLength(this.display3), concat3.length, displayLength(that.display3), 4)
                        initVector(vec, concat4, 4)
                    case 5 =>
                        val concat1 = $rebalanced(this.display0, null, that.display0, (this.focus & 31) + 1, 0, if (that.depth == 1) that.endIndex else that.display0.length, 1)
                        val concat2 = $rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2)
                        val concat3 = $rebalanced(this.display2, concat2, that.display2, displayLength(this.display2), concat2.length, displayLength(that.display2), 3)
                        val concat4 = $rebalanced(this.display3, concat3, that.display3, displayLength(this.display3), concat3.length, displayLength(that.display3), 4)
                        val concat5 = $rebalanced(this.display4, concat4, that.display4, displayLength(this.display4), concat4.length, displayLength(that.display4), 5)
                        initVector(vec, concat5, 5)
                    case 6 =>
                        val concat1 = $rebalanced(this.display0, null, that.display0, (this.focus & 31) + 1, 0, if (that.depth == 1) that.endIndex else that.display0.length, 1)
                        val concat2 = $rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2)
                        val concat3 = $rebalanced(this.display2, concat2, that.display2, displayLength(this.display2), concat2.length, displayLength(that.display2), 3)
                        val concat4 = $rebalanced(this.display3, concat3, that.display3, displayLength(this.display3), concat3.length, displayLength(that.display3), 4)
                        val concat5 = $rebalanced(this.display4, concat4, that.display4, displayLength(this.display4), concat4.length, displayLength(that.display4), 5)
                        val concat6 = $rebalanced(this.display5, concat5, that.display5, displayLength(this.display5), concat5.length, displayLength(that.display5), 6)
                        initVector(vec, concat6, 6)
                    case _ => throw new IllegalStateException()

                }

                ${asserts(q"vec.assertVectorInvariant()")}

                vec
            }
        """
    }

    private def rebalancedDef() = {
        q"""
            private def $rebalanced(displayLeft: Array[AnyRef], concat: Array[AnyRef], displayRight: Array[AnyRef], lengthLeft: Int, lengthConcat: Int, lengthRight: Int, _depth: Int): Array[AnyRef] = {
                var offset = 0
                val alen =
                    if (_depth == 1) lengthLeft + lengthRight
                    else lengthLeft + lengthConcat + lengthRight - 1 - (if (lengthLeft == 0) 0 else 2) - (if (lengthRight == 0) 0 else 2)
                val all = new Array[AnyRef](alen)
                if (lengthLeft > 0) {
                    val s = lengthLeft - (if (_depth == 1) 0 else 2)
                    Platform.arraycopy(displayLeft, 0, all, 0, s)
                    offset += s
                }
                if (lengthConcat > 0) {
                    Platform.arraycopy(concat, 0, all, offset, concat.length - 1)
                    offset += concat.length - 1
                }
                if (lengthRight > 0) {
                    val s = lengthRight - (if (_depth == 1) 0 else 2)
                    Platform.arraycopy(displayRight, if (_depth == 1) 0 else 1, all, offset, s)
                }

                val (szs, nalen) = computeNewSizes(all, alen, _depth)

                copiedAcross(all, szs, nalen, _depth)
            }
         """
    }


    private def computeNewSizesDef() = {
        q"""
            private def computeNewSizes(all: Array[AnyRef], alen: Int, _depth: Int) = {
                val szs = new Array[Int](alen)
                var totalCount = 0
                var i = 0
                while (i < alen) {
                    val sz = if (_depth == 1) 1
                             else if (_depth == 2) all(i).asInstanceOf[Array[AnyRef]].length
                             else all(i).asInstanceOf[Array[AnyRef]].length - 1
                    szs(i) = sz
                    totalCount += sz
                    i += 1
                }

                // COMPUTE NEW SIZES
                // Calculate the ideal or effective number of slots
                // used to limit number of extra slots.
                val effectiveNumberOfSlots = totalCount / 32 + 1 // <-- "desired" number of slots???

                val MinWidth = 31 // min number of slots allowed...

                var nalen = alen
                // note - this makes multiple passes, can be done in one.
                // redistribute the smallest slots until only the allowed extras remain
                val EXTRAS = 2
                while (nalen > effectiveNumberOfSlots + EXTRAS) {
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
                        val msz = math.min(el + szs(ix + 1), 32)
                        szs(ix) = msz
                        el = el + szs(ix + 1) - msz

                        ix += 1
                    } while (el > 0)

                    // shuffle up remaining slot sizes
                    while (ix < nalen - 1) {
                        szs(ix) = szs(ix + 1)
                        ix += 1
                    }
                    nalen -= 1
                }

                (szs, nalen)
            }
        """
    }

    private def withComputedSizesDef() = {
        q"""
            private def withComputedSizes(node: Array[AnyRef], depth: Int): Array[AnyRef] = {
                ${asserts(q"assert(hasWritableTail==false)")}
                ${asserts(q"assert(node != null)")}
                ${asserts(q"assert(0 <= depth && depth <= 6)")}
                // TODO: Do not set sizes if the node is perfectly balanced
                if (depth > 1) {
                    var i = 0
                    var acc = 0
                    val end = node.length - 1
                    val sizes = new Array[Int](end)
                    while (i < end) {
                        acc += treeSize(node(i).asInstanceOf[Array[AnyRef]], depth - 1)
                        sizes(i) = acc
                        i += 1
                    }
                    node(end) = sizes
                } else {
                    var i = 0
                    var acc = 0
                    val end = node.length - 1
                    val sizes = new Array[Int](end)
                    while (i < end) {
                        acc += node(i).asInstanceOf[Array[AnyRef]].length
                        sizes(i) = acc
                        i += 1
                    }
                    node(end) = sizes
                }
                node
            }
        """
    }

    private def treeSize() = {
        q"""
            @tailrec
            private def treeSize(tree: Array[AnyRef], _depth: Int, acc: Int = 0): Int = {
                ${asserts(q"assert(this.hasWritableTail==false)")}
                ${asserts(q"assert(tree != null)")}
                ${asserts(q"assert(0 <= _depth && _depth <= 6)")}

                if (_depth == 0) acc + tree.length
                else {
                    val treeSizes = tree(tree.length - 1).asInstanceOf[Array[Int]]
                    if (treeSizes != null) treeSizes(treeSizes.length - 1)
                    else treeSize(tree(tree.length - 2).asInstanceOf[Array[AnyRef]], _depth - 1, acc + (tree.length - 2) * (1 << (5 * _depth)))
                }
            }
        """
    }

    private def copiedAcross() = {
        q"""
            private def copiedAcross(all: Array[AnyRef], sizes: Array[Int], lengthSizes: Int, depth: Int): Array[AnyRef] = {
                ${asserts(q"assert(all != null)")}
                ${asserts(q"assert(lengthSizes <= sizes.length)")}
                ${asserts(q"assert(0 <= depth && depth <= 6)")}

                if (depth == 1) {
                    val top = new Array[AnyRef](lengthSizes + 1)
                    var iTop = 0
                    var accSizes = 0
                    while (iTop < top.length - 1) {
                        val nodeSize = sizes(iTop)
                        val node = new Array[AnyRef](nodeSize)
                        Platform.arraycopy(all, accSizes, node, 0, nodeSize)
                        accSizes += nodeSize
                        top(iTop) = node
                        iTop += 1
                    }
                    top
                } else {
                    var iAll = 0
                    var allSubNode = all(0).asInstanceOf[Array[AnyRef]]
                    var jAll = 0

                    val top = new Array[AnyRef]((lengthSizes >> 5) + (if ((lengthSizes & 31) == 0) 1 else 2))
                    val topSizes = new Array[Int](top.length - 1)
                    top(top.length - 1) = topSizes

                    var iTop = 0
                    while (iTop < top.length - 1) {
                        val node = new Array[AnyRef](math.min(${treeBranchWidth + treeInvariants}, lengthSizes + 1 - (iTop << 5)))

                        var iNode = 0
                        while (iNode < node.length - 1) {
                            val sizeBottom = sizes((iTop << 5) + iNode)
                            val bottom = new Array[AnyRef](sizeBottom + (if (depth == 2) 0 else 1))
                            node(iNode) = bottom
                            var iBottom = 0
                            while (iBottom < bottom.length) {
                                bottom(iBottom) = allSubNode(jAll)
                                jAll += 1
                                if (jAll >= allSubNode.length) {
                                    iAll += 1
                                    jAll = 0
                                    if (iAll < all.length)
                                        allSubNode = all(iAll).asInstanceOf[Array[AnyRef]]
                                }
                                iBottom += 1
                            }

                            iNode += 1
                        }

                        top(iTop) = withComputedSizes(node, depth - 1)
                        iTop += 1
                    }

                    top
                }
            }
        """
    }

    //
    // Invariant
    //

    private def assertVectorInvariantDef() = {
        q"""
            private[immutable] def $assertVectorInvariant(): Unit = {
                    assert(0 <= depth && depth <= 6, depth)

                    assert(isEmpty == ($depth == 0), (isEmpty, depth))
                    assert(isEmpty == (length == 0), (isEmpty, length))
                    assert(length == endIndex, (length, endIndex))

                    assert((depth == 0 && display0 == null) || (depth > 0 && display0 != null), s"depth==0 <==> display0==null " :+(depth, display0))
                    assert((depth <= 1 && display1 == null) || (depth > 1 && display1 != null), s"depth<=1 <==> display1==null " :+(depth, display1))
                    assert((depth <= 2 && display2 == null) || (depth > 2 && display2 != null), s"depth<=2 <==> display2==null " :+(depth, display2))
                    assert((depth <= 3 && display3 == null) || (depth > 3 && display3 != null), s"depth<=3 <==> display3==null " :+(depth, display3))
                    assert((depth <= 4 && display4 == null) || (depth > 4 && display4 != null), s"depth<=4 <==> display4==null " :+(depth, display4))
                    assert((depth <= 5 && display5 == null) || (depth > 5 && display5 != null), s"depth<=5 <==> display5==null " :+(depth, display5))

                    if (display5 != null) {
                        assert(display4 != null)
                        if (focusDepth < 6) assert(display5((focusRelax >> 25) & 31) == display4)
                        else assert(display5((focus >> 25) & 31) == display4)
                    }
                    if (display4 != null) {
                        assert(display3 != null)
                        if (focusDepth < 5) assert(display4((focusRelax >> 20) & 31) == display3)
                        else assert(display4((focus >> 20) & 31) == display3)
                    }
                    if (display3 != null) {
                        assert(display2 != null)
                        if (focusDepth < 4) assert(display3((focusRelax >> 15) & 31) == display2)
                        else assert(display3((focus >> 15) & 31) == display2)
                    }
                    if (display2 != null) {
                        assert(display1 != null)
                        if (focusDepth < 3) assert(display2((focusRelax >> 10) & 31) == display1)
                        else assert(display2((focus >> 10) & 31) == display1)
                    }
                    if (display1 != null) {
                        assert(display0 != null)
                        if (focusDepth < 2) assert(display1((focusRelax >> 5) & 31) == display0)
                        else assert(display1((focus >> 5) & 31) == display0)
                    }

                    assert(0 <= focusStart && focusStart <= focusEnd && focusEnd <= endIndex, (focusStart, focusEnd, endIndex))
                    assert(focusStart == focusEnd || focusEnd != 0, "focusStart==focusEnd ==> focusEnd==0" +(focusStart, focusEnd))

                    assert(0 <= focusDepth && focusDepth <= depth, (focusDepth, depth))

                    def checkSize(node: Array[AnyRef], depth: Int, expectedSize: Int, strictSize: Boolean = false): Unit = {
                        if (depth > 1) {
                            val sizes = node.last.asInstanceOf[Array[Int]]
                            if (sizes != null) {
                                assert(node.length == sizes.length + 1)
                                if (strictSize) {
                                    assert(sizes.last == expectedSize, (sizes.last, expectedSize))
                                } else {
                                    assert(expectedSize <= sizes.last && sizes.last - 32 < expectedSize)
                                }
                                for (i <- 0 until sizes.length - 1)
                                    checkSize(node(i).asInstanceOf[Array[AnyRef]], depth - 1, sizes(i) - (if (i == 0) 0 else sizes(i - 1)), strictSize = true)
                                checkSize(node(node.length - 2).asInstanceOf[Array[AnyRef]], depth - 1, sizes.last - sizes(sizes.length - 2), strictSize = true)
                            } else {
                                for (i <- 0 until node.length - 2)
                                    checkSize(node(i).asInstanceOf[Array[AnyRef]], depth - 1, 1 << (5 * depth - 5), strictSize = true)
                                val expectedLast = expectedSize - (1 << (5 * depth - 5)) * (node.length - 2)
                                assert(1 <= expectedLast && expectedLast <= (1 << (5 * depth)))
                                checkSize(node(node.length - 2).asInstanceOf[Array[AnyRef]], depth - 1, expectedLast)
                            }
                        } else {
                            if (strictSize) {
                                assert(node.length == expectedSize, (node.mkString("Array(", ",", ")"), expectedSize))
                            } else {
                                assert(node.length == expectedSize || node.length == $treeBranchWidth, (node, expectedSize))
                            }
                        }
                    }

                    depth match {
                        case 0 =>
                        case 1 => checkSize(display0, 1, endIndex)
                        case 2 => checkSize(display1, 2, endIndex)
                        case 3 => checkSize(display2, 3, endIndex)
                        case 4 => checkSize(display3, 4, endIndex)
                        case 5 => checkSize(display4, 5, endIndex)
                        case 6 => checkSize(display5, 6, endIndex)
                    }


                }
        """
    }
}