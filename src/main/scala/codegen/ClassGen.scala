package codegen


import scala.reflect.runtime.universe._

trait ClassGen {

    def generateClassDef(): Tree
}

trait MethodsGen {
    def generateMethods(): Seq[Tree]
}

