package scala
package collection
package immutable
package rrbvector

import scala.collection.parallel.immutable.rrbvector.ParRRBVector

import scala.annotation.unchecked.uncheckedVariance

import scala.collection.generic._

object RRBVector extends scala.collection.generic.IndexedSeqFactory[RRBVector] {
    def newBuilder[A]: mutable.Builder[A, RRBVector[A]] = new RRBVectorBuilder[A]()

    implicit def canBuildFrom[A]: scala.collection.generic.CanBuildFrom[Coll, A, RRBVector[A]] = ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

    lazy private val EMPTY_VECTOR = new RRBVector[Nothing](0)

    override def empty[A]: RRBVector[A] = EMPTY_VECTOR

    @inline private[immutable] final val compileAssertions = false
}

final class RRBVector[+A] private[immutable](override private[immutable] val endIndex: Int) extends scala.collection.AbstractSeq[A] with scala.collection.immutable.IndexedSeq[A] with scala.collection.generic.GenericTraversableTemplate[A, RRBVector] with scala.collection.IndexedSeqLike[A, RRBVector[A]] with RRBVectorPointer[A@uncheckedVariance] with Serializable {
    self =>

    private[immutable] var dirty: Boolean = false

    override def par = new ParRRBVector[A](this)

    //    override def toVector = this

    override def companion: scala.collection.generic.GenericCompanion[RRBVector] = RRBVector

    def length: Int = endIndex

    override def lengthCompare(len: Int): Int = endIndex.-(len)

    override def iterator: RRBVectorIterator[A] = {
        if (this.dirty) {
            this.stabilize()
            this.dirty = false
            if (RRBVector.compileAssertions) this.assertVectorInvariant()
        }
        val it = new RRBVectorIterator[A](0, endIndex)
        it.initIteratorFrom(this)
        it
    }

    override def reverseIterator: RRBVectorReverseIterator[A] = {
        if (this.dirty) {
            this.stabilize()
            this.dirty = false
            if (RRBVector.compileAssertions) this.assertVectorInvariant()
        }
        val it = new RRBVectorReverseIterator[A](0, endIndex)
        it.initIteratorFrom(this)
        it
    }

    def apply(index: Int): A = {
        val _focusStart = this.focusStart
        if /* index is in focused subtree */ (_focusStart <= index && index < focusEnd) {
            val indexInFocus = index.-(_focusStart)
            getElem(indexInFocus, indexInFocus ^ focus)
        } else if /* index is in the vector bounds */ (0 <= index && index < endIndex) {
            getElementFromRoot(index)
        } else
            throw new IndexOutOfBoundsException(index.toString)
    }

    private def createSingletonVector[B](elem: B): RRBVector[B] = {
        val resultVector = new RRBVector[B](1)
        resultVector.initSingleton(elem)
        if (RRBVector.compileAssertions) resultVector.assertVectorInvariant()
        resultVector
    }

    override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[RRBVector[A], B, That]): That =
        if (bf.eq(IndexedSeq.ReusableCBF)) {
            val _endIndex = this.endIndex
            if (_endIndex != 0) {
                val resultVector = new RRBVector[B](_endIndex + 1)
                resultVector.dirty = this.dirty
                resultVector.initWithFocusFrom(this)
                resultVector.append(elem)
                if (RRBVector.compileAssertions) resultVector.assertVectorInvariant()
                resultVector.asInstanceOf[That]
            } else {
                createSingletonVector(elem).asInstanceOf[That]
            }
        } else {
            super.:+(elem)(bf)
        }

    private def append[B](elem: B) = {
        val _endIndex = endIndex - 1
        if /* vector focus is not focused block of the last element */ (((focusStart + focus) ^ (_endIndex - 1)) >= 32) {
            stabilizeAndFocusOn(_endIndex - 1)
        }

        val elemIndexInBlock = (_endIndex - focusStart) & 31
        if /* if next element will go in current block position */ (elemIndexInBlock != 0) {
            appendOnCurrentBlock(elem, elemIndexInBlock)
        } else /* next element will go in a new block position */ {
            appendBackSetupNewBlock(elem, elemIndexInBlock)
        }
        if (RRBVector.compileAssertions) assertVectorInvariant()
    }


    private def appendOnCurrentBlock[B](elem: B, elemIndexInBlock: Int): Unit = {
        val d0 = new Array[AnyRef](elemIndexInBlock + 1)
        System.arraycopy(display0, 0, d0, 0, elemIndexInBlock)
        d0(elemIndexInBlock) = elem.asInstanceOf[AnyRef]
        display0 = d0
        focusEnd = endIndex
        val _depth = depth
        if (_depth > 1) {
            if (!dirty) {
                copyDisplaysAndNullFocusedBranch(_depth, focus | focusRelax)
                dirty = true
            }
        }
        if (RRBVector.compileAssertions) assertVectorInvariant()
    }

    private[immutable] def stabilizeAndFocusOn(index: Int): Unit = {
        if (dirty) {
            stabilize()
            dirty = false
        }
        focusOn(index)
    }

    override def +:[B >: A, That](elem: B)(implicit bf: CanBuildFrom[RRBVector[A], B, That]): That =
        if (bf.eq(IndexedSeq.ReusableCBF))
            if (this.endIndex == 0) {
                val resultVector = new RRBVector[B](1)
                resultVector.initSingleton(elem)
                if (RRBVector.compileAssertions) resultVector.assertVectorInvariant()
                resultVector.asInstanceOf[That]
            } else {
                val resultVector = new RRBVector[B](this.endIndex + 1)
                resultVector.initWithFocusFrom(this)
                if (resultVector.focusStart != 0 || (resultVector.focus & -32) != 0) {
                    /* the current focused block is not on the left most leaf block of the vector */
                    if (this.dirty) {
                        resultVector.stabilize()
                        resultVector.dirty = false
                        if (RRBVector.compileAssertions) resultVector.assertVectorInvariant()
                    }
                    resultVector.focusOn(0)
                }

                val newD0len = display0.length + 1
                if /* element fits in current block */ (newD0len <= 32) {
                    resultVector.focusEnd = newD0len
                    val newD0 = new Array[AnyRef](newD0len)
                    newD0(0) = elem.asInstanceOf[AnyRef]
                    System.arraycopy(resultVector.display0, 0, newD0, 1, newD0len - 1)
                    resultVector.display0 = newD0
                    copyTopAndComputeSizes(2)
                } else {
                    resultVector.prependFrontSetupNewBlock()
                    resultVector.display0(0) = elem.asInstanceOf[AnyRef]
                }

                if (RRBVector.compileAssertions) resultVector.assertVectorInvariant()

                resultVector.asInstanceOf[That]
            }
        else
            super.:+(elem)(bf)

    override def isEmpty: Boolean = this.endIndex.==(0)

    override def head: A =
        if (this.endIndex != 0)
            apply(0)
        else
            throw new UnsupportedOperationException("empty.head")

    override def take(n: Int): RRBVector[A] =
        if (n <= 0)
            RRBVector.empty
        else if (n < endIndex)
            takeFront0(n)
        else
            this

    override def dropRight(n: Int): RRBVector[A] =
        if (n <= 0)
            this
        else if (n < endIndex)
            takeFront0(endIndex - n)
        else
            RRBVector.empty

    override def slice(from: Int, until: Int): RRBVector[A] = take(until).drop(from)

    override def splitAt(n: Int): (RRBVector[A], RRBVector[A]) = (take(n), drop(n))

    override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[RRBVector[A], B, That]): That = if (bf.eq(IndexedSeq.ReusableCBF))
        if (that.isEmpty)
            this.asInstanceOf[That]
        else {
            that match {
                case thatVec: RRBVector[B] /* if thatVec.length > 1024 */ =>
                    if (this.isEmpty)
                        thatVec.asInstanceOf[That]
                    else {
                        val newVec = new RRBVector(this.endIndex + thatVec.endIndex)
                        newVec.initWithFocusFrom(this)
                        newVec.concatenate(this.endIndex, thatVec)
                        newVec.asInstanceOf[That]
                    }
                case _ =>
                    super.++(that.seq)
            }
        } else super.++(that.seq)

    override def tail: RRBVector[A] =
        if (this.endIndex.!=(0))
            this.drop(1)
        else
            throw new UnsupportedOperationException("empty.tail")

    override def last: A =
        if (this.endIndex != 0)
            this.apply(this.endIndex - 1)
        else
            throw new UnsupportedOperationException("empty.last")

    override def init: RRBVector[A] =
        if (this.endIndex != 0)
            dropRight(1)
        else
            throw new UnsupportedOperationException("empty.init")


    private[immutable] def appendBackSetupNewBlock[B](elem: B, elemIndexInBlock: Int) = {
        val oldDepth = depth
        val newRelaxedIndex = (endIndex - 1) - focusStart + focusRelax
        val focusJoined = focus | focusRelax
        val xor = newRelaxedIndex ^ focusJoined
        val _dirty = dirty
        setupNewBlockInNextBranch(focusJoined, xor, _dirty)
        if /* setupNewBlockInNextBranch(...) increased the depth of the tree */ (oldDepth == depth && !_dirty) {
            var i = if (xor < 1024) 2 else if (xor < 32768) 3 else if (xor < 1048576) 4 else if (xor < 33554432) 5 else 6
            if (i < oldDepth) {
                val _focusDepth = focusDepth
                var display: Array[AnyRef] = i match {
                    case 2 => display2
                    case 3 => display3
                    case 4 => display4
                    case 5 => display5
                }
                do {
                    val displayLen = display.length - 1
                    val newSizes: Array[Int] =
                        if (i >= _focusDepth) {
                            makeDirtySizes(display(displayLen).asInstanceOf[Array[Int]], displayLen - 1)
                        } else null

                    val newDisplay = new Array[AnyRef](display.length)
                    System.arraycopy(display, 0, newDisplay, 0, displayLen - 1)
                    if (i >= _focusDepth)
                        newDisplay(displayLen) = newSizes

                    i match {
                        case 2 =>
                            display2 = newDisplay
                            display = display3
                        case 3 =>
                            display3 = newDisplay
                            display = display4
                        case 4 =>
                            display4 = newDisplay
                            display = display5
                        case 5 =>
                            display5 = newDisplay
                    }
                    i += 1
                } while (i < oldDepth)
            }
        }

        if (oldDepth == focusDepth)
            initFocus(endIndex - 1, 0, endIndex, depth, 0)
        else
            initFocus(endIndex - 1, endIndex - 1, endIndex, 1, newRelaxedIndex & -32)

        display0(elemIndexInBlock) = elem.asInstanceOf[AnyRef]
        dirty = true
        if (RRBVector.compileAssertions) this.assertVectorInvariant()
    }

    private[immutable] def prependFrontSetupNewBlock(): Unit = {
        if (RRBVector.compileAssertions) {
            assert(display0.length == 32)
        }

        var currentDepth = focusDepth
        if (currentDepth == 1)
            currentDepth += 1
        var display = currentDepth match {
            case 1 =>
                currentDepth = 2
                display1
            case 2 => display1
            case 3 => display2
            case 4 => display3
            case 5 => display4
            case 6 => display5
        }
        while /* the insertion depth has not been found */ (display != null && display.length == 33) {
            currentDepth += 1
            currentDepth match {
                case 2 => display = display1
                case 3 => display = display2
                case 4 => display = display3
                case 5 => display = display4
                case 6 => display = display5
                case _ => throw new IllegalStateException()
            }
        }

        // create new node at this depth and all singleton nodes under it on left most branch
        setupNewBlockInInitBranch(currentDepth)

        // update sizes of nodes above the insertion depth
        copyTopAndComputeSizes(currentDepth + 1)

        initFocus(0, 0, 1, 1, 0)
    }


    private[immutable] def concatenate[B >: A](currentSize: Int, that: RRBVector[B]): scala.Unit = {
        if (this.dirty) {
            this.stabilize()
            this.dirty = false
        }

        if (that.dirty) {
            that.stabilize()
            that.dirty = false
        }

        this.focusOn(currentSize.-(1))
        math.max(this.depth, that.depth) match {
            case 1 =>
                val concat = rebalancedLeafs(display0, that.display0, isTop = true)
                initFromRoot(concat, if (endIndex <= 32) 1 else 2)
            case 2 =>
                var d0: Array[AnyRef] = null
                var d1: Array[AnyRef] = null
                if (that.focus.&(-32).==(0)) {
                    d1 = that.display1
                    d0 = that.display0
                } else {
                    if (that.display1 != null)
                        d1 = that.display1
                    if (d1 == null)
                        d0 = that.display0
                    else
                        d0 = d1(0).asInstanceOf[Array[AnyRef]]
                }
                var concat: Array[AnyRef] = rebalancedLeafs(this.display0, d0, isTop = false)
                concat = rebalanced(this.display1, concat, that.display1, 2)
                if (concat.length.==(2))
                    initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], 2)
                else
                    initFromRoot(withComputedSizes(concat, 3), 3)
            case 3 =>
                var d0: Array[AnyRef] = null
                var d1: Array[AnyRef] = null
                var d2: Array[AnyRef] = null
                if (that.focus.&(-32).==(0)) {
                    d2 = that.display2
                    d1 = that.display1
                    d0 = that.display0
                }
                else {
                    if (that.display2 != null)
                        d2 = that.display2
                    if (d2.==(null))
                        d1 = that.display1
                    else
                        d1 = d2(0).asInstanceOf[Array[AnyRef]]
                    if (d1.==(null))
                        d0 = that.display0
                    else
                        d0 = d1(0).asInstanceOf[Array[AnyRef]]
                }
                var concat: Array[AnyRef] = rebalancedLeafs(this.display0, d0, isTop = false)
                concat = rebalanced(this.display1, concat, d1, 2)
                concat = rebalanced(this.display2, concat, that.display2, 3)
                if (concat.length.==(2))
                    initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], 3)
                else
                    initFromRoot(withComputedSizes(concat, 4), 4)
            case 4 =>
                var d0: Array[AnyRef] = null
                var d1: Array[AnyRef] = null
                var d2: Array[AnyRef] = null
                var d3: Array[AnyRef] = null
                if (that.focus.&(-32).==(0)) {
                    d3 = that.display3
                    d2 = that.display2
                    d1 = that.display1
                    d0 = that.display0
                }
                else {
                    if (that.display3 != null)
                        d3 = that.display3
                    if (d3.==(null))
                        d2 = that.display2
                    else
                        d2 = d3(0).asInstanceOf[Array[AnyRef]]
                    if (d2.==(null))
                        d1 = that.display1
                    else
                        d1 = d2(0).asInstanceOf[Array[AnyRef]]
                    if (d1.==(null))
                        d0 = that.display0
                    else
                        d0 = d1(0).asInstanceOf[Array[AnyRef]]
                }
                var concat: Array[AnyRef] = rebalancedLeafs(this.display0, d0, isTop = false)
                concat = rebalanced(this.display1, concat, d1, 2)
                concat = rebalanced(this.display2, concat, d2, 3)
                concat = rebalanced(this.display3, concat, that.display3, 4)
                if (concat.length.==(2))
                    initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], 4)
                else
                    initFromRoot(withComputedSizes(concat, 5), 5)
            case 5 =>
                var d0: Array[AnyRef] = null
                var d1: Array[AnyRef] = null
                var d2: Array[AnyRef] = null
                var d3: Array[AnyRef] = null
                var d4: Array[AnyRef] = null
                if (that.focus.&(-32) == 0) {
                    d4 = that.display4
                    d3 = that.display3
                    d2 = that.display2
                    d1 = that.display1
                    d0 = that.display0
                }
                else {
                    if (that.display4 != null)
                        d4 = that.display4
                    if (d4.==(null))
                        d3 = that.display3
                    else
                        d3 = d4(0).asInstanceOf[Array[AnyRef]]
                    if (d3.==(null))
                        d2 = that.display2
                    else
                        d2 = d3(0).asInstanceOf[Array[AnyRef]]
                    if (d2.==(null))
                        d1 = that.display1
                    else
                        d1 = d2(0).asInstanceOf[Array[AnyRef]]
                    if (d1.==(null))
                        d0 = that.display0
                    else
                        d0 = d1(0).asInstanceOf[Array[AnyRef]]
                }
                var concat: Array[AnyRef] = rebalancedLeafs(this.display0, d0, isTop = false)
                concat = rebalanced(this.display1, concat, d1, 2)
                concat = rebalanced(this.display2, concat, d2, 3)
                concat = rebalanced(this.display3, concat, d3, 4)
                concat = rebalanced(this.display4, concat, that.display4, 5)
                if (concat.length.==(2))
                    initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], 5)
                else
                    initFromRoot(withComputedSizes(concat, 6), 6)
            case 6 =>
                var d0: Array[AnyRef] = null
                var d1: Array[AnyRef] = null
                var d2: Array[AnyRef] = null
                var d3: Array[AnyRef] = null
                var d4: Array[AnyRef] = null
                var d5: Array[AnyRef] = null
                if ((that.focus & -32) == 0) {
                    d5 = that.display5
                    d4 = that.display4
                    d3 = that.display3
                    d2 = that.display2
                    d1 = that.display1
                    d0 = that.display0
                }
                else {
                    if (that.display5 != null)
                        d5 = that.display5
                    if (d5.==(null))
                        d4 = that.display4
                    else
                        d4 = d5(0).asInstanceOf[Array[AnyRef]]
                    if (d4.==(null))
                        d3 = that.display3
                    else
                        d3 = d4(0).asInstanceOf[Array[AnyRef]]
                    if (d3.==(null))
                        d2 = that.display2
                    else
                        d2 = d3(0).asInstanceOf[Array[AnyRef]]
                    if (d2.==(null))
                        d1 = that.display1
                    else
                        d1 = d2(0).asInstanceOf[Array[AnyRef]]
                    if (d1.==(null))
                        d0 = that.display0
                    else
                        d0 = d1(0).asInstanceOf[Array[AnyRef]]
                }
                var concat: Array[AnyRef] = rebalancedLeafs(this.display0, d0, isTop = false)
                concat = rebalanced(this.display1, concat, d1, 2)
                concat = rebalanced(this.display2, concat, d2, 3)
                concat = rebalanced(this.display3, concat, d3, 4)
                concat = rebalanced(this.display4, concat, d4, 5)
                concat = rebalanced(this.display5, concat, that.display5, 6)
                if (concat.length.==(2))
                    initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], 6)
                else
                    initFromRoot(withComputedSizes(concat, 7), 7)
            case _ => throw new IllegalStateException()
        }
        if (RRBVector.compileAssertions)
            this.assertVectorInvariant()
    }

    private def rebalanced(displayLeft: Array[AnyRef], concat: Array[AnyRef], displayRight: Array[AnyRef], currentDepth: Int): Array[AnyRef] = {
        val leftLength = if (displayLeft == null) 0 else displayLeft.length - 1
        val concatLength = if (concat == null) 0 else concat.length - 1
        val rightLength = if (displayRight == null) 0 else displayRight.length - 1
        val branching = computeBranching(displayLeft, concat, displayRight, currentDepth)
        val top = new Array[AnyRef]((branching >> 10) + (if ((branching & 1023) == 0) 1 else 2))
        var mid = new Array[AnyRef](if ((branching >> 10) == 0) ((branching + 31) >> 5) + 1 else 33)
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
                    if (displayLeft != null) {
                        currentDisplay = displayLeft
                        displayEnd = if (concat == null) leftLength else leftLength - 1
                    }
                case 1 =>
                    if (concat.==(null))
                        displayEnd = 0
                    else {
                        currentDisplay = concat
                        displayEnd = concatLength
                    }
                    i = 0
                case 2 =>
                    if (displayRight != null) {
                        currentDisplay = displayRight
                        displayEnd = rightLength
                        i = if (concat == null) 0 else 1
                    }
            }
            while (i < displayEnd) {
                val displayValue = currentDisplay(i).asInstanceOf[Array[AnyRef]]
                val displayValueEnd = if (currentDepth == 2) displayValue.length else displayValue.length - 1
                if ((iBot | j) == 0 && displayValueEnd == 32) {
                    if (currentDepth != 2 && bot != null) {
                        withComputedSizes(bot, currentDepth - 1)
                        bot = null
                    }
                    mid(iMid) = displayValue
                    i += 1
                    iMid += 1
                    iSizes += 1
                }
                else {
                    val numElementsToCopy = math.min(displayValueEnd.-(j), 32.-(iBot))
                    if (iBot == 0) {
                        if (currentDepth != 2 && bot != null)
                            withComputedSizes(bot, currentDepth.-(1))
                        bot = new Array[AnyRef](math.min(branching.-(iTop.<<(10)).-(iMid.<<(5)), 32).+(if (currentDepth.==(2)) 0 else 1))
                        mid(iMid) = bot
                    }

                    System.arraycopy(displayValue, j, bot, iBot, numElementsToCopy)
                    j.+=(numElementsToCopy)
                    iBot.+=(numElementsToCopy)
                    if (j.==(displayValueEnd)) {
                        i += 1
                        j = 0
                    }

                    if (iBot == 32) {
                        iMid += 1
                        iBot = 0
                        iSizes += 1
                        if (currentDepth != 2 && bot != null)
                            withComputedSizes(bot, currentDepth - 1)
                    }

                }
                if (iMid == 32) {
                    top(iTop) = withComputedSizes(mid, currentDepth)
                    iTop += 1
                    iMid = 0
                    val remainingBranches = branching.-(iTop.<<(5).|(iMid).<<(5).|(iBot))
                    mid = if (remainingBranches.>(0))
                        new Array[AnyRef](if (remainingBranches.>>(10).==(0)) (remainingBranches + 63).>>(5) else 33)
                    else null
                }

            }
            d += 1
        }
        while (d < 3)
        if (currentDepth.!=(2).&&(bot != null))
            withComputedSizes(bot, currentDepth.-(1))

        if (mid != null)
            top(iTop) = withComputedSizes(mid, currentDepth)

        top
    }

    private def rebalancedLeafs(displayLeft: Array[AnyRef], displayRight: Array[AnyRef], isTop: Boolean): Array[AnyRef] = {
        val leftLength = displayLeft.length
        val rightLength = displayRight.length
        if (leftLength.==(32)) {
            val top = new Array[AnyRef](3)
            top(0) = displayLeft
            top(1) = displayRight
            top
        }
        else
        if (leftLength.+(rightLength).<=(32)) {
            val mergedDisplay = new Array[AnyRef](leftLength.+(rightLength))
            System.arraycopy(displayLeft, 0, mergedDisplay, 0, leftLength)
            System.arraycopy(displayRight, 0, mergedDisplay, leftLength, rightLength)
            if (isTop)
                mergedDisplay
            else {
                val top = new Array[AnyRef](2)
                top(0) = mergedDisplay
                top
            }
        }
        else {
            val top = new Array[AnyRef](3)
            val arr0 = new Array[AnyRef](32)
            val arr1 = new Array[AnyRef](leftLength + rightLength - 32)
            top(0) = arr0
            top(1) = arr1
            System.arraycopy(displayLeft, 0, arr0, 0, leftLength)
            System.arraycopy(displayRight, 0, arr0, leftLength, 32 - leftLength)
            System.arraycopy(displayRight, 32 - leftLength, arr1, 0, rightLength - 32 + leftLength)
            top
        }
    }

    private def computeBranching(displayLeft: Array[AnyRef], concat: Array[AnyRef], displayRight: Array[AnyRef], currentDepth: Int) = {
        val leftLength = if (displayLeft.==(null))
            0
        else
            displayLeft.length.-(1)
        val concatLength = if (concat.==(null))
            0
        else
            concat.length.-(1)
        val rightLength = if (displayRight.==(null))
            0
        else
            displayRight.length.-(1)
        var branching = 0
        if (currentDepth.==(1)) {
            branching = leftLength + concatLength + rightLength
            if (leftLength != 0)
                branching -= 1
            if (rightLength != 0)
                branching -= 1
        }
        else {
            var i = 0
            while (i.<(leftLength.-(1))) {
                branching.+=(displayLeft(i).asInstanceOf[Array[AnyRef]].length)
                i += 1
            }
            i = 0
            while (i.<(concatLength)) {
                branching.+=(concat(i).asInstanceOf[Array[AnyRef]].length)
                i += 1
            }
            i = 1
            while (i.<(rightLength)) {
                branching.+=(displayRight(i).asInstanceOf[Array[AnyRef]].length)
                i += 1
            }
            if (currentDepth.!=(2)) {
                branching.-=(leftLength.+(concatLength).+(rightLength))
                if (leftLength.!=(0))
                    branching.+=(1)

                if (rightLength.!=(0))
                    branching.+=(1)

            }

        }
        branching
    }

    private def takeFront0(n: Int): RRBVector[A] = {
        if (dirty) {
            stabilize()
            dirty = false
        }

        val vec = new RRBVector[A](n)
        vec.initWithFocusFrom(this)
        if (depth > 1) {
            vec.focusOn(n.-(1))
            val d0len = vec.focus.&(31).+(1)
            if (d0len.!=(32)) {
                val d0 = new Array[AnyRef](d0len)
                System.arraycopy(vec.display0, 0, d0, 0, d0len)
                vec.display0 = d0
            }

            val cutIndex = vec.focus | vec.focusRelax
            vec.cleanTop(cutIndex)
            vec.focusDepth = math.min(vec.depth, vec.focusDepth)
            if (vec.depth > 1) {
                vec.copyDisplays(vec.focusDepth, cutIndex)
                var i = vec.depth
                var offset = 0
                while (i > vec.focusDepth) {
                    val display = i match {
                        case 2 => vec.display1
                        case 3 => vec.display2
                        case 4 => vec.display3
                        case 5 => vec.display4
                        case 6 => vec.display5
                    }
                    val oldSizes = display(display.length.-(1)).asInstanceOf[Array[Int]]
                    val newLen = ((vec.focusRelax >> (5 * (i - 1))) & 31) + 1
                    val newSizes = new Array[Int](newLen)
                    System.arraycopy(oldSizes, 0, newSizes, 0, newLen.-(1))
                    newSizes(newLen - 1) = n - offset
                    if (newLen.>(1))
                        offset.+=(newSizes(newLen.-(2)))

                    val newDisplay = new Array[AnyRef](newLen.+(1))
                    System.arraycopy(display, 0, newDisplay, 0, newLen)
                    newDisplay.update(newLen.-(1), null)
                    newDisplay.update(newLen, newSizes)
                    i match {
                        case 2 => vec.display1 = newDisplay
                        case 3 => vec.display2 = newDisplay
                        case 4 => vec.display3 = newDisplay
                        case 5 => vec.display4 = newDisplay
                        case 6 => vec.display5 = newDisplay
                    }
                    i -= 1
                }
                vec.stabilizeDisplayPath(vec.depth, cutIndex)
                vec.focusEnd = n
            }
            else
                vec.focusEnd = n
        } else if ( /* depth==1 && */ n != 32) {
            val d0 = new Array[AnyRef](n)
            System.arraycopy(vec.display0, 0, d0, 0, n)
            vec.display0 = d0
            vec.initFocus(0, 0, n, 1, 0)
        } /* else { do nothing } */
        if (RRBVector.compileAssertions) vec.assertVectorInvariant()
        vec
    }

    private[immutable] def assertVectorInvariant(): Unit = {
        if (RRBVector.compileAssertions) {
            assert(0 <= depth && depth <= 6, depth)
            assert(isEmpty == (depth == 0), scala.Tuple2(isEmpty, depth))
            assert(isEmpty == (length == 0), scala.Tuple2(isEmpty, length))
            assert(length == endIndex, scala.Tuple2(length, endIndex))
            assert((depth <= 0 && display0 == null) || (depth > 0 && display0 != null))
            assert((depth <= 1 && display1 == null) || (depth > 0 && display1 != null))
            assert((depth <= 2 && display2 == null) || (depth > 0 && display2 != null))
            assert((depth <= 3 && display3 == null) || (depth > 0 && display3 != null))
            assert((depth <= 4 && display4 == null) || (depth > 0 && display4 != null))
            assert((depth <= 5 && display5 == null) || (depth > 0 && display5 != null))

            if (!dirty) {
                if (display5 != null) {
                    assert(display4 != null)
                    if (focusDepth <= 5) assert(display5((focusRelax >> 25) & 31) == display4)
                    else assert(display5((focus >> 25) & 31) == display4)
                }
                if (display4 != null) {
                    assert(display3 != null)
                    if (focusDepth <= 4) assert(display4((focusRelax >> 20) & 31) == display3)
                    else assert(display4((focus >> 20) & 31) == display3)
                }
                if (display3 != null) {
                    assert(display2 != null)
                    if (focusDepth <= 3) assert(display3((focusRelax >> 15) & 31) == display2)
                    else assert(display3((focus >> 15) & 31) == display2)
                }
                if (display2 != null) {
                    assert(display1 != null)
                    if (focusDepth <= 2) assert(display2((focusRelax >> 10) & 31) == display1)
                    else assert(display2((focus >> 10) & 31) == display1)
                }
                if (display1 != null) {
                    assert(display0 != null)
                    if (focusDepth <= 1) assert(display1((focusRelax >> 5) & 31) == display0)
                    else assert(display1((focus >> 5) & 31) == display0)
                }
            } else {
                assert(depth > 1)
                if (display5 != null) {
                    assert(display4 != null)
                    if (focusDepth <= 5) assert(display5((focusRelax >> 25) & 31) == null)
                    else assert(display5((focus >> 25) & 31) == null)
                }
                if (display4 != null) {
                    assert(display3 != null)
                    if (focusDepth <= 4) assert(display4((focusRelax >> 20) & 31) == null)
                    else assert(display4((focus >> 20) & 31) == null)
                }
                if (display3 != null) {
                    assert(display2 != null)
                    if (focusDepth <= 3) assert(display3((focusRelax >> 15) & 31) == null)
                    else assert(display3((focus >> 15) & 31) == null)
                }
                if (display2 != null) {
                    assert(display1 != null)
                    if (focusDepth <= 2) assert(display2((focusRelax >> 10) & 31) == null)
                    else assert(display2((focus >> 10) & 31) == null)
                }
                if (display1 != null) {
                    assert(display0 != null)
                    if (focusDepth <= 1) assert(display1((focusRelax >> 5) & 31) == null)
                    else assert(display1((focus >> 5) & 31) == null)
                }
            }


            assert(0 <= focusStart && focusStart <= focusEnd && focusEnd <= endIndex, scala.Tuple3(focusStart, focusEnd, endIndex))
            assert(focusStart.==(focusEnd).||(focusEnd.!=(0)), "focusStart==focusEnd ==> focusEnd==0".+(focusStart, focusEnd))
            assert(0 <= focusDepth && focusDepth <= depth, scala.Tuple2(focusDepth, depth))
            def checkSizes(node: Array[AnyRef], currentDepth: Int, _endIndex: Int): Unit = {
                if (currentDepth.>(1)) {
                    if (node != null) {
                        val sizes = node.last.asInstanceOf[Array[Int]]
                        if (sizes != null) {
                            assert(node.length.==(sizes.length.+(1)))
                            if (!dirty)
                                assert(sizes.last.==(_endIndex), scala.Tuple2(sizes.last, _endIndex))

                            var i = 0
                            while (i < sizes.length.-(1)) {
                                checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth.-(1), sizes(i).-(if (i.==(0)) 0 else sizes(i.-(1))))
                                i += 1
                            }
                            checkSizes(node(node.length.-(2)).asInstanceOf[Array[AnyRef]], currentDepth.-(1), if (sizes.length.>(1)) sizes.last.-(sizes(sizes.length.-(2))) else sizes.last)
                        }
                        else {
                            var i = 0
                            while (i < node.length.-(2)) {
                                checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth.-(1), 1.<<(5.*(currentDepth.-(1))))
                                i += 1
                            }
                            val expectedLast = _endIndex - (1.<<(5.*(currentDepth.-(1))).*(node.length.-(2)))
                            assert(1 <= expectedLast && expectedLast.<=(1.<<(5.*(currentDepth))))
                            checkSizes(node(node.length.-(2)).asInstanceOf[Array[AnyRef]], currentDepth.-(1), expectedLast)
                        }
                    } else {
                        assert(dirty)
                    }
                } else if (node != null) {
                    assert(node.length == _endIndex)
                } else {
                    assert(dirty)
                }
            }
            depth match {
                case 1 => checkSizes(display0, 1, endIndex)
                case 2 => checkSizes(display1, 2, endIndex)
                case 3 => checkSizes(display2, 3, endIndex)
                case 4 => checkSizes(display3, 4, endIndex)
                case 5 => checkSizes(display4, 5, endIndex)
                case 6 => checkSizes(display5, 6, endIndex)
                case _ => ()
            }
        }
    }

    private[immutable] def debugToString: String = {
        s"""
           |RRBVector (
           |    display0 = $display0 ${if (display0 != null) display0.mkString("[", ", ", "]") else ""}
           |    display1 = $display1 ${if (display1 != null) display1.mkString("[", ", ", "]") else ""}
           |    display2 = $display2 ${if (display2 != null) display2.mkString("[", ", ", "]") else ""}
           |    display3 = $display3 ${if (display3 != null) display3.mkString("[", ", ", "]") else ""}
           |    display4 = $display4 ${if (display4 != null) display4.mkString("[", ", ", "]") else ""}
           |    display5 = $display5 ${if (display5 != null) display5.mkString("[", ", ", "]") else ""}
           |    depth = $depth
           |    endIndex = $endIndex
           |    focus = $focus
           |    focusStart = $focusStart
           |    focusEnd = $focusEnd
           |    focusRelax = $focusRelax
           |    dirty = $dirty
           |)
         """.stripMargin
    }
}

final class RRBVectorBuilder[A] extends mutable.Builder[A, RRBVector[A]] with RRBVectorPointer[A@uncheckedVariance] {
    display0 = new Array[AnyRef](32)
    depth = 1
    private var blockIndex = 0
    private var lo = 0

    private var acc: RRBVector[A] = null

    private[collection] def endIndex = {
        var sz = blockIndex + lo
        if (acc != null)
            sz += acc.endIndex
        sz
    }

    def +=(elem: A): this.type = {
        if (lo >= 32) {
            val newBlockIndex = blockIndex + 32
            gotoNextBlockStartWritable(newBlockIndex, newBlockIndex ^ blockIndex)
            blockIndex = newBlockIndex
            lo = 0
        }
        display0.update(lo, elem.asInstanceOf[AnyRef])
        lo += 1
        this
    }

    override def ++=(xs: TraversableOnce[A]): this.type = {
        if (xs.nonEmpty) {
            xs match {
                case thatVec: RRBVector[A] /* if thatVec.length > 1024 */ =>
                    if (endIndex != 0) {
                        acc = this.result() ++ xs
                        this.clearCurrent()
                    } else if (acc != null) {
                        acc = acc ++ thatVec
                    } else {
                        acc = thatVec
                    }
                case _ =>
                    super.++=(xs)
            }
        }
        this
    }

    private def resultCurrent(): RRBVector[A] = {
        val size = blockIndex + lo
        if (size == 0)
            RRBVector.empty
        else {
            val resultVector = new RRBVector[A](size)
            resultVector.initFrom(this)
            resultVector.display0 = copyOf(resultVector.display0, lo, lo)
            val _depth = depth
            if (_depth > 1) {
                resultVector.copyDisplays(_depth, size - 1)
                resultVector.stabilizeDisplayPath(_depth, size - 1)
            }
            resultVector.gotoPos(0, size - 1)
            resultVector.initFocus(0, 0, size, _depth, 0)
            if (RRBVector.compileAssertions) resultVector.assertVectorInvariant()
            resultVector
        }
    }

    def result(): RRBVector[A] = {
        val current = resultCurrent()
        val resultVector =
            if (acc == null) current
            else acc ++ current
        if (RRBVector.compileAssertions) resultVector.assertVectorInvariant()
        resultVector
    }

    private def clearCurrent(): Unit = {
        display0 = new Array[AnyRef](32)
        display1 = null
        display2 = null
        display3 = null
        display4 = null
        display5 = null
        depth = 1
        blockIndex = 0
        lo = 0
    }

    def clear(): Unit = {
        clearCurrent()
        acc = null
    }
}

class RRBVectorIterator[+A](startIndex: Int, override private[immutable] val endIndex: Int) extends AbstractIterator[A] with Iterator[A] with RRBVectorPointer[A@uncheckedVariance] {
    /* Index in the vector of the first element of current block, i.e. current display0 */
    private var blockIndex: Int = _
    /* Index in current block, i.e. current display0 */
    private var lo: Int = _
    /* End index (or length) of current block, i.e. current display0 */
    private var endLo: Int = _
    private var _hasNext: Boolean = _

    private[collection] final def initIteratorFrom[B >: A](that: RRBVectorPointer[B]): Unit = {
        initWithFocusFrom(that)
        _hasNext = startIndex < endIndex
        if (_hasNext) {
            focusOn(startIndex)
            blockIndex = focusStart.+(focus.&(-32))
            lo = focus & 31
            endLo = math.min(focusEnd.-(blockIndex), 32)
        }
        else {
            blockIndex = 0
            lo = 0
            endLo = 1
            display0 = new Array[AnyRef](1)
        }
    }

    final def hasNext = _hasNext

    def next(): A = {
        val _lo = lo
        val res: A = display0(_lo).asInstanceOf[A]
        lo = _lo + 1
        val _endLo = endLo
        if (_lo + 1 != _endLo) {
            res
        } else {
            val oldBlockIndex = blockIndex
            val newBlockIndex = oldBlockIndex + _endLo
            blockIndex = newBlockIndex
            lo = 0
            if (newBlockIndex < focusEnd) {
                val _focusStart = focusStart
                val newBlockIndexInFocus = newBlockIndex - _focusStart
                gotoNextBlockStart(newBlockIndexInFocus, newBlockIndexInFocus ^ (oldBlockIndex - _focusStart))
            } else if (newBlockIndex < endIndex) {
                focusOn(newBlockIndex)
            } else {
                /* setup dummy index that will fail with IndexOutOfBound in subsequent 'next()' invocations */
                lo = (focusEnd - 1) & 31
                blockIndex = endIndex
                if (_hasNext) _hasNext = false
                else throw new NoSuchElementException("reached iterator end")
            }
            endLo = math.min(focusEnd - newBlockIndex, 32)
            res
        }
    }

    private[collection] def remaining: Int = math.max(endIndex - (blockIndex + lo), 0)

}

class RRBVectorReverseIterator[+A](startIndex: Int, final override private[immutable] val endIndex: Int) extends AbstractIterator[A] with Iterator[A] with RRBVectorPointer[A@uncheckedVariance] {
    private var lastIndexOfBlock: Int = _
    private var lo: Int = _
    private var endLo: Int = _
    private var _hasNext: Boolean = _

    private[collection] final def initIteratorFrom[B >: A](that: RRBVectorPointer[B]): Unit = {
        initWithFocusFrom(that)
        _hasNext = startIndex < endIndex
        if (_hasNext) {
            val idx = endIndex - 1
            focusOn(idx)
            lastIndexOfBlock = idx
            lo = (idx - focusStart) & 31
            endLo = math.max(startIndex.-(focusStart).-(lastIndexOfBlock), 0)
        } else {
            lastIndexOfBlock = 0
            lo = 0
            endLo = 0
            display0 = new Array[AnyRef](1)
        }
    }

    final def hasNext = _hasNext

    def next(): A = if (_hasNext) {
        val res = display0(lo).asInstanceOf[A]
        lo -= 1
        if (lo >= endLo)
            res
        else {
            val newBlockIndex = lastIndexOfBlock - 32
            if (focusStart <= newBlockIndex) {
                val _focusStart = focusStart
                val newBlockIndexInFocus = newBlockIndex - _focusStart
                gotoPrevBlockStart(newBlockIndexInFocus, newBlockIndexInFocus.^(lastIndexOfBlock.-(_focusStart)))
                lastIndexOfBlock = newBlockIndex
                lo = 31
                endLo = math.max(startIndex - focusStart - focus, 0)
                res
            } else if (startIndex < focusStart) {
                val newIndex = focusStart - 1
                focusOn(newIndex)
                lastIndexOfBlock = newIndex
                lo = (newIndex - focusStart) & 31
                endLo = math.max(startIndex - focusStart - lastIndexOfBlock, 0)
                res
            } else {
                _hasNext = false
                res
            }
        }
    } else
        throw new NoSuchElementException("reached iterator end")
}

private[immutable] trait RRBVectorPointer[A] {
    private[immutable] final var display0: Array[AnyRef] = _
    private[immutable] final var display1: Array[AnyRef] = _
    private[immutable] final var display2: Array[AnyRef] = _
    private[immutable] final var display3: Array[AnyRef] = _
    private[immutable] final var display4: Array[AnyRef] = _
    private[immutable] final var display5: Array[AnyRef] = _
    private[immutable] final var depth: Int = _
    private[immutable] final var focusStart: Int = 0
    private[immutable] final var focusEnd: Int = 0
    private[immutable] final var focusDepth: Int = 0
    private[immutable] final var focus: Int = 0
    private[immutable] final var focusRelax: Int = 0

    private[immutable] def endIndex: Int

    private[immutable] final def initWithFocusFrom[U](that: RRBVectorPointer[U]): Unit = {
        initFocus(that.focus, that.focusStart, that.focusEnd, that.focusDepth, that.focusRelax)
        initFrom(that)
    }

    private[immutable] final def initFocus[U](focus: Int, focusStart: Int, focusEnd: Int, focusDepth: Int, focusRelax: Int): Unit = {
        this.focus = focus
        this.focusStart = focusStart
        this.focusEnd = focusEnd
        this.focusDepth = focusDepth
        this.focusRelax = focusRelax
    }

    private[immutable] final def initFromRoot(root: Array[AnyRef], depth: Int): Unit = {
        depth match {
            case 1 => display0 = root
            case 2 => display1 = root
            case 3 => display2 = root
            case 4 => display3 = root
            case 5 => display4 = root
            case 6 => display5 = root
        }
        this.depth = depth
        focusEnd = focusStart
        focusOn(0)
    }

    private[immutable] final def initFrom[U](that: RRBVectorPointer[U]): Unit = {
        depth = that.depth
        that.depth match {
            case 0 => ()
            case 1 => this.display0 = that.display0
            case 2 =>
                this.display0 = that.display0
                this.display1 = that.display1
            case 3 =>
                this.display0 = that.display0
                this.display1 = that.display1
                this.display2 = that.display2
            case 4 =>
                this.display0 = that.display0
                this.display1 = that.display1
                this.display2 = that.display2
                this.display3 = that.display3
            case 5 =>
                this.display0 = that.display0
                this.display1 = that.display1
                this.display2 = that.display2
                this.display3 = that.display3
                this.display4 = that.display4
            case 6 =>
                this.display0 = that.display0
                this.display1 = that.display1
                this.display2 = that.display2
                this.display3 = that.display3
                this.display4 = that.display4
                this.display5 = that.display5
            case _ => throw new IllegalStateException()
        }
    }

    private[immutable] final def initSingleton[B >: A](elem: B): Unit = {
        initFocus(0, 0, 1, 1, 0)
        val d0 = new Array[AnyRef](1)
        d0(0) = elem.asInstanceOf[AnyRef]
        display0 = d0
        depth = 1
    }

    private[immutable] final def root(): AnyRef = depth match {
        case 0 => null
        case 1 => display0
        case 2 => display1
        case 3 => display2
        case 4 => display3
        case 5 => display4
        case 6 => display5
        case _ => throw new IllegalStateException()
    }

    private[immutable] final def focusOn(index: Int): Unit =
        if (focusStart <= index && index < focusEnd) {
            val indexInFocus = index - focusStart
            val xor = indexInFocus ^ focus
            if (xor.>=(32))
                gotoPos(indexInFocus, xor)
            focus = index
        } else
            gotoPosFromRoot(index)

    private[immutable] final def getElementFromRoot(index: Int): A = {
        var indexInSubTree = index
        var currentDepth = depth
        var display: Array[AnyRef] = currentDepth match {
            case 2 => display1
            case 3 => display2
            case 4 => display3
            case 5 => display4
            case 6 => display5
        }

        var sizes = display(display.length.-(1)).asInstanceOf[Array[Int]]
        do {
            val sizesIdx = getIndexInSizes(sizes, indexInSubTree)
            if (sizesIdx != 0)
                indexInSubTree -= sizes(sizesIdx - 1)
            display = display(sizesIdx).asInstanceOf[Array[AnyRef]]
            if (currentDepth > 2)
                sizes = display(display.length - 1).asInstanceOf[Array[Int]]
            else
                sizes = null
            currentDepth -= 1
        } while (sizes != null)

        currentDepth match {
            case 1 => getElem0(display, indexInSubTree)
            case 2 => getElem1(display, indexInSubTree)
            case 3 => getElem2(display, indexInSubTree)
            case 4 => getElem3(display, indexInSubTree)
            case 5 => getElem4(display, indexInSubTree)
            case 6 => getElem5(display, indexInSubTree)
            case _ => throw new IllegalStateException
        }
    }

    private final def getIndexInSizes(sizes: Array[Int], indexInSubTree: Int): Int = {
        var is = 0
        while (sizes(is) <= indexInSubTree)
            is += 1
        is
    }

    private[immutable] final def gotoPosFromRoot(index: Int): Unit = {
        var _startIndex: Int = 0
        var _endIndex: Int = endIndex
        var currentDepth: Int = depth
        var _focusRelax: Int = 0
        var continue: Boolean = currentDepth > 1

        if (continue) {
            var display: Array[AnyRef] = currentDepth match {
                case 2 => display1
                case 3 => display2
                case 4 => display3
                case 5 => display4
                case 6 => display5
                case _ => throw new IllegalStateException()
            }
            do {
                val sizes = display(display.length - 1).asInstanceOf[Array[Int]]
                if (sizes == null) {
                    continue = false
                } else {
                    val is = getIndexInSizes(sizes, index - _startIndex)
                    display = display(is).asInstanceOf[Array[AnyRef]]
                    currentDepth match {
                        case 2 =>
                            display0 = display
                            continue = false
                        case 3 => display1 = display
                        case 4 => display2 = display
                        case 5 => display3 = display
                        case 6 => display4 = display
                    }
                    if (is < sizes.length - 1)
                        _endIndex = _startIndex + sizes(is)

                    if (is != 0)
                        _startIndex += sizes(is - 1)

                    currentDepth -= 1
                    _focusRelax |= is << (5 * currentDepth)
                }
            } while (continue)
        }
        val indexInFocus = index - _startIndex
        gotoPos(indexInFocus, 1 << (5 * (currentDepth - 1)))
        initFocus(indexInFocus, _startIndex, _endIndex, currentDepth, _focusRelax)
    }

    /**
     * Returns a new version of oldSizes where the size of the dirty subtree is set to 0 and the rest
     * of the sizes are adjusted
     * @param oldSizes
     * @param dirtyBranchIndex
     * @return a new Array[Int] with the adjusted sizes
     */
    private[immutable] final def makeDirtySizes(oldSizes: Array[Int], dirtyBranchIndex: Int): Array[Int] = {
        val len = oldSizes.length
        val newSizes = new Array[Int](len)

        var delta = oldSizes(dirtyBranchIndex)
        if (dirtyBranchIndex > 0) {
            delta -= oldSizes(dirtyBranchIndex - 1)
            System.arraycopy(oldSizes, 0, newSizes, 0, dirtyBranchIndex)
        }
        var i = dirtyBranchIndex
        while (i < len) {
            newSizes(i) = oldSizes(i) - delta
            i += 1
        }

        newSizes
    }

    private final def makeNewRoot(display: Array[AnyRef]): Array[AnyRef] = {
        val newRoot = new Array[AnyRef](3)
        newRoot(0) = display
        val dLen = display.length
        val dSizes = display(dLen - 1)
        if (dSizes != null) {
            val newRootSizes = new Array[Int](2)
            val dSize = dSizes.asInstanceOf[Array[Int]](dLen - 2)
            newRootSizes(0) = dSize
            newRootSizes(1) = dSize
            newRoot(2) = newRootSizes
        }
        newRoot
    }


    /**
     * Makes a dirty copy of the node
     * @param node
     * @param dirty
     * @return
     */
    private final def copyAndIncRoot(node: Array[AnyRef], dirty: Boolean): Array[AnyRef] = {
        val len = node.length
        val newRoot = copyOf(node, len, len + 1)
        val sizes = node(len - 1)
        if (sizes != null) {
            if (dirty) newRoot(len) = node(len - 1)
            else newRoot(len) = makeDirtySizes(node(len - 1).asInstanceOf[Array[Int]], len - 1)
        }
        newRoot
    }

    private[immutable] final def setupNewBlockInNextBranch(oldFocus: Int, xor: Int, dirty: Boolean): Unit = {
        if (xor < 1024) {
            if (depth == 1) {
                val newRoot = new Array[AnyRef](3)
                newRoot(0) = display0
                depth = 2
                display1 = newRoot
            } else {
                display1 = copyAndIncRoot(display1, dirty)
                if (dirty)
                    display1((oldFocus >> 5) & 31) = display0
            }
            display0 = new Array(1)
        } else if (xor < 32768) {
            copyDisplaysAndStabilizeDisplayPath(2, xor)
            if (depth == 2) {
                depth = 3
                display2 = makeNewRoot(display1)
            } else {
                display2 = copyAndIncRoot(display2, dirty)
                if (dirty)
                    display2((oldFocus >> 10) & 31) = display1
            }
            display0 = new Array(1)
            display1 = new Array(2) // TODO check if is really necessary (maybe could be nulled an initialized when stabilized)
        } else if (xor < 1048576) {
            copyDisplaysAndStabilizeDisplayPath(3, xor)
            if (depth == 3) {
                depth = 4
                display3 = makeNewRoot(display2)
            } else {
                display3 = copyAndIncRoot(display3, dirty)
                if (dirty)
                    display3((oldFocus >> 15) & 31) = display2
            }
            display0 = new Array(1)
            display1 = new Array(2) // TODO check if is really necessary (maybe could be nulled an initialized when stabilized)
            display2 = new Array(2) // TODO check if is really necessary (maybe could be nulled an initialized when stabilized)
        } else if (xor < 33554432) {
            copyDisplaysAndStabilizeDisplayPath(4, xor)
            if (depth == 4) {
                depth = 5
                display4 = makeNewRoot(display3)
            } else {
                display4 = copyAndIncRoot(display4, dirty)
                if (dirty)
                    display4((oldFocus >> 20) & 31) = display3
            }

            display0 = new Array(1)
            display1 = new Array(2)
            display2 = new Array(2)
            display3 = new Array(2)
        } else if (xor < 1073741824) {
            copyDisplaysAndStabilizeDisplayPath(5, xor)
            if (depth == 5) {
                depth = 6
                display5 = makeNewRoot(display4)
            } else {
                display5 = copyAndIncRoot(display3, dirty)
                if (dirty)
                    display5((oldFocus >> 20) & 31) = display4
            }
            display0 = new Array(1)
            display1 = new Array(2)
            display2 = new Array(2)
            display3 = new Array(2)
            display4 = new Array(2)
        } else
            throw new IllegalArgumentException()
    }

    private[immutable] final def setupNewBlockInInitBranch(insertionDepth: Int): Unit = {
        insertionDepth match {
            case 2 =>
                val d0 = new Array[AnyRef](1)
                var d1: Array[AnyRef] = null
                if (depth == 1) {
                    depth = 2
                    d1 = new Array[AnyRef](3)
                    d1(0) = d0
                    d1(1) = display0
                } else {
                    val oldD1 = display1
                    d1 = new Array[AnyRef](oldD1.length + 1)
                    d1(0) = d0
                    System.arraycopy(oldD1, 0, d1, 1, oldD1.length - 1)
                }
                display1 = withComputedSizes(d1, 2)
                display0 = d0
            case 3 =>
                val d0 = new Array[AnyRef](1)
                val d1 = new Array[AnyRef](2)
                d1(0) = d0
                var d2: Array[AnyRef] = null
                if (depth == 2) {
                    depth = 3
                    d2 = new Array[AnyRef](3)
                    d2(0) = d1
                    d2(1) = display1
                } else {
                    val oldD2 = display2
                    d2 = new Array[AnyRef](oldD2.length + 1)
                    d2(0) = d1
                    System.arraycopy(oldD2, 0, d2, 1, oldD2.length - 1)
                }
                display2 = withComputedSizes(d2, 3)
                display1 = d1
                display0 = d0
            case 4 =>
                val d0 = new Array[AnyRef](1)
                val d1 = new Array[AnyRef](2)
                val d2 = new Array[AnyRef](2)
                d1(0) = d0
                d2(0) = d1
                var d3: Array[AnyRef] = null
                if (depth == 3) {
                    depth = 4
                    d3 = new Array[AnyRef](3)
                    d3(0) = d2
                    d3(1) = display2
                } else {
                    val oldD3 = display3
                    d3 = new Array[AnyRef](oldD3.length + 1)
                    d3(0) = d2
                    System.arraycopy(oldD3, 0, d3, 1, oldD3.length - 1)
                }
                display3 = withComputedSizes(d3, 4)
                display2 = d2
                display1 = d1
                display0 = d0
            case 5 =>
                val d0 = new Array[AnyRef](1)
                val d1 = new Array[AnyRef](2)
                val d2 = new Array[AnyRef](2)
                val d3 = new Array[AnyRef](2)
                d1(0) = d0
                d2(0) = d1
                d3(0) = d2
                var d4: Array[AnyRef] = null
                if (depth == 4) {
                    depth = 5
                    d4 = new Array[AnyRef](3)
                    d4(0) = d3
                    d4(1) = display3
                } else {
                    val oldD4 = display4
                    d4 = new Array[AnyRef](oldD4.length + 1)
                    d4(0) = d3
                    System.arraycopy(oldD4, 0, d4, 1, oldD4.length - 1)
                }
                display4 = withComputedSizes(d4, 5)
                display3 = d3
                display2 = d2
                display1 = d1
                display0 = d0
            case 6 =>
                val d0 = new Array[AnyRef](1)
                val d1 = new Array[AnyRef](2)
                val d2 = new Array[AnyRef](2)
                val d3 = new Array[AnyRef](2)
                val d4 = new Array[AnyRef](2)
                d1(0) = d0
                d2(0) = d1
                d3(0) = d2
                d4(0) = d3
                var d5: Array[AnyRef] = null
                if (depth == 5) {
                    depth = 6
                    d5 = new Array[AnyRef](3)
                    d5(0) = d4
                    d5(1) = display4
                } else {
                    val oldD5 = display5
                    d5 = new Array[AnyRef](oldD5.length + 1)
                    d5(0) = d4
                    System.arraycopy(oldD5, 0, d5, 1, oldD5.length - 1)
                }
                display5 = withComputedSizes(d5, 6)
                display4 = d4
                display3 = d3
                display2 = d2
                display1 = d1
                display0 = d0
            case _ => throw new IllegalStateException()
        }
    }

    private[immutable] final def getElem(index: Int, xor: Int): A = {
        if (xor < 32) getElem0(display0, index)
        else if (xor < 1024) getElem1(display1, index)
        else if (xor < 32768) getElem2(display2, index)
        else if (xor < 1048576) getElem3(display3, index)
        else if (xor < 33554432) getElem4(display4, index)
        else if (xor < 1073741824) getElem5(display5, index)
        else throw new IllegalArgumentException(xor.toString)
    }

    private final def getElem0(display: Array[AnyRef], index: Int): A =
        display(index & 31).asInstanceOf[A]

    private final def getElem1(display: Array[AnyRef], index: Int): A =
        display((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]

    private final def getElem2(display: Array[AnyRef], index: Int): A =
        display((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]

    private final def getElem3(display: Array[AnyRef], index: Int): A =
        display((index >> 15) & 31).asInstanceOf[Array[AnyRef]]((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]

    private final def getElem4(display: Array[AnyRef], index: Int): A =
        display((index >> 20) & 31).asInstanceOf[Array[AnyRef]]((index >> 15) & 31).asInstanceOf[Array[AnyRef]]((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]

    private final def getElem5(display: Array[AnyRef], index: Int): A =
        display((index >> 25) & 31).asInstanceOf[Array[AnyRef]]((index >> 20) & 31).asInstanceOf[Array[AnyRef]]((index >> 15) & 31).asInstanceOf[Array[AnyRef]]((index >> 10) & 31).asInstanceOf[Array[AnyRef]]((index >> 5) & 31).asInstanceOf[Array[AnyRef]](index & 31).asInstanceOf[A]

    private[immutable] final def gotoPos(index: Int, xor: Int): Unit = {
        if (xor >= 32) {
            if (xor < 1024) gotoPos0(display1, index, xor)
            else if (xor < 32768) gotoPos1(display2, index, xor)
            else if (xor < 1048576) gotoPos2(display3, index, xor)
            else if (xor < 33554432) gotoPos3(display4, index, xor)
            else if (xor < 1073741824) gotoPos4(display5, index, xor)
            else throw new IllegalArgumentException()
        }

        //        if (xor.<(32))
        //            ()
        //        else if (xor < 1024)
        //            display0 = display1(((index >> 5) & 31)).asInstanceOf[Array[AnyRef]]
        //        else if (xor < 32768) {
        //            display1 = display2(((index >> 10) & 31)).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(((index >> 5) & 31)).asInstanceOf[Array[AnyRef]]
        //        } else if (xor < 1048576) {
        //            display2 = display3(((index >> 15) & 31)).asInstanceOf[Array[AnyRef]]
        //            display1 = display2(((index >> 10) & 31)).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(((index >> 5) & 31)).asInstanceOf[Array[AnyRef]]
        //        } else if (xor < 33554432) {
        //            display3 = display4(((index >> 20) & 31)).asInstanceOf[Array[AnyRef]]
        //            display2 = display3(((index >> 15) & 31)).asInstanceOf[Array[AnyRef]]
        //            display1 = display2(((index >> 10) & 31)).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(((index >> 5) & 31)).asInstanceOf[Array[AnyRef]]
        //        } else if (xor<1073741824) {
        //            display4 = display5(((index >> 25) & 31)).asInstanceOf[Array[AnyRef]]
        //            display3 = display4(((index >> 20) & 31)).asInstanceOf[Array[AnyRef]]
        //            display2 = display3(((index >> 15) & 31)).asInstanceOf[Array[AnyRef]]
        //            display1 = display2(((index >> 10) & 31)).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(((index >> 5) & 31)).asInstanceOf[Array[AnyRef]]
        //        }
        //        else
        //            throw new IllegalArgumentException()
    }

    private final def gotoPos4(d5: Array[AnyRef], index: Int, xor: Int): Unit = {
        val _d4 = d5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
        display4 = _d4
        gotoPos3(_d4, index, xor)
    }

    private final def gotoPos3(d4: Array[AnyRef], index: Int, xor: Int): Unit = {
        val _d3 = d4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
        display3 = _d3
        gotoPos2(_d3, index, xor)
    }

    private final def gotoPos2(d3: Array[AnyRef], index: Int, xor: Int): Unit = {
        val _d2 = d3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
        display2 = _d2
        gotoPos1(_d2, index, xor)
    }

    private final def gotoPos1(d2: Array[AnyRef], index: Int, xor: Int): Unit = {
        val _d1 = d2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
        display1 = _d1
        gotoPos0(_d1, index, xor)
    }

    private final def gotoPos0(d1: Array[AnyRef], index: Int, xor: Int): Unit = {
        display0 = d1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
    }


    private[immutable] final def gotoNextBlockStart(index: Int, xor: Int): Unit = {
        if (xor < 1024) {
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        } else if (xor < 32768) {
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        } else if (xor < 1048576) {
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2(0).asInstanceOf[Array[AnyRef]]
            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        } else if (xor < 33554432) {
            display3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            display2 = display3(0).asInstanceOf[Array[AnyRef]]
            display1 = display2(0).asInstanceOf[Array[AnyRef]]
            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        } else if (xor < 1073741824) {
            display4 = display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
            display3 = display4(0).asInstanceOf[Array[AnyRef]]
            display2 = display3(0).asInstanceOf[Array[AnyRef]]
            display1 = display2(0).asInstanceOf[Array[AnyRef]]
            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        } else
            throw new IllegalArgumentException()
    }

    private[immutable] final def gotoPrevBlockStart(index: Int, xor: Int): Unit = {
        if (xor < 1024)
            display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
        else if (xor < 32768) {
            display1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        } else if (xor < 1048576) {
            display2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
            display1 = display2(31).asInstanceOf[Array[AnyRef]]
            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        } else if (xor < 33554432) {
            display3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
            display2 = display3(31).asInstanceOf[Array[AnyRef]]
            display1 = display2(31).asInstanceOf[Array[AnyRef]]
            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        } else if (xor < 1073741824) {
            display4 = display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
            display3 = display4(31).asInstanceOf[Array[AnyRef]]
            display2 = display3(31).asInstanceOf[Array[AnyRef]]
            display1 = display2(31).asInstanceOf[Array[AnyRef]]
            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        } else
            throw new IllegalArgumentException()
    }

    private[immutable] final def gotoNextBlockStartWritable(index: Int, xor: Int): Unit = {
        if (xor < 1024) {
            if (depth.==(1)) {
                display1 = new Array(33)
                display1.update(0, display0)
                depth += 1
            }
            display0 = new Array(32)
            display1((index >> 5) & 31) = display0
        } else if (xor < 32768) {
            if (depth.==(2)) {
                display2 = new Array(33)
                display2.update(0, display1)
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(33)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
        } else if (xor < 1048576) {
            if (depth.==(3)) {
                display3 = new Array(33)
                display3.update(0, display2)
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(33)
            display2 = new Array(33)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
            display3((index >> 15) & 31) = display2
        } else if (xor < 33554432) {
            if (depth == 4) {
                display4 = new Array(33)
                display4.update(0, display3)
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(33)
            display2 = new Array(33)
            display3 = new Array(33)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
            display3((index >> 15) & 31) = display2
            display4((index >> 20) & 31) = display3
        } else if (xor < 1073741824) {
            if (depth == 5) {
                display5 = new Array(33)
                display5.update(0, display4)
                depth += 1
            }
            display0 = new Array(32)
            display1 = new Array(33)
            display2 = new Array(33)
            display3 = new Array(33)
            display4 = new Array(33)
            display1((index >> 5) & 31) = display0
            display2((index >> 10) & 31) = display1
            display3((index >> 15) & 31) = display2
            display4((index >> 20) & 31) = display3
            display5((index >> 25) & 31) = display4
        } else
            throw new IllegalArgumentException()
    }

    private[immutable] final def stabilize(): Unit = {
        if (RRBVector.compileAssertions) {
            assert(depth > 1)
        }
        val _focusDepth = focusDepth
        val stabilizationIndex = focus | focusRelax
        copyDisplaysAndStabilizeDisplayPath(_focusDepth, stabilizationIndex)

        val _depth = depth
        var currentLevel = _focusDepth
        var display = currentLevel match {
            case 1 => display1
            case 2 => display2
            case 3 => display3
            case 4 => display4
            case 5 => display5
        }
        while (currentLevel < _depth) {
            val newDisplay = copyOf(display)
            val idx = (stabilizationIndex >> (5 * currentLevel)) & 31
            currentLevel match {
                case 1 =>
                    newDisplay(idx) = display0
                    display1 = withRecomputeSizes(newDisplay, 2, idx) // withComputedSizes(newDisplay, 2)
                    display = display2
                case 2 =>
                    newDisplay(idx) = display1
                    display2 = withRecomputeSizes(newDisplay, 3, idx) // withComputedSizes(newDisplay, 3)
                    display = display3
                case 3 =>
                    newDisplay(idx) = display2
                    display3 = withRecomputeSizes(newDisplay, 4, idx) // withComputedSizes(newDisplay, 4)
                    display = display4
                case 4 =>
                    newDisplay(idx) = display3
                    display4 = withRecomputeSizes(newDisplay, 5, idx) // withComputedSizes(newDisplay, 5)
                    display = display5
                case 5 =>
                    newDisplay(idx) = display4
                    display5 = withRecomputeSizes(newDisplay, 6, idx) // withComputedSizes(newDisplay, 6)
            }
            currentLevel += 1
        }
    }

    private[immutable] final def copyDisplays(_depth: Int, _focus: Int): Unit = {
        if (_depth >= 2) {
            if (_depth >= 3) {
                if (_depth >= 4) {
                    if (_depth >= 5) {
                        if (_depth == 6) {
                            val idx5 = ((_focus >> 25) & 31) + 1
                            display5 = copyOf(display5, idx5, idx5.+(1))
                        }
                        val idx4 = ((_focus >> 20) & 31) + 1
                        display4 = copyOf(display4, idx4, idx4.+(1))
                    }
                    val idx3 = ((_focus >> 15) & 31) + 1
                    display3 = copyOf(display3, idx3, idx3.+(1))
                }
                val idx2 = ((_focus >> 10) & 31) + 1
                display2 = copyOf(display2, idx2, idx2.+(1))
            }
            val idx1 = ((_focus >> 5) & 31) + 1
            display1 = copyOf(display1, idx1, idx1 + 1)
        }
    }

    private[immutable] final def copyDisplaysAndNullFocusedBranch(_depth: Int, _focus: Int): Unit = {
        _depth match {
            case 2 =>
                display1 = copyOfAndNull(display1, (_focus >> 5) & 31)
            case 3 =>
                display1 = copyOfAndNull(display1, (_focus >> 5) & 31)
                display2 = copyOfAndNull(display2, (_focus >> 10) & 31)
            case 4 =>
                display1 = copyOfAndNull(display1, (_focus >> 5) & 31)
                display2 = copyOfAndNull(display2, (_focus >> 10) & 31)
                display3 = copyOfAndNull(display3, (_focus >> 15) & 31)
            case 5 =>
                display1 = copyOfAndNull(display1, (_focus >> 5) & 31)
                display2 = copyOfAndNull(display2, (_focus >> 10) & 31)
                display3 = copyOfAndNull(display3, (_focus >> 15) & 31)
                display4 = copyOfAndNull(display4, (_focus >> 20) & 31)
            case 6 =>
                display1 = copyOfAndNull(display1, (_focus >> 5) & 31)
                display2 = copyOfAndNull(display2, (_focus >> 10) & 31)
                display3 = copyOfAndNull(display3, (_focus >> 15) & 31)
                display4 = copyOfAndNull(display4, (_focus >> 20) & 31)
                display5 = copyOfAndNull(display5, (_focus >> 25) & 31)
        }
    }

    private final def copyDisplaysAndStabilizeDisplayPath(_depth: Int, _focus: Int): Unit = {
        _depth match {
            case 2 =>
                val d1 = copyOf(display1)
                d1((_focus >> 5) & 31) = display0
                display1 = d1
            case 3 =>
                val d1 = copyOf(display1)
                d1((_focus >> 5) & 31) = display0
                display1 = d1
                val d2 = copyOf(display2)
                d2((_focus >> 10) & 31) = d1
                display2 = d2
            case 4 =>
                val d1 = copyOf(display1)
                d1((_focus >> 5) & 31) = display0
                display1 = d1
                val d2 = copyOf(display2)
                d2((_focus >> 10) & 31) = d1
                display2 = d2
                val d3 = copyOf(display3)
                d3((_focus >> 15) & 31) = d2
                display3 = d3
            case 5 =>
                val d1 = copyOf(display1)
                d1((_focus >> 5) & 31) = display0
                display1 = d1
                val d2 = copyOf(display2)
                d2((_focus >> 10) & 31) = d1
                display2 = d2
                val d3 = copyOf(display3)
                d3((_focus >> 15) & 31) = d2
                display3 = d3
                val d4 = copyOf(display4)
                d4((_focus >> 20) & 31) = d3
                display4 = d4
            case 6 =>
                val d1 = copyOf(display1)
                d1((_focus >> 5) & 31) = display0
                display1 = d1
                val d2 = copyOf(display2)
                d2((_focus >> 10) & 31) = d1
                display2 = d2
                val d3 = copyOf(display3)
                d3((_focus >> 15) & 31) = d2
                display3 = d3
                val d4 = copyOf(display4)
                d4((_focus >> 20) & 31) = d3
                display4 = d4
                val d5 = copyOf(display5)
                d5((_focus >> 25) & 31) = d4
                display5 = d5
        }
    }

    private[immutable] final def copyTopAndComputeSizes(fromDepth: Int): Unit = {
        val _depth = depth
        var currentDepth = fromDepth
        while (currentDepth <= _depth) {
            currentDepth match {
                case 2 =>
                    val d1 = display1
                    val len = d1.length
                    val newD1 = copyOf(d1, len - 1, len)
                    newD1(0) = display0
                    display1 = withComputedSizes(newD1, 2)
                case 3 =>
                    val d2 = display2
                    val len = d2.length
                    val newD2 = copyOf(d2, len - 1, len)
                    newD2(0) = display1
                    display2 = withComputedSizes(newD2, 3)
                case 4 =>
                    val d3 = display3
                    val len = d3.length
                    val newD3 = copyOf(d3, len - 1, len)
                    newD3(0) = display2
                    display3 = withComputedSizes(newD3, 4)
                case 5 =>
                    val d4 = display4
                    val len = d4.length
                    val newD4 = copyOf(d4, len - 1, len)
                    newD4(0) = display3
                    display4 = withComputedSizes(newD4, 5)
                case 6 =>
                    val d5 = display5
                    val len = d5.length
                    val newD5 = copyOf(d5, len - 1, len)
                    newD5(0) = display4
                    display5 = withComputedSizes(newD5, 6)
            }
            currentDepth += 1
        }
    }

    private[immutable] final def copyDisplaysTop(currentDepth: Int, _focusRelax: Int): Unit = {
        var _currentDepth = currentDepth
        while (_currentDepth.<(this.depth)) {
            _currentDepth match {
                case 2 =>
                    val cutIndex = (_focusRelax >> 5) & 31
                    display1 = copyOf(display1, cutIndex.+(1), cutIndex.+(2))
                case 3 =>
                    val cutIndex = (_focusRelax >> 10) & 31
                    display2 = copyOf(display2, cutIndex.+(1), cutIndex.+(2))
                case 4 =>
                    val cutIndex = (_focusRelax >> 15) & 31
                    display3 = copyOf(display3, cutIndex.+(1), cutIndex.+(2))
                case 5 =>
                    val cutIndex = (_focusRelax >> 20) & 31
                    display4 = copyOf(display4, cutIndex.+(1), cutIndex.+(2))
                case 6 =>
                    val cutIndex = (_focusRelax >> 25) & 31
                    display5 = copyOf(display5, cutIndex.+(1), cutIndex.+(2))
                case _ => throw new IllegalStateException()
            }
            _currentDepth += 1
        }
    }

    private[immutable] final def stabilizeDisplayPath(_depth: Int, _focus: Int): Unit = {

        if (_depth > 1) {
            val d1 = display1
            d1((_focus >> 5) & 31) = display0
            if (_depth > 2) {
                val d2 = display2
                d2((_focus >> 10) & 31) = d1
                if (_depth > 3) {
                    val d3 = display3
                    d3((_focus >> 15) & 31) = d2
                    if (_depth > 4) {
                        val d4 = display4
                        d4((_focus >> 20) & 31) = d3
                        if (_depth > 5) {
                            display5((_focus >> 25) & 31) = d4
                        }
                    }
                }
            }
        }
        //        _depth match {
        //            case 1 => ()
        //            case 2 =>
        //                display1.update(((_focus>>5)&31), display0)
        //            case 3 =>
        //                display2.update(((_focus>>10)&31), display1)
        //                display1.update(((_focus>>5)&31), display0)
        //            case 4 =>
        //                display3.update(((_focus>>15)&31), display2)
        //                display2.update(((_focus>>10)&31), display1)
        //                display1.update(((_focus>>5)&31), display0)
        //            case 5 =>
        //                display4.update(((_focus>>20)&31), display3)
        //                display3.update(((_focus>>15)&31), display2)
        //                display2.update(((_focus>>10)&31), display1)
        //                display1.update(((_focus>>5)&31), display0)
        //            case 6 =>
        //                display5.update(((_focus>>25)&31), display4)
        //                display4.update(((_focus>>20)&31), display3)
        //                display3.update(((_focus>>15)&31), display2)
        //                display2.update(((_focus>>10)&31), display1)
        //                display1.update(((_focus>>5)&31), display0)
        //        }
    }

    private[immutable] final def cleanTop(cutIndex: Int): Unit = this.depth match {
        case 2 =>
            if ((cutIndex >> 5) == 0) {
                display1 = null
                this.depth = 1
            } else
                this.depth = 2
        case 3 =>
            if ((cutIndex >> 10) == 0) {
                display2 = null
                if ((cutIndex >> 5) == 0) {
                    display1 = null
                    this.depth = 1
                } else
                    this.depth = 2
            } else
                this.depth = 3
        case 4 =>
            if ((cutIndex >> 15) == 0) {
                display3 = null
                if ((cutIndex >> 10) == 0) {
                    display2 = null
                    if ((cutIndex >> 5) == 0) {
                        display1 = null
                        this.depth = 1
                    } else
                        this.depth = 2
                } else
                    this.depth = 3
            } else
                this.depth = 4
        case 5 =>
            if ((cutIndex >> 20) == 0) {
                display4 = null
                if ((cutIndex >> 15) == 0) {
                    display3 = null
                    if ((cutIndex >> 10) == 0) {
                        display2 = null
                        if ((cutIndex >> 5) == 0) {
                            display1 = null
                            this.depth = 1
                        } else
                            this.depth = 2
                    } else
                        this.depth = 3
                } else
                    this.depth = 4
            } else
                this.depth = 5
        case 6 =>
            if ((cutIndex >> 25) == 0) {
                display5 = null
                if ((cutIndex >> 20) == 0) {
                    display4 = null
                    if ((cutIndex >> 15) == 0) {
                        display3 = null
                        if ((cutIndex >> 10) == 0) {
                            display2 = null
                            if ((cutIndex >> 5) == 0) {
                                display1 = null
                                this.depth = 1
                            } else
                                this.depth = 2
                        } else
                            this.depth = 3
                    } else
                        this.depth = 4
                } else
                    this.depth = 5
            } else
                this.depth = 6
    }


    private[immutable] final def copyOf(array: Array[AnyRef], numElements: Int, newSize: Int) = {
        val newArray = new Array[AnyRef](newSize)
        System.arraycopy(array, 0, newArray, 0, numElements)
        newArray
    }

    private[immutable] final def copyOfAndNull(array: Array[AnyRef], nullIndex: Int) = {
        val len = array.length
        val newArray = new Array[AnyRef](len)
        System.arraycopy(array, 0, newArray, 0, len - 1)
        newArray(nullIndex) = null
        val sizes = array(len - 1).asInstanceOf[Array[Int]]
        if (sizes != null) {
            newArray(len - 1) = makeDirtySizes(sizes, nullIndex)
        }
        newArray

    }

    private[immutable] final def copyOf(array: Array[AnyRef]) = {
        val len = array.length
        val newArray = new Array[AnyRef](len)
        System.arraycopy(array, 0, newArray, 0, len)
        newArray
    }

    protected def withRecomputeSizes(node: Array[AnyRef], currentDepth: Int, branchToUpdate: Int): Array[AnyRef] = {
        if (RRBVector.compileAssertions) {
            assert(node != null)
            assert(currentDepth > 1)
        }
        val end = node.length - 1
        val oldSizes = node(end).asInstanceOf[Array[Int]]
        val newSizes = new Array[Int](end)

        val delta = treeSize(node, currentDepth - 1)
        if (branchToUpdate > 0)
            System.arraycopy(oldSizes, 0, newSizes, 0, branchToUpdate)
        var i = branchToUpdate
        while (i < end) {
            newSizes(i) = oldSizes(i) + delta
            i += 1
        }

        node
    }

    protected def withComputedSizes(node: Array[AnyRef], currentDepth: Int): Array[AnyRef] = {
        var i = 0
        var acc = 0
        val end = node.length - 1
        val sizes = new Array[Int](end)
        if (currentDepth > 1) {
            while (i.<(end)) {
                acc.+=(treeSize(node(i).asInstanceOf[Array[AnyRef]], currentDepth.-(1)))
                sizes(i) = acc
                i += 1
            }
            val last = node(end.-(1)).asInstanceOf[Array[AnyRef]]
            if (end.>(1).&&(sizes(end.-(2)).!=(end.-(1).<<(5.*(currentDepth.-(1))))).||(currentDepth.>(2).&&(last(last.length.-(1)) != null)))
                node(end) = sizes

        } else {
            while (i < end) {
                acc += node(i).asInstanceOf[Array[AnyRef]].length
                sizes(i) = acc
                i += 1
            }
            if (end > 1 && sizes(end - 2) != ((end - 1) << 5))
                node(end) = sizes

        }
        node
    }

    private def treeSize(tree: Array[AnyRef], currentDepth: Int): Int = {
        if (currentDepth == 1)
            tree.length
        else {
            val treeSizes = tree(tree.length - 1).asInstanceOf[Array[Int]]
            if (treeSizes != null)
                treeSizes(treeSizes.length - 1)
            else {
                var _tree = tree
                var _currentDepth = currentDepth
                var acc = 0
                while (_currentDepth.>(1)) {
                    acc += (_tree.length - 2) * (1 << (5 * (_currentDepth - 1)))
                    _currentDepth.-=(1)
                    _tree = _tree(_tree.length - 2).asInstanceOf[Array[AnyRef]]
                }
                acc.+(_tree.length)
            }
        }
    }
}
