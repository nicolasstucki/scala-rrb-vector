package codegen.vectorpointer


import codegen.VectorPackage

import scala.annotation.tailrec
import scala.reflect.runtime.universe._

trait VectorPointerClass {
    self: VectorPackage with VectorPointerCode =>

    protected def generateVectorPointerClass() = {
        q"""
                private[immutable] trait $vectorPointerClassName[$A] {
                    ..${traitFieldsDef()}

                    ${rootDef()}
                    ${initFromRootDef()}
                    ${initFromDef()}
                    ${initFocusDef()}
                    ${gotoIndexDef()}
                    ${allDisplaySizesDef()}
                    ${putDisplaySizesDef()}
                    ${gotoPosRelaxedDef()}
                    ${getElementDef()}
                    ${gotoPosDef()}
                    ${gotoNextBlockStartDef()}
                    ${gotoPrevBlockStartDef()}
                    ${setUpNextBlockStartTailWritableDef()}
                    ${gotoNextBlockStartWritableDef()}
                    ${copyDisplaysDef()}
                    ${copyDisplaysTopDef()}
                    ${stabilizeDef()}
                    ${copyOfDef()}
                    ${mergeLeafsDef()}

                }
             """
    }

    // Field definitions

    private def traitFieldsDef(): List[Tree] = {
        def genPrivateImmutableVar(name: TermName, tpt: Tree, init: Tree): Tree =
            q"private[immutable] var $name: $tpt = $init"

        val focusFields = focusStart :: focusEnd :: focus :: focusDepth :: focusRelax :: Nil map (genPrivateImmutableVar(_, tq"Int", q"0"))
        val depthField = genPrivateImmutableVar(depth, tq"Int", pq"_")
        val hasWritableTailField = genPrivateImmutableVar(hasWritableTail, tq"Boolean", q"false")
        val displayFields = display0 :: display1 :: display2 :: display3 :: display4 :: display5 :: Nil map (genPrivateImmutableVar(_, tq"Array[AnyRef]", pq"_"))

        focusFields ::: depthField :: hasWritableTailField :: displayFields
    }

    // Method implementations

    private def getDisplayName(_depth: Int): TermName = _depth match {
        case 1 => display0
        case 2 => display1
        case 3 => display2
        case 4 => display3
        case 5 => display4
        case 6 => display5
    }

    private def getDisplayTree(_depth: Int): Tree = _depth match {
        case 0 => q"null"
        case 1 => q"$display0"
        case 2 => q"$display1"
        case 3 => q"$display2"
        case 4 => q"$display3"
        case 5 => q"$display4"
        case 6 => q"$display5"
    }

    private def rootDef() = {
        q"""
             private[immutable] def $root(): AnyRef = {
                 ${rootCode(q"$depth")}
             }
         """
    }

    private def rootCode(depth: Tree) = depth match {
        case q"${d: Int}" =>
            if (0 <= d && d < 6) getDisplayTree(d)
            else q"throw new IllegalStateException"
        case _ =>
            val cases = (List.range(0, 6) map (i => cq"$i => ${getDisplayTree(i)}")) ::: cq"_ => throw new IllegalStateException" :: Nil
            q"$depth match { case ..$cases }"
    }

    private def initFromRootDef() = {
        val cases =
            cq"0 => throw new IllegalArgumentException()" ::
              (List.range(1, 6) map (i => cq"$i => ${getDisplayTree(i)} = root")) :::
              cq"_ => throw new IllegalStateException()" :: Nil
        q"""
             private[immutable] def $initFromRoot(root: Array[AnyRef], _depth: Int, _endIndex: Int): Unit = {
                 _depth match { case ..$cases }
                 $depth = _depth
                 gotoIndex(0, _endIndex)
             }
         """
    }

    private def initFromDef() = {
        def depthCase(depth: Int) = {
            val stats = (1 to depth) map getDisplayName map (d => q"$d = that.$d")
            q"{..$stats}"
        }
        val cases =
            cq"0 => " ::
              (List.range(1, 7) map (i => cq"$i => ${depthCase(i)}")) :::
              cq"_ => throw new IllegalStateException()" :: Nil
        q"""
             private[immutable] def $initFrom[U](that: $vectorPointerClassName[U]): Unit = {
                 $initFocus(that.$focus, that.$focusStart, that.$focusEnd, that.$focusDepth, that.$focusRelax)
                 $depth = that.$depth
                 that.$depth match { case ..$cases }
             }
         """
    }

    private def initFocusDef() = {
        q"""
             private[immutable] final def $initFocus(_focus: Int, _focusStart: Int, _focusEnd: Int, _focusDepth: Int, _focusRelax: Int) = {
                 this.$focus = _focus
                 this.$focusStart = _focusStart
                 this.$focusEnd = _focusEnd
                 this.$focusDepth = _focusDepth
                 this.$focusRelax = _focusRelax
             }
         """
    }

    private def gotoIndexDef() = {
        q"""
              private[immutable] final def $gotoIndex(index: Int, endIndex: Int) = {
                 val $focusStart = this.$focusStart
                 if ($focusStart <= index && index < $focusEnd) {
                     val indexInFocus = index - $focusStart
                     val xor = indexInFocus ^ $focus
                     if /* is not focused on last block */ (xor >= (1 << 5)) {
                         $gotoPos(indexInFocus, xor)
                     }
                     focus = indexInFocus
                 } else {
                     $gotoPosRelaxed(index, 0, endIndex, $depth)
                 }
              }
         """
    }

    private def allDisplaySizesDef() = {
        val cases =
            (List.range(1, 6) map (i => cq"$i => ${getDisplayTree(i + 1)}.last.asInstanceOf[Array[Int]]")) :::
              cq"_ => null" :: Nil
        q"""
             private[immutable] def $allDisplaySizes(): Array[Array[Int]] = {
                 val allSises: Array[Array[Int]] = new Array(5)
                 for (i <- $focusDepth until $depth) {
                     allSises(i - 1) = i match { case ..$cases }
                 }
                 allSises
             }
         """
    }

    private def putDisplaySizesDef() = {
        val currentDepth = TermName("_depth")
        val cases =
            (List.range(1, 6) map getDisplayTree).zipWithIndex.map { case (d, i) => cq"$i => $d($d.length-1) = allSizes($depth-1)"} :::
              cq"_ => null" :: Nil
        q"""
             private[immutable] def $putDisplaySizes(allSizes: Array[Array[Int]]): Unit = {
                 for ($currentDepth <- $focusDepth until $depth) {
                     $currentDepth match { case ..$cases }
                 }
             }
         """
    }

    private def gotoPosRelaxedDef() = {
        q"""
             @tailrec
             private[immutable] final def $gotoPosRelaxed(index: Int, _startIndex: Int, _endIndex: Int, _depth: Int, _focusRelax: Int = 0): Unit = {
                 val display = _depth match {
                     case 0 => null
                     case 1 => display0
                     case 2 => display1
                     case 3 => display2
                     case 4 => display3
                     case 5 => display4
                     case 6 => display5
                     case _ => throw new IllegalArgumentException("depth=" + _depth)
                 }

                 if (_depth > 1 && display(display.length - 1) != null) {
                     val sizes = display(display.length - 1).asInstanceOf[Array[Int]]
                     val indexInSubTree = index - _startIndex
                     var is = 0
                     while (sizes(is) <= indexInSubTree)
                         is += 1
                     _depth match {
                         case 2 => display0 = display(is).asInstanceOf[Array[AnyRef]]
                         case 3 => display1 = display(is).asInstanceOf[Array[AnyRef]]
                         case 4 => display2 = display(is).asInstanceOf[Array[AnyRef]]
                         case 5 => display3 = display(is).asInstanceOf[Array[AnyRef]]
                         case 6 => display4 = display(is).asInstanceOf[Array[AnyRef]]
                         case _ => throw new IllegalArgumentException("depth=" + _depth)
                     }
                     $gotoPosRelaxed(index, if (is == 0) _startIndex else _startIndex + sizes(is - 1), if (is < sizes.length - 1) _startIndex + sizes(is) else _endIndex, _depth - 1, _focusRelax | (is << (5 * _depth - 5)))
                 } else {
                     val indexInFocus = index - _startIndex
                     $gotoPos(indexInFocus, 1 << (5 * (_depth - 1)))
                     $initFocus(indexInFocus, _startIndex, _endIndex, _depth, _focusRelax)
                 }
             }
          """
    }


    private def getElementDef() = {
        val index = TermName("index")
        val xor = TermName("xor")
        q"private[immutable] final def $getElement(index: Int, xor: Int): A = ${getElementCode(index, xor)}"

    }


    private def gotoPosDef() = {
        q"""
             private[immutable] final def $gotoPos(index: Int, xor: Int): Unit = {
                 if /* level = 0 */ (xor < (1 << 5)) {
                     // could maybe removed
                 } else if /* level = 1 */ (xor < (1 << 10)) {
                     $display0 = display1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
                 } else if /* level = 2 */ (xor < (1 << 15)) {
                     val d1 = display2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
                     $display1 = d1
                     $display0 = d1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
                 } else if /* level = 3 */ (xor < (1 << 20)) {
                     val d2 = display3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
                     val d1 = d2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
                     $display2 = d2
                     $display1 = d1
                     $display0 = d1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
                 } else if /* level = 4 */ (xor < (1 << 25)) {
                     val d3 = display4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
                     val d2 = d3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
                     val d1 = d2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
                     $display3 = d3
                     $display2 = d2
                     $display1 = d1
                     $display0 = d1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
                 } else if /* level = 5 */ (xor < (1 << 30)) {
                     val d4 = display5((index >> 25) & 31).asInstanceOf[Array[AnyRef]]
                     val d3 = d4((index >> 20) & 31).asInstanceOf[Array[AnyRef]]
                     val d2 = d3((index >> 15) & 31).asInstanceOf[Array[AnyRef]]
                     val d1 = d2((index >> 10) & 31).asInstanceOf[Array[AnyRef]]
                     $display4 = d4
                     $display3 = d3
                     $display2 = d2
                     $display1 = d1
                     $display0 = d1((index >> 5) & 31).asInstanceOf[Array[AnyRef]]
                 } else /* level < 0 || 5 < level */ {
                     throw new IllegalArgumentException()
                 }
             }
         """
    }

    private def gotoNextBlockStartDef() = {
        def loadDisplayAtIndex(i: Int) = q"${getDisplayName(i - 1)} = ${getDisplayTree(i)}((index >> ${5 * (i - 1)}) & 31).asInstanceOf[Array[AnyRef]]"
        def loadDisplayAtZero(i: Int) = q"${getDisplayName(i - 1)} = ${getDisplayTree(i)}(0).asInstanceOf[Array[AnyRef]]"
        q"""
             private[immutable] final def $gotoNextBlockStart(index: Int, xor: Int): Unit = {
                     // goto block start pos
                     if (xor < (1 << 10)) {
                         // level = 1
                         ${loadDisplayAtIndex(2)}
                     } else if (xor < (1 << 15)) {
                         // level = 2
                         ${loadDisplayAtIndex(3)}
                         ${loadDisplayAtZero(2)}
                     } else if (xor < (1 << 20)) {
                         // level = 3
                         ${loadDisplayAtIndex(4)}
                         ${loadDisplayAtZero(3)}
                         ${loadDisplayAtZero(2)}
                     } else if (xor < (1 << 25)) {
                         // level = 4
                         ${loadDisplayAtIndex(5)}
                         ${loadDisplayAtZero(4)}
                         ${loadDisplayAtZero(3)}
                         ${loadDisplayAtZero(2)}
                     } else if (xor < (1 << 30)) {
                         // level = 5
                         ${loadDisplayAtIndex(6)}
                         ${loadDisplayAtZero(5)}
                         ${loadDisplayAtZero(4)}
                         ${loadDisplayAtZero(3)}
                         ${loadDisplayAtZero(2)}
                     } else {
                         // level = 6
                         throw new IllegalArgumentException()
                     }
                 }
         """
    }

    private def gotoPrevBlockStartDef() = {
        val index = q"${TermName("index")}"
        q"""
             private[immutable] final def $gotoPrevBlockStart($index: Int, xor: Int): Unit = {
                 // goto block start pos
                 if (xor < (1 << 10)) {
                     // level = 1
                     ${getDisplayName(1)} = ${getDisplayTree(2)}($index >> (${5 * (2 - 1)}) & 31).asInstanceOf[Array[AnyRef]]
                 } else if (xor < (1 << 15)) {
                     // level = 2
                     ${getDisplayName(2)} = ${getDisplayTree(3)}($index >> (${5 * (3 - 1)}) & 31).asInstanceOf[Array[AnyRef]]
                     display0 = display1(31).asInstanceOf[Array[AnyRef]]
                 } else if (xor < (1 << 20)) {
                     // level = 3
                     ${getDisplayName(3)} = ${getDisplayTree(4)}($index >> (${5 * (4 - 1)}) & 31).asInstanceOf[Array[AnyRef]]
                     display1 = display2(31).asInstanceOf[Array[AnyRef]]
                     display0 = display1(31).asInstanceOf[Array[AnyRef]]
                 } else if (xor < (1 << 25)) {
                     // level = 4
                     ${getDisplayName(4)} = ${getDisplayTree(5)}($index >> (${5 * (5 - 1)}) & 31).asInstanceOf[Array[AnyRef]]
                     display2 = display3(31).asInstanceOf[Array[AnyRef]]
                     display1 = display2(31).asInstanceOf[Array[AnyRef]]
                     display0 = display1(31).asInstanceOf[Array[AnyRef]]
                 } else if (xor < (1 << 30)) {
                     // level = 5
                     ${getDisplayName(5)} = ${getDisplayTree(6)}($index >> (${5 * (6 - 1)}) & 31).asInstanceOf[Array[AnyRef]]
                     display3 = display4(31).asInstanceOf[Array[AnyRef]]
                     display2 = display3(31).asInstanceOf[Array[AnyRef]]
                     display1 = display2(31).asInstanceOf[Array[AnyRef]]
                     display0 = display1(31).asInstanceOf[Array[AnyRef]]
                 } else {
                     // level = 6
                     throw new IllegalArgumentException()
                 }
             }
         """
    }


    private def setUpNextBlockStartTailWritableDef() = {
        val index = TermName("_index")
        val xor = TermName("_or")
        q"""
             private[immutable] final def $setUpNextBlockStartTailWritable($index: Int, $xor: Int): Unit = {
                 if (${indexIsInLevel(q"$xor", q"1")}) {
                     if ($depth == 1) {
                         $display1 = new Array(${2 + treeInvariants})
                         $display1(0) = $display0
                         $depth += 1
                     } else {
                         val len = $display1.length
                         $display1 = copyOf(display1, len, len + 1)
                     }
                     $display0 = new Array($treeBranchWidth)
                 }
                 else if (${indexIsInLevel(q"$xor", q"2")}) {
                     if (depth == 2) {
                         $display2 = new Array(${2 + treeInvariants})
                         display2(0) = display1
                         depth += 1
                     } else {
                         val len = display2.length
                         display2 = copyOf(display2, len, len + 1)
                     }
                     display0 = new Array($treeBranchWidth)
                     display1 = new Array(${1 + treeBranchWidth})
                 } else if (${indexIsInLevel(q"$xor", q"3")}) {
                     if (depth == 3) {
                         display3 = new Array(${2 + treeInvariants})
                         display3(0) = display2
                         depth += 1
                     } else {
                         val len = display3.length
                         display3 = copyOf(display3, len, len + 1)
                     }
                     display0 = new Array($treeBranchWidth)
                     display1 = new Array(${1 + treeBranchWidth})
                     display2 = new Array(${1 + treeBranchWidth})
                 } else if (${indexIsInLevel(q"$xor", q"4")}) {
                     if (depth == 4) {
                         display4 = new Array(${2 + treeInvariants})
                         display4(0) = display3
                         depth += 1
                     } else {
                         val len = display4.length
                         display4 = copyOf(display4, len, len + 1)
                     }
                     display0 = new Array($treeBranchWidth)
                     display1 = new Array(${1 + treeBranchWidth})
                     display2 = new Array(${1 + treeBranchWidth})
                     display3 = new Array(${1 + treeBranchWidth})
                 } else if (${indexIsInLevel(q"$xor", q"5")}) {
                     if (depth == 5) {
                         display5 = new Array(${2 + treeInvariants})
                         display5(0) = display4
                         depth += 1
                     } else {
                         val len = display5.length
                         display5 = copyOf(display5, len, len + 1)
                     }
                     display0 = new Array($treeBranchWidth)
                     display1 = new Array(${1 + treeBranchWidth})
                     display2 = new Array(${1 + treeBranchWidth})
                     display3 = new Array(${1 + treeBranchWidth})
                     display4 = new Array(${1 + treeBranchWidth})
                 } else /* level < 0 || 5 < level */ {
                     throw new IllegalArgumentException()
                 }
             }
         """
    }


    private def gotoNextBlockStartWritableDef() = {
        q"""
             private[immutable] final def $gotoNextBlockStartWritable(index: Int, xor: Int): Unit = {
                 if /* level = 1 */ (xor < (1 << 10)) {
                     if (depth == 1) {
                         display1 = new Array(${treeBranchWidth + treeInvariants} )
                         display1(0) = display0
                         depth += 1
                     }
                     display0 = new Array($treeBranchWidth)
                     display1((index >> 5) & 31) = display0
                 } else if /* level = 2 */ (xor < (1 << 15)) {
                     if (depth == 2) {
                         display2 = new Array(${treeBranchWidth + treeInvariants} )
                         display2(0) = display1
                         depth += 1
                     }
                     display0 = new Array($treeBranchWidth)
                     display1 = new Array(${treeBranchWidth + treeInvariants} )
                     display1((index >> 5) & 31) = display0
                     display2((index >> 10) & 31) = display1
                 } else if /* level = 3 */ (xor < (1 << 20)) {
                     if (depth == 3) {
                         display3 = new Array(${treeBranchWidth + treeInvariants} )
                         display3(0) = display2
                         depth += 1
                     }
                     display0 = new Array($treeBranchWidth)
                     display1 = new Array(${treeBranchWidth + treeInvariants})
                     display2 = new Array(3)
                     display1((index >> 5) & 31) = display0
                     display2((index >> 10) & 31) = display1
                     display3((index >> 15) & 31) = display2
                 } else if /* level = 4 */ (xor < (1 << 25)) {
                     if (depth == 4) {
                         display4 = new Array(${treeBranchWidth + treeInvariants} )
                         display4(0) = display3
                         depth += 1
                     }
                     display0 = new Array($treeBranchWidth)
                     display1 = new Array(${treeBranchWidth + treeInvariants} )
                     display2 = new Array(${treeBranchWidth + treeInvariants} )
                     display3 = new Array(${treeBranchWidth + treeInvariants} )
                     display1((index >> 5) & 31) = display0
                     display2((index >> 10) & 31) = display1
                     display3((index >> 15) & 31) = display2
                     display4((index >> 20) & 31) = display3
                 } else if /* level = 5 */ (xor < (1 << 30)) {
                     if (depth == 5) {
                         display5 = new Array(33)
                         display5(0) = display4
                         depth += 1
                     }
                     display0 = new Array($treeBranchWidth)
                     display1 = new Array(${treeBranchWidth + treeInvariants} )
                     display2 = new Array(${treeBranchWidth + treeInvariants} )
                     display3 = new Array(${treeBranchWidth + treeInvariants} )
                     display4 = new Array(${treeBranchWidth + treeInvariants} )
                     display1((index >> 5) & 31) = display0
                     display2((index >> 10) & 31) = display1
                     display3((index >> 15) & 31) = display2
                     display4((index >> 20) & 31) = display3
                     display5((index >> 25) & 31) = display4
                 } else /* level < 0 || 5 < level */ {
                     throw new IllegalArgumentException()
                 }
             }
         """
    }


    private def copyDisplaysDef() = {
        def copyDisplaysDepth(d: Int) = {
            def copyDisplay(i: Int) = {
                val t = TermName(s"idx_$i")
                val indexDef =
                    if (i == d - 1) q"val $t = _focus >> ${5 * i}"
                    else q"val $t = (_focus >> ${5 * i}) & 31"
                setDisplay(i, q"$indexDef; copyOf(${displayAt(i)}, $t + 1, $t + 2)")

            }
            val displayCopies = (1 until d) map copyDisplay
            q"..$displayCopies"
        }

        val code = matchOnDepth(q"_depth", 1 to 6, copyDisplaysDepth)

        q"""
             private[immutable] final def $copyDisplays(_depth: Int, _focus: Int): Unit = {
                 ${asserts(q"assert(0 < _depth && _depth <= 6)")}
                 ${asserts(q"assert((_focus >> (5 * _depth)) == 0, (_depth, _focus))")}

                 $code
             }
         """
    }

    private def copyDisplaysTopDef() = {
        q"""
             private[immutable] final def $copyDisplaysTop(_currentDepth: Int, _focusRelax: Int): Unit = {
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
         """
    }


    private def stabilizeDef() = {
        def stabilizeDepth(depth: Int) = {
            def stabilizeDisplay(i: Int) = updateDisplay(i, q"(_focus >> ${5 * i}) & 31", displayAt(i - 1))
            val updates = (depth - 1 to 1 by -1) map stabilizeDisplay
            q"..$updates"
        }
        val code = matchOnDepth(q"_depth", 1 to 6, stabilizeDepth)

        q"""
         private[immutable] final def $stabilize(_depth: Int, _focus: Int): Unit =
         {
             ${asserts(q"assert(0 < _depth && _depth <= 6)")}
             ${asserts(q"assert((_focus >> (5 * _depth)) == 0, (_depth, _focus))")}
             $code
         }
         """
    }


    private def copyOfDef() = {
        val asserts = if (noAssertions) q""
        else q"""
                   assert(a != null)
                     assert(numElements <= newSize, (numElements, newSize))
                     assert(numElements <= a.length, (numElements, a.length))
                           """

        q"""
             private[immutable] final def $copyOf(a: Array[AnyRef], numElements: Int, newSize: Int) = {
                 $asserts
                 val b = new Array[AnyRef](newSize)
                 Platform.arraycopy(a, 0, b, 0, numElements)
                 b
             }
         """
    }

    private def closeTailLeafCode(): Unit = {
        q"""
             val cutIndex = ($focus & 31) + 1
             ${setDisplay(0, q"$copyOf($display0, cutIndex, cutIndex)")}
         """
    }

    private def mergeLeafsDef() = {
        val leaf0 = TermName("leaf0")
        val leaf1 = TermName("leaf1")
        val length0 = TermName("length0")
        val length1 = TermName("length1")
        val asserts = if (noAssertions) q"" else q"assert($length0 + $length1 <= $treeBranchWidth)"
        val code = mergeLeafsCode(leaf0, length0, leaf1, length1)
        q"""
             private[immutable] final def $mergeLeafs($leaf0: Array[AnyRef], $length0: Int, $leaf1: Array[AnyRef], $length1: Int): Array[AnyRef] = {
                 $asserts
                 $code
             }
         """
    }
}

