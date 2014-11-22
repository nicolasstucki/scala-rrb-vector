package codegen
package vector
package vectorpointer

import codegen.vector.vectorobject.VectorObjectCodeGen

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorPointerCodeGen {
    self: VectorProperties with VectorObjectCodeGen =>

    // Field names

    val focusStart = TermName("focusStart")
    val focusEnd = TermName("focusEnd")
    val focus = TermName("focus")
    val focusDepth = TermName("focusDepth")
    val focusRelax = TermName("focusRelax")

    val endIndex = TermName("endIndex")
    val depth = TermName("depth")

    // Method names

    val initWithFocusFrom = TermName("initWithFocusFrom")
    val initFocus = TermName("initFocus")
    val initFromRoot = TermName("initFromRoot")
    val initFrom = TermName("initFrom")
    val initSingleton = TermName("initSingleton")

    val root = TermName("root")

    val focusOn = TermName("focusOn")
    val getElementFromRoot = TermName("getElementFromRoot")
    protected[vectorpointer] val getIndexInSizes = TermName("getIndexInSizes")

    protected[vectorpointer] val gotoPosFromRoot = TermName("gotoPosFromRoot")
    val setupNewBlockInNextBranch = TermName("setupNewBlockInNextBranch")
    val setupNewBlockInInitBranch = TermName("setupNewBlockInInitBranch")

    val getElement = TermName("getElem")

    def getElementI(i: Int) = TermName("getElem" + i)

    val gotoPos = TermName("gotoPos")

    val gotoNextBlockStart = TermName("gotoNextBlockStart")
    val gotoPrevBlockStart = TermName("gotoPrevBlockStart")
    val gotoNextBlockStartWritable = TermName("gotoNextBlockStartWritable")

    val normalize = TermName("normalize")
    val stabilizeDisplayPath = TermName("stabilizeDisplayPath")
    val cleanTopTake = TermName("cleanTopTake")
    val cleanTopDrop = TermName("cleanTopDrop")

    val copyDisplays = TermName("copyDisplays")
    val copyDisplaysAndNullFocusedBranch = TermName("copyDisplaysAndNullFocusedBranch")
    val copyDisplaysAndStabilizeDisplayPath = TermName("copyDisplaysAndStabilizeDisplayPath")
    val copyDisplaysTop = TermName("copyDisplaysTop")
    val copyOf = TermName("copyOf")
    val copyOfRight = TermName("copyOfRight")
    val copyOfAndNull = TermName("copyOfAndNull")

    val makeNewRoot0 = TermName("makeNewRoot0")
    val makeNewRoot1 = TermName("makeNewRoot1")
    val makeTransientSizes = TermName("makeTransientSizes")

    val copyAndIncRightRoot = TermName("copyAndIncRightRoot")
    val copyAndIncLeftRoot = TermName("copyAndIncLeftRoot")

    val withComputedSizes1 = TermName("withComputedSizes1")
    val withComputedSizes = TermName("withComputedSizes")
    val withRecomputedSizes = TermName("withRecomputedSizes")
    val notBalanced = TermName("notBalanced")

    val treeSize = TermName("treeSize")

    val debugToString = TermName("debugToSting")

    // Method definitions

    protected def initWithFocusFromCode(thatParam: TermName): Tree = {
        q"""
            $initFocus($thatParam.$focus, $thatParam.$focusStart, $thatParam.$focusEnd, $thatParam.$focusDepth, $thatParam.$focusRelax)
            $initFrom($thatParam)
         """
    }

    protected def initFocusCode(focusParam: TermName, focusStartParam: TermName, focusEndParam: TermName, focusDepthParam: TermName, focusRelaxParam: TermName): Tree = {
        q"""
            this.$focus = $focusParam
            this.$focusStart = $focusStartParam
            this.$focusEnd = $focusEndParam
            this.$focusDepth = $focusDepthParam
            this.$focusRelax = $focusRelaxParam
         """
    }

    protected def initFromRootCode(rootParam: TermName, depthParam: TermName): Tree = {
        q"""
            ..${assertions(q"$rootParam != null", q"0 < $depthParam", q"$depthParam <= $maxTreeDepth")}
            ${matchOnInt(q"$depthParam", 1 to maxTreeDepth, d => q"${displayAt(d - 1)} = $rootParam")}
            this.$depth = $depthParam
            $focusEnd = $focusStart
            $focusOn(0)
         """
    }

    def initFromCode(thatParam: TermName) = {
        def depthCase(depth: Int): Tree = {
            val stats = (1 to depth) map (d => displayNameAt(d - 1)) map (display => q"this.$display = $thatParam.$display")
            q"{..$stats}"
        }
        q"""
            ..${assertions(q"$thatParam != null")}
            $depth = $thatParam.$depth
            ..${matchOnInt(q"$thatParam.$depth", 0 to maxTreeDepth, depthCase, Some(q"throw new IllegalStateException"))}
        """
    }

    private[vectorpointer] def initSingletonCode(elemParam: TermName) = {
        q"""
            $initFocus(0, 0, 1, 1, 0)
            val d0 = new Array[AnyRef](1)
            d0.update(0, $elemParam.asInstanceOf[AnyRef])
            ${displayAt(0)} = d0
            $depth = 1
         """
    }

    def rootCode(depth: Tree) = depth match {
        case q"${d: Int}" =>
            if (0 <= d && d < maxTreeDepth) displayAt(d - 1)
            else q"throw new IllegalStateException"
        case _ =>
            matchOnInt(q"$depth", 0 to maxTreeDepth, d => if (d == 0) q"null" else displayAt(d - 1), Some(q"throw new IllegalStateException"))
    }

    private[vectorpointer] def focusOnCode(indexParam: TermName): Tree = {
        q"""
            if ($focusStart <= $indexParam && $indexParam < $focusEnd) {
                val indexInFocus = $indexParam - $focusStart
                val xor = indexInFocus ^ $focus
                if (xor >= $blockWidth) {
                    $gotoPos(indexInFocus, xor)
                }
                $focus = $indexParam
            } else {
                $gotoPosFromRoot($indexParam)
            }
         """
    }

    private[vectorpointer] def getElementFromRootCode(indexParam: TermName): Tree = {
        q"""
            ..${assertions(q"0 <= $indexParam", q"1 < $depth", q"$depth <= $maxTreeDepth")}
            var indexInSubTree = $indexParam
            var currentDepth = $depth
            var display: Array[AnyRef] = ${matchOnInt(q"currentDepth", 2 to maxTreeDepth, d => displayAt(d - 1))}
            var sizes = ${getBlockSizes(q"display")}
            do {
                val sizesIdx = $getIndexInSizes(sizes, indexInSubTree)
                if (sizesIdx != 0)
                    indexInSubTree -= sizes(sizesIdx - 1)
                display = display(sizesIdx).asInstanceOf[Array[AnyRef]]
                if (currentDepth > 2)
                    sizes = display(display.length - 1).asInstanceOf[Array[Int]]
                else
                    sizes = null
                currentDepth -= 1
            } while (sizes != null)
            ${matchOnInt(q"currentDepth", 1 to maxTreeDepth, d => q"${getElementI(d - 1)}(display, indexInSubTree)", Some(q"throw new IllegalStateException"))}
         """
    }

    private[vectorpointer] def getIndexInSizesCode(sizes: TermName, indexInSubTree: TermName): Tree = {
        q"""
            ..${assertions(q"0 <= $indexInSubTree", q"$indexInSubTree < $sizes($sizes.length - 1)")}
            var is = 0
            while ($sizes(is) <= $indexInSubTree)
                is += 1
            is
         """
    }

    private[vectorpointer] def gotoPosFromRootCode(indexParam: TermName): Tree = {
        val currentStartIndex = TermName("_startIndex")
        val currentEndIndex = TermName("_endIndex")
        val currentDepth = TermName("currentDepth")
        val currentFocusRelax = TermName("_focusRelax")
        q"""
            ..${assertions(q"0 <= $indexParam")}
            var $currentStartIndex: Int = 0
            var $currentEndIndex: Int = $endIndex
            var $currentDepth: Int = $depth
            var $currentFocusRelax: Int = 0
            var continue: Boolean = $currentDepth > 1

            if (continue) {
                var display = ${matchOnInt(q"$currentDepth", 2 to maxTreeDepth, d => displayAt(d - 1), Some(q"throw new IllegalStateException"))}
                do {
                    val sizes = ${getBlockSizes(q"display")}
                    if(sizes == null) {
                        continue = false
                    } else {
                        val is = $getIndexInSizes(sizes, $indexParam - $currentStartIndex)
                        display = display(is).asInstanceOf[Array[AnyRef]]
                        ${matchOnInt(q"$currentDepth", 2 to maxTreeDepth, d => if (d == 2) q"${displayAt(0)} = display; continue = false" else q"${displayAt(d - 2)} = display")}
                        if (is < sizes.length - 1)
                            $currentEndIndex = $currentStartIndex + sizes(is)
                        if (is != 0)
                            $currentStartIndex += sizes(is - 1)
                        $currentDepth -= 1
                        $currentFocusRelax |= (is << ($blockIndexBits * $currentDepth))
                    }
                } while (continue)
            }
            val indexInFocus = $indexParam - $currentStartIndex
            $gotoPos(indexInFocus, 1 << ($blockIndexBits * ($currentDepth - 1)))
            $initFocus(indexInFocus, $currentStartIndex, $currentEndIndex, $currentDepth, $currentFocusRelax)
         """
    }


    private[vectorpointer] def makeTransientSizesCode(oldSizes: TermName, transientBranchIndex: TermName) = {
        q"""
            val newSizes = new Array[Int]($oldSizes.length)
            var delta = $oldSizes($transientBranchIndex)
            if ($transientBranchIndex > 0) {
                delta -= $oldSizes($transientBranchIndex - 1)
                if (!$oldSizes.eq(newSizes))
                    System.arraycopy($oldSizes, 0, newSizes, 0, $transientBranchIndex)
            }
            var i = $transientBranchIndex
            val len = newSizes.length
            while (i < len) {
                newSizes(i) = $oldSizes(i) - delta
                i += 1
            }
            newSizes
         """
    }


    private[vectorpointer] def copyAndIncRightRootCode(node: TermName, transient: TermName, currentLevel: TermName) = {
        q"""
            val len = $node.length
            val newRoot = $copyOf($node, len - 1, len + 1)
            val oldSizes = $node(len - 1).asInstanceOf[Array[Int]]
            if (oldSizes != null) {
                val newSizes = new Array[Int](len)
                System.arraycopy(oldSizes, 0, newSizes, 0, len - 1)
                if ($transient) {
                    newSizes(len - 1) = 1 << ($blockIndexBits * $currentLevel)
                }
                newSizes(len - 1) = newSizes(len - 2)
                newRoot(len) = newSizes
            }
            newRoot
         """
    }

    private[vectorpointer] def copyAndIncLeftRootCode(node: TermName, transient: TermName, currentLevel: TermName) = {
        q"""
            val len = $node.length
            val newRoot = new Array[AnyRef](len + 1)
            System.arraycopy($node, 0, newRoot, 1, len - 1)

            val oldSizes = $node(len - 1)
            val newSizes = new Array[Int](len)
            if (oldSizes != null) {
                if ($transient) {
                    System.arraycopy(oldSizes, 1, newSizes, 2, len - 2)
                } else {
                    System.arraycopy(oldSizes, 0, newSizes, 1, len - 1)
                }
            } else {
                val subTreeSize = 1 << ($blockIndexBits * $currentLevel)
                var acc = 0
                var i = 1
                while (i < len - 1) {
                    acc += subTreeSize
                    newSizes(i) = acc
                    i += 1
                }
                newSizes(i) = acc + treeSize($node($node.length - 2).asInstanceOf[Array[AnyRef]], currentLevel)
            }
            newRoot(len) = newSizes
            newRoot
         """
    }

    private[vectorpointer] def withRecomputedSizesCode(node: TermName, currentDepth: TermName, branchToUpdate: TermName) = {
        q"""
            ..${assertions(q"$node != null", q"1 < $currentDepth")}
            val end = $node.length - 1
            val oldSizes = $node(end).asInstanceOf[Array[Int]]
            if (oldSizes != null) {
                val newSizes = new Array[Int](end)
                val delta = $treeSize($node($branchToUpdate).asInstanceOf[Array[AnyRef]], $currentDepth - 1)
                if ($branchToUpdate > 0)
                    System.arraycopy(oldSizes, 0, newSizes, 0, $branchToUpdate)
                var i = $branchToUpdate
                while (i < end) {
                    newSizes(i) = oldSizes(i) + delta
                    i += 1
                }
                if ($notBalanced($node, newSizes, $currentDepth, end))
                    $node(end) = newSizes
            }
            $node
         """
    }

    private[vectorpointer] def withComputedSizes1Code(node: TermName) = {
        q"""
            var i = 0
            var acc = 0
            val end = $node.length - 1
            if (end > 1) {
                val sizes = new Array[Int](end)
                while (i < end) {
                    acc += $node(i).asInstanceOf[Array[AnyRef]].length
                    sizes(i) = acc
                    i += 1
                }
                if /* node is not balanced */ (sizes(end - 2) != ((end - 1) << $blockIndexBits))
                    $node(end) = sizes
            }
            $node
         """
    }

    private[vectorpointer] def withComputedSizesCode(node: TermName, currentDepth: TermName) = {
        q"""
            ..${assertions(q"$node != null", q"1 < $currentDepth")}
            var i = 0
            var acc = 0
            val end = $node.length - 1
            if (end > 1) {
                val sizes = new Array[Int](end)
                while (i < end) {
                    acc += $treeSize($node(i).asInstanceOf[Array[AnyRef]], $currentDepth - 1)
                    sizes(i) = acc
                    i += 1
                }
                if ($notBalanced($node, sizes, $currentDepth, end))
                    $node(end) = sizes
            } else if (end == 1 && $currentDepth > 2) {
                val child = $node(0).asInstanceOf[Array[AnyRef]]
                val childSizes = child(child.length - 1).asInstanceOf[Array[Int]]
                if (childSizes != null) {
                    if (childSizes.length != 1) {
                        val sizes = new Array[Int](1)
                        sizes(0) = childSizes(childSizes.length - 1)
                        $node(end) = sizes
                    } else {
                        $node(end) = childSizes
                    }
                }
            }
            $node
         """
    }

    private[vectorpointer] def notBalancedCode(node: TermName, sizes: TermName, currentDepth: TermName, end: TermName) = {
        q"""
            ($sizes($end - 2) != (($end - 1) << ($blockIndexBits * ($currentDepth - 1)))) || (
          ($currentDepth > 2) && {
              val last = $node($end - 1).asInstanceOf[Array[AnyRef]]
              last(last.length - 1) != null
          }
          )
         """
    }

    private[vectorpointer] def treeSizeCode(node: TermName, currentDepth: TermName) = {
        q"""
            def treeSizeRec(node: Array[AnyRef], currentDepth: Int, acc: Int): Int = {
                if (currentDepth == 1)
                    acc + node.length
                else {
                    val treeSizes = node(node.length - 1).asInstanceOf[Array[Int]]
                    if (treeSizes != null)
                        acc + treeSizes(treeSizes.length - 1)
                    else {
                        val len = node.length
                        treeSizeRec(node(len - ${1 + blockInvariants}).asInstanceOf[Array[AnyRef]], currentDepth - 1, acc + (len - ${1 + blockInvariants}) * (1 << ($blockIndexBits * (currentDepth - 1))))
                    }
                }
            }
            treeSizeRec($node, $currentDepth, 0)
         """
    }

    private[vectorpointer] def makeNewRoot0Code(node: TermName) = {
        q"""
            val newRoot = new Array[AnyRef](${2 + blockInvariants})
            newRoot(0) = $node
            val dLen = $node.length
            val dSizes = $node(dLen - 1)
            if (dSizes != null) {
                val newRootSizes = new Array[Int](2)
                val dSize = dSizes.asInstanceOf[Array[Int]](dLen - 2)
                newRootSizes(0) = dSize
                newRootSizes(1) = dSize
                newRoot(2) = newRootSizes
            }
            newRoot
         """
    }

    private[vectorpointer] def makeNewRoot1Code(node: TermName, currentDepth: TermName) = {
        q"""
            val dSize = treeSize($node, $currentDepth - 1)
            val newRootSizes = new Array[Int](2)
            /* newRootSizes(0) = 0 */
            newRootSizes(1) = dSize
            val newRoot = new Array[AnyRef](3)
            newRoot(1) = $node
            newRoot(2) = newRootSizes
            newRoot
         """
    }

    private[vectorpointer] def setupNewBlockInNextBranchCode(xor: TermName, transient: TermName) = {
        def codeForLevel(lvl: Int): Tree = {
            q"""
                ..${if (lvl > 1) q"if ($transient) $normalize($lvl)" :: Nil else Nil}
                if ($depth == $lvl) {
                    $depth = ${lvl + 1}
                    ${
                if (lvl == 1)
                    q"""
                        val newRoot = new Array[AnyRef](${2 + blockInvariants})
                        newRoot(0) = ${displayAt(lvl - 1)}
                        ${displayAt(lvl)} = newRoot
                     """
                else
                    q"${displayAt(lvl)} = $makeNewRoot0(${displayAt(lvl - 1)})"
            }
                } else {
                    val newRoot = copyAndIncRightRoot(${displayAt(lvl)}, transient, $lvl)
                    if (transient) {
                        val oldTransientBranch = newRoot.length - 3
                        $withRecomputedSizes(newRoot, ${lvl + 1}, oldTransientBranch)
                        newRoot(oldTransientBranch) = ${displayAt(lvl - 1)}
                    }
                    ${displayAt(lvl)} = newRoot
                }
                ${displayAt(0)} = new Array(1)
                ${if (lvl > 1) q"val _emptyTransientBlock = $vectorObjectName.$o_emptyTransientBlock" else q""}
                ..${(1 until lvl) map (lv => q"${displayAt(lv)} = _emptyTransientBlock")}
             """
        }
        ifInLevel(q"$xor", 1 to maxTreeLevel, codeForLevel, q"throw new IllegalArgumentException")
    }

    private[vectorpointer] def setupNewBlockInInitBranchCode(insertionDepth: TermName, transient: TermName) = {
        def codeForDepth(dep: Int): Tree = {
            q"""
                ..${if (dep > 1) q"if ($transient) $normalize(${dep - 1})" :: Nil else Nil}
                if ($depth == ${dep - 1}) {
                    $depth = $dep
                    ${
                if (dep == 2)
                    q"""
                        val sizes = new Array[Int](2)
                        sizes(1) = ${displayAt(0)}.length
                        val newRoot = new Array[AnyRef](${2 + blockInvariants})
                        newRoot(1) = ${displayAt(0)}
                        newRoot(2) = sizes
                        ${displayAt(1)} = newRoot
                     """
                else
                    q"${displayAt(dep - 1)} = $makeNewRoot1(${displayAt(dep - 2)}, $dep)"
            }
                } else {
                    val newRoot = $copyAndIncLeftRoot(${displayAt(dep - 1)}, $transient, ${dep - 1})
                    if ($transient) {
                        $withRecomputedSizes(newRoot, $dep, 1)
                        newRoot(1) = ${displayAt(dep - 2)}
                    }
                    ${displayAt(dep - 1)} = newRoot
                }
                ${displayAt(0)} = new Array(1)
                ${if (dep > 2) q"val _emptyTransientBlock = $vectorObjectName.$o_emptyTransientBlock" else q""}
                ..${(1 until dep - 1) map (lv => q"${displayAt(lv)} = _emptyTransientBlock")}
             """
        }
        matchOnInt(q"$insertionDepth", 2 to maxTreeDepth, codeForDepth, Some(q"throw new IllegalStateException"))
    }

    private[codegen] def getElementCode(index: TermName, xor: TermName): Tree = {
        ifInLevel(q"$xor", 0 to maxTreeLevel, lvl => q"${getElementI(lvl)}(${displayAt(lvl)}, $index)", q"throw new IllegalArgumentException")
    }

    private[codegen] def getElementICode(i: Int, block: TermName, index: TermName): Tree = {
        @tailrec def getElemFromDisplay(display: Tree, level: Int): Tree = {
            val idx = branchIndex(q"$index", q"$level")
            if (level == 0) q"$display($idx).asInstanceOf[$A]"
            else getElemFromDisplay(q"$display($idx).asInstanceOf[Array[AnyRef]]", level - 1)
        }
        getElemFromDisplay(q"$block", i)
    }

    def gotoPosCode(index: TermName, xor: TermName) = {
        def gotoPosFormLevel(level: Int): Tree = {
            q"..${level to 1 by -1 map (lvl => q"${displayAt(lvl - 1)} = ${displayAt(lvl)}(($index >> ${blockIndexBits * lvl}) & $blockMask).asInstanceOf[Array[AnyRef]]")}"
        }
        ifInLevel(q"xor", 0 to maxTreeLevel, gotoPosFormLevel, q"throw new IllegalArgumentException")
    }


    private def gotoBlockStart(index: TermName, xor: TermName, defaultIndex: Int): Tree = {
        def gotoBlockStartRec(lvl: Int): Seq[Tree] = {
            if (lvl == maxTreeLevel + 1)
                q"""
                        if($xor >= ${1 << (blockIndexBits * lvl)}) {
                            throw new IllegalArgumentException
                        } else {
                            ${displayAt(lvl - 2)} = ${displayAt(lvl - 1)}((index >> ${(lvl - 1) * blockIndexBits}) & $blockMask).asInstanceOf[Array[AnyRef]]
                        }
                     """ :: Nil
            else
                Seq(
                    q"""
                            if($xor >= ${1 << (blockIndexBits * lvl)}) {
                                ..${gotoBlockStartRec(lvl + 1)}
                                ..${if (lvl < maxTreeDepth) q"idx = $defaultIndex" :: Nil else Nil}
                            } else {
                                idx = ($index >> ${(lvl - 1) * blockIndexBits}) & $blockMask
                            }
                         """,
                    q"${displayAt(lvl - 2)} = ${displayAt(lvl - 1)}(idx).asInstanceOf[Array[AnyRef]]"
                )
        }
        q"""
            var idx = $defaultIndex
            ..${gotoBlockStartRec(2)}
         """
    }

    def gotoNextBlockStartCode(index: TermName, xor: TermName) = {
        gotoBlockStart(index, xor, 0)

    }

    def gotoPrevBlockStartCode(index: TermName, xor: TermName) = {
        gotoBlockStart(index, xor, blockMask)
    }

    def gotoNextBlockStartWritableCode(index: TermName, xor: TermName) = {
        def gotoNextBlockStartWritableFromLevel(lvl: Int) = {
            q"""
                if ($depth == $lvl) {
                    ${displayAt(lvl)} = new Array(${blockWidth + blockInvariants})
                    ${displayAt(lvl)}(0) = ${displayAt(lvl - 1)}
                    $depth += 1
                }
                ${displayAt(0)} = new Array($blockWidth)
                ..${(1 until lvl) map (i => q"${displayAt(i)} = new Array(${blockWidth + blockInvariants})")}
                ..${(1 to lvl) map (i => q"${displayAt(i)}(($index >> ${blockIndexBits * i}) & $blockMask) = ${displayAt(i - 1)}")}
             """
        }
        ifInLevel(q"$xor", 1 to maxTreeLevel, gotoNextBlockStartWritableFromLevel, q"throw new IllegalArgumentException")
    }


    private[codegen] def normalizeCode(depthParam: TermName) = {
        q"""
            ..${assertions(q"1< $depthParam")}
            val _focusDepth = $focusDepth
            val stabilizationIndex = $focus | $focusRelax
            $copyDisplaysAndStabilizeDisplayPath(_focusDepth, stabilizationIndex)

            var currentLevel = _focusDepth
            if (currentLevel < $depthParam) {
                var display = ${matchOnInt(q"currentLevel", 1 to maxTreeLevel, displayAt)}
                do {
                    val newDisplay = copyOf(display)
                    val idx = (stabilizationIndex >> ($blockIndexBits * currentLevel)) & 31
                    ${
            matchOnInt(q"currentLevel", 1 to maxTreeLevel, lvl =>
                q"""
                    newDisplay(idx) = ${displayNameAt(lvl - 1)}
                    ${displayNameAt(lvl)} = $withRecomputedSizes(newDisplay, ${lvl + 1}, idx)
                    ..${if (lvl != maxTreeLevel) q"display = ${displayNameAt(lvl + 1)}" :: Nil else Nil}
                 """
            )
        }
                    currentLevel += 1
                } while (currentLevel < $depthParam)
            }
         """
    }

    private[codegen] def stabilizeDisplayPathCode(depthParam: TermName, focusParam: TermName) = {
        def disp(l: Int) = TermName("d" + l)
        def stabilizeDisplayPathFromLevel(lvl: Int): Tree = {
            if (lvl < maxTreeLevel)
                q"""
                if($lvl < $depthParam) {
                    val ${disp(lvl)} = ${displayAt(lvl)}
                    ${disp(lvl)}(($focusParam >> ${blockIndexBits * lvl}) & $blockMask) = ${if (lvl == 1) displayAt(0) else q"${disp(lvl - 1)}"}
                    ${stabilizeDisplayPathFromLevel(lvl + 1)}
                }
             """
            else
                q"if ($depthParam == $maxTreeDepth) ${displayAt(lvl)}(($focusParam >> ${blockIndexBits * lvl}) & $blockMask) = ${disp(lvl - 1)}"
        }
        stabilizeDisplayPathFromLevel(1)
    }


    private[vectorpointer] def cleanTopTakeCode(cutIndex: TermName) = {
        def cleanLevelAndGoDown(lvl: Int): Tree =
            q"""
                if (($cutIndex >> ${lvl * blockIndexBits}) == 0) {
                    ${displayAt(lvl)} = null
                    ${if (lvl == 1) q"this.$depth = 1" else cleanLevelAndGoDown(lvl - 1)}
                } else this.$depth = ${lvl + 1}
             """
        matchOnInt(q"this.$depth", 2 to maxTreeDepth, d => cleanLevelAndGoDown(d - 1))
    }

    private[vectorpointer] def cleanTopDropCode(cutIndex: TermName) = {
        def cleanLevelAndGoDown(lvl: Int): Tree =
            q"""
                if (($cutIndex >> ${lvl * blockIndexBits}) == ${displayAt(lvl)}.length - ${1 + blockInvariants}) {
                    ${displayAt(lvl)} = null
                    ${if (lvl == 1) q"this.$depth = 1" else cleanLevelAndGoDown(lvl - 1)}
                } else this.$depth = ${lvl + 1}
             """
        matchOnInt(q"this.$depth", 2 to maxTreeDepth, d => cleanLevelAndGoDown(d - 1))
    }

    def copyDisplaysCode(depthParam: Tree, focusParam: TermName) = {

        def copyDisplayAtDepth(dep: Int): Tree = {
            val idx = TermName("idx" + (dep - 1))
            q"""
                if($dep <= $depthParam) {
                    ..${if (dep < maxTreeDepth) copyDisplayAtDepth(dep + 1) :: Nil else Nil}
                    val $idx = (($focusParam >> ${blockIndexBits * (dep - 1)}) & $blockMask) + 1
                    ${displayNameAt(dep - 1)} = $copyOf(${displayNameAt(dep - 1)}, $idx, $idx + 1)
                }
             """
        }
        copyDisplayAtDepth(2)
    }

    def copyDisplaysAndNullFocusedBranchCode(depthParam: TermName, focusParam: TermName) = {
        matchOnInt(q"$depthParam", 2 to maxTreeDepth, dep => q"..${1 until dep map (lvl => q"${displayAt(lvl)} = $copyOfAndNull(${displayAt(lvl)}, ($focusParam >> ${blockIndexBits * lvl}) & $blockMask)")}")
    }

    def copyDisplaysAndStabilizeDisplayPathCode(depthParam: TermName, focusParam: TermName) = {
        def disp(l: Int) = q"${TermName("d" + l)}"
        matchOnInt(q"$depthParam", 1 to maxTreeDepth, dep => q"..${
            1 until dep flatMap { lvl =>
                val q"{ ..$stats }" = q"val ${disp(lvl)}: Array[AnyRef] = $copyOf(${displayAt(lvl)})"
                stats :::
                  q"${disp(lvl)}(($focusParam >> ${blockIndexBits * lvl}) & $blockMask) = ${if (lvl > 1) disp(lvl - 1) else displayAt(0)}" ::
                  q"${displayAt(lvl)} = ${disp(lvl)}" :: Nil
            }
        }")
    }


    def copyDisplaysTopCode(currentDepth: TermName, focusRelaxParam: TermName) = {
        def copyDisplayAndCut(lvl: Int) =
            q"""
                val cutIndex = ($focusRelaxParam >> ${blockIndexBits * lvl}) & $blockMask
                ${displayAt(lvl)} = $copyOf(${displayAt(lvl)}, cutIndex + 1, cutIndex + 2)
             """
        q"""
            var _currentDepth = currentDepth
            while (_currentDepth < this.$depth) {
                ${matchOnInt(q"_currentDepth", 2 to maxTreeDepth, d => copyDisplayAndCut(d - 1), Some(q"throw new IllegalStateException"))}
                _currentDepth += 1
            }
         """
    }

    private[vectorpointer] def copyOfCode(array: Tree) = {
        val newArray = TermName("newArray")
        q"""
            ..${assertions(q"$array!=null")}
            val len = $array.length
            val $newArray = new Array[AnyRef](len)
            System.arraycopy($array, 0, $newArray, 0, len)
            $newArray
        """
    }

    private[vectorpointer] def copyOfCode(array: Tree, numElements: Tree, newSize: Tree) = {
        val newArray = TermName("newArray")
        q"""
            ..${assertions(q"$array!=null", q"0 <= $numElements", q"$numElements <= $newSize", q"$numElements <= $array.length")}
            val $newArray = new Array[AnyRef]($newSize)
            System.arraycopy($array, 0, $newArray, 0, $numElements)
            $newArray
        """
    }

    private[vectorpointer] def copyOfAndNullCode(array: TermName, nullIndex: TermName) = {
        val newArray = TermName("newArray")
        q"""
            ..${assertions(q"$array!=null", q"0 <= $nullIndex", q"$nullIndex <= $array.length")}
            val len = $array.length
            val $newArray = new Array[AnyRef](len)
            System.arraycopy($array, 0, $newArray, 0, len - 1)
            $newArray($nullIndex) = null
            val sizes = $array(len - 1).asInstanceOf[Array[Int]]
            if (sizes != null) {
                $newArray(len - 1) = $makeTransientSizes(sizes, $nullIndex)
            }
            $newArray
        """
    }


    protected def debugToStringCode(others: Tree*) = {
        q"""
            val sb = new StringBuilder
            sb append "RRBVector (\n"
            ..${0 to maxTreeLevel map (lvl => q"""sb append ("\t" + ${"display" + lvl} + " = " + ${displayAt(lvl)} + " " + (if(${displayAt(lvl)} != null) ${displayAt(lvl)}.mkString("[", ", ", "]") else "") + "\n")""")}
            sb append ("\tdepth = " + $depth + "\n")
            sb append ("\tendIndex = " + $endIndex + "\n")
            sb append ("\tfocus = " + $focus + "\n")
            sb append ("\tfocusStart = " + $focusStart + "\n")
            sb append ("\tfocusEnd = " + $focusEnd + "\n")
            sb append ("\tfocusRelax = " + $focusRelax + "\n")
            ..${others map (other => q"sb append ($other)")}
            sb append ")"
            sb.toString
         """
    }

    // Helper code

    protected def displayAt(level: Int) = q"${displayNameAt(level)}"

    protected def displayNameAt(level: Int) = {
        if (0 <= level && level <= maxTreeLevel) TermName("display" + level)
        else throw new IllegalArgumentException(level.toString)
    }

    protected def branchIndex(index: Tree, level: Tree) = (index, level) match {
        case (q"${i: Int}", q"${l: Int}") => q"${(i >> (blockIndexBits * l)) & blockMask}"
        case (q"${i: Int}", _) => if (i == 0) q"0" else q"($i >> ($blockIndexBits * $level)) & $blockMask"
        case (_, q"${l: Int}") => if (l == 0) q"$index & $blockMask" else q"($index >> ${blockIndexBits * l}) & $blockMask"
        case _ => q"($index >> ($blockIndexBits * $level)) & $blockMask"
    }

    protected def getBlockSizes(display: Tree) = {
        q"$display($display.length - 1).asInstanceOf[Array[Int]]"
    }

    protected def getBlockSizes(display: Tree, displayLength: Tree) = {
        displayLength match {
            case q"${len: Int}" => q"$display(${len - 1}).asInstanceOf[Array[Int]]"
            case _ => q"$display($displayLength - 1).asInstanceOf[Array[Int]]"
        }
    }

    protected def setBlockSizes(display: Tree, sizes: Tree) = {
        q"$display($display.length - 1) = $sizes"
    }

    protected def matchOnInt(num: Tree, numCases: Seq[Int], code: Int => Tree, defaultOption: Option[Tree] = None): Tree = {
        num match {
            case q"${d: Int}" =>
                if (numCases.contains(d))
                    defaultOption match {
                        case Some(default) => default
                        case None => throw new IllegalArgumentException
                    }
                else
                    code(d)
            case _ =>
                val cases = numCases map (i => cq"$i => ${code(i)}")
                defaultOption match {
                    case Some(default) => q"$num match { case ..$cases; case _ => $default }"
                    case None => q"$num match { case ..$cases }"
                }
        }
    }

    protected def getIndexLevel(index: Tree) = index match {
        case q"${idx: Int}" =>
            q"${(31 - java.lang.Integer.numberOfLeadingZeros(idx)) / blockIndexBits}"
        case _ =>
            q"(31 - java.lang.Integer.numberOfLeadingZeros($index)) / $blockIndexBits"
    }

    protected def indexIsInLevel(index: Tree, level: Tree) = level match {
        case q"${lvl: Int}" => q"$index < ${1 << (blockIndexBits * (lvl + 1))}"
        case _ => q"$index < (1 << ($blockIndexBits * ($level + 1)))"
    }

    protected def ifInLevel(xor: Tree, levels: Seq[Int], code: Int => Tree, ifNotInLevels: Tree): Tree = {
        if (levels.isEmpty) ifNotInLevels
        else {
            val currentLevel = levels.head
            val ifClause = code(currentLevel)
            val elseClause = ifInLevel(xor, levels.tail, code, ifNotInLevels)
            q"if (${indexIsInLevel(xor, q"$currentLevel")}) $ifClause else $elseClause"
        }
    }


}
