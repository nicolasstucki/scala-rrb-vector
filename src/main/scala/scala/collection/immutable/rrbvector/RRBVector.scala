package scala
package collection
package immutable
package rrbvector

import scala.annotation.tailrec
import scala.annotation.unchecked.uncheckedVariance

import scala.collection.generic.{GenericCompanion, GenericTraversableTemplate, CanBuildFrom, IndexedSeqFactory}
import scala.compat.Platform


object RRBVector extends IndexedSeqFactory[RRBVector] {
    def newBuilder[A]: mutable.Builder[A, RRBVector[A]] = new RRBVectorBuilder[A]

    implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, RRBVector[A]] =
        ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

    private[immutable] val NIL = new RRBVector[Nothing](0)

    override def empty[A]: RRBVector[A] = NIL

    private[immutable] final def singleton[A](value: A): RRBVector[A] = {
        val vec = new RRBVector[A](1)
        vec.display0 = new Array[AnyRef](vec.TREE_BRANCH_WIDTH)
        vec.display0(0) = value.asInstanceOf[AnyRef]
        vec.depth = 1
        vec.focusEnd = 1
        vec.focusDepth = 1
        vec.hasWritableTail = true
        vec
    }

    private[immutable] final val useAssertions = false

}

final class RRBVector[+A] private[immutable](val endIndex: Int)
  extends AbstractSeq[A]
  with IndexedSeq[A]
  with GenericTraversableTemplate[A, RRBVector]
  with IndexedSeqLike[A, RRBVector[A]]
  with RRBVectorPointer[A@uncheckedVariance]
  with Serializable {
    //  with CustomParallelizable[A, ParVector[A]] {
    self =>

    override def companion: GenericCompanion[RRBVector] = RRBVector

    def length = endIndex

    override def lengthCompare(len: Int): Int = length - len


    //
    // Iterators
    //

    private[collection] def initIterator[B >: A](s: RRBVectorIterator[B]) {
        s.initFrom(this)
        if (depth > 0) s.resetIterator()
    }

    private[collection] def initIterator[B >: A](s: RRBVectorReverseIterator[B]) {
        s.initFrom(this)
        if (depth > 0) s.initIterator()
    }

    override def iterator: RRBVectorIterator[A] = {
        val s = new RRBVectorIterator[A](0, endIndex)
        initIterator(s)
        s
    }

    override def reverseIterator: RRBVectorReverseIterator[A] = {
        val s = new RRBVectorReverseIterator[A](0, endIndex)
        initIterator(s)
        s
    }

    //
    // SeqLike
    //

    def /*SeqLike*/ apply(index: Int): A = {
        val _focusStart = this.focusStart
        if (_focusStart <= index && index < focusEnd) {
            val indexInFocus = index - _focusStart
            getElement(indexInFocus, indexInFocus ^ focus).asInstanceOf[A]
        } else if (0 <= index && index < endIndex) {
            gotoPosRelaxed(index, 0, endIndex, depth)
            display0((index - _focusStart) & 31).asInstanceOf[A]
        } else {
            throw new IndexOutOfBoundsException(index.toString)
        }

    }

    override def /*SeqLike*/ :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[RRBVector[A], B, That]): That =
        if (bf eq IndexedSeq.ReusableCBF) appendedBack(elem).asInstanceOf[That] // just ignore bf
        else super.:+(elem)(bf)

    //
    // IterableLike
    //

    override /*IterableLike*/ def isEmpty: Boolean = endIndex == 0

    override /*IterableLike*/ def head: A = {
        if (isEmpty) throw new UnsupportedOperationException("empty.head")
        apply(0)
    }

    override /*IterableLike*/ def slice(from: Int, until: Int): RRBVector[A] = take(until).drop(from)

    override /*IterableLike*/ def splitAt(n: Int): (RRBVector[A], RRBVector[A]) = (take(n), drop(n))


    //
    // TraversableLike
    //

    override /*TraversableLike*/ def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[RRBVector[A], B, That]): That = {
        if (bf eq IndexedSeq.ReusableCBF) {
            if (that.isEmpty) this.asInstanceOf[That]
            else {
                that match {
                    case vec: RRBVector[B] => {
                        if (this.isEmpty) vec.asInstanceOf[That]
                        else this.concatenated[B](vec).asInstanceOf[That]
                    }
                    case _ => super.++(that)
                }
            }
        } else super.++(that.seq)
    }

    override /*TraversableLike*/ def tail: RRBVector[A] = {
        if (isEmpty) throw new UnsupportedOperationException("empty.tail")
        drop(1)
    }

    override /*TraversableLike*/ def last: A = {
        if (isEmpty) throw new UnsupportedOperationException("empty.last")
        apply(length - 1)
    }

    override /*TraversableLike*/ def init: RRBVector[A] = {
        if (isEmpty) throw new UnsupportedOperationException("empty.init")
        dropRight(1)
    }


    //
    // Private methods
    //

    private def appendedBack[B >: A](value: B): RRBVector[B] = {
        val endIndex = this.endIndex
        if (endIndex == 0) return RRBVector.singleton[B](value)

        val vec = new RRBVector[B](endIndex + 1)
        vec.initFrom(this)
        vec.gotoIndex(endIndex - 1, endIndex - 1)
        if (this.hasWritableTail) {
            this.hasWritableTail = false
            val elemIndexInBlock = (endIndex - vec.focusStart) & 31
            vec.display0(elemIndexInBlock) = value.asInstanceOf[AnyRef]
            vec.focusEnd += 1
            if (elemIndexInBlock < TREE_BRANCH_WIDTH - 1)
                vec.hasWritableTail = true
        } else vec.appendBackNewTail(value)

        vec
    }

    private def appendBackNewTail[B >: A](value: B): Unit = {
        if (RRBVector.useAssertions) {
            assert(endIndex - 2 == focus + focusStart)
        }
        val elemIndexInBlock = (endIndex - focusStart - 1) & 31
        val _depth = depth
        if (elemIndexInBlock != 0) {
            val deltaSize = 32 - display0.length
            display0 = copyOf(display0, display0.length, TREE_BRANCH_WIDTH)
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
            if (elemIndexInBlock < TREE_BRANCH_WIDTH - 1)
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
                        newSizes(newSizes.length - 1) = newSizes(newSizes.length - 2) + TREE_BRANCH_WIDTH
                    } else {
                        newSizes(newSizes.length - 1) += 32
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
                initFocus(0, endIndex - 1, endIndex, 1, newRelaxedIndex & ~31)
            }
            hasWritableTail = true
        }
    }

    private[immutable] def concatenated[B >: A](that: RRBVector[B]): RRBVector[B] = {
        if (RRBVector.useAssertions) {
            this.assertVectorInvariant()
            that.assertVectorInvariant()
            assert(this.length > 0)
            assert(that.length > 0)
        }

        this.gotoIndex(this.endIndex - 1, this.endIndex)
        that.gotoIndex(0, that.endIndex)

        val newSize = this.length + that.length

        def initVector(vec: RRBVector[B], concat: Array[AnyRef], depth: Int): Unit = {
            if (concat.length == 2) vec.initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], depth, newSize)
            else {
                vec.initFromRoot(withComputedSizes(concat, depth), depth + 1, newSize)
            }
        }

        val vec = new RRBVector[B](newSize)

        @inline def displayLength(display: Array[AnyRef]): Int = if (display != null) display.length else 0

        math.max(this.depth, that.depth) match {
            case 1 =>
                val concat1 = rebalanced(this.display0, null, that.display0, this.endIndex, 0, that.endIndex, 1)
                initVector(vec, concat1, 1)
            case 2 =>
                val concat1 = rebalanced(this.display0, null, that.display0, (this.focus & 31) + 1, 0, if (that.depth == 1) that.endIndex else that.display0.length, 1)
                val concat2 = rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2)
                initVector(vec, concat2, 2)
            case 3 =>
                val concat1 = rebalanced(this.display0, null, that.display0, (this.focus & 31) + 1, 0, if (that.depth == 1) that.endIndex else that.display0.length, 1)
                val concat2 = rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2)
                val concat3 = rebalanced(this.display2, concat2, that.display2, displayLength(this.display2), concat2.length, displayLength(that.display2), 3)
                initVector(vec, concat3, 3)
            case 4 =>
                val concat1 = rebalanced(this.display0, null, that.display0, (this.focus & 31) + 1, 0, if (that.depth == 1) that.endIndex else that.display0.length, 1)
                val concat2 = rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2)
                val concat3 = rebalanced(this.display2, concat2, that.display2, displayLength(this.display2), concat2.length, displayLength(that.display2), 3)
                val concat4 = rebalanced(this.display3, concat3, that.display3, displayLength(this.display3), concat3.length, displayLength(that.display3), 4)
                initVector(vec, concat4, 4)
            case 5 =>
                val concat1 = rebalanced(this.display0, null, that.display0, (this.focus & 31) + 1, 0, if (that.depth == 1) that.endIndex else that.display0.length, 1)
                val concat2 = rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2)
                val concat3 = rebalanced(this.display2, concat2, that.display2, displayLength(this.display2), concat2.length, displayLength(that.display2), 3)
                val concat4 = rebalanced(this.display3, concat3, that.display3, displayLength(this.display3), concat3.length, displayLength(that.display3), 4)
                val concat5 = rebalanced(this.display4, concat4, that.display4, displayLength(this.display4), concat4.length, displayLength(that.display4), 5)
                initVector(vec, concat5, 5)
            case 6 =>
                val concat1 = rebalanced(this.display0, null, that.display0, (this.focus & 31) + 1, 0, if (that.depth == 1) that.endIndex else that.display0.length, 1)
                val concat2 = rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2)
                val concat3 = rebalanced(this.display2, concat2, that.display2, displayLength(this.display2), concat2.length, displayLength(that.display2), 3)
                val concat4 = rebalanced(this.display3, concat3, that.display3, displayLength(this.display3), concat3.length, displayLength(that.display3), 4)
                val concat5 = rebalanced(this.display4, concat4, that.display4, displayLength(this.display4), concat4.length, displayLength(that.display4), 5)
                val concat6 = rebalanced(this.display5, concat5, that.display5, displayLength(this.display5), concat5.length, displayLength(that.display5), 6)
                initVector(vec, concat6, 6)
            case _ => throw new IllegalStateException()

        }
        if (RRBVector.useAssertions) {
            vec.assertVectorInvariant()
        }
        vec
    }

    private def rebalanced(displayLeft: Array[AnyRef], concat: Array[AnyRef], displayRight: Array[AnyRef], lengthLeft: Int, lengthConcat: Int, lengthRight: Int, _depth: Int): Array[AnyRef] = {

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

    private def computeNewSizes(all: Array[AnyRef], alen: Int, _depth: Int) = {
        val szs = new Array[Int](alen)
        var totalCount = 0
        var i = 0
        while (i < alen) {
            val sz = sizeSlot(all(i), _depth - 1)
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

    private def withComputedSizes(node: Array[AnyRef], depth: Int): Array[AnyRef] = {
        if (RRBVector.useAssertions) {
            assert(!hasWritableTail)
            assert(node != null)
            assert(0 <= depth && depth <= 6)
        }
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

    @tailrec
    private def treeSize(tree: Array[AnyRef], depth: Int, acc: Int = 0): Int = {
        if (RRBVector.useAssertions) {
            assert(!this.hasWritableTail)
            assert(tree != null)
            assert(0 <= depth && depth <= 6)
        }
        if (depth == 0) acc + tree.length
        else {
            val treeSizes = tree(tree.length - 1).asInstanceOf[Array[Int]]
            if (treeSizes != null) treeSizes(treeSizes.length - 1)
            else treeSize(tree(tree.length - 2).asInstanceOf[Array[AnyRef]], depth - 1, acc + (tree.length - 2) * (1 << (5 * depth)))
        }
    }

    private def copiedAcross(all: Array[AnyRef], sizes: Array[Int], lengthSizes: Int, depth: Int): Array[AnyRef] = {
        if (RRBVector.useAssertions) {
            assert(all != null)
            assert(sizes != null)
            assert(lengthSizes <= sizes.length)
            assert(0 <= depth && depth <= 6)
        }
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
                val node = new Array[AnyRef](math.min(TREE_BRANCH_WIDTH + TREE_INVARIANTS, lengthSizes + 1 - (iTop << 5)))

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

    private[immutable] def sizeSlot(a: AnyRef, depth: Int) = {
        if (depth == 0) 1
        else if (depth == 1) a.asInstanceOf[Array[AnyRef]].length
        else a.asInstanceOf[Array[AnyRef]].length - 1
    }

    private[immutable] def assertVectorInvariant(): Unit = {
        if (RRBVector.useAssertions) {
            assert(0 <= depth && depth <= 6, depth)

            assert(isEmpty == (depth == 0), (isEmpty, depth))
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
                        assert(node.length == expectedSize || node.length == TREE_BRANCH_WIDTH, (node, expectedSize))
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
    }

}


private[immutable] trait RRBVectorPointer[A] {

    private[immutable] final val TREE_BRANCH_WIDTH: Int = 32
    private[immutable] final val TREE_INVARIANTS: Int = 1

    private[immutable] var focusStart: Int = 0
    private[immutable] var focusEnd: Int = 0
    private[immutable] var focus: Int = 0
    private[immutable] var focusDepth: Int = 0
    private[immutable] var focusRelax: Int = 0

    private[immutable] var depth: Int = _

    private[immutable] var display0: Array[AnyRef] = _
    private[immutable] var display1: Array[AnyRef] = _
    private[immutable] var display2: Array[AnyRef] = _
    private[immutable] var display3: Array[AnyRef] = _
    private[immutable] var display4: Array[AnyRef] = _
    private[immutable] var display5: Array[AnyRef] = _

    private[immutable] var hasWritableTail = false


    private[immutable] def root(): AnyRef = {
        depth match {
            case 0 => null
            case 1 => display0
            case 2 => display1
            case 3 => display2
            case 4 => display3
            case 5 => display4
            case 6 => display5
            case _ => throw new IllegalStateException("Illegal vector depth: " + depth)
        }
    }

    private[immutable] def initFromRoot(root: Array[AnyRef], _depth: Int, _endIndex: Int): Unit = {
        _depth match {
            case 0 => throw new IllegalArgumentException
            case 1 => display0 = root
            case 2 => display1 = root
            case 3 => display2 = root
            case 4 => display3 = root
            case 5 => display4 = root
            case 6 => display5 = root
            case _ => throw new IllegalStateException("Illegal vector depth: " + depth)
        }
        depth = _depth
        // Initialize the display from the root to index 0
        gotoIndex(0, _endIndex)
    }

    //
    // Relaxed radix based methods
    //

    private[immutable] final def initFrom[U](that: RRBVectorPointer[U]): Unit = {
        initFocus(that.focus, that.focusStart, that.focusEnd, that.focusDepth, that.focusRelax)
        depth = that.depth
        depth match {
            case 0 =>
            case 1 =>
                display0 = that.display0
            case 2 =>
                display1 = that.display1
                display0 = that.display0
            case 3 =>
                display2 = that.display2
                display1 = that.display1
                display0 = that.display0
            case 4 =>
                display3 = that.display3
                display2 = that.display2
                display1 = that.display1
                display0 = that.display0
            case 5 =>
                display4 = that.display4
                display3 = that.display3
                display2 = that.display2
                display1 = that.display1
                display0 = that.display0
            case 6 =>
                display5 = that.display5
                display4 = that.display4
                display3 = that.display3
                display2 = that.display2
                display1 = that.display1
                display0 = that.display0
            case _ =>
                throw new IllegalStateException()
        }
    }

    private[immutable] final def initFocus(_focus: Int, _focusStart: Int, _focusEnd: Int, _focusDepth: Int, _focusRelax: Int) = {
        this.focus = _focus
        this.focusStart = _focusStart
        this.focusEnd = _focusEnd
        this.focusDepth = _focusDepth
        this.focusRelax = _focusRelax
    }

    private[immutable] final def gotoIndex(index: Int, endIndex: Int): Unit = {
        val focusStart = this.focusStart
        if (focusStart <= index && index < focusEnd) {
            val indexInFocus = index - focusStart
            val xor = indexInFocus ^ focus
            if /* is not focused on last block */ (xor >= (1 << 5)) {
                gotoPos(indexInFocus, xor)
            }
            focus = indexInFocus
        } else {
            gotoPosRelaxed(index, 0, endIndex, depth)
        }
    }

    private[immutable] def allDisplaySizes(): Array[Array[Int]] = {
        val allSises: Array[Array[Int]] = new Array(5)
        for (i <- focusDepth until depth) {
            allSises(i - 1) = i match {
                case 1 => display1.last.asInstanceOf[Array[Int]]
                case 2 => display2.last.asInstanceOf[Array[Int]]
                case 3 => display3.last.asInstanceOf[Array[Int]]
                case 4 => display4.last.asInstanceOf[Array[Int]]
                case 5 => display5.last.asInstanceOf[Array[Int]]
                case _ => null
            }
        }
        allSises
    }

    private[immutable] def putDisplaySizes(allSizes: Array[Array[Int]]): Unit = {
        for (i <- focusDepth until depth) {
            i match {
                case 1 => display1(display1.length - 1) = allSizes(i - 1)
                case 2 => display2(display2.length - 1) = allSizes(i - 1)
                case 3 => display3(display3.length - 1) = allSizes(i - 1)
                case 4 => display4(display4.length - 1) = allSizes(i - 1)
                case 5 => display5(display5.length - 1) = allSizes(i - 1)
                case _ =>
            }
        }
    }

    /**
     *
     * @param index: Index that will be focused
     * @param _startIndex: The first index of the current subtree. If called from the root, it should be 0.
     * @param _endIndex: The end index of the current subtree where _endIndex-1 is the last element in this subtree.
     *                 If called from the root, it should be the length of the tree.
     * @param _depth: Depth of the current subtree. If called from the root, it should be the depth of the tree.
     */
    @tailrec
    private[immutable] final def gotoPosRelaxed(index: Int, _startIndex: Int, _endIndex: Int, _depth: Int, _focusRelax: Int = 0): Unit = {
        val display = _depth match {
            case 0 => null
            case 1 => display0
            case 2 => display1
            case 3 => display2
            case 4 => display3
            case 5 => display4
            case 6 => display5
            case _ => throw new IllegalArgumentException("depth=" + _depth)
        }

        if (_depth > 1 && display(display.length - 1) != null) {
            val sizes = display(display.length - 1).asInstanceOf[Array[Int]]
            val is = getRelaxedIndex(index - _startIndex, sizes)
            _depth match {
                case 2 => display0 = display(is).asInstanceOf[Array[AnyRef]]
                case 3 => display1 = display(is).asInstanceOf[Array[AnyRef]]
                case 4 => display2 = display(is).asInstanceOf[Array[AnyRef]]
                case 5 => display3 = display(is).asInstanceOf[Array[AnyRef]]
                case 6 => display4 = display(is).asInstanceOf[Array[AnyRef]]
                case _ => throw new IllegalArgumentException("depth=" + _depth)
            }
            gotoPosRelaxed(index, if (is == 0) _startIndex else _startIndex + sizes(is - 1), if (is < sizes.length - 1) _startIndex + sizes(is) else _endIndex, _depth - 1, _focusRelax | (is << (5 * _depth - 5)))
        } else {
            val indexInFocus = index - _startIndex
            gotoPos(indexInFocus, 1 << (5 * (_depth - 1)))
            initFocus(indexInFocus, _startIndex, _endIndex, _depth, _focusRelax)
        }
    }

    private final def getRelaxedIndex(indexInSubTree: Int, sizes: Array[Int]) = {
        var is = 0 //ix >> ((height - 1) * WIDTH_SHIFT)
        while (sizes(is) <= indexInSubTree)
            is += 1
        is
    }


    //
    // RADIX BASED METHODS
    //

    private[immutable] final def getElement(index: Int, xor: Int): A = {
        if /* level = 0 */ (xor < (1 << 5)) {
            display0(index & 31).asInstanceOf[A]
        } else if /* level = 1 */ (xor < (1 << 10)) {
            display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else if /* level = 2 */ (xor < (1 << 15)) {
            display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else if /* level = 3 */ (xor < (1 << 20)) {
            display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else if /* level = 4 */ (xor < (1 << 25)) {
            display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]((index >> 15) & 31).asInstanceOf[Array[AnyRef]]((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else if /* level = 5 */ (xor < (1 << 30)) {
            display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]((index >> 20) & 31).asInstanceOf[Array[AnyRef]]((index >> 15) & 31).asInstanceOf[Array[AnyRef]]((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]
        } else /* level < 0 || 5 < level */ {
            throw new IllegalArgumentException()
        }
    }


    // go to specific position
    // requires structure is at pos oldIndex = xor ^ index,
    // ensures structure is at pos index
    private[immutable] final def gotoPos(index: Int, xor: Int): Unit = {
        if /* level = 0 */ (xor < (1 << 5)) {
            // could maybe removed
        } else if /* level = 1 */ (xor < (1 << 10)) {
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if /* level = 2 */ (xor < (1 << 15)) {
            val d1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display1 = d1
            display0 = d1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if /* level = 3 */ (xor < (1 << 20)) {
            val d2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            val d1 = d2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display2 = d2
            display1 = d1
            display0 = d1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if /* level = 4 */ (xor < (1 << 25)) {
            val d3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            val d2 = d3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            val d1 = d2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display3 = d3
            display2 = d2
            display1 = d1
            display0 = d1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if /* level = 5 */ (xor < (1 << 30)) {
            val d4 = display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
            val d3 = d4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            val d2 = d3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            val d1 = d2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display4 = d4
            display3 = d3
            display2 = d2
            display1 = d1
            display0 = d1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else /* level < 0 || 5 < level */ {
            throw new IllegalArgumentException()
        }
    }


    // xor: oldIndex ^ index
    private[immutable] final def gotoNextBlockStart(index: Int, xor: Int): Unit = {
        // goto block start pos
        if (xor < (1 << 10)) {
            // level = 1
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 15)) {
            // level = 2
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 20)) {
            // level = 3
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2(0).asInstanceOf[Array[AnyRef]]
            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 25)) {
            // level = 4
            display3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            display2 = display3(0).asInstanceOf[Array[AnyRef]]
            display1 = display2(0).asInstanceOf[Array[AnyRef]]
            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 30)) {
            // level = 5
            display4 = display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
            display3 = display4(0).asInstanceOf[Array[AnyRef]]
            display2 = display3(0).asInstanceOf[Array[AnyRef]]
            display1 = display2(0).asInstanceOf[Array[AnyRef]]
            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        } else {
            // level = 6
            throw new IllegalArgumentException()
        }
    }

    // xor: oldIndex ^ index
    private[immutable] final def gotoPrevBlockStart(index: Int, xor: Int): Unit = {
        // goto block start pos
        if (xor < (1 << 10)) {
            // level = 1
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 15)) {
            // level = 2
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 20)) {
            // level = 3
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2(31).asInstanceOf[Array[AnyRef]]
            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 25)) {
            // level = 4
            display3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            display2 = display3(31).asInstanceOf[Array[AnyRef]]
            display1 = display2(31).asInstanceOf[Array[AnyRef]]
            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        } else if (xor < (1 << 30)) {
            // level = 5
            display4 = display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
            display3 = display4(31).asInstanceOf[Array[AnyRef]]
            display2 = display3(31).asInstanceOf[Array[AnyRef]]
            display1 = display2(31).asInstanceOf[Array[AnyRef]]
            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        } else {
            // level = 6
            throw new IllegalArgumentException()
        }
    }

    // USED BY APPENDED

    // xor: oldIndex ^ index
    private[immutable] final def setUpNextBlockStartTailWritable(index: Int, xor: Int): Unit = {
        // goto block start pos
        if /* level = 1 */ (xor < (1 << 10)) {
            if (depth == 1) {
                display1 = new Array(2 + TREE_INVARIANTS)
                display1(0) = display0
                depth += 1
            } else {
                val len = display1.length
                display1 = copyOf(display1, len, len + 1)
            }
            display0 = new Array(TREE_BRANCH_WIDTH)
        }

        else if /* level = 2 */ (xor < (1 << 15)) {
            if (depth == 2) {
                display2 = new Array(2 + TREE_INVARIANTS)
                display2(0) = display1
                depth += 1
            } else {
                val len = display2.length
                display2 = copyOf(display2, len, len + 1)
            }
            display0 = new Array(TREE_BRANCH_WIDTH)
            display1 = new Array(1 + TREE_INVARIANTS)
        } else if /* level = 3 */ (xor < (1 << 20)) {
            if (depth == 3) {
                display3 = new Array(2 + TREE_INVARIANTS)
                display3(0) = display2
                depth += 1
            } else {
                val len = display3.length
                display3 = copyOf(display3, len, len + 1)
            }
            display0 = new Array(TREE_BRANCH_WIDTH)
            display1 = new Array(1 + TREE_INVARIANTS)
            display2 = new Array(1 + TREE_INVARIANTS)
        } else if /* level = 4 */ (xor < (1 << 25)) {
            if (depth == 4) {
                display4 = new Array(2 + TREE_INVARIANTS)
                display4(0) = display3
                depth += 1
            } else {
                val len = display4.length
                display4 = copyOf(display4, len, len + 1)
            }
            display0 = new Array(TREE_BRANCH_WIDTH)
            display1 = new Array(1 + TREE_INVARIANTS)
            display2 = new Array(1 + TREE_INVARIANTS)
            display3 = new Array(1 + TREE_INVARIANTS)
        } else if /* level = 5 */ (xor < (1 << 30)) {
            if (depth == 5) {
                display5 = new Array(2 + TREE_INVARIANTS)
                display5(0) = display4
                depth += 1
            } else {
                val len = display5.length
                display5 = copyOf(display5, len, len + 1)
            }
            display0 = new Array(TREE_BRANCH_WIDTH)
            display1 = new Array(1 + TREE_INVARIANTS)
            display2 = new Array(1 + TREE_INVARIANTS)
            display3 = new Array(1 + TREE_INVARIANTS)
            display4 = new Array(1 + TREE_INVARIANTS)
        } else /* level < 0 || 5 < level */ {
            throw new IllegalArgumentException()
        }
    }

    // USED BY BUILDER

    // xor: oldIndex ^ index
    private[immutable] final def gotoNextBlockStartWritable(index: Int, xor: Int): Unit = {
        // goto block start pos
        if /* level = 1 */ (xor < (1 << 10)) {
            if (depth == 1) {
                display1 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
                display1(0) = display0
                depth += 1
            }
            display0 = new Array(TREE_BRANCH_WIDTH)
            display1((index >> 5) & 31) = display0
        } else if /* level = 2 */ (xor < (1 << 15)) {
            if (depth == 2) {
                display2 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
                display2(0) = display1
                depth += 1
            }
            display0 = new Array(TREE_BRANCH_WIDTH)
            display1 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
        } else if /* level = 3 */ (xor < (1 << 20)) {
            if (depth == 3) {
                display3 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
                display3(0) = display2
                depth += 1
            }
            display0 = new Array(TREE_BRANCH_WIDTH)
            display1 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
            display2 = new Array(3)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
            display3((index >> 15) & 31) = display2
        } else if /* level = 4 */ (xor < (1 << 25)) {
            if (depth == 4) {
                display4 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
                display4(0) = display3
                depth += 1
            }
            display0 = new Array(TREE_BRANCH_WIDTH)
            display1 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
            display2 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
            display3 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
            display3((index >> 15) & 31) = display2
            display4((index >> 20) & 31) = display3
        } else if /* level = 5 */ (xor < (1 << 30)) {
            if (depth == 5) {
                display5 = new Array(33)
                display5(0) = display4
                depth += 1
            }
            display0 = new Array(TREE_BRANCH_WIDTH)
            display1 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
            display2 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
            display3 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
            display4 = new Array(TREE_BRANCH_WIDTH + TREE_INVARIANTS)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
            display3((index >> 15) & 31) = display2
            display4((index >> 20) & 31) = display3
            display5((index >> 25) & 31) = display4
        } else /* level < 0 || 5 < level */ {
            throw new IllegalArgumentException()
        }
    }


    private[immutable] final def copyDisplays(_depth: Int, _focus: Int): Unit = {
        if (RRBVector.useAssertions) {
            assert(0 < _depth && _depth <= 6)
            assert((_focus >> (5 * _depth)) == 0, (_depth, _focus))
        }
        _depth match {
            case 1 =>
            case 2 =>
                val f1 = _focus >> 5
                display1 = copyOf(display1, f1 + 1, f1 + 2)
            case 3 =>
                val f2 = _focus >> 10
                val f1 = (_focus >> 5) & 31
                display2 = copyOf(display2, f2 + 1, f2 + 2)
                display1 = copyOf(display1, f1 + 1, f1 + 2)
            case 4 =>
                val f3 = _focus >> 15
                val f2 = (_focus >> 10) & 31
                val f1 = (_focus >> 5) & 31
                display3 = copyOf(display3, f3 + 1, f3 + 2)
                display2 = copyOf(display2, f2 + 1, f2 + 2)
                display1 = copyOf(display1, f1 + 1, f1 + 2)
            case 5 =>
                val f4 = _focus >> 20
                val f3 = (_focus >> 15) & 31
                val f2 = (_focus >> 10) & 31
                val f1 = (_focus >> 5) & 31
                display4 = copyOf(display4, f4 + 1, f4 + 2)
                display3 = copyOf(display3, f3 + 1, f3 + 2)
                display2 = copyOf(display2, f2 + 1, f2 + 2)
                display1 = copyOf(display1, f1 + 1, f1 + 2)
            case 6 =>
                val f5 = _focus >> 25
                val f4 = (_focus >> 20) & 31
                val f3 = (_focus >> 15) & 31
                val f2 = (_focus >> 10) & 31
                val f1 = (_focus >> 5) & 31
                display5 = copyOf(display5, f5 + 1, f5 + 2)
                display4 = copyOf(display4, f4 + 1, f4 + 2)
                display3 = copyOf(display3, f3 + 1, f3 + 2)
                display2 = copyOf(display2, f2 + 1, f2 + 2)
                display1 = copyOf(display1, f1 + 1, f1 + 2)
            case _ => throw new IllegalStateException()
        }
    }

    private[immutable] final def copyDisplaysTop(_currentDepth: Int, _focusRelax: Int): Unit = {
        _currentDepth match {
            case 2 =>
                val f1 = (_focusRelax >> 5) & 31
                display1 = copyOf(display1, f1 + 1, f1 + 2)
            case 3 =>
                val f2 = (_focusRelax >> 10) & 31
                display2 = copyOf(display2, f2 + 1, f2 + 2)
            case 4 =>
                val f3 = (_focusRelax >> 15) & 31
                display3 = copyOf(display3, f3 + 1, f3 + 2)
            case 5 =>
                val f4 = (_focusRelax >> 20) & 31
                display4 = copyOf(display4, f4 + 1, f4 + 2)
            case 6 =>
                val f5 = (_focusRelax >> 25) & 31
                display5 = copyOf(display5, f5 + 1, f5 + 2)
            case _ => throw new IllegalStateException()
        }
        if (_currentDepth < depth)
            copyDisplaysTop(_currentDepth + 1, _focusRelax)

    }

    private[immutable] final def stabilize(_depth: Int, _focus: Int): Unit = {
        if (RRBVector.useAssertions) {
            assert(0 < _depth && _depth <= 6)
            assert((_focus >> (5 * _depth)) == 0, (_depth, _focus))
        }
        _depth match {
            case 1 =>
            case 2 =>
                display1(_focus >> 5) = display0
            case 3 =>
                display2(_focus >> 10) = display1
                display1((_focus >> 5) & 31) = display0
            case 4 =>
                display3(_focus >> 15) = display2
                display2((_focus >> 10) & 31) = display1
                display1((_focus >> 5) & 31) = display0
            case 5 =>
                display4(_focus >> 20) = display3
                display3((_focus >> 15) & 31) = display2
                display2((_focus >> 10) & 31) = display1
                display1((_focus >> 5) & 31) = display0
            case 6 =>
                display5(_focus >> 25) = display4
                display4((_focus >> 20) & 31) = display3
                display3((_focus >> 15) & 31) = display2
                display2((_focus >> 10) & 31) = display1
                display1((_focus >> 5) & 31) = display0
            case _ => throw new IllegalStateException()
        }
    }

    private[immutable] final def copyOf(a: Array[AnyRef], numElements: Int, newSize: Int) = {
        if (RRBVector.useAssertions) {
            assert(a != null)
            assert(numElements <= newSize, (numElements, newSize))
            assert(numElements <= a.length, (numElements, a.length))
        }
        val b = new Array[AnyRef](newSize)
        Platform.arraycopy(a, 0, b, 0, numElements)
        b
    }

    /**
     * requires focus on the last element
     */
    private[immutable] final def closeTailLeaf(): Unit = {
        val cutIndex = (focus & 31) + 1
        display0 = copyOf(display0, cutIndex, cutIndex)
    }

    private[immutable] final def mergeLeafs(leaf0: Array[AnyRef], length0: Int, leaf1: Array[AnyRef], length1: Int): Array[AnyRef] = {
        if (RRBVector.useAssertions) {
            assert(length0 + length1 <= 32)
        }
        val newLeaf = new Array[AnyRef](length0 + length1)
        Platform.arraycopy(leaf0, 0, newLeaf, 0, length0)
        Platform.arraycopy(leaf1, 0, newLeaf, length0, length1)
        newLeaf
    }

}

class RRBVectorIterator[+A](startIndex: Int, endIndex: Int)
  extends AbstractIterator[A]
  with Iterator[A]
  with RRBVectorPointer[A@uncheckedVariance] {

    private var blockIndex: Int = _
    private var lo: Int = _
    private var endLo: Int = _

    private var _hasNext: Boolean = startIndex < endIndex

    def hasNext = _hasNext

    private[immutable] final def resetIterator(): Unit = {
        if (focusStart <= startIndex && startIndex < focusEnd)
            gotoPos(startIndex, startIndex ^ focus)
        else
            gotoPosRelaxed(startIndex, 0, endIndex, depth)
        blockIndex = focusStart
        lo = startIndex - focusStart
        endLo = math.min(focusEnd - blockIndex, TREE_BRANCH_WIDTH)
    }

    def next(): A = {
        if (!_hasNext) throw new NoSuchElementException("reached iterator end")

        val res = display0(lo).asInstanceOf[A]
        lo += 1

        if (lo == endLo) {
            val newBlockIndex = blockIndex + endLo
            if (newBlockIndex < focusEnd) {
                gotoNextBlockStart(newBlockIndex, blockIndex ^ newBlockIndex)
            } else if (newBlockIndex < endIndex) {
                gotoPosRelaxed(newBlockIndex, 0, endIndex, depth)
            } else {
                _hasNext = false
            }
            blockIndex = newBlockIndex
            lo = 0
            endLo = math.min(focusEnd - blockIndex, TREE_BRANCH_WIDTH)
        }

        res
    }
}


class RRBVectorReverseIterator[+A](startIndex: Int, endIndex: Int)
  extends AbstractIterator[A]
  with Iterator[A]
  with RRBVectorPointer[A@uncheckedVariance] {

    private var blockIndexInFocus: Int = _
    private var lo: Int = _
    private var endLo: Int = _

    private var _hasNext: Boolean = startIndex < endIndex

    def hasNext = _hasNext

    private[immutable] final def initIterator(): Unit = {
        val idx = endIndex - 1
        if (focusStart <= idx && idx < focusEnd)
            gotoPos(idx, idx ^ focus)
        else
            gotoPosRelaxed(idx, 0, endIndex, depth)
        val indexInFocus = idx - focusStart
        blockIndexInFocus = indexInFocus & ~31
        lo = indexInFocus & 31
        endLo = math.max(startIndex - focusStart - blockIndexInFocus, 0)
    }

    def next(): A = {
        if (!_hasNext) throw new NoSuchElementException("reached iterator end")

        val res = display0(lo).asInstanceOf[A]
        lo -= 1

        if (lo < endLo) {
            val newBlockIndex = blockIndexInFocus - TREE_BRANCH_WIDTH
            if (focusStart <= newBlockIndex) {
                gotoPrevBlockStart(newBlockIndex, newBlockIndex ^ blockIndexInFocus)
                blockIndexInFocus = newBlockIndex
                lo = 31
                endLo = math.max(startIndex - focusStart - focus, 0)
            } else if (startIndex <= blockIndexInFocus - 1) {
                val newIndexInFocus = blockIndexInFocus - 1
                gotoPosRelaxed(newIndexInFocus, 0, endIndex, depth)
                blockIndexInFocus = newIndexInFocus & ~31
                lo = newIndexInFocus & 31
                endLo = math.max(startIndex - focusStart - blockIndexInFocus, 0)
            } else {
                _hasNext = false
            }
        }

        res
    }
}


final class RRBVectorBuilder[A]() extends mutable.Builder[A, RRBVector[A]] with RRBVectorPointer[A@uncheckedVariance] {

    display0 = new Array[AnyRef](TREE_BRANCH_WIDTH)
    depth = 1
    hasWritableTail = true

    private var blockIndex = 0
    private var lo = 0

    def +=(elem: A): this.type = {
        if (lo >= TREE_BRANCH_WIDTH) {
            val newBlockIndex = blockIndex + TREE_BRANCH_WIDTH
            gotoNextBlockStartWritable(newBlockIndex, newBlockIndex ^ blockIndex)
            blockIndex = newBlockIndex
            lo = 0
        }

        display0(lo) = elem.asInstanceOf[AnyRef]
        lo += 1
        this
    }

    override def ++=(xs: TraversableOnce[A]): this.type =
        super.++=(xs)

    def result(): RRBVector[A] = {
        val size = blockIndex + lo
        if (size == 0)
            return RRBVector.empty
        val vec = new RRBVector[A](size)

        vec.initFrom(this)

        // TODO: Optimization: check if stabilization is really necessary on all displays based on the last index.
        val _depth = depth
        if (_depth > 1) {
            vec.copyDisplays(_depth, size - 1)
            if (_depth > 2)
                vec.stabilize(_depth, size - 1)
        }

        vec.gotoPos(0, size - 1)
        vec.focus = 0
        vec.focusEnd = size
        vec.focusDepth = _depth

        if (RRBVector.useAssertions) {
            vec.assertVectorInvariant()
        }

        vec
    }

    def clear(): Unit = {
        display0 = new Array[AnyRef](TREE_BRANCH_WIDTH)
        display1 = null
        display2 = null
        display3 = null
        display4 = null
        display5 = null
        depth = 1
        blockIndex = 0
        lo = 0
        hasWritableTail = true
    }
}