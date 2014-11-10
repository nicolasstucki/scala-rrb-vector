package codegen
package vector
package vectorpointer

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorPointerCodeGen {
    self: VectorProperties =>

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

    val getElement = TermName("getElement")
    val gotoPos = TermName("gotoPos")

    val gotoNextBlockStart = TermName("gotoNextBlockStart")
    val gotoPrevBlockStart = TermName("gotoPrevBlockStart")
    val gotoNextBlockStartWritable = TermName("gotoNextBlockStartWritable")

    val stabilize = TermName("stabilize")
    val stabilizeDisplayPath = TermName("stabilizeDisplayPath")
    val cleanTop = TermName("cleanTop")

    val copyDisplays = TermName("copyDisplays")
    val copyDisplaysTop = TermName("copyDisplaysTop")
    val copyOf = TermName("copyOf")

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
            ..${assertions(q"$rootParam != null", q"0 < $depthParam", q"$depthParam <=6")}
            ${matchOnInt(q"$depthParam", 1 to 6, d => q"${displayAt(d - 1)} = $rootParam")}
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
            ..${matchOnInt(q"$thatParam.$depth", 0 to 6, depthCase, Some(q"throw new IllegalStateException"))}
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
            if (0 <= d && d < 6) displayAt(d - 1)
            else q"throw new IllegalStateException"
        case _ =>
            matchOnInt(q"$depth", 0 to 6, d => if (d == 0) q"null" else displayAt(d - 1), Some(q"throw new IllegalStateException"))
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
            ..${assertions(q"2 <= $depth", q"$depth <= 6")}
            var indexInSubTree = $indexParam
            var currentDepth = $depth
            var display: Array[AnyRef] = null
            ${matchOnInt(q"currentDepth", 2 to 6, d => q"display = ${displayAt(d - 1)}")}
            while (currentDepth > 1) {
                val sizes = ${getBlockSizes(q"display")}
                if (sizes == null) {
                    val depthShift = $blockIndexBits * (currentDepth - 1)
                    val idx = indexInSubTree >> depthShift
                    indexInSubTree -= idx << depthShift
                    display = display(idx).asInstanceOf[Array[AnyRef]]
                } else {
                    val sizesIdx = getIndexInSizes(sizes, indexInSubTree)
                    if (sizesIdx != 0)
                        indexInSubTree -= sizes(sizesIdx - 1)
                    display = display(sizesIdx).asInstanceOf[Array[AnyRef]]
                }
                currentDepth -= 1
            }
            display(indexInSubTree).asInstanceOf[A]
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
            var $currentStartIndex: Int = 0
            var $currentEndIndex: Int = $endIndex
            var $currentDepth: Int = $depth
            var $currentFocusRelax: Int = 0
            var continue: Boolean = $currentDepth > 1

            while (continue) {
                ..${assertions(q"0 <= $currentStartIndex", q"$currentStartIndex <= $currentEndIndex", q"0 < $currentDepth")}
                if ($currentDepth <= 1) {
                    continue = false
                } else {
                    val display = ${matchOnInt(q"$currentDepth", 2 to 6, d => if (d == 0) q"null" else displayAt(d - 1), Some(q"throw new IllegalStateException"))}
                    val sizes = ${getBlockSizes(q"display")}
                    if(sizes == null) {
                        continue = false
                    } else {
                        val is = $getIndexInSizes(sizes, $indexParam - $currentStartIndex)
                        ${matchOnInt(q"$currentDepth", 2 to 6, d => q"${displayAt(d - 2)} = display(is).asInstanceOf[Array[AnyRef]]")}
                        if (is < sizes.length - 1)
                            $currentEndIndex = $currentStartIndex + sizes(is)
                        if (is != 0)
                            $currentStartIndex += sizes(is - 1)
                        $currentDepth -= 1
                        $currentFocusRelax |= (is << ($blockIndexBits * $currentDepth))
                    }
                }
            }
            val indexInFocus = $indexParam - $currentStartIndex
            $gotoPos(indexInFocus, 1 << ($blockIndexBits * ($currentDepth - 1)))
            $initFocus(indexInFocus, $currentStartIndex, $currentEndIndex, $currentDepth, $currentFocusRelax)
         """
    }

    private[vectorpointer] def setupNewBlockInNextBranchCode(indexParam: TermName, xorParam: TermName) = {
        def codeForLevel(lvl: Int): Tree = {
            q"""
                if ($depth == $lvl) {
                    val newRoot = new Array[AnyRef](${2 + blockInvariants})
                    newRoot.update(0, ${displayAt(lvl - 1)})
                    ${
                if (lvl == 1) q""
                else
                    q"""
                    val dLen = ${displayAt(lvl - 1)}.length
                    val dSizes = ${displayAt(lvl - 1)}(dLen - 1)
                    if (dSizes != null) {
                        val newRootSizes = new Array[Int](2)
                        val dSize = dSizes.asInstanceOf[Array[Int]](dLen - ${1 + blockInvariants})
                        newRootSizes(0) = dSize
                        newRootSizes(1) = dSize + 1
                        newRoot(2) = newRootSizes
                    }
                """
            }
                    ${displayAt(lvl)} = newRoot
                    depth = ${lvl + 1}
                } else {
                    val len = ${displayAt(lvl)}.length // ((index >> 10) & 31)
                    val newRoot = copyOf(${displayAt(lvl)}, len, len + 1)
                    val sizes = ${displayAt(lvl)}(len - 1)
                    if (sizes != null) {
                        val newSizes = new Array[Int](len)
                        Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len - 1)
                        newSizes(len - 1) = newSizes(len - ${1 + blockInvariants}) + 1
                        newRoot(len) = newSizes
                    }
                    ${displayAt(lvl)} = newRoot
                }
                ${displayAt(0)} = new Array(1)
                ..${(1 until lvl) map (lv => q"${displayAt(lv)} = new Array(${1 + blockInvariants})")}
                ..${(1 to lvl) map (lv => q"${displayAt(lv)}(($indexParam >> ${blockIndexBits * lv}) & $blockMask) = ${displayAt(lv - 1)}")}
             """
        }
        ifInLevel(q"$xorParam", 1 to 5, codeForLevel, q"throw new IllegalArgumentException")
    }

    private[codegen] def getElementCode(index: TermName, xor: TermName): Tree = {
        @tailrec def getElemFromDisplay(display: Tree, level: Int): Tree = {
            val idx = branchIndex(q"$index", q"$level")
            if (level == 0) q"$display($idx).asInstanceOf[$A]"
            else getElemFromDisplay(q"$display($idx).asInstanceOf[Array[AnyRef]]", level - 1)
        }
        ifInLevel(q"$xor", 0 to 5, i => getElemFromDisplay(displayAt(i), i), q"throw new IllegalArgumentException")
    }

    def gotoPosCode(index: TermName, xor: TermName) = {
        if (DIRECT_LEVEL) {
            def loadDisplay(lvl: Int) = q"${displayAt(lvl - 1)} = ${displayAt(lvl)}(($index >> ${blockIndexBits * lvl}) & $blockMask).asInstanceOf[Array[AnyRef]]"
            ifInLevel(q"$xor", 0 to 5, lvl => q"..${(lvl to 1 by -1) map loadDisplay}", q"throw new IllegalArgumentException")
        } else {
            def rec(lvl: Int): Tree = {
                val di = TermName("d" + lvl)
                val disub = TermName("_d" + (lvl - 1))
                val recCall =
                    if (lvl < 5) q" val $di = ${rec(lvl + 1)}"
                    else q"if (xor >= 1073741824) throw new IllegalArgumentException"
                val disubVal =
                    if (lvl < 5) q"$di((index >> ${blockIndexBits * lvl}) & $blockMask).asInstanceOf[Array[AnyRef]]"
                    else q"${displayAt(lvl)}((index>>${blockIndexBits * lvl}) & $blockMask).asInstanceOf[Array[AnyRef]]"
                q"""
                    if($xor >= ${1 << (blockIndexBits * lvl)}) {
                        $recCall
                        val $disub = $disubVal
                        ${displayAt(lvl - 1)} = $disub
                        $disub
                    } else ${displayAt(lvl - 1)}
                 """

            }
            val d1 = TermName("d1")
            q"""
                if($xor >= ${1 << blockIndexBits}) {
                    val $d1 = ${rec(2)}
                    ${displayAt(0)} = $d1((index >> $blockIndexBits) & $blockMask).asInstanceOf[Array[AnyRef]]
                }
             """
        }
    }


    private def gotoBlockStart(index: TermName, xor: TermName, defaultIndex: Int): Tree = {
        if (!DIRECT_LEVEL) {
            def gotoBlockStartRec(lvl: Int): Seq[Tree] = {
                if (lvl == 6)
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
                                ..${if (lvl < 6) q"idx = $defaultIndex" :: Nil else Nil}
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
        } else {
            def loadDisplayAtIndex(lvl: Int, i: Int) = {
                if (i == lvl) q"${displayAt(i - 1)} = ${displayAt(i)}(($index >> ${blockIndexBits * i}) & $blockMask).asInstanceOf[Array[AnyRef]]"
                else q"${displayAt(i - 1)} = ${displayAt(i)}($defaultIndex).asInstanceOf[Array[AnyRef]]"
            }
            ifInLevel(q"$xor", 1 to 5, lvl => q"..${(lvl to 1 by -1) map (i => loadDisplayAtIndex(lvl, i))}", q"throw new IllegalArgumentException")
        }

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
        ifInLevel(q"$xor", 1 to 5, gotoNextBlockStartWritableFromLevel, q"throw new IllegalArgumentException")
    }


    private[codegen] def stabilizeCode() = {
        q"""
            val _depth = $depth
            if (_depth > 1) {
                val stabilizationIndex = $focus | $focusRelax
                val deltaSize = ${displayAt(0)}.length - (${displayAt(1)}((stabilizationIndex >> $blockIndexBits) & $blockMask).asInstanceOf[Array[AnyRef]].length)
                val _focusDepth = focusDepth
                $copyDisplays(_focusDepth, stabilizationIndex)
                $stabilizeDisplayPath(_focusDepth, stabilizationIndex)
                var currentDepth = _focusDepth + 1
                var display: Array[AnyRef] = null
                ${matchOnInt(q"currentDepth", 2 to 6, d => q"display = ${displayAt(d - 1)}")}
                while (currentDepth <= _depth) {
                    val oldSizes = display(display.length - 1 ).asInstanceOf[Array[Int]]
                    val newSizes = new Array[Int](oldSizes.length)
                    val lastSizesIndex = oldSizes.length - 1
                    Platform.arraycopy(oldSizes, 0, newSizes, 0, lastSizesIndex)
                    newSizes(lastSizesIndex) = oldSizes(lastSizesIndex) + deltaSize
                    val idx = (stabilizationIndex >> ($blockIndexBits * currentDepth)) & $blockMask
                    val newDisplay = copyOf(display, idx, idx + 2)
                    newDisplay(newDisplay.length - 1) = newSizes
                    ${matchOnInt(q"currentDepth", 2 to 6, d => q"newDisplay(idx) = ${displayAt(d - 2)}; ${displayAt(d - 1)}(idx) = newDisplay; ..${if (d < 6) q"display = ${displayAt(d)}" :: Nil else Nil}")}
                    currentDepth += 1
                }
            }
         """
    }

    private[codegen] def stabilizeDisplayPathCode(depthParam: TermName, focusParam: TermName) = {
        def stabilizeFromLevel(level: Int) = {
            def stabilizeDisplay(i: Int) =
                q"${displayAt(i)}(($focusParam >> ${blockIndexBits * i}) & $blockMask) = ${displayAt(i - 1)}"
            q"..${(level to 1 by -1) map stabilizeDisplay}"
        }
        q"""
            ..${assertions(q"0 < $depthParam", q"$depthParam <= this.$depth")}
            ${matchOnInt(q"$depthParam", 1 to 6, d => stabilizeFromLevel(d - 1))}
         """
    }


    private[vectorpointer] def cleanTopCode(cutIndex: TermName) = {
        def cleanLevelAndGoDown(lvl: Int): Tree =
            q"""
                if (($cutIndex >> ${lvl * blockIndexBits}) == 0) {
                    ${displayAt(lvl)} = null
                    ${if (lvl == 1) q"this.$depth = 1" else cleanLevelAndGoDown(lvl - 1)}
                } else this.$depth = ${lvl + 1}
             """
        matchOnInt(q"this.$depth", 2 to 6, d => cleanLevelAndGoDown(d - 1))
    }

    def copyDisplaysCode(depthParam: Tree, focusParam: TermName) = {
        def copyDisplaysDepth(depths: Seq[Int]): Tree = {
            val idx = TermName(s"idx")
            def copyDisplay(i: Int) = {
                val indexVal = q"($focusParam >> ${blockIndexBits * i}) & $blockMask"
                val indexSet =
                    if (i == 1) q"var $idx = $indexVal" else q"$idx = $indexVal"
                val updateWithCopy = q"${displayAt(i)} = $copyOf(${displayAt(i)}, $idx + 1, $idx + 2)"
                Seq(indexSet, updateWithCopy)
            }
            q"..${depths flatMap copyDisplay}"

        }
        depthParam match {
            case q"${n: Int}" =>
                if (0 <= n && n <= 6) copyDisplaysDepth(Seq(n))
                else throw new IllegalArgumentException
            case _ => matchOnInt(depthParam, 1 to 6, d => copyDisplaysDepth(1 until d))
        }

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
                ${matchOnInt(q"_currentDepth", 2 to 6, d => copyDisplayAndCut(d - 1), Some(q"throw new IllegalStateException"))}
                _currentDepth += 1
            }
         """
    }

    private[vectorpointer] def copyOfCode(array: Tree, numElements: Tree, newSize: Tree) = {
        val newArray = TermName("newArray")
        q"""
            ..${assertions(q"$array!=null", q"$numElements <= $newSize", q"$numElements <= $array.length")}
            val $newArray = new Array[AnyRef]($newSize)
            Platform.arraycopy($array, 0, $newArray, 0, $numElements)
            $newArray
        """
    }


    // Helper code

    protected def displayAt(level: Int) = q"${displayNameAt(level)}"

    protected def displayNameAt(level: Int) = {
        if (0 <= level && level < 6) TermName("display" + level)
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
                DEPTH_MATCH match {
                    case DEPTH_MATCH_METHOD.WITH_MATCH =>
                        val cases = numCases map (i => cq"$i => ${code(i)}")
                        defaultOption match {
                            case Some(default) => q"$num match { case ..$cases; case _ => $default }"
                            case None => q"$num match { case ..$cases }"
                        }
                    case DEPTH_MATCH_METHOD.WITH_IF_ELSE_IF =>
                        def matchOnIntRec(numRef: TermName, numCases: Seq[Int]): Tree = {
                            numCases.headOption match {
                                case Some(caseNumber) =>
                                    q"if($numRef == $caseNumber) ${code(caseNumber)} else ${matchOnIntRec(numRef, numCases.tail)}"
                                case None =>
                                    defaultOption match {
                                        case Some(default) => default
                                        case None => q"()"
                                    }
                            }
                        }
                        num match {
                            case q"${numRef: TermName}" =>
                                matchOnIntRec(numRef, numCases)
                            case _ =>
                                val numRef = TermName("valueToMatch")
                                q"""
                                    val $numRef = $num
                                    ${matchOnIntRec(numRef, numCases)}
                                 """
                        }
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
        if (DIRECT_LEVEL) {
            matchOnInt(getIndexLevel(xor), levels, code, Some(ifNotInLevels))
        } else {
            if (levels.isEmpty) ifNotInLevels
            else {
                val currentLevel = levels.head
                val ifClause = code(currentLevel)
                val elseClause = ifInLevel(xor, levels.tail, code, ifNotInLevels)
                q"if (${indexIsInLevel(xor, q"$currentLevel")}) $ifClause else $elseClause"
            }
        }

    }
}
