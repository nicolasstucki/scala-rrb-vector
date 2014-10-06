package codegen.vectorpointer

import codegen.VectorPackage

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorPointerCode {
    self: VectorPackage =>

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

    // Methods names
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
    val setUpNextBlockStartTailWritable = TermName("setUpNextBlockStartTailWritable")
    val gotoNextBlockStartWritable = TermName("gotoNextBlockStartWritable")
    val copyDisplays = TermName("copyDisplays")
    val copyDisplaysTop = TermName("copyDisplaysTop")
    val stabilize = TermName("stabilize")
    val copyOf = TermName("copyOf")
    val mergeLeafs = TermName("mergeLeafs")

    val treeBranchWidth = 32
    val treeInvariants = 1


    protected def initFromRootCode(rootParam: TermName, depthParam: TermName, endIndexParam: TermName): Tree = {
        val cases = matchOnDepth(q"$depthParam", 1 to 6, d => q"${setDisplay(d - 1, q"$rootParam")}")
        q"""
            $cases
            $depth = $depthParam
            $gotoIndex(0, $endIndexParam)
         """
    }

    //

    private[codegen] def getElementCode(index: TermName, xor: TermName): Tree = {
        @tailrec def getElemFromDisplay(display: Tree, level: Int): Tree = {
            val idx = branchIndex(q"$index", q"$level")
            if (level == 0) q"$display($idx).asInstanceOf[$A]"
            else getElemFromDisplay(q"$display($idx).asInstanceOf[Array[AnyRef]]", level - 1)
        }
        ifInLevel(0 to 5, q"$xor", i => getElemFromDisplay(displayAt(i), i))
    }

    protected def mergeLeafsCode(leaf0: TermName, length0: TermName, leaf1: TermName, length1: TermName) = {
        q"""
             val newLeaf = new Array[AnyRef]($length0 + $length1)
             Platform.arraycopy($leaf0, 0, newLeaf, 0, $length0)
             Platform.arraycopy($leaf1, 0, newLeaf, $length0, $length1)
             newLeaf
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
        case (q"${i: Int}", q"${l: Int}") => q"${(i >> (5 * l)) & 31}"
        case (q"${i: Int}", _) => if (i == 0) q"0" else q"($i >> (5 * $level)) & 31"
        case (_, q"${l: Int}") => if (l == 0) q"$index & 31" else q"($index >> ${5 * l}) & 31"
        case _ => q"($index >> (5 * $level)) & 31"
    }

    protected def getBranch(display: Tree, index: Tree) = {
        q"$display($index)"
    }

    protected def getBranchSizes(display: TermName) = {
        q"$display($display.length-1).asInstanceOf[Array[Int]]"
    }

    protected def setDisplay(level: Int, newDisplay: Tree) = {
        val display = displayNameAt(level)
        q"$display = $newDisplay"
    }

    protected def updateDisplay(level: Int, index: Tree, newDisplay: Tree) = {
        val display = displayNameAt(level)
        q"$display($index) = $newDisplay"
    }

    protected def indexIsInLevel(index: Tree, level: Tree) = level match {
        case q"${lvl: Int}" => q"$index < ${1 << (5 * (lvl + 1))}"
        case _ => q"$index < (1 << (5 * ($level + 1)))"
    }

    protected def matchOnDepth(depth: Tree, depths: Seq[Int], code: Int => Tree) = {
        depth match {
            case q"${d: Int}" =>
                if (depths.contains(d))
                    q"throw new IllegalArgumentException"
                else
                    code(d)
            case _ =>
                val cases = depths map (i => cq"$i => ${code(i)}")
                q"$depth match { case ..$cases; case _ => throw new IllegalArgumentException }"
        }
    }

    protected def ifInLevel(levels: Range, xor: Tree, code: Int => Tree): Tree = {
        if (levels.isEmpty) q"throw new IllegalArgumentException"
        else {
            val currentLevel = levels.head
            val ifClause = code(currentLevel)
            val elseClause = ifInLevel(levels.tail, xor, code)
            q"if ($xor < ${1 << (5 * (currentLevel + 1))}) $ifClause else $elseClause"
        }
    }
}
