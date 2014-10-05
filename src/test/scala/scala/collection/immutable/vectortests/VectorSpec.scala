package scala.collection.immutable.vectortests

import org.scalatest._


abstract class VectorSpec[A, Vec <: IndexedSeq[A]] extends WordSpec {

    def element(n: Int): A

    def emptyVector: Vec

    def tabulatedVector(n: Int): Vec

    def plus(vec: Vec, elem: A): Vec

    def plus(elem: A, vec: Vec): Vec

    def plusPlus(vec1: Vec, vec2: Vec): Vec

    "The Vector companion object" should {

    }
    "A Vector" when {
        "empty" should {
            def vector = emptyVector
            "have size 0" in assertResult(0)(vector.size)
            "have length 0" in assertResult(0)(vector.length)
            "return true when isEmpty is invoked" in assertResult(true)(vector.isEmpty)
            "return false when nonEmpty is invoked" in assertResult(false)(vector.nonEmpty)
            "return false with any index when isDefinedAt is invoked" in {
                assertResult(false)(vector.isDefinedAt(-1))
                assertResult(false)(vector.isDefinedAt(0))
                assertResult(false)(vector.isDefinedAt(1))
            }
            "produce UnsupportedOperationException when head is invoked" in {
                intercept[UnsupportedOperationException](vector.head)
            }
            "produce UnsupportedOperationException when last is invoked" in {
                intercept[UnsupportedOperationException](vector.last)
            }
            "produce UnsupportedOperationException when init is invoked" in {
                intercept[UnsupportedOperationException](vector.init)
            }
            "produce UnsupportedOperationException when tail is invoked" in {
                intercept[UnsupportedOperationException](vector.tail)
            }
            "return -n for lengthCompare(n)" in {
                for (i <- -10 to 10)
                    assertResult(-i)(vector.lengthCompare(i))
            }
            "return an empty vector when drop is invoked" in {
                assertResult(vector)(vector.drop(-1))
                assertResult(vector)(vector.drop(0))
                assertResult(vector)(vector.drop(1))
            }
            "return an empty vector when dropRight is invoked" in {
                assertResult(vector)(vector.dropRight(-1))
                assertResult(vector)(vector.dropRight(0))
                assertResult(vector)(vector.dropRight(1))
            }
            "return an empty vector when take is invoked" in {
                assertResult(vector)(vector.take(-1))
                assertResult(vector)(vector.take(0))
                assertResult(vector)(vector.take(1))
            }
            "return an empty vector when takeRight is invoked" in {
                assertResult(vector)(vector.takeRight(-1))
                assertResult(vector)(vector.takeRight(0))
                assertResult(vector)(vector.takeRight(1))
            }
            "return an empty iterator" in {
                assert(vector.iterator.isEmpty)
            }
            "return an empty reverseIterator" in {
                assert(vector.reverseIterator.isEmpty)
            }
            "return an empty vector when slice is invoked" in {
                assertResult(vector)(vector.slice(0, 1))
                assertResult(vector)(vector.slice(0, -1))
                assertResult(vector)(vector.slice(0, 0))
                assertResult(vector)(vector.slice(0, 2))
            }
            "return a vector with exactly one element when :+ is invoked" in {
                def v = vector :+ element(42)
                assertResult(1)(v.length)
                assertResult(Vector(element(42)))(v)
            }
            "return a vector with exactly one element when +: is invoked" in {
                def v = element(42) +: vector
                assertResult(1)(v.length)
                assertResult(Vector(element(42)))(v)
            }
        }

        for (n <- Seq(1, 8, 16, 32, 33, 64, 65, 1024, 1025)) {
            s"contains $n elements" should {
                def vector = tabulatedVector(n)
                testNonEmptyVectorProperties(vector, n)
                s"return the i-th element of Vector.tabulate($n)(i => element(i)) should be i when apply is invoked" in {
                    for (i <- 0 until n) assertResult(element(i))(vector(i))
                }
                "reverseIterator yields the correct sequence of elements" in {
                    val it = vector.reverseIterator
                    for (i <- 0 until n) {
                        assert(it.hasNext)
                        assertResult(element(n - i - 1))(it.next())
                    }
                }
            }
        }
        "two vectors are concatenated" when {
            for (n <- Seq(1, 5, 8, 16, 17, 32, 33, 53, 64, 65, 1024, 1025)) {
                def left = tabulatedVector(n)
                s"left vector contains $n elements" when {
                    for (m <- Seq(1, 5, 8, 16, 17, 32, 33, 53, 64, 65, 1024, 1025)) {
                        def right = tabulatedVector(m)
                        s"right vector contains $m elements" should {
                            val vector = plusPlus(left, right)
                            testNonEmptyVectorProperties(vector, n + m)
                            "is equal (content/order wise) to appending all elements one by one" in {
                                var b = emptyVector
                                for (e <- left) b = plus(b, e)
                                for (e <- right) b = plus(b, e)
                                assert(b === vector)
                            }
                            "iterator is equal (content/order wise) to the concatenated iterators" in {
                                assert((vector.iterator zip (left.iterator ++ right.iterator)).forall { case (a, b) => a == b})
                            }
                            "reverseIterator is equal (content/order wise) to the concatenated reverseIterators" in {
                                assert((vector.reverseIterator zip (right.reverseIterator ++ left.reverseIterator)).forall { case (a, b) => a == b})
                            }
                        }
                    }
                }
            }
        }
        "vector generated by random concatenation of smaller ones" when {
//            implicit val config = RRBVectorGenerator.defaultConfig(element)

            //            for (i <- 1 to 1000) {
            //                val n = config.rnd.nextInt(i) + 1
            //                s"$i. vector of size $n" should {
            //                    val vector = RRBVectorGenerator.vectorOfSize(n)
            //                    testNonEmptyVectorProperties(vector, n)
            //                }
            //            }

        }

    }

    def testNonEmptyVectorProperties(vector: => Vec, n: Int) = {
        s"have size $n" in assertResult(n)(vector.size)
        s"have length $n" in assertResult(n)(vector.length)
        "return false when isEmpty is invoked" in assertResult(false)(vector.isEmpty)
        "return true when nonEmpty is invoked" in assertResult(true)(vector.nonEmpty)
        s"return true with any index from 0 until $n when isDefinedAt is invoked" in {
            for (i <- 0 until n) assertResult(true)(vector.isDefinedAt(i))
        }
        "return false with any negative index when isDefinedAt is invoked" in {
            for (i <- -5 until 0) assertResult(false)(vector.isDefinedAt(i))
        }
        s"return false with any index greater or equals to $n when isDefinedAt is invoked" in {
            for (i <- n until n + 5) assertResult(false)(vector.isDefinedAt(i))
        }
        "return a vector with exactly one additional element when :+ is invoked" in {
            def v = plus(vector, element(42))
            assertResult(n + 1)(v.length)
        }
        "return a vector with exactly one additional element when +: is invoked" in {
            def v = plus(element(42), vector)
            assertResult(n + 1)(v.length)
        }
    }

}
