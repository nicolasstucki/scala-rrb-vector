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

        final def mapFun1(x: Int): Int = x % 3

        final def mapFun2(x: Int): Int = (x + 1) % 534564634

        final def mapFun3(x: Int): Int = (x * x) % 534564634


        final def vectorTypeName: String = "Int"
    }

    trait StringGenerator extends VectorGeneratorType[String] {

        @inline final def element(n: Int): String = n.toString

        final def mapSelfFun(x: String) = x

        final def mapBenchFun(x: String) = {
            mapBenchFunCompute(1).toString
        }

        final def mapFun1(x: String): String = {
            val split = x splitAt (x.length / 2)
            split._1 + split._2
        }

        final def mapFun2(x: String): String = s"${x.length + 10}"

        final def mapFun3(x: String): String = s"${x.hashCode}"

        final def vectorTypeName: String = "String"
    }

}
