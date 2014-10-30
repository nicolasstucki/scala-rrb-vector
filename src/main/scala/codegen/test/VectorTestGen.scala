package codegen.test

import codegen.VectorProperties

import scala.reflect.runtime.universe._

trait VectorTestGen {
    self: VectorProperties =>

    def generateVectorTestClasses() = {
        val name = vectorClassName.toString

        inPackages(
            s"scala.collection.immutable.vectortests.generated".split('.'),
            q"""
                package $subpackage {
                    import scala.collection.immutable.vectorutils.generated.$subpackage._
                    import scala.collection.immutable.vectortests._
                    import scala.collection.immutable.vectorutils._

                    abstract class $vectorTestClassName[A] extends VectorSpec[A] with $vectorGeneratorClassName[A]

                    class ${TypeName(s"Int${name}Test")} extends $vectorTestClassName[Int] with VectorGeneratorType.IntGenerator

                    class ${TypeName(s"String${name}Test")} extends $vectorTestClassName[String] with VectorGeneratorType.StringGenerator
                }
             """)

    }
}
