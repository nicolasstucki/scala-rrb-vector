package codegen
package vector
package vectorpointer


import scala.reflect.runtime.universe._


trait VectorPointerMethodsGen {
    self: VectorPointerCodeGen with VectorProperties =>

    protected[vectorpointer] def generateVectorPointerMethods() = {
        val displays = (0 to maxTreeDepth) map (i => fieldDef(TermName(s"display$i"), tq"Array[AnyRef]"))

        var fields = Seq.empty[Tree]
        fields = fields :+ fieldDef(depth, tq"Int")
        fields = fields ++ Seq(focusStart, focusEnd, focusDepth, focus, focusRelax).map(tn => fieldDef(tn, tq"Int", Some(q"0")))
        fields = fields :+ q"private[immutable] def $endIndex: Int"

        val methods =
            Seq(initWithFocusFromDef, initFocusDef, initFromRootDef, initFromDef, initSingletonDef, rootDef,
                focusOnDef, getElementFromRootDef, getIndexInSizesDef, gotoPosFromRootDef, setupNewBlockInNextBranchDef,
                setupNewBlockInInitBranchDef, gotoPosDef, gotoNextBlockStartDef, gotoPrevBlockStartDef, gotoNextBlockStartWritableDef,
                normalizeDef, copyDisplaysDef, copyDisplaysAndNullFocusedBranchDef, copyDisplaysAndStabilizeDisplayPathDef,
                copyDisplaysTopDef, stabilizeDisplayPathDef, cleanTopTakeDef, cleanTopDropDef, copyOf1Def, copyOf3Def,
                copyOfAndNullDef, makeNewRoot0Def, makeNewRoot1Def, makeTransientSizesDef, copyAndIncRightRootDef,
                copyAndIncLeftRootDef, withComputedSizes1Def, withComputedSizesDef, withRecomputedSizesDef, notBalancedDef, treeSizeDef, getElementDef) ++
              ((0 to maxTreeLevel) map getElementIDef)

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
        val xorParam = TermName("xor")
        val transientParam = TermName("transient")
        val code = setupNewBlockInNextBranchCode(xorParam, transientParam)
        q"private[immutable] final def $setupNewBlockInNextBranch($xorParam: Int, $transientParam: Boolean): Unit = $code"
    }

    private def setupNewBlockInInitBranchDef = {
        val xorParam = TermName("insertionDepth")
        val transientParam = TermName("transient")
        val code = setupNewBlockInInitBranchCode(xorParam, transientParam)
        q"private[immutable] final def $setupNewBlockInInitBranch($xorParam: Int, $transientParam: Boolean): Unit = $code"
    }

    private def getElementDef: Tree = {
        val index = TermName("index")
        val xor = TermName("xor")
        val code = getElementCode(index, xor)
        q"private[immutable] final def $getElement($index: Int, $xor: Int): $A = $code"
    }

    private def getElementIDef(i: Int): Tree = {
        val block = TermName("block")
        val index = TermName("index")
        val code = getElementICode(i, block, index)
        q"private final def ${getElementI(i)}($block: Array[AnyRef], $index: Int): $A = $code"
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


    private[vectorpointer] def normalizeDef = {
        val depthParam = TermName("_depth")
        val code = normalizeCode(depthParam)
        q"private[immutable] final def $normalize($depthParam: Int): Unit = $code"
    }

    private[vectorpointer] def stabilizeDisplayPathDef = {
        val depthParam = TermName("_depth")
        val focusParam = TermName("_focus")
        val code = stabilizeDisplayPathCode(depthParam, focusParam)
        q"private[immutable] final def $stabilizeDisplayPath($depthParam: Int, $focusParam: Int): Unit = $code"
    }

    private[vectorpointer] def cleanTopTakeDef = {
        val cutIndex = TermName("cutIndex")
        val code = cleanTopTakeCode(cutIndex)
        q"private[immutable] def $cleanTopTake($cutIndex: Int): Unit = $code"
    }

    private[vectorpointer] def cleanTopDropDef = {
        val cutIndex = TermName("cutIndex")
        val code = cleanTopDropCode(cutIndex)
        q"private[immutable] def $cleanTopDrop($cutIndex: Int): Unit = $code"
    }


    private[vectorpointer] def copyDisplaysDef = {
        val depthParam = TermName("_depth")
        val focusParam = TermName("_focus")
        val code = copyDisplaysCode(q"$depthParam", focusParam)
        q"private[immutable] final def $copyDisplays($depthParam: Int, $focusParam: Int): Unit = $code"
    }

    private def copyDisplaysAndNullFocusedBranchDef = {
        val depthParam = TermName("_depth")
        val focusParam = TermName("_focus")
        val code = copyDisplaysAndNullFocusedBranchCode(depthParam, focusParam)
        q"private[immutable] final def $copyDisplaysAndNullFocusedBranch($depthParam: Int, $focusParam: Int): Unit = $code"
    }

    private def copyDisplaysAndStabilizeDisplayPathDef = {
        val depthParam = TermName("_depth")
        val focusParam = TermName("_focus")
        val code = copyDisplaysAndStabilizeDisplayPathCode(depthParam, focusParam)
        q"private[immutable] final def $copyDisplaysAndStabilizeDisplayPath($depthParam: Int, $focusParam: Int): Unit = $code"
    }


    private[vectorpointer] def copyDisplaysTopDef = {
        val currentDepth = TermName("currentDepth")
        val focusRelaxParam = TermName("_focusRelax")
        val code = copyDisplaysTopCode(currentDepth, focusRelaxParam)
        q"private[immutable] final def $copyDisplaysTop($currentDepth: Int, $focusRelaxParam: Int): Unit = $code"
    }


    private[vectorpointer] def copyOf1Def = {
        val array = TermName("array")
        val code = copyOfCode(q"$array")
        q"private[immutable] final def $copyOf($array: Array[AnyRef]) = $code"
    }

    private[vectorpointer] def copyOf3Def = {
        val array = TermName("array")
        val numElements = TermName("numElements")
        val newSize = TermName("newSize")
        val code = copyOfCode(q"$array", q"$numElements", q"$newSize")
        q"private[immutable] final def $copyOf($array: Array[AnyRef], $numElements: Int, $newSize: Int) = $code"
    }

    private[vectorpointer] def copyOfAndNullDef = {
        val arrayParam = TermName("array")
        val nullIndexParam = TermName("nullIndex")
        val code = copyOfAndNullCode(arrayParam, nullIndexParam)
        q"private[immutable] final def $copyOfAndNull($arrayParam: Array[AnyRef], $nullIndexParam: Int) = $code"
    }

    private[vectorpointer] def makeNewRoot0Def = {
        val nodeParam = TermName("node")
        val code = makeNewRoot0Code(nodeParam)
        q"private final def $makeNewRoot0($nodeParam: Array[AnyRef]) = $code"
    }

    private[vectorpointer] def makeNewRoot1Def = {
        val nodeParam = TermName("node")
        val currentDepthParam = TermName("currentDepth")
        val code = makeNewRoot1Code(nodeParam, currentDepthParam)
        q"private final def $makeNewRoot1($nodeParam: Array[AnyRef], $currentDepthParam: Int) = $code"
    }

    private[vectorpointer] def makeTransientSizesDef = {
        val oldSizesParam = TermName("oldSizesParam")
        val transientBranchIndexParam = TermName("transientBranchIndex")
        val code = makeTransientSizesCode(oldSizesParam, transientBranchIndexParam)
        q"private[immutable] final def $makeTransientSizes($oldSizesParam: Array[Int], $transientBranchIndexParam: Int) = $code"
    }

    private[vectorpointer] def copyAndIncRightRootDef = {
        val nodeParam = TermName("node")
        val transientParam = TermName("transient")
        val currentLevelParam = TermName("currentLevel")
        val code = copyAndIncRightRootCode(nodeParam, transientParam, currentLevelParam)
        q"private final def $copyAndIncRightRoot($nodeParam: Array[AnyRef], $transientParam: Boolean, $currentLevelParam: Int) = $code"
    }

    private[vectorpointer] def copyAndIncLeftRootDef = {
        val nodeParam = TermName("node")
        val transientParam = TermName("transient")
        val currentLevelParam = TermName("currentLevel")
        val code = copyAndIncLeftRootCode(nodeParam, transientParam, currentLevelParam)
        q"private final def $copyAndIncLeftRoot($nodeParam: Array[AnyRef], $transientParam: Boolean, $currentLevelParam: Int) = $code"
    }

    private[vectorpointer] def withComputedSizes1Def = {
        val nodeParam = TermName("node")
        val code = withComputedSizes1Code(nodeParam)
        q"private[immutable] final def $withComputedSizes1($nodeParam: Array[AnyRef]) = $code"
    }

    private[vectorpointer] def withComputedSizesDef = {
        val nodeParam = TermName("node")
        val currentDepthParam = TermName("currentDepth")
        val code = withComputedSizesCode(nodeParam, currentDepthParam)
        q"private[immutable] final def $withComputedSizes($nodeParam: Array[AnyRef], $currentDepthParam: Int) = $code"
    }

    private[vectorpointer] def withRecomputedSizesDef = {
        val nodeParam = TermName("node")
        val currentDepthParam = TermName("currentDepth")
        val branchToUpdateParam = TermName("branchToUpdate")
        val code = withRecomputedSizesCode(nodeParam, currentDepthParam, branchToUpdateParam)
        q"private final def $withRecomputedSizes($nodeParam: Array[AnyRef], $currentDepthParam: Int, $branchToUpdateParam: Int) = $code"
    }

    private[vectorpointer] def notBalancedDef = {
        val nodeParam = TermName("node")
        val sizesParam = TermName("sizes")
        val currentDepthParam = TermName("currentDepth")
        val endParam = TermName("end")
        val code = notBalancedCode(nodeParam, sizesParam, currentDepthParam, endParam)
        q"@inline private final def $notBalanced($nodeParam: Array[AnyRef], $sizesParam: Array[Int], $currentDepthParam: Int, $endParam: Int) = $code"
    }


    private[vectorpointer] def treeSizeDef = {
        val treeParam = TermName("tree")
        val currentDepthParam = TermName("currentDepth")
        val code = treeSizeCode(treeParam, currentDepthParam)
        q"private final def $treeSize($treeParam: Array[AnyRef], $currentDepthParam: Int) = $code"
    }


}

