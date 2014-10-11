package codegen.vectorclass

import codegen.vectorpointer.VectorPointerCodeGen
import codegen.VectorProperties


import scala.reflect.runtime.universe._

trait VectorCodeGen {
    self: VectorProperties with VectorPointerCodeGen =>

    //
    // Field names
    //

    val v_endIndex = TermName("endIndex")

    //
    // Method names
    //

    // SeqLike
    final val v_apply = TermName("apply")

    // IterableLike
    final val v_isEmpty = TermName("isEmpty")
    final val v_head = TermName("head")
    final val v_take = TermName("take")
    final val v_drop = TermName("drop")
    final val v_dropRight = TermName("dropRight")
    final val v_slice = TermName("slice")
    final val v_splitAt = TermName("splitAt")

    // TraversableLike
    final val v_tail = TermName("tail")
    final val v_last = TermName("last")
    final val v_init = TermName("init")

    // Private
    val v_appendedBack = TermName("appendedBack")
    val v_takeFront0 = TermName("takeFront0")

    val v_concatenated = TermName("concatenated")
    val v_rebalanced = TermName("rebalanced")
    val v_copiedAcross = TermName("copiedAcross")
    val v_computeNewSizes = TermName("computeNewSizes")
    val v_withComputedSizes = TermName("withComputedSizes")
    val v_treeSize = TermName("treeSize")

    val v_assertVectorInvariant = TermName("rebalanced")

    //
    // Method definitions
    //


}
