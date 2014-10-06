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

    trait AnyRefGenerator extends VectorGeneratorType[AnyRef]{

        @inline final def element(n: Int): AnyRef = n.toString.asInstanceOf[AnyRef]

        def vectorTypeName: String = "AnyRef"
    }

}
