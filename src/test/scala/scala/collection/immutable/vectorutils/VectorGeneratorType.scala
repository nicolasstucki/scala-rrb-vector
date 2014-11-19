package scala.collection.immutable.vectorutils


trait VectorGeneratorType[A] {
    def element(n: Int): A

    def vectorTypeName: String


    protected def mapBenchFunCompute(): Int = {
        var y = 5436456
        var i = 0
        while (i < 10000) {
            y = java.lang.Integer.reverse(y)
            i += 1
        }
        y
    }
}

object VectorGeneratorType {

    trait IntGenerator extends VectorGeneratorType[Int] {

        @inline final def element(n: Int): Int = n

        final def mapSelfFun(x: Int) = x

        final def mapBenchFun(x: Int) = {
            x + mapBenchFunCompute()
        }


        final def vectorTypeName: String = "Int"
    }

    trait StringGenerator extends VectorGeneratorType[String] {

        @inline final def element(n: Int): String = n.toString

        final def mapSelfFun(x: String) = x

        final def mapBenchFun(x: String) = {
            x + mapBenchFunCompute()
        }

        final def vectorTypeName: String = "String"
    }

}
