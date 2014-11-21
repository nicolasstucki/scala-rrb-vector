package scala.collection.immutable.vectorutils


trait VectorGeneratorType[A] {
    def element(n: Int): A

    def vectorTypeName: String

    protected final def mapBenchFunCompute(x: Int): Int = {
        Math.cos(3.1415 + Math.cos(1.0 + Math.sin(2.0 + Math.cos(Math.sin(Math.sin(Math.cos(x + 1.0))))))).toInt
    }
}

object VectorGeneratorType {

    trait IntGenerator extends VectorGeneratorType[Int] {

        @inline final def element(n: Int): Int = n

        final def mapSelfFun(x: Int) = x

        final def mapBenchFun(x: Int) = {
            mapBenchFunCompute(1)
        }


        final def vectorTypeName: String = "Int"
    }

    trait StringGenerator extends VectorGeneratorType[String] {

        @inline final def element(n: Int): String = n.toString

        final def mapSelfFun(x: String) = x

        final def mapBenchFun(x: String) = {
            mapBenchFunCompute(1).toString
        }

        final def vectorTypeName: String = "String"
    }

}
