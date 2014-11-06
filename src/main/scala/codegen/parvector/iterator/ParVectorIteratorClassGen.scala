package codegen
package parvector
package iterator


import scala.reflect.runtime.universe._

trait ParVectorIteratorClassGen {
    self: ParVectorIteratorMethodsGen with VectorProperties with ParVectorIteratorCodeGen =>

    def generateParVectorIteratorClassDef(): Tree = {
        q"""
            class $parVectorIteratorClassName($pit_start: Int, $pit_end: Int) extends $vectorIteratorClassName[$A]($pit_start, $pit_end) with SeqSplitter[$A] {
                ..${generateParVectorIteratorMethods()}
            }
         """
    }
}
