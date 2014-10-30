package scala.collection.immutable.vectorutils


trait VectorGeneratorType[A] {
    def element(n: Int): A

    def vectorTypeName: String
}

object VectorGeneratorType {

    trait IntGenerator extends VectorGeneratorType[Int]{

        @inline final def element(n: Int): Int = n

        def vectorTypeName: String = "Int"
    }

    trait StringGenerator extends VectorGeneratorType[String]{

        @inline final def element(n: Int): String = n.toString

        def vectorTypeName: String = "String"
    }

}
