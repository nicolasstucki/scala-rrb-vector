package scala.collection.immutable.oldrrbvector

import scala.compat.Platform

import VectorProps._

private[immutable] trait VectorPointer[A] {

    private[immutable] var height: Int = 0

    private[immutable] var display5: Array[AnyRef] = _
    private[immutable] var display4: Array[AnyRef] = _
    private[immutable] var display3: Array[AnyRef] = _
    private[immutable] var display2: Array[AnyRef] = _
    private[immutable] var display1: Array[AnyRef] = _
    private[immutable] var display0: Array[AnyRef] = _

    private[immutable] def root: AnyRef = {
        height match {
            case 0 => null
            case 1 => display0
            case 2 => display1
            case 3 => display2
            case 4 => display3
            case 5 => display4
            case 6 => display5
            case _ => throw new IllegalStateException("Illegal vector height: " + height)
        }
    }

    protected final def getElementFromFocus(indexInFocus: Int, xor: Int): A = {
        if /* level = 0 */ (xor < (1 << WIDTH_SHIFT)) {
            display0(indexInFocus & 31).asInstanceOf[A]
        } else if /* level = 1 */ (xor < (1 << (2 * WIDTH_SHIFT))) {
            display1(((indexInFocus >> WIDTH_SHIFT) & 31) + 1).asInstanceOf[Array[AnyRef]](indexInFocus & 31).asInstanceOf[A]
        } else if /* level = 2 */ (xor < (1 << (3 * WIDTH_SHIFT))) {
            display2(((indexInFocus >> (2 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]](((indexInFocus >> WIDTH_SHIFT) & 31) + 1).asInstanceOf[Array[AnyRef]](indexInFocus & 31).asInstanceOf[A]
        } else if /* level = 3 */ (xor < (1 << (4 * WIDTH_SHIFT))) {
            display3(((indexInFocus >> (3 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]](((indexInFocus >> (2 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]](((indexInFocus >> WIDTH_SHIFT) & 31) + 1).asInstanceOf[Array[AnyRef]](indexInFocus & 31).asInstanceOf[A]
        } else if /* level = 4 */ (xor < (1 << (5 * WIDTH_SHIFT))) {
            display4(((indexInFocus >> (4 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]](((indexInFocus >> (3 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]](((indexInFocus >> (2 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]](((indexInFocus >> WIDTH_SHIFT) & 31) + 1).asInstanceOf[Array[AnyRef]](indexInFocus & 31).asInstanceOf[A]
        } else if /* level = 5 */ (xor < (1 << 30)) {
            display5(((indexInFocus >> (5 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]](((indexInFocus >> (4 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]](((indexInFocus >> (3 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]](((indexInFocus >> (2 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]](((indexInFocus >> WIDTH_SHIFT) & 31) + 1).asInstanceOf[Array[AnyRef]](indexInFocus & 31).asInstanceOf[A]
        } else /* level > 5 */ {
            throw new IllegalArgumentException(s"xor=$xor")
        }
    }

    protected final def focusPositionFromOldFocus(indexInFocus: Int, xor: Int): Unit = {
        if /* level = 0 */ (xor < (1 << WIDTH_SHIFT)) {
            // Do nothing
        } else if /* level = 1 */ (xor < (1 << (2 * WIDTH_SHIFT))) {
            display0 = display1(((indexInFocus >> WIDTH_SHIFT) & 31) + 1).asInstanceOf[Array[AnyRef]]
        } else if /* level = 2 */ (xor < (1 << (3 * WIDTH_SHIFT))) {
            display1 = display2(((indexInFocus >> (2 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]]
            display0 = display1(((indexInFocus >> WIDTH_SHIFT) & 31) + 1).asInstanceOf[Array[AnyRef]]
        } else if /* level = 3 */ (xor < (1 << (4 * WIDTH_SHIFT))) {
            display2 = display3(((indexInFocus >> (3 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]]
            display1 = display2(((indexInFocus >> (2 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]]
            display0 = display1(((indexInFocus >> WIDTH_SHIFT) & 31) + 1).asInstanceOf[Array[AnyRef]]
        } else if /* level = 4 */ (xor < (1 << (5 * WIDTH_SHIFT))) {
            display3 = display4(((indexInFocus >> (4 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]]
            display2 = display3(((indexInFocus >> (3 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]]
            display1 = display2(((indexInFocus >> (2 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]]
            display0 = display1(((indexInFocus >> WIDTH_SHIFT) & 31) + 1).asInstanceOf[Array[AnyRef]]
        } else if /* level = 5 */ (xor < (1 << (6 * WIDTH_SHIFT))) {
            display4 = display5(((indexInFocus >> (5 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]]
            display3 = display4(((indexInFocus >> (4 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]]
            display2 = display3(((indexInFocus >> (3 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]]
            display1 = display2(((indexInFocus >> (2 * WIDTH_SHIFT)) & 31) + 1).asInstanceOf[Array[AnyRef]]
            display0 = display1(((indexInFocus >> WIDTH_SHIFT) & 31) + 1).asInstanceOf[Array[AnyRef]]
        } else /* level > 5 */ {
            throw new IllegalArgumentException(s"xor=$xor")
        }
    }

    protected final def unitLeaf(elem: AnyRef): Array[AnyRef] = {
        val tl = new Array[AnyRef](1)
        tl(0) = elem.asInstanceOf[AnyRef]
        tl
    }

    protected final def unitBranch(node: AnyRef): Array[AnyRef] = {
        val tb = new Array[AnyRef](1 + INVAR)
        tb(1) = node
        tb
    }

    protected final def appendedToLeaf(leaf: Array[AnyRef], value: AnyRef): Array[AnyRef] = {
        val newLeaf = new Array[AnyRef](leaf.length + 1)
        Platform.arraycopy(leaf, 0, newLeaf, 0, leaf.length)
        newLeaf(leaf.length) = value
        newLeaf
    }

    protected final def appendedToBranchNoInvar(branch: Array[AnyRef], node: AnyRef): Array[AnyRef] = {
        val oldBranchLength = branch.length
        val newBranch = new Array[AnyRef](oldBranchLength + 1)
        Platform.arraycopy(branch, 1, newBranch, 1, oldBranchLength - INVAR)
        newBranch(oldBranchLength) = node
        newBranch
    }

    protected final def replacedLastOfBranchNoInvar(branch: Array[AnyRef], node: AnyRef): Array[AnyRef] = {
        val branchLength = branch.length
        val newBranch = new Array[AnyRef](branchLength)
        Platform.arraycopy(branch, 1, newBranch, 1, branchLength - INVAR)
        newBranch(branchLength - 1) = node
        newBranch
    }
}

