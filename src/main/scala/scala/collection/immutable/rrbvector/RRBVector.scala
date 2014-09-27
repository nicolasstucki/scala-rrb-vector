package scala
package collection
package immutable
package rrbvector

import java.io.Serializable

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
        vec.display0 = new Array[AnyRef](32)
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

    override /*IterableLike*/ def isEmpty: Boolean = endIndex == 0

    // Iterators

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

    // SeqLike

    def /*SeqLike*/ apply(index: Int): A = {
        if (focusStart <= index && index < focusEnd) {
            val indexInFocus = index - focusStart
            getElem(indexInFocus, indexInFocus ^ focus)
        } else if (0 <= index && index < endIndex) {
            gotoPosRelaxed(index, 0, endIndex, depth)
            display0((index - focusStart) & 31).asInstanceOf[A]
        } else {
            throw new IndexOutOfBoundsException(index.toString)
        }

    }

    override def /*SeqLike*/ :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[RRBVector[A], B, That]): That =
        if (bf eq IndexedSeq.ReusableCBF) appendedBack(elem).asInstanceOf[That] // just ignore bf
        else super.:+(elem)(bf)

    // IterableLike

    override /*IterableLike*/ def head: A = {
        if (isEmpty) throw new UnsupportedOperationException("empty.head")
        apply(0)
    }

    override /*IterableLike*/ def slice(from: Int, until: Int): RRBVector[A] = take(until).drop(from)

    override /*IterableLike*/ def splitAt(n: Int): (RRBVector[A], RRBVector[A]) = (take(n), drop(n))

    override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[RRBVector[A], B, That]): That = {
        if (bf eq IndexedSeq.ReusableCBF) {
            if (that.isEmpty) this.asInstanceOf[That]
            else {
                that match {
                    case vec: RRBVector[B] => this.concatenated[B](vec).asInstanceOf[That]
                    case _ => super.++(that)
                }
            }
        } else super.++(that.seq)
    }

    // TraversableLike

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


    private def appendedBack[B >: A](value: B): RRBVector[B] = {
        val endIndex = this.endIndex
        if (endIndex == 0) return RRBVector.singleton[B](value)

        val vec = new RRBVector[A](endIndex + 1)
        vec.initFrom(this)

        // Focus on the right most branch
        vec.gotoIndex(endIndex - 1, endIndex)

        // Make sure that vec.display0 is writable
        if (this.hasWritableTail) this.hasWritableTail = false
        else vec.makeWritableTail(endIndex)

        val elemIndexInBlock = (endIndex - vec.focusStart) & 31
        vec.display0(elemIndexInBlock) = value.asInstanceOf[AnyRef]
        vec.focusEnd += 1
        vec.hasWritableTail = elemIndexInBlock < 31

        // TODO: update sizes
        //        withComputedSizes(vec.display2, 2)
        //        withComputedSizes(vec.display3, 3)
        //        withComputedSizes(vec.display4, 4)
        //        withComputedSizes(vec.display5, 5)

        if (RRBVector.useAssertions) {
            vec.assertVectorInvariant()
        }

        vec
    }

    private[immutable] def concatenated[B >: A](that: RRBVector[B]): RRBVector[B] = {
        if (RRBVector.useAssertions) {
            this.assertVectorInvariant()
            that.assertVectorInvariant()
            assert(this.length > 0)
            assert(that.length > 0)
        }

        this.closeTail()
        that.closeTail()

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

        math.max(this.depth, that.depth) match {
            case 1 =>
                val concat1 = rebalanced(this.display0, null, that.display0, 1)
                initVector(vec, concat1, 1)
            case 2 =>
                val concat1 = rebalanced(this.display0, null, that.display0, 1)
                val concat2 = rebalanced(this.display1, concat1, that.display1, 2)
                initVector(vec, concat2, 2)
            case 3 =>
                val concat1 = rebalanced(this.display0, null, that.display0, 1)
                val concat2 = rebalanced(this.display1, concat1, that.display1, 2)
                val concat3 = rebalanced(this.display2, concat2, that.display2, 3)
                initVector(vec, concat3, 3)
            case 4 =>
                val concat1 = rebalanced(this.display0, null, that.display0, 1)
                val concat2 = rebalanced(this.display1, concat1, that.display1, 2)
                val concat3 = rebalanced(this.display2, concat2, that.display2, 3)
                val concat4 = rebalanced(this.display3, concat3, that.display3, 4)
                initVector(vec, concat4, 4)
            case 5 =>
                val concat1 = rebalanced(this.display0, null, that.display0, 1)
                val concat2 = rebalanced(this.display1, concat1, that.display1, 2)
                val concat3 = rebalanced(this.display2, concat2, that.display2, 3)
                val concat4 = rebalanced(this.display3, concat3, that.display3, 4)
                val concat5 = rebalanced(this.display4, concat4, that.display4, 5)
                initVector(vec, concat5, 5)
            case 6 =>
                val concat1 = rebalanced(this.display0, null, that.display0, 1)
                val concat2 = rebalanced(this.display1, concat1, that.display1, 2)
                val concat3 = rebalanced(this.display2, concat2, that.display2, 3)
                val concat4 = rebalanced(this.display3, concat3, that.display3, 4)
                val concat5 = rebalanced(this.display4, concat4, that.display4, 5)
                val concat6 = rebalanced(this.display5, concat5, that.display5, 6)
                initVector(vec, concat6, 6)
            case _ => throw new IllegalStateException()

        }
        if (RRBVector.useAssertions) {
            vec.assertVectorInvariant()
        }
        vec
    }

    private def closeTail() = {
        gotoIndex(endIndex - 1, endIndex)
        closeTailLeaf()
        stabilize(depth, focus)
        hasWritableTail = false
    }

    private def rebalanced(displayLeft: Array[AnyRef], concat: Array[AnyRef], displayRight: Array[AnyRef], depth: Int): Array[AnyRef] = {

        @inline def displayLength(display: Array[AnyRef]): Int = if (display != null) display.length else 0

        val thisDisplay1Length = displayLength(displayLeft)
        val concatLength = displayLength(concat)
        val thatDisplay1Length = displayLength(displayRight)
        var offset = 0

        val alen =
            if (depth == 1) thisDisplay1Length + thatDisplay1Length
            else thisDisplay1Length + concatLength + thatDisplay1Length - 1 - (if (thisDisplay1Length == 0) 0 else 2) - (if (thatDisplay1Length == 0) 0 else 2)
        val all = new Array[AnyRef](alen)
        if (thisDisplay1Length > 0) {
            val s = thisDisplay1Length - (if (depth == 1) 0 else 2)
            Platform.arraycopy(displayLeft, 0, all, 0, s)
            offset += s
        }
        if (concatLength > 0) {
            Platform.arraycopy(concat, 0, all, offset, concat.length - 1)
            offset += concat.length - 1
        }
        if (thatDisplay1Length > 0) {
            val s = thatDisplay1Length - (if (depth == 1) 0 else 2)
            Platform.arraycopy(displayRight, if (depth == 1) 0 else 1, all, offset, s)
        }

        // LOAD CURRENT SIZESs
        val szs = new Array[Int](alen)
        var tcnt = 0
        var i = 0
        while (i < alen) {
            val sz = sizeSlot(all(i), depth - 1)
            szs(i) = sz
            tcnt += sz
            i += 1
        }

        // COMPUTE NEW SIZES
        // Calculate the ideal or effective number of slots
        // used to limit number of extra slots.
        val effslt = tcnt / 32 + 1 // <-- "desired" number of slots???

        val MinWidth = 31 // min number of slots allowed...

        var nalen = alen
        // note - this makes multiple passes, can be done in one.
        // redistribute the smallest slots until only the allowed extras remain
        val EXTRAS = 2
        while (nalen > effslt + EXTRAS) {
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

        copiedAcross(all, szs, nalen, depth)
    }

    private def withComputedSizes(node: Array[AnyRef], depth: Int): Array[AnyRef] = {
        if (RRBVector.useAssertions) {
            assert(!this.hasWritableTail)
            assert(node != null)
            assert(0 <= depth && depth <= 6)
        }
        if (depth == 1) {
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
        } else {
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
        }
        node
    }

    private def treeSize(tree: Array[AnyRef], depth: Int): Int = {
        if (RRBVector.useAssertions) {
            assert(!this.hasWritableTail)
            assert(tree != null)
            assert(0 <= depth && depth <= 6)
        }
        if (depth == 0) tree.length
        else {
            val treeSizes = tree(tree.length - 1).asInstanceOf[Array[Int]]
            if (treeSizes != null) treeSizes(treeSizes.length - 1)
            else (tree.length - 2) * (1 << (5 * depth)) + treeSize(tree(tree.length - 2).asInstanceOf[Array[AnyRef]], depth - 1)
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
                val node = new Array[AnyRef](math.min(33, lengthSizes + 1 - (iTop << 5)))

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

            assert((depth == 0 && display0 == null) || (depth > 0 && display0 != null), s"depth==0 <==> display0==null ${(depth, display0)}")
            assert((depth <= 1 && display1 == null) || (depth > 1 && display1 != null), s"depth<=1 <==> display1==null ${(depth, display1)}")
            assert((depth <= 2 && display2 == null) || (depth > 2 && display2 != null), s"depth<=2 <==> display2==null ${(depth, display2)}")
            assert((depth <= 3 && display3 == null) || (depth > 3 && display3 != null), s"depth<=3 <==> display3==null ${(depth, display3)}")
            assert((depth <= 4 && display4 == null) || (depth > 4 && display4 != null), s"depth<=4 <==> display4==null ${(depth, display4)}")
            assert((depth <= 5 && display5 == null) || (depth > 5 && display5 != null), s"depth<=5 <==> display5==null ${(depth, display5)}")

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

            // TODO assert tree sizes and block sizes
        }
    }

}


private[immutable] trait RRBVectorPointer[A] {

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
        initFocus(that.focus, that.focusStart, that.focusEnd, that.focusDepth)
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

    private[immutable] final def initFocus(focus: Int, focusStart: Int, focusEnd: Int, focusDepth: Int) = {
        this.focus = focus
        this.focusStart = focusStart
        this.focusEnd = focusEnd
        this.focusDepth = focusDepth
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

    /**
     * Assume it is focused on the end
     */
    private[immutable] def makeWritableTail(endIndex: Int) = {
        // + Assume that display0 is the right most block
        val _focusDepth = this.focusDepth
        val endIndexInFocus = endIndex - focusStart
        if /* space left in current block */ ((endIndexInFocus & 31) != 0) {
            display0 = copyOf(display0, display0.length, 32)
            if (_focusDepth == depth) copyDisplays(_focusDepth, focus)
            else relaxedStabilize()
            stabilize(depth, focus | focusRelax)
        } else if /* is rb-tree */ (_focusDepth == depth) {
            copyDisplays(_focusDepth, focus)
            // TODO: Improve performance. May not need to stabilize all the way down
            stabilize(depth, focus)
            gotoNextBlockStartWritable(endIndexInFocus, endIndexInFocus ^ focus, true)
            focusDepth = depth
            focus = endIndexInFocus
            focusEnd = endIndexInFocus
        } else /* is rrb-tree */ {
            // TODO: Improve performance. May not need to stabilize all the way down
            relaxedStabilize()
            gotoNextBlockStartWritable(endIndexInFocus, (endIndexInFocus) ^ focus, true)
            assert(false, "implementation missing")
            // TODO: gotoNextBlockStartWritable non focused part, set focus start

            focus = endIndexInFocus
            focusStart = ???
            focusEnd = endIndexInFocus
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
            gotoPosRelaxed(index, if (is == 0) _startIndex else _startIndex + sizes(is - 1), _startIndex + sizes(is), _depth - 1, _focusRelax | (is << (5 * depth - 5)))
        } else {
            val indexInFocus = index - _startIndex
            gotoPos(indexInFocus, 1 << (5 * (_depth - 1)))
            initFocus(indexInFocus, _startIndex, _endIndex, _depth)
            this.focusRelax = _focusRelax
        }
    }

    private final def getRelaxedIndex(indexInSubTree: Int, sizes: Array[Int]) = {
        var is = 0 //ix >> ((height - 1) * WIDTH_SHIFT)
        while (sizes(is) <= indexInSubTree)
            is += 1
        is
    }

    private[immutable] final def relaxedStabilize(): Unit = {
        copyDisplays(focusDepth, focus)
        copyDisplaysTop(focusDepth + 1, focusRelax)
    }


    //
    // RADIX BASED METHODS
    //

    private[immutable] final def getElem(index: Int, xor: Int): A = {
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
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if /* level = 3 */ (xor < (1 << 20)) {
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if /* level = 4 */ (xor < (1 << 25)) {
            display3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if /* level = 5 */ (xor < (1 << 30)) {
            display4 = display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
            display3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
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


    // USED BY BUILDER

    // xor: oldIndex ^ index
    private[immutable] final def gotoNextBlockStartWritable(index: Int, xor: Int, closed: Boolean): Unit = {
        val newBrachBlockSize = if (closed) 2 else 33
        // goto block start pos
        if /* level = 1 */ (xor < (1 << 10)) {
            if (depth == 1) {
                display1 = new Array(if (closed) 3 else 33)
                display1(0) = display0
                depth += 1
            }
            display0 = new Array(32)
            display1((index >> 5) & 31) = display0
        } else if /* level = 2 */ (xor < (1 << 15)) {
            if (depth == 2) {
                display2 = new Array(if (closed) 3 else 33)
                display2(0) = display1
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(newBrachBlockSize)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
        } else if /* level = 3 */ (xor < (1 << 20)) {
            if (depth == 3) {
                display3 = new Array(if (closed) 3 else 33)
                display3(0) = display2
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(newBrachBlockSize)
            display2 = new Array(newBrachBlockSize)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
            display3((index >> 15) & 31) = display2
        } else if /* level = 4 */ (xor < (1 << 25)) {
            if (depth == 4) {
                display4 = new Array(if (closed) 3 else 33)
                display4(0) = display3
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(newBrachBlockSize)
            display2 = new Array(newBrachBlockSize)
            display3 = new Array(newBrachBlockSize)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
            display3((index >> 15) & 31) = display2
            display4((index >> 20) & 31) = display3
        } else if /* level = 5 */ (xor < (1 << 30)) {
            if (depth == 5) {
                display5 = new Array(if (closed) 3 else 33)
                display5(0) = display4
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(newBrachBlockSize)
            display2 = new Array(newBrachBlockSize)
            display3 = new Array(newBrachBlockSize)
            display4 = new Array(newBrachBlockSize)
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
        }
        _depth match {
            case 1 =>
            case 2 =>
                val f1 = (_focus >> 5) & 31
                display1 = copyOf(display1, f1 + 1, f1 + 2)
            case 3 =>
                val f2 = (_focus >> 10) & 31
                val f1 = (_focus >> 5) & 31
                display2 = copyOf(display2, f2 + 1, f2 + 2)
                display1 = copyOf(display1, f1 + 1, f1 + 2)
            case 4 =>
                val f3 = (_focus >> 15) & 31
                val f2 = (_focus >> 10) & 31
                val f1 = (_focus >> 5) & 31
                display3 = copyOf(display3, f3 + 1, f3 + 2)
                display2 = copyOf(display2, f2 + 1, f2 + 2)
                display1 = copyOf(display1, f1 + 1, f1 + 2)
            case 5 =>
                val f4 = (_focus >> 20) & 31
                val f3 = (_focus >> 15) & 31
                val f2 = (_focus >> 10) & 31
                val f1 = (_focus >> 5) & 31
                display4 = copyOf(display4, f4 + 1, f4 + 2)
                display3 = copyOf(display3, f3 + 1, f3 + 2)
                display2 = copyOf(display2, f2 + 1, f2 + 2)
                display1 = copyOf(display1, f1 + 1, f1 + 2)
            case 6 =>
                val f5 = (_focus >> 25) & 31
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
        }
        _depth match {
            case 1 =>
            case 2 =>
                display1((_focus >> 5) & 31) = display0
            case 3 =>
                display2((_focus >> 10) & 31) = display1
                display1((_focus >> 5) & 31) = display0
            case 4 =>
                display3((_focus >> 15) & 31) = display2
                display2((_focus >> 10) & 31) = display1
                display1((_focus >> 5) & 31) = display0
            case 5 =>
                display4((_focus >> 15) & 31) = display3
                display3((_focus >> 15) & 31) = display2
                display2((_focus >> 10) & 31) = display1
                display1((_focus >> 5) & 31) = display0
            case 6 =>
                display5((_focus >> 20) & 31) = display4
                display4((_focus >> 15) & 31) = display3
                display3((_focus >> 15) & 31) = display2
                display2((_focus >> 10) & 31) = display1
                display1((_focus >> 5) & 31) = display0
            case _ => throw new IllegalStateException()
        }
    }

    private[immutable] final def copyOf(a: Array[AnyRef], numElements: Int, newSize: Int) = {
        if (RRBVector.useAssertions) {
            assert(a != null)
            assert(numElements <= newSize)
            assert(numElements <= a.length)
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
        endLo = math.min(focusEnd - blockIndex, 32)
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
            endLo = math.min(focusEnd - blockIndex, 32)
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
            val newBlockIndex = blockIndexInFocus - 32
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

    display0 = new Array[AnyRef](32)
    depth = 1
    hasWritableTail = true

    private var blockIndex = 0
    private var lo = 0

    def +=(elem: A): this.type = {
        if (lo >= display0.length) {
            val newBlockIndex = blockIndex + 32
            gotoNextBlockStartWritable(newBlockIndex, blockIndex ^ newBlockIndex, false)
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
        vec.gotoPos(size - 1, size - 1)
        if (depth > 1) {
            vec.copyDisplays(depth, size - 1)
            vec.stabilize(depth, size - 1)
        }

        vec.gotoPos(0, size - 1)
        vec.focus = 0
        vec.focusEnd = size
        vec.focusDepth = depth

        if (RRBVector.useAssertions) {
            vec.assertVectorInvariant()
        }

        vec
    }

    def clear(): Unit = {
        display0 = new Array[AnyRef](32)
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