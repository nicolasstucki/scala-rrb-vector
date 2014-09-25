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
        //        if (bf eq IndexedSeq.ReusableCBF) {
        //            if (that.isEmpty) this.asInstanceOf[That]
        //            else {
        //                that match {
        //                    case vec: RRBVector[B] => this.concatenated[B](vec).asInstanceOf[That]
        //                    case _ => super.++(that)
        //                }
        //            }
        //        }
        //        else
        super.++(that.seq)
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

        vec
    }

    //    private def concatenated[B >: A](that: RRBVector[B]): RRBVector[B] = {
    //        assert(this.length > 0)
    //        assert(that.length > 0)
    //
    //        val thisLength = this.endIndex
    //        val thatLength = that.endIndex
    //        val newLength = thisLength + thatLength
    //        if (newLength <= 32) {
    //            val vec = new RRBVector[B](newLength)
    //            vec.display0 = mergeLeafs(this.display0, thisLength, that.display0, thatLength)
    //            vec.depth = 1
    //            vec.initFocus(0, 0, newLength, 1, 0)
    //            vec
    //        } else {
    //            this.closeTail()
    //            that.closeTail()
    //
    //            val vec = new RRBVector[B](newLength)
    //            val thisDepth = this.depth
    //            val thatDepth = that.depth
    //            val thisRoot = this.root()
    //            val thatRoot = that.root()
    //            val balancedBranch =
    //                if (thisDepth > thatDepth) {
    //                    val leftBranch = thisRoot.asInstanceOf[Array[AnyRef]]
    //                    val concatenatedBranch = concatenatedSubTree(leftBranch(leftBranch.length - 1), thisDepth - 1, thatRoot, thatDepth)
    //                    vec.rebalanced(leftBranch, concatenatedBranch, null, thisDepth, true)
    //                } else if (thisDepth < thatDepth) {
    //                    val rightBranch = thatRoot.asInstanceOf[Array[AnyRef]]
    //                    val concatenatedBranch = concatenatedSubTree(thisRoot, thisDepth, rightBranch(1), thatDepth - 1)
    //                    vec.rebalanced(null, concatenatedBranch, rightBranch, thatDepth, true)
    //                } else if (thisDepth == 1 /* && thatDepth == 1 */ ) {
    //                    val concatenatedBranch = concatenatedSubTree(thisRoot, thisDepth, thatRoot, thatDepth)
    //                    vec.depth = 1
    //                    vec.rebalanced(null, concatenatedBranch, null, thatDepth, true)
    //                } else {
    //                    val leftBranch = thisRoot.asInstanceOf[Array[AnyRef]]
    //                    val rightBranch = thatRoot.asInstanceOf[Array[AnyRef]]
    //                    val concatenatedBranch = concatenatedSubTree(leftBranch(leftBranch.length - 1), thisDepth - 1, rightBranch(1), thatDepth - 1)
    //                    vec.rebalanced(leftBranch, concatenatedBranch, rightBranch, thisDepth, true)
    //                }
    //            val sizedBalancedBranch = setSizes(balancedBranch, vec.depth)
    //            vec.initFromRoot(sizedBalancedBranch, vec.depth, newLength)
    //            vec
    //        }
    //    }

    //    private def closeTail() = {
    //        gotoIndex(endIndex - 1, endIndex)
    //        closeTailLeaf()
    //        stabilize(depth, focus)
    //        hasWritableTail = false
    //    }

    //    private final def concatenatedSubTree(leftNode: AnyRef, leftHeight: Int, rightNode: AnyRef, rightHeight: Int): Array[AnyRef] = {
    //        if (leftHeight > rightHeight) {
    //            val leftBranch = leftNode.asInstanceOf[Array[AnyRef]]
    //            val concatenatedBranch = concatenatedSubTree(leftBranch(leftBranch.length - 1), leftHeight - 1, rightNode, rightHeight)
    //            val balancedBranch = rebalanced(leftBranch, concatenatedBranch, null, leftHeight, false)
    //            balancedBranch
    //        } else if (leftHeight < rightHeight) {
    //            val rightBranch = rightNode.asInstanceOf[Array[AnyRef]]
    //            val concatenatedBranch = concatenatedSubTree(leftNode, leftHeight, rightBranch(1), rightHeight - 1)
    //            val balancedBranch = rebalanced(null, concatenatedBranch, rightBranch, rightHeight, false)
    //            balancedBranch
    //        } else if (leftHeight == 1 /* && rightHeight == 1 */ ) {
    //            araNewAbove(leftNode, rightNode)
    //        } else {
    //            // two heights the same so move down both
    //            val leftBranch = leftNode.asInstanceOf[Array[AnyRef]]
    //            val rightBranch = rightNode.asInstanceOf[Array[AnyRef]]
    //            val concatenatedBranch = concatenatedSubTree(leftBranch(leftBranch.length - 1), leftHeight - 1, rightBranch(1), rightHeight - 1)
    //            val balancedBranch = rebalanced(leftBranch, concatenatedBranch, rightBranch, leftHeight, false)
    //            balancedBranch
    //        }
    //    }

    //    private final def rebalanced(al: Array[AnyRef], ac: Array[AnyRef], ar: Array[AnyRef], height: Int, isTop: Boolean): Array[AnyRef] = {
    //        // From prototype
    //        // Put all the slots at this level in one array ++Note:  This can be avoided by indexing the sub arrays as one
    //        // remember Ara(0) is Size
    //        val all = araNewJoin(al, ac, ar)
    //
    //        // shuffle slot sizes to fit invariant
    //        val alen = all.length
    //        val szs = new Array[Int](alen)
    //
    //        var tcnt = 0
    //        // find total slots in the two levels.
    //        var i = 0
    //        while (i < alen) {
    //            val sz = sizeSlot(all(i), height - 1)
    //            szs(i) = sz
    //            tcnt += sz
    //            i += 1
    //        }
    //
    //        // szs(i) holds #slots of all(i), tcnt is sum
    //        // ---
    //
    //        // Calculate the ideal or effective number of slots
    //        // used to limit number of extra slots.
    //        val effslt = tcnt / 32 + 1 // <-- "desired" number of slots???
    //
    //        val MinWidth = 31 // min number of slots allowed...
    //
    //        var nalen = alen
    //        // note - this makes multiple passes, can be done in one.
    //        // redistribute the smallest slots until only the allowed extras remain
    //        val EXTRAS = 2
    //        while (nalen > effslt + EXTRAS) {
    //            // TR each loop iteration removes the first short block
    //            // TR what if no small one is found? doesn't check ix < szs.length,
    //            // TR we know there are small ones. what if the small ones are all at the right?
    //            // TR how do we know there is (enough) stuff right of them to balance?
    //
    //            var ix = 0
    //            // skip over any blocks large enough
    //            while (szs(ix) > MinWidth) ix += 1
    //
    //            // Found a short one so redistribute over following ones
    //            var el = szs(ix) // current size <= MinWidth
    //            do {
    //                val msz = math.min(el + szs(ix + 1), 32)
    //                szs(ix) = msz
    //                el = el + szs(ix + 1) - msz
    //
    //                ix += 1
    //            } while (el > 0)
    //
    //            // shuffle up remaining slot sizes
    //            while (ix < nalen - 1) {
    //                szs(ix) = szs(ix + 1)
    //                ix += 1
    //            }
    //            nalen -= 1
    //        }
    //
    //
    //        //println("shuffle: "+hw+ " " + (all map { (x:AnyRef) => x match {case a: Array[AnyRef] => a.mkString("{,",",","}") }} mkString))//TR
    //        //println("szs: "+szs.mkString)//TR
    //
    //
    //        // Now copy across according to model sizes in szs
    //        val nall = copyAcross(all, szs, nalen, height)
    //
    //        // nall.length = nalen + 1 (accommodate size slot)
    //
    //        // split across two nodes if greater than Width
    //        // This splitting/copying can be avoided by moving this logic into the copyAcross
    //        // and only creating one or two arrays as needed.
    //        if (nalen <= 32) {
    //            val na = araNewCopy(nall, 0, nalen)
    //            if (isTop) {
    //                this.depth += height
    //                na
    //            } else
    //                araNewAbove(setSizes(na, height))
    //
    //        } else {
    //            val nal = araNewCopy(nall, 0, 332)
    //            val nar = araNewCopy(nall, 32, nalen - 32)
    //            val arr = araNewAbove(setSizes(nal, height), setSizes(nar, height))
    //            if (isTop) {
    //                this.depth += height + 1
    //                arr
    //            } else
    //                arr
    //        }
    //
    //    }

    //    private def copyAcross(all: Array[AnyRef], szs: Array[Int], slen: Int, height: Int): Array[AnyRef] = {
    //        // From prototype
    //        // Takes the slot size model and copies across slots to match it.
    //
    //        val nall = new Array[AnyRef](slen + 1)
    //        var ix = 0 // index into the all input array
    //        var offset = 0 // offset into an individual slot array.
    //        // It points to the next sub tree in the array to be copied
    //
    //        if (height == 1) {
    //            var i = 0
    //            while (i < slen) {
    //                val nsize = szs(i)
    //                val ge = all(ix).asInstanceOf[Array[AnyRef]]
    //                val asIs = (offset == 0) && (nsize == ge.length)
    //
    //                if (asIs) {
    //                    ix += 1;
    //                    nall(i) = ge
    //                } else {
    //                    var fillcnt = 0
    //                    var offs = offset
    //                    var nix = ix
    //                    var rta: Array[AnyRef] = null
    //
    //                    var ga: Array[AnyRef] = null
    //                    // collect enough slots together to match the size needed
    //                    while ((fillcnt < nsize) && (nix < all.length)) {
    //                        val gaa = all(nix).asInstanceOf[Array[AnyRef]]
    //                        ga = if (fillcnt == 0) new Array[AnyRef](nsize) else ga
    //                        val lena = gaa.length
    //                        if (nsize - fillcnt >= lena - offs) {
    //                            //for(i<-0 until lena-offs) ga(i+fillcnt)=gaa(i+offs)
    //                            System.arraycopy(gaa, offs, ga, fillcnt, lena - offs)
    //                            fillcnt += lena - offs
    //                            nix += 1
    //                            offs = 0
    //                        } else {
    //                            //for(i<-0 until nsize-fillcnt) ga(i+fillcnt)=gaa(i+offs)
    //                            System.arraycopy(gaa, offs, ga, fillcnt, nsize - fillcnt)
    //                            offs += nsize - fillcnt
    //                            fillcnt = nsize
    //                        }
    //                        rta = ga
    //                    }
    //
    //                    ix = nix
    //                    offset = offs
    //                    nall(i) = rta
    //                }
    //                i += 1
    //            }
    //
    //        } else {
    //            // not bottom
    //
    //            var i = 0
    //            while (i < slen) {
    //                val nsize = szs(i)
    //                val ae = all(ix).asInstanceOf[Array[AnyRef]]
    //                val asIs = (offset == 0) && (nsize == ae.length - 1)
    //
    //                if (asIs) {
    //                    ix += 1
    //                    nall(i) = ae
    //                } else {
    //                    var fillcnt = 0
    //                    var offs = offset
    //                    var nix = ix
    //                    var rta: Array[AnyRef] = null
    //
    //                    var aa: Array[AnyRef] = null
    //                    // collect enough slots together to match the size needed
    //                    while ((fillcnt < nsize) && (nix < all.length)) {
    //                        val aaa = all(nix).asInstanceOf[Array[AnyRef]]
    //                        aa = if (fillcnt == 0) new Array[AnyRef](nsize + 1) else aa
    //                        val lena = aaa.length - 1
    //                        if (nsize - fillcnt >= lena - offs) {
    //                            //for(i<-0 until lena-offs) aa(i+fillcnt+1)=aaa(i+offs+1)
    //                            System.arraycopy(aaa, offs + 1, aa, fillcnt + 1, lena - offs)
    //                            nix += 1
    //                            fillcnt += lena - offs
    //                            offs = 0
    //                        } else {
    //                            //for(i<-0 until nsize-fillcnt) aa(i+fillcnt+1)=aaa(i+offs+1)
    //                            System.arraycopy(aaa, offs + 1, aa, fillcnt + 1, nsize - fillcnt)
    //                            offs += nsize - fillcnt
    //                            fillcnt = nsize
    //                        }
    //                        rta = aa
    //                    }
    //
    //                    rta = setSizes(rta, height - 1)
    //                    ix = nix
    //                    offset = offs
    //                    nall(i) = rta
    //                }
    //                i += 1
    //            }
    //        } // end bottom
    //        nall
    //    }

}


private[immutable] trait RRBVectorPointer[A] {

    private[immutable] var focus: Int = 0
    private[immutable] var focusStart: Int = 0
    private[immutable] var focusEnd: Int = 0
    private[immutable] var focusDepth: Int = 0
    private[immutable] var focusRelaxed: Int = 0

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
        initFocus(that.focus, that.focusStart, that.focusEnd, that.focusDepth, that.focusRelaxed)
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

    private[immutable] final def initFocus(focus: Int, focusStart: Int, focusEnd: Int, focusDepth: Int, focusRelaxed: Int) = {
        this.focus = focus
        this.focusStart = focusStart
        this.focusEnd = focusEnd
        this.focusDepth = focusDepth
        this.focusRelaxed = focusRelaxed
    }

    private[immutable] final def gotoIndex(index: Int, endIndex: Int): Unit = {
        val focusStart = this.focusStart
        if (focusStart <= index && index < focusEnd) {
            val indexInFocus = index - focusStart
            val xor = indexInFocus ^ focus
            if /* is not focused on last block */ (xor >= (1 << 5)) {
                gotoPos(indexInFocus, xor)
                focus = index
            }
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
            if (_focusDepth == depth) copyAndStabilize(_focusDepth, focus)
            else relaxedStabilize()
        } else if /* is rb-tree */ (_focusDepth == depth) {
            copyAndStabilize(_focusDepth, focus)
            // TODO: Improve performance. May not need to stabilize all the way down
            gotoNextBlockStartWritable(endIndexInFocus, endIndexInFocus ^ focus, true)
            focusDepth = depth
            focus = endIndexInFocus
            focusEnd = endIndexInFocus
        } else /* is rrb-tree */ {
            relaxedStabilize()
            // TODO: Improve performance. May not need to stabilize all the way down
            gotoNextBlockStartWritable(endIndexInFocus, endIndexInFocus ^ focus, true)
            // TODO: gotoNextBlockStartWritable non focused part
            // TODO set focusStart
            focus = endIndexInFocus
            focusStart = ???
            focusEnd = endIndexInFocus
            focusRelaxed = ???
        }
    }

    /**
     *
     * @param index: Index that will be focused
     * @param _startIndex: The first index of the current subtree. If called from the root, it should be 0.
     * @param _endIndex: The end index of the current subtree where _endIndex-1 is the last element in this subtree.
     *                 If called from the root, it should be the length of the tree.
     * @param _depth: Depth of the current subtree. If called from the root, it should be the depth of the tree.
     * @param _focusRelaxed: Current set of indices for the chosen path in the tree.
     */
    @tailrec
    private[immutable] final def gotoPosRelaxed(index: Int, _startIndex: Int, _endIndex: Int, _depth: Int, _focusRelaxed: Int = 0): Unit = {
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
            val accFocusRelaxed = _focusRelaxed | (is << (_depth - 1))
            gotoPosRelaxed(index, if (is == 0) _startIndex else _startIndex + sizes(is - 1), _startIndex + sizes(is), _depth - 1, accFocusRelaxed)
        } else {
            val indexInFocus = index - _startIndex
            gotoPos(indexInFocus, 1 << (5 * (_depth - 1)))
            initFocus(indexInFocus, _startIndex, _endIndex, _depth, _focusRelaxed)
        }
    }

    private final def getRelaxedIndex(indexInSubTree: Int, sizes: Array[Int]) = {
        var is = 0 //ix >> ((height - 1) * WIDTH_SHIFT)
        while (sizes(is) <= indexInSubTree)
            is += 1
        is
    }

    private[immutable] final def relaxedStabilize(): Unit = {
        copyAndStabilize(focusDepth, focus)
        // TODO: stabilize non focus part
        ???
    }


//    private[immutable] final def setSizes(a: Array[AnyRef], height: Int) = {
//        // From prototype
//        var sigma = 0
//        val lena = a.length - 1
//        val szs = new Array[Int](lena)
//        //cost+=lena
//        var i = 0
//        while (i < lena) {
//            sigma += sizeSubTrie(a(i), height - 1, 0)
//            szs(i) = sigma
//            i += 1
//        }
//        a(lena) = szs
//        a
//    }

//    private[immutable] final def sizeSubTrie(treeNode: AnyRef, height: Int, acc: Int): Int = {
//        // From prototype
//        if (height > 1) {
//            val treeBranch = treeNode.asInstanceOf[Array[AnyRef]]
//            val len = treeBranch.length
//            if (treeBranch(0) == null) {
//                val sltsz = height - 1
//                sizeSubTrie(treeBranch(len - 1), sltsz, acc + (1 << (5 * sltsz)) * (len - 2))
//            } else {
//                val sn = treeBranch(0).asInstanceOf[Array[Int]]
//                acc + sn(sn.length - 1)
//            }
//        } else {
//            acc + treeNode.asInstanceOf[Array[AnyRef]].length
//        }
//    }

//    private[immutable] final def sizeSlot(a: AnyRef, height: Int) = {
//        // From prototype
//        if (height > 1)
//            a.asInstanceOf[Array[AnyRef]].length - 1
//        else
//            a.asInstanceOf[Array[AnyRef]].length
//    }

//    private[immutable] final def araNewCopy(nall: Array[AnyRef], start: Int, len: Int) = {
//        // From prototype
//        val na = new Array[AnyRef](len + 1)
//        Platform.arraycopy(nall, start, na, 0, len)
//        na
//    }

//    private[immutable] final def araNewJoin(al: Array[AnyRef], ac: Array[AnyRef], ar: Array[AnyRef]): Array[AnyRef] = {
//        // From prototype
//        // result does not contain size slot!!!
//        val lenl = if (al != null) al.length - 2 else 0
//        val lenc = if (ac != null) ac.length - 1 else 0
//        val lenr = if (ar != null) ar.length - 2 else 0
//        var allx = 0
//        val all = new Array[AnyRef](lenl + lenc + lenr)
//        if (lenl > 0) {
//            //for(i<-0 until lenl) all(i)=al(i+1)
//            System.arraycopy(al, 0, all, 0, lenl)
//            allx += lenl
//        }
//        //for(i<-0 until lenc) all(i+allx)=ac(i+1)
//        System.arraycopy(ac, 0, all, allx, lenc)
//        allx += lenc // <--- bug? wouldn't that exceed range of ac???
//        if (lenr > 0) {
//            //for(i<-0 until lenr)all(i+allx)=ar(i+2)
//            System.arraycopy(ar, 1, all, allx, lenr)
//        }
//        all
//    }

//    private[immutable] final def araNewAbove(gal: AnyRef): Array[AnyRef] = {
//        // From prototype
//        val na = new Array[AnyRef](2)
//        na(0) = gal
//        na
//    }

//    private[immutable] final def araNewAbove(til: AnyRef, tir: AnyRef): Array[AnyRef] = {
//        // From prototype
//        val na = new Array[AnyRef](3)
//        na(0) = til
//        na(1) = tir
//        na
//    }


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


    private[immutable] final def copyAndStabilize(_depth: Int, _focus: Int): Unit = {
        // assert(0 < _depth && _depth <= 6)
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
        stabilize(_depth, _focus)
    }

    private[immutable] final def stabilize(_depth: Int, _focus: Int): Unit = {
        // assert(0 < _depth && _depth <= 6)
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
        // assert(a != null)
        // assert(numElements <= newSize)
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
        // assert(length0 + length1 <= 32)
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
        val s = new RRBVector[A](size)

        s.initFrom(this)

        // TODO optimization: check if stabilization is really necessary on all displays based on the last index.
        s.gotoPos(size - 1, size - 1)
        if (depth > 1) s.copyAndStabilize(depth, size - 1)

        s.gotoPos(0, size - 1)
        s.focus = 0
        s.focusEnd = size
        s.focusDepth = depth
        s
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