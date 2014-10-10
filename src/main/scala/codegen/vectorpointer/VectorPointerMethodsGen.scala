package codegen
package vectorpointer


import codegen.VectorPackage

import scala.annotation.tailrec
import scala.reflect.runtime.universe._





trait VectorPointerMethodsGen extends MethodsGen {
    self: VectorPointerCodeGen with VectorPointerClassGen with VectorProperties =>

    def generateMethods() = {
        val displays = ((0 to 5) map (i => fieldDef(TermName(s"display$i"), tq"Array[AnyRef]")))
        val fields = fieldDef(depth, tq"Int") +: Seq(focusStart, focusEnd, focusDepth, focus, focusRelax).map(tn => fieldDef(tn, tq"Int", Some(q"0")))
        val methods = Seq(rootDef, initFromRootDef, initFromDef, initFromDef, initFocusDef, gotoIndexDef,
            allDisplaySizesDef, putDisplaySizesDef, gotoPosRelaxedDef, getElementDef, gotoPosDef, gotoNextBlockStartDef, gotoPrevBlockStartDef,
            setUpNextBlockStartTailWritableDef, gotoNextBlockStartWritableDef, copyDisplaysDef,
            copyDisplaysTopDef(), stabilizeDef(), cleanTopDef(), copyOfDef(), mergeLeafsDef())

        displays ++ fields ++ methods
    }

    private def fieldDef(name: TermName, typ: Tree, init: Option[Tree] = None) = init match {
        case Some(init) => q"private[immutable] var $name: $typ = $init"
        case None => q"private[immutable] var $name: $typ = _"
    }


    def rootDef() = {
        val code = rootCode(q"$depth")
        q"private[immutable] def $root(): AnyRef = $code"
    }


    private[vectorpointer] def initFromRootDef() = {
        val rootParam = TermName("root")
        val depthParam = TermName("_depth")
        val endIndexParam = TermName("_endIndex")
        val code = initFromRootCode(rootParam, depthParam, endIndexParam)
        q"private[immutable] def $initFromRoot($rootParam: Array[AnyRef], $depthParam: Int, $endIndexParam: Int): Unit = $code"
    }

    private[vectorpointer] def initFromDef() = {
        val that = TermName("that")
        val code = initFromCode(q"$that")
        q"private[immutable] def $initFrom[U]($that: $vectorPointerClassName[U]): Unit = $code"
    }

    private[vectorpointer] def initFocusDef() = {
        val focus = TermName("_focus")
        val focusStart = TermName("_focusStart")
        val focusEnd = TermName("_focusEnd")
        val focusDepth = TermName("_focusDepth")
        val focusRelax = TermName("_focusRelax")
        val code = initFocusCode(q"$focus", q"$focusStart", q"$focusEnd", q"$focusDepth", q"$focusRelax")
        q"private[immutable] final def $initFocus($focus: Int, $focusStart: Int, $focusEnd: Int, $focusDepth: Int, $focusRelax: Int) = $code"
    }

    private[vectorpointer] def gotoIndexDef() = {
        val index = TermName("index")
        val endIndex = TermName("endIndex")
        val code = gotoIndexCode(q"$index", q"$endIndex")
        q"private[immutable] final def $gotoIndex($index: Int, $endIndex: Int) = $code"
    }

    private[vectorpointer] def allDisplaySizesDef() = {
        val code = allDisplaySizesCode()
        q"private[immutable] def $allDisplaySizes(): Array[Array[Int]] = $code"
    }


    private[vectorpointer] def putDisplaySizesDef() = {
        val allSizes = TermName("allSizes")
        val code = putDisplaySizesCode(q"$allSizes")
        q"private[immutable] def $putDisplaySizes($allSizes: Array[Array[Int]]): Unit = $code"
    }


    private def gotoPosRelaxedDef() = {
        val indexParam = TermName("index")
        val startIndexParam = TermName("_startIndex")
        val endIndexParam = TermName("_endIndex")
        val depthParam = TermName("_depth")
        val focusRelaxParam = TermName("_focusRelax")
        val code = gotoPosRelaxedCode(q"$indexParam", q"$startIndexParam", q"$endIndexParam", q"$depthParam", q"$focusRelaxParam")
        q"@tailrec private[immutable] final def $gotoPosRelaxed($indexParam: Int, $startIndexParam: Int, $endIndexParam: Int, $depthParam: Int, $focusRelaxParam: Int = 0): Unit = $code"

    }

    private def getElementDef() = {
        val index = TermName("index")
        val xor = TermName("xor")
        val code = getElementCode(index, xor)
        q"private[immutable] final def $getElement($index: Int, $xor: Int): $A = $code"
    }

    private def gotoPosDef() = {
        val index = TermName("index")
        val xor = TermName("xor")
        val code = gotoPosCode(q"$index", q"$xor")
        q"private[immutable] final def $gotoPos($index: Int, $xor: Int): Unit = $code"
    }

    private def gotoNextBlockStartDef() = {
        val index = TermName("index")
        val xor = TermName("xor")
        val code = gotoNextBlockStartCode(q"$index", q"$xor")
        q"private[immutable] final def $gotoNextBlockStart($index: Int, $xor: Int): Unit = $code"
    }


    private def gotoPrevBlockStartDef() = {
        val index = TermName("index")
        val xor = TermName("xor")
        val code = gotoPrevBlockStartCode(q"$index", q"$xor")
        q"private[immutable] final def $gotoPrevBlockStart($index: Int, $xor: Int): Unit = $code"
    }


    private def setUpNextBlockStartTailWritableDef() = {
        val index = TermName("index")
        val xor = TermName("xor")
        val code = setUpNextBlockStartTailWritableCode(q"$index", q"$xor")
        q"private[immutable] final def $setUpNextBlockStartTailWritable($index: Int, $xor: Int): Unit = $code"
    }


    private def gotoNextBlockStartWritableDef() = {
        val index = TermName("index")
        val xor = TermName("xor")
        val code = gotoNextBlockStartWritableCode(q"$index", q"$xor")
        q"private[immutable] final def $gotoNextBlockStartWritable($index: Int, $xor: Int): Unit = $code"
    }


    private[vectorpointer] def copyDisplaysDef() = {
        val depthParam = TermName("_depth")
        val focusParam = TermName("_focus")
        val code = copyDisplaysCode(q"$depthParam", q"$focusParam")
        //        ${asserts(q"assert(0 < _depth && _depth <= 6)")}
        //        ${asserts(q"assert((_focus >> (5 * _depth)) == 0, (_depth, _focus))")}
        q"private[immutable] final def $copyDisplays($depthParam: Int, $focusParam: Int): Unit = $code"
    }

    private[vectorpointer] def copyDisplaysTopDef() = {
        val currentDepth = TermName("currentDepth")
        val focusRelaxParam = TermName("_focusRelax")
        val code = copyDisplaysTopCode(q"$currentDepth", q"$focusRelaxParam")
        q"private[immutable] final def $copyDisplaysTop($currentDepth: Int, $focusRelaxParam: Int): Unit = $code"
    }

    private[vectorpointer] def cleanTopDef() = {
        val cutIndex = TermName("cutIndex")
        val code = cleanTopCode(q"$cutIndex")
        q"private[immutable] def $cleanTop($cutIndex: Int): Unit = $code"
    }


    private[vectorpointer] def stabilizeDef() = {
        val depthParam = TermName("_depth")
        val focusParam = TermName("_focus")
        val code = stabilizeCode(q"$depthParam", q"$focusParam")
        //asserts(q"assert(0 < _depth && _depth <= 6)")
        // asserts(q"assert((_focus >> (5 * _depth)) == 0, (_depth, _focus))")
        q"private[immutable] final def $stabilize($depthParam: Int, $focusParam: Int): Unit = $code"
    }


    private[vectorpointer] def copyOfDef() = {
        //        val asserts = if (useAssertions) q""
        //        else q"""
        //        assert(a != null)
        //        assert(numElements <= newSize, (numElements, newSize))
        //        assert(numElements <= a.length, (numElements, a.length))
        //        """
        val array = TermName("array")
        val numElements = TermName("numElements")
        val newSize = TermName("newSize")
        val code = copyOfCode(q"$array", q"$numElements", q"$newSize")
        q"private[immutable] final def $copyOf($array: Array[AnyRef], $numElements: Int, $newSize: Int) = $code"
    }


    def mergeLeafsDef() = {
        val leaf0 = TermName("leaf0")
        val leaf1 = TermName("leaf1")
        val length0 = TermName("length0")
        val length1 = TermName("length1")
        // val asserts = if (useAssertions) q"assert($length0 + $length1 <= $blockWidth)" else q""
        val code = mergeLeafsCode(q"$leaf0", q"$length0", q"$leaf1", q"$length1")
        q"private[immutable] final def $mergeLeafs($leaf0: Array[AnyRef], $length0: Int, $leaf1: Array[AnyRef], $length1: Int): Array[AnyRef] = $code"
    }
}

