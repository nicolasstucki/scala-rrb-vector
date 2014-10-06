package codegen.vectorbuilder

import codegen.VectorPackage
import codegen.vectorpointer._
import codegen.vectorclass._

import scala.reflect.runtime.universe._

trait VectorBuilderClass {
    self: VectorPackage with VectorBuilderCode with VectorPointerCode with VectorCode =>

    def generateVectorBuilderClass() = {
        q"""
            final class $vectorBuilderName[$A]()
                extends mutable.Builder[$A, $vectorClassName[$A]]
                with $vectorPointerClassName[$A @uncheckedVariance] {

                    $display0 = new Array[AnyRef]($treeBranchWidth)
                    $depth = 1
                    $hasWritableTail = true

                    private var $b_blockIndex = 0
                    private var $b_lo = 0

                    ${plusEqDef}
                    ${plusPlusEqDef}
                    ${resultDef}
                    ${clearDef}
            }
            """
    }


    private def plusEqDef = {
        q"""
            def $b_plusEq(elem: A): this.type = {
                if (lo >= $treeBranchWidth) {
                    val newBlockIndex = blockIndex + $treeBranchWidth
                    $gotoNextBlockStartWritable(newBlockIndex, newBlockIndex ^ $b_blockIndex)
                    $b_blockIndex = newBlockIndex
                    $b_lo = 0
                }

                $display0($b_lo) = elem.asInstanceOf[AnyRef]
                $b_lo += 1
                this
            }
        """
    }

    private def plusPlusEqDef = {
        q"""
           override def $b_plusPlusEq(xs: TraversableOnce[A]): this.type =
                super.++=(xs)
        """
    }

    private def resultDef = {
        q"""
            def $b_result(): $vectorClassName[A] = {
                val size = $b_blockIndex + $b_lo
                if (size == 0)
                    return $vectorObjectName.empty
                val vec = new $vectorClassName[A](size)

                vec.$initFrom(this)

                // TODO: Optimization: check if stabilization is really necessary on all displays based on the last index.
                val _depth = $depth
                if (_depth > 1) {
                    vec.$copyDisplays(_depth, size - 1)
                    if (_depth > 2)
                        vec.$stabilize(_depth, size - 1)
                }

                vec.$gotoPos(0, size - 1)
                vec.$focus = 0
                vec.$focusEnd = size
                vec.$focusDepth = _depth

                ${asserts(q"vec.$assertVectorInvariant()")}

                vec
            }
        """
    }

    private def clearDef = {
        def nullDisplays = (1 to 5) map (i => q"${displayNameAt(i)} = null")
        q"""
            def $b_clear(): Unit = {
                $display0 = new Array[AnyRef]($treeBranchWidth)
                ..$nullDisplays
                $depth = 1
                $b_blockIndex = 0
                $b_lo = 0
                $hasWritableTail = true
            }
        """
    }
}



