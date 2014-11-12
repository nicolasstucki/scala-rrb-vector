package scala
package collection
package immutable
package rrbvector

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.immutable.rrbvector.ParRRBVector
import scala.compat.Platform

import scala.annotation.unchecked.uncheckedVariance

import scala.collection.generic._

object RRBVector extends scala.collection.generic.IndexedSeqFactory[RRBVector] {
    def newBuilder[A]: mutable.Builder[A, RRBVector[A]] = new RRBVectorBuilder[A]()

    implicit def canBuildFrom[A]: scala.collection.generic.CanBuildFrom[Coll, A, RRBVector[A]] = ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

    lazy private val EMPTY_VECTOR = new RRBVector[Nothing](0)

    override def empty[A]: RRBVector[A] = EMPTY_VECTOR
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
        }
        val it = new RRBVectorIterator[A](0, endIndex)
        it.initIteratorFrom(this)
        it
    }

    override def reverseIterator: RRBVectorReverseIterator[A] = {
        if (this.dirty) {
            this.stabilize()
            this.dirty = false
        }
        val it = new RRBVectorReverseIterator[A](0, endIndex)
        it.initIteratorFrom(this)
        it
    }

    def apply(index: Int): A = {
        val _focusStart = this.focusStart
        if /* index is in focused subtree */ (_focusStart <= index && index < focusEnd) {
            val indexInFocus = index.-(_focusStart)
            getElement(indexInFocus, indexInFocus ^ focus)
        } else if /* index is in the vector bounds */ (0 <= index && index < endIndex) {
            getElementFromRoot(index)
        } else
            throw new IndexOutOfBoundsException(index.toString)
    }

    override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[RRBVector[A], B, That]): That = if (bf.eq(IndexedSeq.ReusableCBF))
        if (this.endIndex == 0) {
            val resultVector = new RRBVector[B](1)
            resultVector.initSingleton(elem)
            resultVector.asInstanceOf[That]
        } else {
            val _endIndex = this.endIndex
            val resultVector = new RRBVector[B](_endIndex + 1)
            resultVector.initWithFocusFrom(this)
            resultVector.dirty = this.dirty
            if /* next element will go in a new block */ ((((_endIndex - 1) - this.focusStart) ^ this.focus) >= 32) {
                if (resultVector.dirty) {
                    resultVector.stabilize()
                    resultVector.dirty = false
                }
                resultVector.focusOn(_endIndex.-(1))
            }

            val elemIndexInBlock = _endIndex.-(resultVector.focusStart).&(31)
            if /* next element will go in a new block position */ (elemIndexInBlock == 0) {
                if (resultVector.dirty) {
                    resultVector.stabilize()
                    resultVector.dirty = false
                }
                resultVector.appendBackSetupNewBlock()
                resultVector.display0.update(elemIndexInBlock, elem.asInstanceOf[AnyRef])
                resultVector.asInstanceOf[That]
            } else /* if next element will go in current block position */ {
                resultVector.dirty = true
                resultVector.focusEnd = resultVector.endIndex
                val d0 = new Array[AnyRef](elemIndexInBlock + 1)
                Platform.arraycopy(resultVector.display0, 0, d0, 0, elemIndexInBlock)
                d0(elemIndexInBlock) = elem.asInstanceOf[AnyRef]
                resultVector.display0 = d0
                resultVector.asInstanceOf[That]
            }
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
                case thatVec: RRBVector[B] =>
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

    override def tail: RRBVector[A] = if (this.endIndex.!=(0))
        this.drop(1)
    else
        throw new UnsupportedOperationException("empty.tail")

    override def last: A = if (this.endIndex.!=(0))
        this.apply(this.length.-(1))
    else
        throw new UnsupportedOperationException("empty.last")

    override def init: RRBVector[A] = if (this.endIndex.!=(0))
        dropRight(1)
    else
        throw new UnsupportedOperationException("empty.init")

    private[immutable] def appendBackSetupNewBlock() = {
        val oldDepth = depth
        val newRelaxedIndex = (endIndex - 1) - focusStart + focusRelax
        val xor = newRelaxedIndex ^ (focus | focusRelax)
        setupNewBlockInNextBranch(newRelaxedIndex, xor)
        if /* setupNewBlockInNextBranch(...) increased the depth of the tree */ (oldDepth == depth) {

            var i =
                if (xor.<(1024)) 2
                else if (xor.<(32768)) 3
                else if (xor.<(1048576)) 4
                else if (xor.<(33554432)) 5
                else 6
            val _focusDepth = focusDepth
            while (i.<(oldDepth)) {
                var display: Array[AnyRef] = null
                var newDisplay: Array[AnyRef] = null
                var newSizes: Array[Int] = null
                i match {
                    case 2 => display = display2
                    case 3 => display = display3
                    case 4 => display = display4
                    case 5 => display = display5
                }
                val displayLen = display.length.-(1)
                if (i.>=(_focusDepth)) {
                    val oldSizes = display(displayLen).asInstanceOf[Array[Int]]
                    newSizes = new Array[Int](displayLen)
                    Platform.arraycopy(oldSizes, 0, newSizes, 0, displayLen.-(1))
                    newSizes.update(displayLen.-(1), oldSizes(displayLen.-(1)).+(1))
                }

                newDisplay = new Array[AnyRef](display.length)
                Platform.arraycopy(display, 0, newDisplay, 0, displayLen)
                if (i >= _focusDepth)
                    newDisplay(displayLen) = newSizes

                i match {
                    case 2 =>
                        newDisplay((newRelaxedIndex >> 10) & 31) = display1
                        display2 = newDisplay
                    case 3 =>
                        newDisplay((newRelaxedIndex >> 15) & 31) = display2
                        display3 = newDisplay
                    case 4 =>
                        newDisplay((newRelaxedIndex >> 20) & 31) = display3
                        display4 = newDisplay
                    case 5 =>
                        newDisplay((newRelaxedIndex >> 25) & 31) = display4
                        display5 = newDisplay
                }
                i += 1
            }

        }

        if (oldDepth.==(focusDepth))
            initFocus(endIndex.-(1), 0, endIndex, depth, 0)
        else
            initFocus(endIndex.-(1), endIndex.-(1), endIndex, 1, newRelaxedIndex.&(-32))
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
                initFromRoot(concat, if (endIndex.<=(32))
                    1
                else
                    2)
            case 2 =>
                var d0: Array[AnyRef] = null
                var d1: Array[AnyRef] = null
                if (that.focus.&(-32).==(0)) {
                    d1 = that.display1
                    d0 = that.display0
                }
                else {
                    if (that.display1.!=(null))
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
                    if (that.display2.!=(null))
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
                    if (that.display3.!=(null))
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
                    if (that.display4.!=(null))
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
                    if (that.display5.!=(null))
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
    }

    private def rebalanced(displayLeft: Array[AnyRef], concat: Array[AnyRef], displayRight: Array[AnyRef], currentDepth: Int): Array[AnyRef] = {
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
        val branching = computeBranching(displayLeft, concat, displayRight, currentDepth)
        val top = new Array[AnyRef](branching.>>(10).+(if (branching.&(1023).==(0))
            1
        else
            2))
        var mid = new Array[AnyRef](if (branching.>>(10).==(0))
            branching.+(31).>>(5).+(1)
        else
            33)
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
                case 0 => if (displayLeft.!=(null)) {
                    currentDisplay = displayLeft
                    if (concat.==(null))
                        displayEnd = leftLength
                    else
                        displayEnd = leftLength.-(1)
                }

                case 1 =>
                    if (concat.==(null))
                        displayEnd = 0
                    else {
                        currentDisplay = concat
                        displayEnd = concatLength
                    }
                    i = 0
                case 2 => if (displayRight.!=(null)) {
                    currentDisplay = displayRight
                    displayEnd = rightLength
                    if (concat.==(null))
                        i = 0
                    else
                        i = 1
                }

            }
            while (i.<(displayEnd)) {
                val displayValue = currentDisplay(i).asInstanceOf[Array[AnyRef]]
                val displayValueEnd = displayValue.length.-(if (currentDepth.==(2))
                    0
                else
                    1)
                if (iBot.|(j).==(0).&&(displayValueEnd.==(32))) {
                    if (currentDepth.!=(2).&&(bot.!=(null))) {
                        withComputedSizes(bot, currentDepth.-(1))
                        bot = null
                    }

                    mid.update(iMid, displayValue)
                    i += 1
                    iMid.+=(1)
                    iSizes.+=(1)
                }
                else {
                    val numElementsToCopy = math.min(displayValueEnd.-(j), 32.-(iBot))
                    if (iBot.==(0)) {
                        if (currentDepth.!=(2).&&(bot.!=(null)))
                            withComputedSizes(bot, currentDepth.-(1))

                        bot = new Array[AnyRef](math.min(branching.-(iTop.<<(10)).-(iMid.<<(5)), 32).+(if (currentDepth.==(2))
                            0
                        else
                            1))
                        mid.update(iMid, bot)
                    }

                    Platform.arraycopy(displayValue, j, bot, iBot, numElementsToCopy)
                    j.+=(numElementsToCopy)
                    iBot.+=(numElementsToCopy)
                    if (j.==(displayValueEnd)) {
                        i.+=(1)
                        j = 0
                    }

                    if (iBot.==(32)) {
                        iMid.+=(1)
                        iBot = 0
                        iSizes.+=(1)
                        if (currentDepth != 2 && bot != null)
                            withComputedSizes(bot, currentDepth - 1)
                    }

                }
                if (iMid.==(32)) {
                    top.update(iTop, withComputedSizes(mid, currentDepth))
                    iTop.+=(1)
                    iMid = 0
                    val remainingBranches = branching.-(iTop.<<(5).|(iMid).<<(5).|(iBot))
                    if (remainingBranches.>(0))
                        mid = new Array[AnyRef](if (remainingBranches.>>(10).==(0))
                            remainingBranches.+(63).>>(5)
                        else
                            33)
                    else
                        mid = null
                }

            }
            d.+=(1)
        }
        while (d.<(3))
        if (currentDepth.!=(2).&&(bot.!=(null)))
            withComputedSizes(bot, currentDepth.-(1))

        if (mid.!=(null))
            top.update(iTop, withComputedSizes(mid, currentDepth))

        top
    }

    private def rebalancedLeafs(displayLeft: Array[AnyRef], displayRight: Array[AnyRef], isTop: Boolean): Array[AnyRef] = {
        val leftLength = displayLeft.length
        val rightLength = displayRight.length
        if (leftLength.==(32)) {
            val top = new Array[AnyRef](3)
            top.update(0, displayLeft)
            top.update(1, displayRight)
            top
        }
        else
        if (leftLength.+(rightLength).<=(32)) {
            val mergedDisplay = new Array[AnyRef](leftLength.+(rightLength))
            Platform.arraycopy(displayLeft, 0, mergedDisplay, 0, leftLength)
            Platform.arraycopy(displayRight, 0, mergedDisplay, leftLength, rightLength)
            if (isTop)
                mergedDisplay
            else {
                val top = new Array[AnyRef](2)
                top.update(0, mergedDisplay)
                top
            }
        }
        else {
            val top = new Array[AnyRef](3)
            val arr0 = new Array[AnyRef](32)
            val arr1 = new Array[AnyRef](leftLength.+(rightLength).-(32))
            top.update(0, arr0)
            top.update(1, arr1)
            Platform.arraycopy(displayLeft, 0, arr0, 0, leftLength)
            Platform.arraycopy(displayRight, 0, arr0, leftLength, 32.-(leftLength))
            Platform.arraycopy(displayRight, 32.-(leftLength), arr1, 0, rightLength.-(32).+(leftLength))
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
                i.+=(1)
            }
            i = 0
            while (i.<(concatLength)) {
                branching.+=(concat(i).asInstanceOf[Array[AnyRef]].length)
                i.+=(1)
            }
            i = 1
            while (i.<(rightLength)) {
                branching.+=(displayRight(i).asInstanceOf[Array[AnyRef]].length)
                i.+=(1)
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

    private def withComputedSizes(node: Array[AnyRef], currentDepth: Int): Array[AnyRef] = {
        var i = 0
        var acc = 0
        val end = node.length.-(1)
        val sizes = new Array[Int](end)
        if (currentDepth.>(1)) {
            while (i.<(end)) {
                acc.+=(treeSize(node(i).asInstanceOf[Array[AnyRef]], currentDepth.-(1)))
                sizes.update(i, acc)
                i.+=(1)
            }
            val last = node(end.-(1)).asInstanceOf[Array[AnyRef]]
            if (end.>(1).&&(sizes(end.-(2)).!=(end.-(1).<<(5.*(currentDepth.-(1))))).||(currentDepth.>(2).&&(last(last.length.-(1)).!=(null))))
                node.update(end, sizes)

        } else {
            while (i.<(end)) {
                acc.+=(node(i).asInstanceOf[Array[AnyRef]].length)
                sizes.update(i, acc)
                i.+=(1)
            }
            if (end.>(1).&&(sizes(end.-(2)).!=(end.-(1).<<(5))))
                node.update(end, sizes)

        }
        node
    }

    private def treeSize(tree: Array[AnyRef], currentDepth: Int): Int =
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
                Platform.arraycopy(vec.display0, 0, d0, 0, d0len)
                vec.display0 = d0
            }

            val cutIndex = vec.focus | vec.focusRelax
            vec.cleanTop(cutIndex)
            vec.focusDepth = math.min(vec.depth, vec.focusDepth)
            if (vec.depth > 1) {
                vec.copyDisplays(vec.focusDepth, cutIndex)
                var i = vec.depth
                var offset = 0
                while (i.>(vec.focusDepth)) {
                    val display = i match {
                        case 2 => vec.display1
                        case 3 => vec.display2
                        case 4 => vec.display3
                        case 5 => vec.display4
                        case 6 => vec.display5
                    }
                    val oldSizes = display(display.length.-(1)).asInstanceOf[Array[Int]]
                    val newLen = vec.focusRelax.>>(5.*(i.-(1))).&(31).+(1)
                    val newSizes = new Array[Int](newLen)
                    Platform.arraycopy(oldSizes, 0, newSizes, 0, newLen.-(1))
                    newSizes.update(newLen.-(1), n.-(offset))
                    if (newLen.>(1))
                        offset.+=(newSizes(newLen.-(2)))

                    val newDisplay = new Array[AnyRef](newLen.+(1))
                    Platform.arraycopy(display, 0, newDisplay, 0, newLen)
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
            Platform.arraycopy(vec.display0, 0, d0, 0, n)
            vec.display0 = d0
            vec.initFocus(0, 0, n, 1, 0)
        } /* else { do nothing } */

        vec
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
            resultVector
        }
    }

    def result(): RRBVector[A] = {
        val current = resultCurrent()
        if (acc == null) current
        else acc ++ current
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
    private var blockIndex: Int = _
    private var lo: Int = _
    private var endLo: Int = _
    private var _hasNext: Boolean = _

    final private[collection] def initIteratorFrom[B >: A](that: RRBVectorPointer[B]): Unit = {
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
        lo = _lo.+(1)
        val _endLo = endLo
        if (_lo.+(1).!=(_endLo))
            res
        else {
            val oldBlockIndex = blockIndex
            val newBlockIndex = oldBlockIndex.+(_endLo)
            blockIndex = newBlockIndex
            lo = 0
            if (newBlockIndex.<(focusEnd)) {
                val _focusStart = focusStart
                val newBlockIndexInFocus = newBlockIndex.-(_focusStart)
                gotoNextBlockStart(newBlockIndexInFocus, newBlockIndexInFocus.^(oldBlockIndex.-(_focusStart)))
            }
            else
            if (newBlockIndex.<(endIndex))
                focusOn(newBlockIndex)
            else {
                lo = (focusEnd - 1) & 31
                blockIndex = endIndex
                if (_hasNext)
                    _hasNext = false
                else
                    throw new NoSuchElementException("reached iterator end")
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

    final private[collection] def initIteratorFrom[B >: A](that: RRBVectorPointer[B]): Unit = {
        initWithFocusFrom(that)
        _hasNext = startIndex < endIndex
        if (_hasNext) {
            val idx = endIndex.-(1)
            focusOn(idx)
            lastIndexOfBlock = idx
            lo = idx.-(focusStart).&(31)
            endLo = math.max(startIndex.-(focusStart).-(lastIndexOfBlock), 0)
        }
        else {
            lastIndexOfBlock = 0
            lo = 0
            endLo = 0
            display0 = new Array[AnyRef](1)
        }
    }

    final def hasNext = _hasNext

    def next(): A = if (_hasNext) {
        val res = display0(lo).asInstanceOf[A]
        lo.-=(1)
        if (lo.>=(endLo))
            res
        else {
            val newBlockIndex = lastIndexOfBlock.-(32)
            if (focusStart.<=(newBlockIndex)) {
                val _focusStart = focusStart
                val newBlockIndexInFocus = newBlockIndex.-(_focusStart)
                gotoPrevBlockStart(newBlockIndexInFocus, newBlockIndexInFocus.^(lastIndexOfBlock.-(_focusStart)))
                lastIndexOfBlock = newBlockIndex
                lo = 31
                endLo = math.max(startIndex.-(focusStart).-(focus), 0)
                res
            }
            else
            if (startIndex.<(focusStart)) {
                val newIndex = focusStart.-(1)
                focusOn(newIndex)
                lastIndexOfBlock = newIndex
                lo = newIndex.-(focusStart).&(31)
                endLo = math.max(startIndex.-(focusStart).-(lastIndexOfBlock), 0)
                res
            }
            else {
                _hasNext = false
                res
            }
        }
    }
    else
        throw new NoSuchElementException("reached iterator end")
}

private[immutable] trait RRBVectorPointer[A] {
    final private[immutable] var display0: Array[AnyRef] = _
    final private[immutable] var display1: Array[AnyRef] = _
    final private[immutable] var display2: Array[AnyRef] = _
    final private[immutable] var display3: Array[AnyRef] = _
    final private[immutable] var display4: Array[AnyRef] = _
    final private[immutable] var display5: Array[AnyRef] = _
    final private[immutable] var depth: Int = _
    final private[immutable] var focusStart: Int = 0
    final private[immutable] var focusEnd: Int = 0
    final private[immutable] var focusDepth: Int = 0
    final private[immutable] var focus: Int = 0
    final private[immutable] var focusRelax: Int = 0

    private[immutable] def endIndex: Int

    final private[immutable] def initWithFocusFrom[U](that: RRBVectorPointer[U]): Unit = {
        initFocus(that.focus, that.focusStart, that.focusEnd, that.focusDepth, that.focusRelax)
        initFrom(that)
    }

    final private[immutable] def initFocus[U](focus: Int, focusStart: Int, focusEnd: Int, focusDepth: Int, focusRelax: Int): Unit = {
        this.focus = focus
        this.focusStart = focusStart
        this.focusEnd = focusEnd
        this.focusDepth = focusDepth
        this.focusRelax = focusRelax
    }

    final private[immutable] def initFromRoot(root: Array[AnyRef], depth: Int): Unit = {
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

    final private[immutable] def initFrom[U](that: RRBVectorPointer[U]): Unit = {
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

    final private[immutable] def initSingleton[B >: A](elem: B): Unit = {
        initFocus(0, 0, 1, 1, 0)
        val d0 = new Array[AnyRef](1)
        d0.update(0, elem.asInstanceOf[AnyRef])
        display0 = d0
        depth = 1
    }

    final private[immutable] def root(): AnyRef = depth match {
        case 0 => null
        case 1 => display0
        case 2 => display1
        case 3 => display2
        case 4 => display3
        case 5 => display4
        case 6 => display5
        case _ => throw new IllegalStateException()
    }

    final private[immutable] def focusOn(index: Int): Unit = if (focusStart.<=(index).&&(index.<(focusEnd))) {
        val indexInFocus = index.-(focusStart)
        val xor = indexInFocus.^(focus)
        if (xor.>=(32))
            gotoPos(indexInFocus, xor)

        focus = index
    }
    else
        gotoPosFromRoot(index)

    final private[immutable] def getElementFromRoot(index: Int): A = {
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
                sizes = display(display.length.-(1)).asInstanceOf[Array[Int]]
            else
                sizes = null
            currentDepth -= 1
        } while (sizes != null)

        currentDepth match {
            case 1 => getElement0(display, indexInSubTree)
            case 2 => getElement1(display, indexInSubTree)
            case 3 => getElement2(display, indexInSubTree)
            case 4 => getElement3(display, indexInSubTree)
            case 5 => getElement4(display, indexInSubTree)
            case 6 => getElement5(display, indexInSubTree)
            case _ => throw new IllegalStateException
        }
    }

    final private def getIndexInSizes(sizes: Array[Int], indexInSubTree: Int): Int = {
        var is = 0
        while (sizes(is) <=indexInSubTree)
            is+=1
        is
    }

    final private[immutable] def gotoPosFromRoot(index: Int): Unit = {
        var _startIndex: Int = 0
        var _endIndex: Int = endIndex
        var currentDepth: Int = depth
        var _focusRelax: Int = 0
        var continue: Boolean = currentDepth.>(1)
        while (continue)
            if (currentDepth <= 1)
                continue = false
            else {
                val display = currentDepth match {
                    case 2 => display1
                    case 3 => display2
                    case 4 => display3
                    case 5 => display4
                    case 6 => display5
                    case _ => throw new IllegalStateException()
                }
                val sizes = display(display.length.-(1)).asInstanceOf[Array[Int]]
                if (sizes == null)
                    continue = false
                else {
                    val is = getIndexInSizes(sizes, index.-(_startIndex))
                    currentDepth match {
                        case 2 => display0 = display(is).asInstanceOf[Array[AnyRef]]
                        case 3 => display1 = display(is).asInstanceOf[Array[AnyRef]]
                        case 4 => display2 = display(is).asInstanceOf[Array[AnyRef]]
                        case 5 => display3 = display(is).asInstanceOf[Array[AnyRef]]
                        case 6 => display4 = display(is).asInstanceOf[Array[AnyRef]]
                    }
                    if (is.<(sizes.length.-(1)))
                        _endIndex = _startIndex.+(sizes(is))

                    if (is.!=(0))
                        _startIndex += sizes(is.-(1))

                    currentDepth.-=(1)
                    _focusRelax.|=(is.<<(5.*(currentDepth)))
                }
            }
        val indexInFocus = index - _startIndex
        gotoPos(indexInFocus, 1 << (5 * (currentDepth - 1)))
        initFocus(indexInFocus, _startIndex, _endIndex, currentDepth, _focusRelax)
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
            newRootSizes(1) = dSize + 1
            newRoot(2) = newRootSizes
        }
        newRoot
    }

    private final def copyAndIncRoot(display: Array[AnyRef]): Array[AnyRef] = {
        val len = display.length
        val newRoot = copyOf(display, len, len + 1)
        val sizes = display(len - 1)
        if (sizes != null) {
            val newSizes = new Array[Int](len)
            Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len - 1)
            newSizes(len - 1) = newSizes(len - 2) + 1
            newRoot(len) = newSizes
        }
        newRoot
    }

    final private def setupNewBlockInNextBranch2(index: Int, xor: Int): Array[AnyRef] = {
        val d1 = new Array[AnyRef](2)
        val d2: Array[AnyRef] =
            if (xor >= 32768) {
                setupNewBlockInNextBranch3(index, xor)
            } else if (depth == 2) {
                depth = 3
                makeNewRoot(display1)
            } else {
                copyAndIncRoot(display2)
            }
        d2((index >> 10) & 31) = d1
        display2 = d2
        d1
    }

    final private def setupNewBlockInNextBranch3(index: Int, xor: Int): Array[AnyRef] = {
        val d2 = new Array[AnyRef](2)
        val d3: Array[AnyRef] =
            if (xor >= 1048576) {
                setupNewBlockInNextBranch4(index, xor)
            } else if (depth == 3) {
                depth = 4
                makeNewRoot(display2)
            } else {
                copyAndIncRoot(display3)
            }
        d3((index >> 15) & 31) = d2
        display3 = d3
        d2
    }

    final private def setupNewBlockInNextBranch4(index: Int, xor: Int): Array[AnyRef] = {
        val d3 = new Array[AnyRef](2)
        val d4: Array[AnyRef] =
            if (xor >= 33554432) {
                setupNewBlockInNextBranch5(index, xor)
            } else if (depth == 4) {
                depth = 5
                makeNewRoot(display3)
            } else {
                copyAndIncRoot(display4)
            }
        d4((index >> 20) & 31) = d3
        display4 = d4
        d3
    }

    final private def setupNewBlockInNextBranch5(index: Int, xor: Int): Array[AnyRef] = {
        if (xor >= 1073741824)
            throw new IllegalArgumentException(xor.toString)
        val d4 = new Array[AnyRef](2)
        val d5: Array[AnyRef] =
            if (depth == 5) {
                depth = 6
                makeNewRoot(display4)
            } else {
                copyAndIncRoot(display5)
            }
        d5((index >> 25) & 31) = d4
        display5 = d5
        d4
    }

    final private[immutable] def setupNewBlockInNextBranch(index: Int, xor: Int): Unit = {
        val d1: Array[AnyRef] =
            if (xor >= 1024) {
                setupNewBlockInNextBranch2(index, xor)
            } else if (depth == 1) {
                depth = 2
                val _d1 = new Array[AnyRef](3)
                _d1(0) = display0
                _d1
            } else {
                val len = display1.length
                val _d1 = copyOf(display1, len, len + 1)
                val sizes = display1(len - 1)
                if (sizes != null) {
                    val newSizes = new Array[Int](len)
                    Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len - 1)
                    newSizes(len - 1) = newSizes(len - 2) + 1
                    _d1(len) = newSizes
                }
                _d1
            }
        val d0 = new Array[AnyRef](1)
        d1((index >> 5) & 31) = d0
        display1 = d1
        display0 = d0

        ////////////////////////////////////////////

        //        if (xor.<(1024)) {
        //            if (depth == 1) {
        //                val newRoot = new Array[AnyRef](3)
        //                newRoot.update(0, display0)
        //                display1 = newRoot
        //                depth = 2
        //            }
        //            else {
        //                val len = display1.length
        //                val newRoot = copyOf(display1, len, len.+(1))
        //                val sizes = display1(len.-(1))
        //                if (sizes.!=(null)) {
        //                    val newSizes = new Array[Int](len)
        //                    Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len.-(1))
        //                    newSizes.update(len.-(1), newSizes(len.-(2)).+(1))
        //                    newRoot.update(len, newSizes)
        //                }
        //
        //                display1 = newRoot
        //            }
        //            display0 = new Array(1)
        //            display1.update(index.>>(5).&(31), display0)
        //        }
        //        else
        //        if (xor.<(32768)) {
        //            if (depth == 2) {
        //                val newRoot = new Array[AnyRef](3)
        //                newRoot.update(0, display1);
        //                {
        //                    val dLen = display1.length
        //                    val dSizes = display1(dLen.-(1))
        //                    if (dSizes.!=(null)) {
        //                        val newRootSizes = new Array[Int](2)
        //                        val dSize = dSizes.asInstanceOf[Array[Int]](dLen.-(2))
        //                        newRootSizes.update(0, dSize)
        //                        newRootSizes.update(1, dSize.+(1))
        //                        newRoot.update(2, newRootSizes)
        //                    }
        //
        //                }
        //                display2 = newRoot
        //                depth = 3
        //            }
        //            else {
        //                val len = display2.length
        //                val newRoot = copyOf(display2, len, len.+(1))
        //                val sizes = display2(len.-(1))
        //                if (sizes.!=(null)) {
        //                    val newSizes = new Array[Int](len)
        //                    Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len.-(1))
        //                    newSizes.update(len.-(1), newSizes(len.-(2)).+(1))
        //                    newRoot.update(len, newSizes)
        //                }
        //
        //                display2 = newRoot
        //            }
        //            display0 = new Array(1)
        //            display1 = new Array(2)
        //            display1.update(index.>>(5).&(31), display0)
        //            display2.update(index.>>(10).&(31), display1)
        //        }
        //        else
        //        if (xor.<(1048576)) {
        //            if (depth.==(3)) {
        //                val newRoot = new Array[AnyRef](3)
        //                newRoot.update(0, display2);
        //                {
        //                    val dLen = display2.length
        //                    val dSizes = display2(dLen.-(1))
        //                    if (dSizes.!=(null)) {
        //                        val newRootSizes = new Array[Int](2)
        //                        val dSize = dSizes.asInstanceOf[Array[Int]](dLen.-(2))
        //                        newRootSizes.update(0, dSize)
        //                        newRootSizes.update(1, dSize.+(1))
        //                        newRoot.update(2, newRootSizes)
        //                    }
        //
        //                }
        //                display3 = newRoot
        //                depth = 4
        //            }
        //            else {
        //                val len = display3.length
        //                val newRoot = copyOf(display3, len, len.+(1))
        //                val sizes = display3(len.-(1))
        //                if (sizes.!=(null)) {
        //                    val newSizes = new Array[Int](len)
        //                    Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len.-(1))
        //                    newSizes.update(len.-(1), newSizes(len.-(2)).+(1))
        //                    newRoot.update(len, newSizes)
        //                }
        //
        //                display3 = newRoot
        //            }
        //            display0 = new Array(1)
        //            display1 = new Array(2)
        //            display2 = new Array(2)
        //            display1.update(index.>>(5).&(31), display0)
        //            display2.update(index.>>(10).&(31), display1)
        //            display3.update(index.>>(15).&(31), display2)
        //        }
        //        else
        //        if (xor.<(33554432)) {
        //            if (depth.==(4)) {
        //                val newRoot = new Array[AnyRef](3)
        //                newRoot.update(0, display3);
        //                {
        //                    val dLen = display3.length
        //                    val dSizes = display3(dLen.-(1))
        //                    if (dSizes.!=(null)) {
        //                        val newRootSizes = new Array[Int](2)
        //                        val dSize = dSizes.asInstanceOf[Array[Int]](dLen.-(2))
        //                        newRootSizes.update(0, dSize)
        //                        newRootSizes.update(1, dSize.+(1))
        //                        newRoot.update(2, newRootSizes)
        //                    }
        //
        //                }
        //                display4 = newRoot
        //                depth = 5
        //            }
        //            else {
        //                val len = display4.length
        //                val newRoot = copyOf(display4, len, len.+(1))
        //                val sizes = display4(len.-(1))
        //                if (sizes.!=(null)) {
        //                    val newSizes = new Array[Int](len)
        //                    Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len.-(1))
        //                    newSizes.update(len.-(1), newSizes(len.-(2)).+(1))
        //                    newRoot.update(len, newSizes)
        //                }
        //
        //                display4 = newRoot
        //            }
        //            display0 = new Array(1)
        //            display1 = new Array(2)
        //            display2 = new Array(2)
        //            display3 = new Array(2)
        //            display1.update(index.>>(5).&(31), display0)
        //            display2.update(index.>>(10).&(31), display1)
        //            display3.update(index.>>(15).&(31), display2)
        //            display4.update(index.>>(20).&(31), display3)
        //        }
        //        else
        //        if (xor.<(1073741824)) {
        //            if (depth.==(5)) {
        //                val newRoot = new Array[AnyRef](3)
        //                newRoot.update(0, display4);
        //                {
        //                    val dLen = display4.length
        //                    val dSizes = display4(dLen.-(1))
        //                    if (dSizes.!=(null)) {
        //                        val newRootSizes = new Array[Int](2)
        //                        val dSize = dSizes.asInstanceOf[Array[Int]](dLen.-(2))
        //                        newRootSizes.update(0, dSize)
        //                        newRootSizes.update(1, dSize.+(1))
        //                        newRoot.update(2, newRootSizes)
        //                    }
        //
        //                }
        //                display5 = newRoot
        //                depth = 6
        //            }
        //            else {
        //                val len = display5.length
        //                val newRoot = copyOf(display5, len, len.+(1))
        //                val sizes = display5(len.-(1))
        //                if (sizes.!=(null)) {
        //                    val newSizes = new Array[Int](len)
        //                    Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len.-(1))
        //                    newSizes.update(len.-(1), newSizes(len.-(2)).+(1))
        //                    newRoot.update(len, newSizes)
        //                }
        //
        //                display5 = newRoot
        //            }
        //            display0 = new Array(1)
        //            display1 = new Array(2)
        //            display2 = new Array(2)
        //            display3 = new Array(2)
        //            display4 = new Array(2)
        //            display1.update(index.>>(5).&(31), display0)
        //            display2.update(index.>>(10).&(31), display1)
        //            display3.update(index.>>(15).&(31), display2)
        //            display4.update(index.>>(20).&(31), display3)
        //            display5.update(index.>>(25).&(31), display4)
        //        }
        //        else
        //            throw new IllegalArgumentException()
    }

    final private def getElement0(display: Array[AnyRef], index: Int): A =
        display(index.&(31)).asInstanceOf[A]

    final private def getElement1(display: Array[AnyRef], index: Int): A =
        display(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]](index.&(31)).asInstanceOf[A]

    final private def getElement2(display: Array[AnyRef], index: Int): A =
        display(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]](index.>>(5).&(31)).asInstanceOf[Array[AnyRef]](index.&(31)).asInstanceOf[A]

    final private def getElement3(display: Array[AnyRef], index: Int): A =
        display(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]](index.>>(10).&(31)).asInstanceOf[Array[AnyRef]](index.>>(5).&(31)).asInstanceOf[Array[AnyRef]](index.&(31)).asInstanceOf[A]

    final private def getElement4(display: Array[AnyRef], index: Int): A =
        display(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]](index.>>(15).&(31)).asInstanceOf[Array[AnyRef]](index.>>(10).&(31)).asInstanceOf[Array[AnyRef]](index.>>(5).&(31)).asInstanceOf[Array[AnyRef]](index.&(31)).asInstanceOf[A]

    final private def getElement5(display: Array[AnyRef], index: Int): A =
        display(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]](index.>>(20).&(31)).asInstanceOf[Array[AnyRef]](index.>>(15).&(31)).asInstanceOf[Array[AnyRef]](index.>>(10).&(31)).asInstanceOf[Array[AnyRef]](index.>>(5).&(31)).asInstanceOf[Array[AnyRef]](index.&(31)).asInstanceOf[A]


    final private[immutable] def getElement(index: Int, xor: Int): A = {
        if (xor < 32) getElement0(display0, index)
        else if (xor < 1024) getElement1(display1, index)
        else if (xor < 32768) getElement2(display2, index)
        else if (xor < 1048576) getElement3(display3, index)
        else if (xor < 33554432) getElement4(display4, index)
        else if (xor < 1073741824) getElement5(display5, index)
        else throw new IllegalArgumentException()
    }

    final private[immutable] def gotoPos0(d1: Array[AnyRef], index: Int, xor: Int): Unit = {
        display0 = d1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
    }

    final private[immutable] def gotoPos1(d2: Array[AnyRef], index: Int, xor: Int): Unit = {
        val _d1 = d2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]]
        display1 = _d1
        gotoPos0(_d1, index, xor)
    }

    final private[immutable] def gotoPos2(d3: Array[AnyRef], index: Int, xor: Int): Unit = {
        val _d2 = d3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]]
        display2 = _d2
        gotoPos1(_d2, index, xor)
    }

    final private[immutable] def gotoPos3(d4: Array[AnyRef], index: Int, xor: Int): Unit = {
        val _d3 = d4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
        display3 = _d3
        gotoPos2(_d3, index, xor)
    }

    final private[immutable] def gotoPos4(d5: Array[AnyRef], index: Int, xor: Int): Unit = {
        val _d4 = d5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
        display4 = _d4
        gotoPos3(_d4, index, xor)
    }

    final private[immutable] def gotoPos(index: Int, xor: Int): Unit = {
        if (xor >= 32) {
            if (xor < 1024) gotoPos0(display1, index, xor)
            else if (xor < 32768) gotoPos1(display2, index, xor)
            else if (xor < 1048576) gotoPos2(display3, index, xor)
            else if (xor < 33554432) gotoPos3(display4, index, xor)
            else if (xor < 1073741824) gotoPos4(display5, index, xor)
            else throw new IllegalArgumentException()
        }


        //        if (xor >= 32) {
        //            val d1 = if (xor >= 1024) {
        //                val d2 = if (xor >= 32768) {
        //                    val d3 = if (xor >= 1048576) {
        //                        val d4 = if (xor >= 33554432) {
        //                            if (xor >= 1073741824)
        //                                throw new IllegalArgumentException()
        //                            val _d4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]]
        //                            display4 = _d4
        //                            _d4
        //                        } else display4
        //                        val _d3 = d4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]]
        //                        display3 = _d3
        //                        _d3
        //                    } else display3
        //                    val _d2 = d3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]]
        //                    display2 = _d2
        //                    _d2
        //                } else display2
        //                val _d1 = d2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]]
        //                display1 = _d1
        //                _d1
        //            } else display1
        //            display0 = d1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
        //        }

        //        if (xor.<(32))
        //            ()
        //        else if (xor.<(1024))
        //            display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
        //        else if (xor.<(32768)) {
        //            display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
        //        } else if (xor.<(1048576)) {
        //            display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]]
        //            display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
        //        } else if (xor.<(33554432)) {
        //            display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]]
        //            display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]]
        //            display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
        //        } else if (xor.<(1073741824)) {
        //            display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]]
        //            display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]]
        //            display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]]
        //            display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
        //        }
        //        else
        //            throw new IllegalArgumentException()
    }

    final private[immutable] def gotoNextBlockStart(index: Int, xor: Int): Unit = {

        //        if (xor >= 1024) {
        //            if (xor >= 32768) {
        //                if (xor >= 1048576) {
        //                    if (xor >= 33554432) {
        //                        if (xor >= 1073741824) throw new IllegalArgumentException()
        //                        else display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]]
        //                        display3 = display4(0).asInstanceOf[Array[AnyRef]]
        //                    } else display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]]
        //                    display2 = display3(0).asInstanceOf[Array[AnyRef]]
        //                } else display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]]
        //                display1 = display2(0).asInstanceOf[Array[AnyRef]]
        //            } else display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        //        } else display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]

        var idx = 0
        if (xor >= 1024) {
            if (xor >= 32768) {
                if (xor >= 1048576) {
                    if (xor >= 33554432) {
                        if (xor >= 1073741824) throw new IllegalArgumentException()
                        else display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]]
                    } else idx = (index >> 20) & 31
                    display3 = display4(idx).asInstanceOf[Array[AnyRef]]
                    idx = 0
                } else idx = (index >> 15) & 31
                display2 = display3(idx).asInstanceOf[Array[AnyRef]]
                idx = 0
            } else idx = (index >> 10) & 31
            display1 = display2(idx).asInstanceOf[Array[AnyRef]]
            idx = 0
        } else idx = (index >> 5) & 31
        display0 = display1(idx).asInstanceOf[Array[AnyRef]]

        //        if (xor < 1024) {
        //            display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
        //        } else {
        //            if (xor < 32768) {
        //                display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]]
        //            } else {
        //                if (xor < 1048576) {
        //                    display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]]
        //                } else {
        //                    if (xor < 33554432) {
        //                        display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]]
        //                    } else {
        //                        if (xor < 1073741824) {
        //                            display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]]
        //                        } else {
        //                            throw new IllegalArgumentException()
        //                        }
        //                        display3 = display4(0).asInstanceOf[Array[AnyRef]]
        //                    }
        //                    display2 = display3(0).asInstanceOf[Array[AnyRef]]
        //                }
        //                display1 = display2(0).asInstanceOf[Array[AnyRef]]
        //            }
        //            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        //        }

        //        if (xor.<(1024))
        //            display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
        //        else
        //        if (xor.<(32768)) {
        //            display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        //        }
        //        else
        //        if (xor.<(1048576)) {
        //            display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]]
        //            display1 = display2(0).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        //        }
        //        else
        //        if (xor.<(33554432)) {
        //            display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]]
        //            display2 = display3(0).asInstanceOf[Array[AnyRef]]
        //            display1 = display2(0).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        //        }
        //        else
        //        if (xor.<(1073741824)) {
        //            display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]]
        //            display3 = display4(0).asInstanceOf[Array[AnyRef]]
        //            display2 = display3(0).asInstanceOf[Array[AnyRef]]
        //            display1 = display2(0).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(0).asInstanceOf[Array[AnyRef]]
        //        }
        //        else
        //            throw new IllegalArgumentException()
    }

    final private[immutable] def gotoPrevBlockStart(index: Int, xor: Int): Unit = {
        //        if (xor >= 1024) {
        //            if (xor >= 32768) {
        //                if (xor >= 1048576) {
        //                    if (xor >= 33554432) {
        //                        if (xor >= 1073741824) throw new IllegalArgumentException()
        //                        else display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]]
        //                        display3 = display4(31).asInstanceOf[Array[AnyRef]]
        //                    } else display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]]
        //                    display2 = display3(31).asInstanceOf[Array[AnyRef]]
        //                } else display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]]
        //                display1 = display2(31).asInstanceOf[Array[AnyRef]]
        //            } else display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        //        } else display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]

        var idx = 31
        if (xor >= 1024) {
            if (xor >= 32768) {
                if (xor >= 1048576) {
                    if (xor >= 33554432) {
                        if (xor >= 1073741824) throw new IllegalArgumentException()
                        else display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]]
                    } else idx = (index >> 20) & 31
                    display3 = display4(idx).asInstanceOf[Array[AnyRef]]
                    idx = 31
                } else idx = (index >> 15) & 31
                display2 = display3(idx).asInstanceOf[Array[AnyRef]]
                idx = 31
            } else idx = (index >> 10) & 31
            display1 = display2(idx).asInstanceOf[Array[AnyRef]]
            idx = 31
        } else idx = (index >> 5) & 31
        display0 = display1(idx).asInstanceOf[Array[AnyRef]]


        //        if (xor.<(1024))
        //            display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
        //        else
        //        if (xor.<(32768)) {
        //            display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        //        }
        //        else
        //        if (xor.<(1048576)) {
        //            display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]]
        //            display1 = display2(31).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        //        }
        //        else
        //        if (xor.<(33554432)) {
        //            display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]]
        //            display2 = display3(31).asInstanceOf[Array[AnyRef]]
        //            display1 = display2(31).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        //        }
        //        else
        //        if (xor.<(1073741824)) {
        //            display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]]
        //            display3 = display4(31).asInstanceOf[Array[AnyRef]]
        //            display2 = display3(31).asInstanceOf[Array[AnyRef]]
        //            display1 = display2(31).asInstanceOf[Array[AnyRef]]
        //            display0 = display1(31).asInstanceOf[Array[AnyRef]]
        //        }
        //        else
        //            throw new IllegalArgumentException()
    }

    final private[immutable] def gotoNextBlockStartWritable(index: Int, xor: Int): Unit = {
        //        val d1 = new Array[AnyRef](33)
        //        if (xor >= 1024) {
        //            val d2 = new Array[AnyRef](33)
        //            if (xor >= 32768) {
        //                val d3 = new Array[AnyRef](33)
        //                if (xor >= 1048576) {
        //                    var d4: Array[AnyRef] = new Array[AnyRef](33)
        //                    if (xor >= 33554432) {
        //                        if (xor >= 1073741824) {
        //                            throw new IllegalArgumentException()
        //                        } else if (depth.==(5)) {
        //                            val d5 = new Array[AnyRef](33)
        //                            d5.update(0, display4)
        //                            display5 = d5
        //                            depth += 1
        //                        }
        //                        display5.update(index.>>(25).&(31), d4)
        //                    } else if (depth.==(4)) {
        //                        d4.update(0, display3)
        //                        depth.+=(1)
        //                    } else {
        //                        d4 = display4
        //                    }
        //                    d4.update(index.>>(20).&(31), d3)
        //                    display4 = d4
        //                } else if (depth.==(3)) {
        //                    d3.update(0, display2)
        //                    depth.+=(1)
        //                }
        //                d3.update(index.>>(15).&(31), d2)
        //                display3 = d3
        //            } else if (depth.==(2)) {
        //                d2.update(0, display1)
        //                depth += 1
        //            }
        //            d2.update(index.>>(10).&(31), d1)
        //            display2 = d2
        //        } else if (depth.==(1)) {
        //            d1.update(0, display0)
        //            depth += 1
        //        }
        //        val d0 = new Array[AnyRef](32)
        //        d1.update(index.>>(5).&(31), d0)
        //        display1 = d1
        //        display0 = d0

        if (xor.<(1024)) {
            if (depth.==(1)) {
                display1 = new Array(33)
                display1.update(0, display0)
                depth.+=(1)
            }
            display0 = new Array(32)
            display1.update(index.>>(5).&(31), display0)
        } else if (xor.<(32768)) {
            if (depth.==(2)) {
                display2 = new Array(33)
                display2.update(0, display1)
                depth.+=(1)
            }
            display0 = new Array(32)
            display1 = new Array(33)
            display1.update(index.>>(5).&(31), display0)
            display2.update(index.>>(10).&(31), display1)
        } else if (xor.<(1048576)) {
            if (depth.==(3)) {
                display3 = new Array(33)
                display3.update(0, display2)
                depth.+=(1)
            }
            display0 = new Array(32)
            display1 = new Array(33)
            display2 = new Array(33)
            display1.update(index.>>(5).&(31), display0)
            display2.update(index.>>(10).&(31), display1)
            display3.update(index.>>(15).&(31), display2)
        } else if (xor.<(33554432)) {
            if (depth.==(4)) {
                display4 = new Array(33)
                display4.update(0, display3)
                depth.+=(1)
            }
            display0 = new Array(32)
            display1 = new Array(33)
            display2 = new Array(33)
            display3 = new Array(33)
            display1.update(index.>>(5).&(31), display0)
            display2.update(index.>>(10).&(31), display1)
            display3.update(index.>>(15).&(31), display2)
            display4.update(index.>>(20).&(31), display3)
        } else if (xor.<(1073741824)) {
            if (depth.==(5)) {
                display5 = new Array(33)
                display5.update(0, display4)
                depth.+=(1)
            }
            display0 = new Array(32)
            display1 = new Array(33)
            display2 = new Array(33)
            display3 = new Array(33)
            display4 = new Array(33)
            display1.update(index.>>(5).&(31), display0)
            display2.update(index.>>(10).&(31), display1)
            display3.update(index.>>(15).&(31), display2)
            display4.update(index.>>(20).&(31), display3)
            display5.update(index.>>(25).&(31), display4)
        } else
            throw new IllegalArgumentException()
    }

    final private[immutable] def stabilize(): Unit = {
        val _depth = depth
        if (_depth.>(1)) {
            val stabilizationIndex = focus.|(focusRelax)
            val deltaSize = display0.length.-(display1(stabilizationIndex.>>(5).&(31)).asInstanceOf[Array[AnyRef]].length)
            val _focusDepth = focusDepth
            copyDisplaysAndStabilizeDisplayPath(_focusDepth, stabilizationIndex)
            var currentDepth = _focusDepth.+(1)
            var display = currentDepth match {
                case 2 => display1
                case 3 => display2
                case 4 => display3
                case 5 => display4
                case 6 => display5
            }
            while (currentDepth.<=(_depth)) {
                val oldSizes = display(display.length.-(1)).asInstanceOf[Array[Int]]
                val newSizes = new Array[Int](oldSizes.length)
                val lastSizesIndex = oldSizes.length.-(1)
                Platform.arraycopy(oldSizes, 0, newSizes, 0, lastSizesIndex)
                newSizes(lastSizesIndex) = oldSizes(lastSizesIndex) + deltaSize
                val idx = stabilizationIndex.>>(5.*(currentDepth)).&(31)
                val newDisplay = copyOf(display, idx, idx.+(2))
                newDisplay.update(newDisplay.length.-(1), newSizes)
                currentDepth match {
                    case 2 =>
                        newDisplay.update(idx, display0)
                        display1.update(idx, newDisplay)
                        display = display2
                    case 3 =>
                        newDisplay.update(idx, display1)
                        display2.update(idx, newDisplay)
                        display = display3
                    case 4 =>
                        newDisplay.update(idx, display2)
                        display3.update(idx, newDisplay)
                        display = display4
                    case 5 =>
                        newDisplay.update(idx, display3)
                        display4.update(idx, newDisplay)
                        display = display5
                    case 6 =>
                        newDisplay.update(idx, display4)
                        display5.update(idx, newDisplay)
                }
                currentDepth.+=(1)
            }

        }

    }

    final private[immutable] def copyDisplays(_depth: Int, _focus: Int): Unit = {
        if (_depth >= 2) {
            if (_depth >= 3) {
                if (_depth >= 4) {
                    if (_depth >= 5) {
                        if (_depth == 6) {
                            val idx5 = _focus.>>(25).&(31) + 1
                            display5 = copyOf(display5, idx5, idx5.+(1))
                        }
                        val idx4 = _focus.>>(20).&(31) + 1
                        display4 = copyOf(display4, idx4, idx4.+(1))
                    }
                    val idx3 = _focus.>>(15).&(31) + 1
                    display3 = copyOf(display3, idx3, idx3.+(1))
                }
                val idx2 = _focus.>>(10).&(31) + 1
                display2 = copyOf(display2, idx2, idx2.+(1))
            }
            val idx1 = _focus.>>(5).&(31) + 1
            display1 = copyOf(display1, idx1, idx1 + 1)
        }

    }

    final private def copyDisplaysAndStabilizeDisplayPath(_depth: Int, _focus: Int): Unit = {
        if (_depth >= 2) {
            val idx1 = _focus.>>(5).&(31)
            val d1 = copyOf(display1, idx1 + 1, idx1 + 2)
            d1.update(idx1, display0)
            display1 = d1
            if (_depth >= 3) {
                val idx2 = _focus.>>(10).&(31)
                val d2 = copyOf(display2, idx2 + 1, idx2.+(2))
                d2.update(idx2, d1)
                display2 = d2
                if (_depth >= 4) {
                    val idx3 = _focus.>>(15).&(31)
                    val d3 = copyOf(display3, idx3 + 1, idx3.+(2))
                    d3.update(idx3, d2)
                    display3 = d3
                    if (_depth >= 5) {
                        val idx4 = _focus.>>(20).&(31)
                        val d4 = copyOf(display4, idx4 + 1, idx4.+(2))
                        d4.update(idx4, d3)
                        display4 = d4
                        if (_depth == 6) {
                            val idx5 = _focus.>>(25).&(31)
                            val d5 = copyOf(display5, idx5 + 1, idx5.+(2))
                            d5.update(idx5, d4)
                            display5 = d5
                        }
                    }
                }
            }
        }
    }

    final private[immutable] def copyDisplaysTop(currentDepth: Int, _focusRelax: Int): Unit = {
        var _currentDepth = currentDepth
        while (_currentDepth.<(this.depth)) {
            _currentDepth match {
                case 2 =>
                    val cutIndex = _focusRelax.>>(5).&(31)
                    display1 = copyOf(display1, cutIndex.+(1), cutIndex.+(2))
                case 3 =>
                    val cutIndex = _focusRelax.>>(10).&(31)
                    display2 = copyOf(display2, cutIndex.+(1), cutIndex.+(2))
                case 4 =>
                    val cutIndex = _focusRelax.>>(15).&(31)
                    display3 = copyOf(display3, cutIndex.+(1), cutIndex.+(2))
                case 5 =>
                    val cutIndex = _focusRelax.>>(20).&(31)
                    display4 = copyOf(display4, cutIndex.+(1), cutIndex.+(2))
                case 6 =>
                    val cutIndex = _focusRelax.>>(25).&(31)
                    display5 = copyOf(display5, cutIndex.+(1), cutIndex.+(2))
                case _ => throw new IllegalStateException()
            }
            _currentDepth += 1
        }
    }

    final private[immutable] def stabilizeDisplayPath(_depth: Int, _focus: Int): Unit = {
        if (_depth > 1) {
            val d1 = display1
            if (_depth > 2) {
                val d2 = display2
                if (_depth > 3) {
                    val d3 = display3
                    if (_depth > 4) {
                        val d4 = display4
                        if (_depth > 5) {
                            display5.update(_focus.>>(25).&(31), d4)
                        }
                        d4.update(_focus.>>(20).&(31), d3)
                    }
                    d3.update(_focus.>>(15).&(31), d2)
                }
                d2.update(_focus.>>(10).&(31), d1)
            }
            d1.update(_focus.>>(5).&(31), display0)
        }
        //        _depth match {
        //            case 1 => ()
        //            case 2 =>
        //                display1.update(_focus.>>(5).&(31), display0)
        //            case 3 =>
        //                display2.update(_focus.>>(10).&(31), display1)
        //                display1.update(_focus.>>(5).&(31), display0)
        //            case 4 =>
        //                display3.update(_focus.>>(15).&(31), display2)
        //                display2.update(_focus.>>(10).&(31), display1)
        //                display1.update(_focus.>>(5).&(31), display0)
        //            case 5 =>
        //                display4.update(_focus.>>(20).&(31), display3)
        //                display3.update(_focus.>>(15).&(31), display2)
        //                display2.update(_focus.>>(10).&(31), display1)
        //                display1.update(_focus.>>(5).&(31), display0)
        //            case 6 =>
        //                display5.update(_focus.>>(25).&(31), display4)
        //                display4.update(_focus.>>(20).&(31), display3)
        //                display3.update(_focus.>>(15).&(31), display2)
        //                display2.update(_focus.>>(10).&(31), display1)
        //                display1.update(_focus.>>(5).&(31), display0)
        //        }
    }

    private[immutable] def cleanTop(cutIndex: Int): Unit = this.depth match {

        case 2 => if (cutIndex.>>(5) == 0) {
            display1 = null
            this.depth = 1
        }
        else
            this.depth = 2
        case 3 => if (cutIndex.>>(10) == 0) {
            display2 = null
            if (cutIndex.>>(5) == 0) {
                display1 = null
                this.depth = 1
            }
            else
                this.depth = 2
        }
        else
            this.depth = 3
        case 4 => if (cutIndex.>>(15) == 0) {
            display3 = null
            if (cutIndex.>>(10) == 0) {
                display2 = null
                if (cutIndex.>>(5) == 0) {
                    display1 = null
                    this.depth = 1
                }
                else
                    this.depth = 2
            }
            else
                this.depth = 3
        }
        else
            this.depth = 4
        case 5 => if (cutIndex.>>(20) == 0) {
            display4 = null
            if (cutIndex.>>(15) == 0) {
                display3 = null
                if (cutIndex.>>(10) == 0) {
                    display2 = null
                    if (cutIndex.>>(5) == 0) {
                        display1 = null
                        this.depth = 1
                    }
                    else
                        this.depth = 2
                }
                else
                    this.depth = 3
            }
            else
                this.depth = 4
        }
        else
            this.depth = 5
        case 6 => if (cutIndex.>>(25) == 0) {
            display5 = null
            if (cutIndex.>>(20) == 0) {
                display4 = null
                if (cutIndex.>>(15) == 0) {
                    display3 = null
                    if (cutIndex.>>(10) == 0) {
                        display2 = null
                        if (cutIndex.>>(5) == 0) {
                            display1 = null
                            this.depth = 1
                        }
                        else
                            this.depth = 2
                    }
                    else
                        this.depth = 3
                }
                else
                    this.depth = 4
            }
            else
                this.depth = 5
        }
        else
            this.depth = 6
    }

    final private[immutable] def copyOf(array: Array[AnyRef], numElements: Int, newSize: Int) = {
        val newArray = new Array[AnyRef](newSize)
        Platform.arraycopy(array, 0, newArray, 0, numElements)
        newArray
    }
}
