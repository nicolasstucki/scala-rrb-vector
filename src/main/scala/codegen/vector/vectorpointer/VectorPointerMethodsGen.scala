package codegen
package vector
package vectorpointer


import scala.reflect.runtime.universe._


trait VectorPointerMethodsGen {
    self: VectorPointerCodeGen with VectorProperties =>

    protected[vectorpointer] def generateVectorPointerMethods() = {
        val displays = (0 to 5) map (i => fieldDef(TermName(s"display$i"), tq"Array[AnyRef]"))

        var fields = Seq.empty[Tree]
        fields = fields :+ fieldDef(depth, tq"Int")
        fields = fields ++ Seq(focusStart, focusEnd, focusDepth, focus, focusRelax).map(tn => fieldDef(tn, tq"Int", Some(q"0")))
        fields = fields :+ q"private[immutable] def $endIndex: Int"

        val methods = Seq(initWithFocusFromDef, initFocusDef, initFromRootDef, initFromDef, initSingletonDef, rootDef,
            focusOnDef, getElementFromRootDef, getIndexInSizesDef, gotoPosFromRootDef, setupNewBlockInNextBranchDef,
            getElementDef, gotoPosDef, gotoNextBlockStartDef, gotoPrevBlockStartDef, gotoNextBlockStartWritableDef,
            stabilizeDef, copyDisplaysDef, copyDisplaysTopDef, stabilizeDisplayPathDef, cleanTopDef, copyOfDef)

        displays ++ fields ++ methods
    }

    private def fieldDef(name: TermName, typ: Tree, initOption: Option[Tree] = None) = initOption match {
        case Some(init) => q"private[immutable] final var $name: $typ = $init"
        case None => q"private[immutable] final var $name: $typ = _"
    }

    private[vectorpointer] def initWithFocusFromDef = {
        val thatParam = TermName("that")
        val code = initWithFocusFromCode(thatParam)
        q"private[immutable] final def $initWithFocusFrom[U]($thatParam: $vectorPointerClassName[U]): Unit = $code"
    }

    private[vectorpointer] def initFocusDef = {
        val focusParam = TermName("focus")
        val focusStartParam = TermName("focusStart")
        val focusEndParam = TermName("focusEnd")
        val focusDepthParam = TermName("focusDepth")
        val focusRelaxParam = TermName("focusRelax")
        val code = initFocusCode(focusParam, focusStartParam, focusEndParam, focusDepthParam, focusRelaxParam)
        q"private[immutable] final def $initFocus[U]($focusParam: Int, $focusStartParam: Int, $focusEndParam: Int, $focusDepthParam: Int, $focusRelaxParam: Int ): Unit = $code"
    }

    private[vectorpointer] def initFromRootDef = {
        val rootParam = TermName("root")
        val depthParam = TermName("depth")
        val code = initFromRootCode(rootParam, depthParam)
        q"private[immutable] final def $initFromRoot($rootParam: Array[AnyRef], $depthParam: Int): Unit = $code"
    }

    private[vectorpointer] def initFromDef = {
        val thatParam = TermName("that")
        val code = initFromCode(thatParam)
        q"private[immutable] final def $initFrom[U]($thatParam: $vectorPointerClassName[U]): Unit = $code"
    }

    private[vectorpointer] def initSingletonDef = {
        val elemParam = TermName("elem")
        val code = initSingletonCode(elemParam)
        q"private[immutable] final def $initSingleton[$B >: $A]($elemParam: $B): Unit = $code"
    }

    private[vectorpointer] def rootDef = {
        val code = rootCode(q"$depth")
        q"private[immutable] final def $root(): AnyRef = $code"
    }

    private[vectorpointer] def focusOnDef = {
        val indexParam = TermName("index")
        val code = focusOnCode(indexParam)
        q"private[immutable] final def $focusOn($indexParam: Int): Unit = $code"
    }

    private[vectorpointer] def getElementFromRootDef = {
        val indexParam = TermName("index")
        val code = getElementFromRootCode(indexParam)
        q"private[immutable] final def $getElementFromRoot($indexParam: Int): $A = $code"
    }


    private[vectorpointer] def getIndexInSizesDef = {
        val sizes = TermName("sizes")
        val indexInSubTree = TermName("indexInSubTree")
        val code = getIndexInSizesCode(sizes, indexInSubTree)
        q"final private def $getIndexInSizes($sizes: Array[Int], $indexInSubTree: Int): Int = $code"
    }

    private[vectorpointer] def gotoPosFromRootDef = {
        val indexParam = TermName("index")
        val code = gotoPosFromRootCode(indexParam)
        q"private[immutable] final def $gotoPosFromRoot($indexParam: Int): Unit = $code"
    }


    private[vectorpointer] def setupNewBlockInNextBranchDef: Tree = {
        val indexParam = TermName("index")
        val xorParam = TermName("xor")
        val code = setupNewBlockInNextBranchCode(indexParam, xorParam)
        q"private[immutable] final def $setupNewBlockInNextBranch($indexParam: Int, $xorParam: Int): Unit = $code"
    }

    private def getElementDef: Tree = {
        val index = TermName("index")
        val xor = TermName("xor")
        val code = getElementCode(index, xor)
        q"private[immutable] final def $getElement($index: Int, $xor: Int): $A = $code"
    }

    private def gotoPosDef: Tree = {
        val index = TermName("index")
        val xor = TermName("xor")
        val code = gotoPosCode(index, xor)
        q"private[immutable] final def $gotoPos($index: Int, $xor: Int): Unit = $code"
    }

    private def gotoNextBlockStartDef: Tree = {
        val index = TermName("index")
        val xor = TermName("xor")
        val code = gotoNextBlockStartCode(index, xor)
        q"private[immutable] final def $gotoNextBlockStart($index: Int, $xor: Int): Unit = $code"
    }

    private def gotoPrevBlockStartDef = {
        val index = TermName("index")
        val xor = TermName("xor")
        val code = gotoPrevBlockStartCode(index, xor)
        q"private[immutable] final def $gotoPrevBlockStart($index: Int, $xor: Int): Unit = $code"
    }

    private def gotoNextBlockStartWritableDef = {
        val index = TermName("index")
        val xor = TermName("xor")
        val code = gotoNextBlockStartWritableCode(index, xor)
        q"private[immutable] final def $gotoNextBlockStartWritable($index: Int, $xor: Int): Unit = $code"
    }


    private[vectorpointer] def stabilizeDef = {
        val code = stabilizeCode()
        q"private[immutable] final def $stabilize(): Unit = $code"
    }

    private[vectorpointer] def stabilizeDisplayPathDef = {
        val depthParam = TermName("_depth")
        val focusParam = TermName("_focus")
        val code = stabilizeDisplayPathCode(depthParam, focusParam)
        q"private[immutable] final def $stabilizeDisplayPath($depthParam: Int, $focusParam: Int): Unit = $code"
    }

    private[vectorpointer] def cleanTopDef = {
        val cutIndex = TermName("cutIndex")
        val code = cleanTopCode(cutIndex)
        q"private[immutable] def $cleanTop($cutIndex: Int): Unit = $code"
    }


    private[vectorpointer] def copyDisplaysDef = {
        val depthParam = TermName("_depth")
        val focusParam = TermName("_focus")
        val code = copyDisplaysCode(q"$depthParam", focusParam)
        q"private[immutable] final def $copyDisplays($depthParam: Int, $focusParam: Int): Unit = $code"
    }

    private[vectorpointer] def copyDisplaysTopDef = {
        val currentDepth = TermName("currentDepth")
        val focusRelaxParam = TermName("_focusRelax")
        val code = copyDisplaysTopCode(currentDepth, focusRelaxParam)
        q"private[immutable] final def $copyDisplaysTop($currentDepth: Int, $focusRelaxParam: Int): Unit = $code"
    }


    private[vectorpointer] def copyOfDef = {
        val array = TermName("array")
        val numElements = TermName("numElements")
        val newSize = TermName("newSize")
        val code = copyOfCode(q"$array", q"$numElements", q"$newSize")
        q"private[immutable] final def $copyOf($array: Array[AnyRef], $numElements: Int, $newSize: Int) = $code"
    }
}

