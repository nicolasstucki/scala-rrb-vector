package scala.collection.immutable.vectorutils


trait VectorGeneratorType[A] {
    def element(n: Int): A

    def vectorTypeName: String
}

object VectorGeneratorType {

    trait IntGenerator extends VectorGeneratorType[Int] {

        @inline final def element(n: Int): Int = n

        final def mapSelfFun(x: Int) = x

        final def vectorTypeName: String = "Int"
    }

    trait StringGenerator extends VectorGeneratorType[String] {

        @inline final def element(n: Int): String = n.toString

        final def mapSelfFun(x: String) = x

        final def vectorTypeName: String = "String"
    }

}
