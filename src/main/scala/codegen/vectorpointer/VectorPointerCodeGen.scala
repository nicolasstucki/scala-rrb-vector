package codegen.vectorpointer

import codegen.VectorProperties

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

    val depth = TermName("depth")

    val display0 = TermName("display0")
    val display1 = TermName("display1")
    val display2 = TermName("display2")
    val display3 = TermName("display3")
    val display4 = TermName("display4")
    val display5 = TermName("display5")

    val hasWritableTail = TermName("hasWritableTail")


    // Method names

    val root = TermName("root")
    val initFromRoot = TermName("initFromRoot")
    val initFrom = TermName("initFrom")
    val initFocus = TermName("initFocus")
    val gotoIndex = TermName("gotoIndex")
    val allDisplaySizes = TermName("allDisplaySizes")
    val putDisplaySizes = TermName("putDisplaySizes")
    val gotoPosRelaxed = TermName("gotoPosRelaxed")
    val getElement = TermName("getElement")
    val gotoPos = TermName("gotoPos")
    val gotoNextBlockStart = TermName("gotoNextBlockStart")
    val gotoPrevBlockStart = TermName("gotoPrevBlockStart")
    val setupNextBlockStartWritable = TermName("setUpNextBlockStartTailWritable")
    val gotoNextBlockStartWritable = TermName("gotoNextBlockStartWritable")
    val copyDisplays = TermName("copyDisplays")
    val copyDisplaysTop = TermName("copyDisplaysTop")
    val stabilize = TermName("stabilize")
    val cleanTop = TermName("cleanTop")
    val copyOf = TermName("copyOf")
    val mergeLeafs = TermName("mergeLeafs")


    // Method definitions

    private[vectorpointer] def getDisplayName(_depth: Int): TermName = _depth match {
        case 1 => display0
        case 2 => display1
        case 3 => display2
        case 4 => display3
        case 5 => display4
        case 6 => display5
    }

    private[vectorpointer] def getDisplayTree(_depth: Int): Tree = _depth match {
        case 0 => q"null"
        case 1 => q"$display0"
        case 2 => q"$display1"
        case 3 => q"$display2"
        case 4 => q"$display3"
        case 5 => q"$display4"
        case 6 => q"$display5"
    }

    def rootCode(depth: Tree) = depth match {
        case q"${d: Int}" =>
            if (0 <= d && d < 6) getDisplayTree(d)
            else q"throw new IllegalStateException"
        case _ =>
            val cases = (List.range(0, 6) map (i => cq"$i => ${getDisplayTree(i)}")) ::: cq"_ => throw new IllegalStateException" :: Nil
            q"$depth match { case ..$cases }"
    }

    protected def initFromRootCode(rootParam: TermName, depthParam: TermName, endIndexParam: TermName): Tree = {
        val cases = matchOnInt(q"$depthParam", 1 to 6, d => q"${displayAt(d - 1)} = $rootParam")
        q"""
            $cases
            $depth = $depthParam
            $gotoIndex(0, $endIndexParam)
         """
    }

    def initFromCode(that: Tree) = {
        def depthCase(depth: Int) = {
            val stats = (1 to depth) map getDisplayName map (d => q"$d = $that.$d")
            q"{..$stats}"
        }
        val cases = matchOnInt(q"$that.$depth", 0 to 6, depthCase, Some(q"throw new IllegalStateException"))
        q"""
            $initFocus($that.$focus, $that.$focusStart, $that.$focusEnd, $that.$focusDepth, $that.$focusRelax)
            $depth = $that.$depth
            ..$cases
        """
    }

    private[vectorpointer] def initFocusCode(focusP: Tree, focusStartP: Tree, focusEndP: Tree, focusDepthP: Tree, focusRelaxP: Tree) = {
        q"""
            this.$focus = $focusP
            this.$focusStart = $focusStartP
            this.$focusEnd = $focusEndP
            this.$focusDepth = $focusDepthP
            this.$focusRelax = $focusRelaxP
        """
    }

    private[vectorpointer] def gotoIndexCode(index: Tree, endIndex: Tree) = {
        val indexInFocus = TermName("indexInFocus")
        val xor = TermName("xor")
        q"""
            val $focusStart = this.$focusStart
            if ($focusStart <= $index && $index < $focusEnd) {
                val $indexInFocus = $index - $focusStart
                val $xor = $indexInFocus ^ $focus
                ${ifInLevel(q"$xor", Seq(0), i => q"()", q"$gotoPos($indexInFocus, $xor)")}
                $focus = $indexInFocus
            } else {
                $gotoPosRelaxed($index, 0, $endIndex, this.$depth)
            }
        """
    }

    private[vectorpointer] def allDisplaySizesCode() = {
        val currentDepth = TermName("currentDepth")
        def sizes(d: Tree) = matchOnInt(d, 1 to 5, i => getBlockSizes(displayAt(i)), Some(q"null"))
        q"""
            val allSizes: Array[Array[Int]] = new Array(5)
            var $currentDepth = $focusDepth
            while ($currentDepth < $depth) {
                allSizes($currentDepth - 1) = ${sizes(q"$currentDepth")}
                $currentDepth += 1
            }
            allSizes
        """
    }

    private[vectorpointer] def putDisplaySizesCode(allSizes: Tree) = {
        val currentDepth = TermName("_depth")
        val update = matchOnInt(q"$currentDepth", 1 to 5, i => q"${displayAt(i)}(${displayAt(i)}.length-1) = allSizes($currentDepth - 1)", Some(q"null"))
        q"""
            var $currentDepth = $focusDepth
            while ($currentDepth < $depth) {
                $update
                $currentDepth += 1
            }
        """
    }

    def gotoPosRelaxedCode(indexParam: Tree, startIndexParam: Tree, endIndexParam: Tree, depthParam: Tree, focusRelaxParam: Tree) = {
        val display = TermName("display")
        val getCurrentDisplay = matchOnInt(depthParam, 0 to 6, i => if (i == 0) q"null" else displayAt(i - 1), Some(q"throw new IllegalArgumentException"))
        val setNewDisplay = matchOnInt(depthParam, 2 to 6, i => q"${displayAt(i - 2)} = $display(is).asInstanceOf[Array[AnyRef]]", Some(q"throw new IllegalArgumentException"))
        q"""
            val $display = $getCurrentDisplay
            if ($depthParam > 1 && $display($display.length - 1) != null) {
                val sizes = ${getBlockSizes(q"$display")}
                val indexInSubTree = $indexParam - $startIndexParam
                var is = 0
                while (sizes(is) <= indexInSubTree)
                    is += 1
                $setNewDisplay
                $gotoPosRelaxed($indexParam, if (is == 0) $startIndexParam else $startIndexParam + sizes(is - 1), if (is < sizes.length - 1) $startIndexParam + sizes(is) else $endIndexParam, $depthParam - 1, $focusRelaxParam | (is << ($blockIndexBits * ($depthParam - 1))))
            } else {
                val indexInFocus = $indexParam - $startIndexParam
                $gotoPos(indexInFocus, 1 << ($blockIndexBits * ($depthParam - 1)))
                $initFocus(indexInFocus, $startIndexParam, $endIndexParam, $depthParam, $focusRelaxParam)
            }
        """
    }

    private[codegen] def getElementCode(index: TermName, xor: TermName): Tree = {
        @tailrec def getElemFromDisplay(display: Tree, level: Int): Tree = {
            val idx = branchIndex(q"$index", q"$level")
            if (level == 0) q"$display($idx).asInstanceOf[$A]"
            else getElemFromDisplay(q"$display($idx).asInstanceOf[Array[AnyRef]]", level - 1)
        }
        ifInLevel(q"$xor", 0 to 5, i => getElemFromDisplay(displayAt(i), i), q"throw new IllegalArgumentException")
    }

    def gotoPosCode(index: Tree, xor: Tree) = {
        def loadDisplay(lvl: Int) = q"${displayAt(lvl - 1)} = ${displayAt(lvl)}(($index >> ${blockIndexBits * lvl}) & $blockMask).asInstanceOf[Array[AnyRef]]"
        ifInLevel(xor, 0 to 5, lvl => q"..${(lvl to 1 by -1) map loadDisplay}", q"throw new IllegalArgumentException")
    }

    def gotoNextBlockStartCode(index: Tree, xor: Tree) = {
        def loadDisplayAtIndex(lvl: Int, i: Int) = {
            if (i == lvl) q"${displayAt(i - 1)} = ${displayAt(i)}(($index >> ${blockIndexBits * i}) & $blockMask).asInstanceOf[Array[AnyRef]]"
            else q"${displayAt(i - 1)} = ${displayAt(i)}(0).asInstanceOf[Array[AnyRef]]"
        }
        ifInLevel(xor, 1 to 5, lvl => q"..${(lvl to 1 by -1) map (i => loadDisplayAtIndex(lvl, i))}", q"throw new IllegalArgumentException")
    }

    def gotoPrevBlockStartCode(index: Tree, xor: Tree) = {
        def loadDisplayAtIndex(lvl: Int, i: Int) = {
            if (i == lvl) q"${displayAt(i - 1)} = ${displayAt(i)}(($index >> ${blockIndexBits * i}) & $blockMask).asInstanceOf[Array[AnyRef]]"
            else q"${displayAt(i - 1)} = ${displayAt(i)}($blockMask).asInstanceOf[Array[AnyRef]]"
        }
        ifInLevel(xor, 1 to 5, lvl => q"..${(lvl to 1 by -1) map (i => loadDisplayAtIndex(lvl, i))}", q"throw new IllegalArgumentException")
    }

    def setUpNextBlockStartTailWritableCode(index: Tree, xor: Tree) = {
        def setUpNewBranchFromLevel(lvl: Int) = {
            q"""
                if (this.$depth == $lvl) {
                    ${displayAt(lvl)} = new Array(${if (CLOSED_BLOCKS) 2 + blockInvariants else blockWidth + blockInvariants})
                    ${displayAt(lvl)}(0) = ${displayAt(lvl - 1)}
                    this.$depth += 1
                } else {
                    val len = ${displayAt(lvl)}.length;
                    ${displayAt(lvl)} = $copyOf(${displayAt(lvl)}, len, ${if (CLOSED_BLOCKS) q"len + 1" else q"${blockWidth + blockInvariants}"})
                }
                ${displayAt(0)} = new Array(${if (CLOSED_BLOCKS) 1 else blockWidth})
                ..${(1 until lvl) map (i => q"${displayAt(i)} = new Array(${if (CLOSED_BLOCKS) 1 + blockInvariants else blockWidth + blockInvariants})")}
             """
        }
        ifInLevel(xor, 1 to 5, setUpNewBranchFromLevel, q"throw new IllegalArgumentException")
    }

    def gotoNextBlockStartWritableCode(index: Tree, xor: Tree) = {
        def gotoNextBlockStartWritableFromLevel(lvl: Int) = {
            q"""
                if (this.$depth == $lvl) {
                    ${displayAt(lvl)} = new Array(${blockWidth + blockInvariants})
                    ${displayAt(lvl)}(0) = ${displayAt(lvl - 1)}
                    this.$depth += 1
                }
                ${displayAt(0)} = new Array(${blockWidth})
                ..${(1 until lvl) map (i => q"${displayAt(i)} = new Array(${blockWidth + blockInvariants})")}
                ..${(1 to lvl) map (i => q"${displayAt(i)}(($index >> ${5 * i}) & $blockMask) = ${displayAt(i - 1)}")}
             """
        }
        ifInLevel(xor, 1 to 5, gotoNextBlockStartWritableFromLevel, q"throw new IllegalArgumentException")
    }


    def copyDisplaysCode(depthParam: Tree, focusParam: Tree) = {
        def copyDisplaysDepth(depths: Seq[Int]): Tree = {
            def copyDisplay(i: Int) = {
                val t = TermName(s"idx_$i")
                val indexDef = q"val $t = ($focusParam >> ${5 * i}) & $blockMask"
                val updateWithCopy = q"${displayAt(i)} = $copyOf(${displayAt(i)}, $t + 1, ${if (CLOSED_BLOCKS) q"$t + 2" else q"${blockWidth + blockInvariants}"})"
                Seq(indexDef, updateWithCopy)
            }
            q"..${depths flatMap copyDisplay}"
        }
        matchOnInt(depthParam, 1 to 6, d => copyDisplaysDepth(1 until d))
    }


    def copyDisplaysTopCode(currentDepth: Tree, focusRelaxParam: Tree) = {
        def copyDisplayAndCut(lvl: Int) =
            q"""
                val cutIndex = ($focusRelaxParam >> ${5 * lvl}) & $blockMask
                ${displayAt(lvl)} = $copyOf(${displayAt(lvl)}, cutIndex + 1, cutIndex + 2)
             """
        q"""
            ${matchOnInt(currentDepth, 2 to 6, d => copyDisplayAndCut(d - 1), Some(q"throw new IllegalStateException"))}
            if ($currentDepth < this.$depth)
                $copyDisplaysTop($currentDepth + 1, $focusRelax)
         """
    }

    def cleanTopCode(cutIndex: Tree) = {
        val depthParam = TermName("_depth")
        def cleanLevelAndGoDown(lvl: Int): Tree =
            q"""
                if (($cutIndex >> ${lvl * blockIndexBits}) == 0) {
                    ${displayAt(lvl)} = null
                    ${if (lvl == 1) q"this.$depth = 1" else q"cleanTopRec($depthParam - 1)"}
                } else depth = ${lvl + 1}
             """
        val cleanTopRecCode = matchOnInt(q"$depthParam", 2 to 6, d => cleanLevelAndGoDown(d - 1))
        q"""
            @tailrec def cleanTopRec($depthParam: Int): Unit = $cleanTopRecCode
            cleanTopRec(this.$depth)
        """
    }

    def stabilizeCode(depthParam: Tree, focusParam: Tree) = {
        def stabilizeFromLevel(level: Int) = {
            def stabilizeDisplay(i: Int) =
                q"${displayAt(i)}(($focusParam >> ${blockIndexBits * i}) & $blockMask) = ${displayAt(i - 1)}"
            q"..${(level to 1 by -1) map stabilizeDisplay}"
        }
        matchOnInt(depthParam, 1 to 6, d => stabilizeFromLevel(d - 1))
    }

    private[vectorpointer] def copyOfCode(array: Tree, numElements: Tree, newSize: Tree) = {
        val newArray = TermName("newArray")
        q"""
            val $newArray = new Array[AnyRef]($newSize)
            Platform.arraycopy($array, 0, $newArray, 0, $numElements)
            $newArray
        """
    }


    protected def mergeLeafsCode(leaf0: Tree, length0: Tree, leaf1: Tree, length1: Tree) = {
        val newLeaf = TermName("newLeaf")
        q"""
             val $newLeaf = new Array[AnyRef]($length0 + $length1)
             Platform.arraycopy($leaf0, 0, $newLeaf, 0, $length0)
             Platform.arraycopy($leaf1, 0, $newLeaf, $length0, $length1)
             $newLeaf
         """
    }


    // Helper code

    protected def displayAt(level: Int) = q"${displayNameAt(level)}"

    protected def displayNameAt(level: Int) = level match {
        case 0 => display0
        case 1 => display1
        case 2 => display2
        case 3 => display3
        case 4 => display4
        case 5 => display5
        case _ => throw new IllegalArgumentException(level.toString)
    }

    protected def branchIndex(index: Tree, level: Tree) = (index, level) match {
        case (q"${i: Int}", q"${l: Int}") => q"${(i >> (blockIndexBits * l)) & blockMask}"
        case (q"${i: Int}", _) => if (i == 0) q"0" else q"($i >> ($blockIndexBits * $level)) & $blockMask"
        case (_, q"${l: Int}") => if (l == 0) q"$index & $blockMask" else q"($index >> ${blockIndexBits * l}) & $blockMask"
        case _ => q"($index >> ($blockIndexBits * $level)) & $blockMask"
    }

    protected def getBlockSizes(display: Tree) = {
        if (CLOSED_BLOCKS)
            q"$display($display.length - 1).asInstanceOf[Array[Int]]"
        else
            q"$display(${blockWidth + blockInvariants - 1}).asInstanceOf[Array[Int]]"
    }


    protected def matchOnInt(num: Tree, numCases: Seq[Int], code: Int => Tree, defaultOption: Option[Tree] = None) = {
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
