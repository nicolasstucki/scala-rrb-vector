package scala.collection.immutable.rrbvector

import scala.annotation.tailrec
import scala.compat.Platform

private[immutable] trait RelaxedVectorPointer[A]
  extends VectorPointer[A] {
    self: VectorProps =>

    private[immutable] var _length: Int = 0

    private[immutable] var focusStart: Int = 0
    private[immutable] var focusEnd: Int = 0

    private[immutable] final def initDisplayWithoutFocus(root: AnyRef, height: Int, length: Int): this.type = {
        this.height = height
        this._length = length
        this.focusStart = 0
        this.focusEnd = 0
        height match {
            case 0 =>
            case 1 => display0 = root.asInstanceOf[Array[AnyRef]]
            case 2 => display1 = root.asInstanceOf[Array[AnyRef]]
            case 3 => display2 = root.asInstanceOf[Array[AnyRef]]
            case 4 => display3 = root.asInstanceOf[Array[AnyRef]]
            case 5 => display4 = root.asInstanceOf[Array[AnyRef]]
            case 6 => display5 = root.asInstanceOf[Array[AnyRef]]
            case _ => throw new IllegalStateException("Illegal vector height: " + height)
        }
        this
    }

    private[immutable] final def initDisplay1(display0: Array[AnyRef], length: Int): this.type = {
        this.height = 1
        this._length = length
        this.focusEnd = length
        this.display0 = display0
        this
    }

    private[immutable] final def initDisplay2(display0: Array[AnyRef], display1: Array[AnyRef], length: Int, focus: Int, focusStart: Int, focusEnd: Int): this.type = {
        this.height = 2
        this._length = length
        this.focus = focus
        this.focusStart = focusStart
        this.focusEnd = focusEnd
        this.display0 = display0
        this.display1 = display1
        this
    }

    private[immutable] final def initDisplay3(display0: Array[AnyRef], display1: Array[AnyRef], display2: Array[AnyRef], length: Int, focus: Int, focusStart: Int, focusEnd: Int): this.type = {
        this.height = 3
        this._length = length
        this.focus = focus
        this.focusStart = focusStart
        this.focusEnd = focusEnd
        this.display0 = display0
        this.display1 = display1
        this.display2 = display2
        this
    }

    private[immutable] final def initDisplay4(display0: Array[AnyRef], display1: Array[AnyRef], display2: Array[AnyRef], display3: Array[AnyRef], length: Int, focus: Int, focusStart: Int, focusEnd: Int): this.type = {
        this.height = 4
        this._length = length
        this.focus = focus
        this.focusStart = focusStart
        this.focusEnd = focusEnd
        this.display0 = display0
        this.display1 = display1
        this.display2 = display2
        this.display3 = display3
        this
    }

    private[immutable] final def initDisplay5(display0: Array[AnyRef], display1: Array[AnyRef], display2: Array[AnyRef], display3: Array[AnyRef], display4: Array[AnyRef], length: Int, focus: Int, focusStart: Int, focusEnd: Int): this.type = {
        this.height = 5
        this._length = length
        this.focus = focus
        this.focusStart = focusStart
        this.focusEnd = focusEnd
        this.display0 = display0
        this.display1 = display1
        this.display2 = display2
        this.display3 = display3
        this.display4 = display4
        this
    }

    private[immutable] final def initDisplay6(display0: Array[AnyRef], display1: Array[AnyRef], display2: Array[AnyRef], display3: Array[AnyRef], display4: Array[AnyRef], display5: Array[AnyRef], length: Int, focus: Int, focusStart: Int, focusEnd: Int): this.type = {
        this.height = 6
        this._length = length
        this.focus = focus
        this.focusStart = focusStart
        this.focusEnd = focusEnd
        this.display0 = display0
        this.display1 = display1
        this.display2 = display2
        this.display3 = display3
        this.display4 = display4
        this.display5 = display5
        this
    }

    private[immutable] final def getElement(index: Int): A = {
        if (focusStart <= index && index < focusEnd) {
            val indexInFocus = index - focusStart
            getElementFromFocus(indexInFocus, indexInFocus ^ focus)
        } else if (index < 0 || this._length <= index) {
            throw new IndexOutOfBoundsException(index.toString)
        } else if (_length == 0) {
            throw new UnsupportedOperationException()
        } else {
            focusOnPositionFromRoot(index)
            display0((index - focusStart) & 31).asInstanceOf[A]
        }
    }

    private[immutable] final def focusOnPosition(index: Int) {
        if (focusStart <= index && index < focusEnd) {
            val indexInFocus = index - focusStart
            focusPositionFromOldFocus(indexInFocus, indexInFocus ^ focus)
        } else if (index < 0 || this._length <= index) {
            throw new IndexOutOfBoundsException(index.toString)
        } else if (_length == 0) {
            throw new UnsupportedOperationException()
        } else {
            focusOnPositionFromRoot(index)
        }
    }

    private[immutable] final def focusOnPositionFromRoot(index: Int) {
        @tailrec
        def focusOnPositionRec(start: Int, end: Int, height: Int): Unit = {
            // TODO: rewrite this method to avoid the match in each loop
            val display = height match {
                case 1 => display0
                case 2 => display1
                case 3 => display2
                case 4 => display3
                case 5 => display4
                case _ => throw new IllegalArgumentException("height=" + height)
            }

            if (height > 1 && display(0) != null) {
                val szs = display(0).asInstanceOf[Array[Int]]
                val ix = index - start
                var is = 0 //ix >> ((height - 1) * WIDTH_SHIFT)
                while (szs(is) <= ix)
                    is += 1
                height match {
                    case 2 => display0 = display(is + 1).asInstanceOf[Array[AnyRef]]
                    case 3 => display1 = display(is + 1).asInstanceOf[Array[AnyRef]]
                    case 4 => display2 = display(is + 1).asInstanceOf[Array[AnyRef]]
                    case 5 => display3 = display(is + 1).asInstanceOf[Array[AnyRef]]
                    case 6 => display4 = display(is + 1).asInstanceOf[Array[AnyRef]]
                    case _ => throw new IllegalArgumentException("height=" + height)
                }

                focusOnPositionRec(
                    if (is == 0) start else start + szs(is - 1),
                    start + szs(is),
                    height - 1)
            } else {
                val indexInFocus = index - start
                focusPositionFromOldFocus(indexInFocus, 1 << (5 * (height - 1)))
                focusStart = start
                focusEnd = end
            }
        }
        focusOnPositionRec(0, _length, height)
    }


    protected final def appendedToBranch(branch: Array[AnyRef], node: AnyRef, size: Int): Array[AnyRef] = {
        val newBranch = appendedToBranchNoInvar(branch, node)
        if (branch(0) != null) {
            val sizes = branch(0).asInstanceOf[Array[Int]]
            val newSizes = new Array[Int](sizes.length + 1)
            Platform.arraycopy(sizes, 0, newSizes, 0, sizes.length)
            newSizes(sizes.length) = sizes(sizes.length - 1) + size
            newBranch(0) = newSizes
        }
        newBranch
    }

    protected final def joinBranches(branch0: Array[AnyRef], branch1: Array[AnyRef]): Array[AnyRef] = {
        val newRoot = new Array[AnyRef](2 + INVAR)
        newRoot(1) = branch0
        newRoot(2) = branch1
        if (branch0(branch0.length - 1) != null) {
            //            // TODO set sizes in tb(0)
        }
        newRoot
    }

    protected final def joinLeafs(leaf0: Array[AnyRef], leaf1: Array[AnyRef]): Array[AnyRef] = {
        val newRoot = new Array[AnyRef](2 + INVAR)
        newRoot(1) = leaf0
        newRoot(2) = leaf1
        if (leaf0.length < WIDTH) {
            val sizes = new Array[Int](2)
            sizes(0) = leaf0.length
            sizes(1) = leaf0.length + leaf1.length
            newRoot(0) = sizes
        }
        newRoot
    }

    protected final def mergeLeafs(leaf0: Array[AnyRef], leaf1: Array[AnyRef]): Array[AnyRef] = {
        val length0 = leaf0.length
        val length1 = leaf1.length
        val newLeaf = new Array[AnyRef](length0 + length1)
        Platform.arraycopy(leaf0, 0, newLeaf, 0, length0)
        Platform.arraycopy(leaf1, 0, newLeaf, length0, length1)
        newLeaf
    }

    protected final def replacedLastWithBranch(branch: Array[AnyRef], node: AnyRef, deltaSize: Int): Array[AnyRef] = {
        val newBranch = replacedLastOfBranchNoInvar(branch, node)
        if (branch(0) != null) {
            val sizes = branch(0).asInstanceOf[Array[Int]]
            val newSizes = new Array[Int](sizes.length)
            Platform.arraycopy(sizes, 0, newSizes, 0, sizes.length - 1)
            newSizes(sizes.length - 1) = sizes(sizes.length - 1) + deltaSize
            newBranch(0) = newSizes
        }
        newBranch
    }
}

