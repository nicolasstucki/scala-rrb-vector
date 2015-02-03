package scala.collection.immutable.fingertree

/**
 * Created by nicolasstucki on 03/02/15.
 * Specialized copy of the finger tree implementation: https://github.com/Sciss/FingerTree
 * Removed unnecessary abstractions for indexed sequences
 */

import collection.generic.CanBuildFrom
import language.higherKinds
import collection.generic.CanBuildFrom
import annotation.unchecked.{uncheckedVariance => uV}
import language.higherKinds

/** Variant of a finger tree which adds a measure. */
object FingerTree {
    def empty[A](): FingerTree[A] = new Empty(0)

    def apply[A](elems: A*): FingerTree[A] = {
        // TODO make this more efficient?
        // Maybe not worth the effort, the best we could do is
        // improve O(N logN) to become O(N).
        // However, it might be good for small trees of a few elements, saving some constant factor.
        // (We could overload apply with one, two, three and four element versions)
        var res = empty[A]
        elems.foreach(res :+= _)
        res
    }

    def one[A](a: A): FingerTree[A] = Single(a)

    def two[A](a: A, b: A): FingerTree[A] = {
        val prefix = One(a)
        val suffix = One(b)
        Deep(2, prefix, empty[Digit[A]], suffix)
    }


    // ---- functions ----

    private def concat[A](left: FingerTree[A], mid: List[A], right: FingerTree[A]): FingerTree[A] =
        (left, right) match {
            case (Empty(_), _) => mid.foldRight(right)((a, b) => a +: b)
            case (_, Empty(_)) => mid.foldLeft(left)((b, a) => b :+ a)
            case (Single(x), _) => x +: mid.foldRight(right)((a, b) => a +: b)
            case (_, Single(x)) => mid.foldLeft(left)((b, a) => b :+ a) :+ x
            case (ld@Deep(_, _, _, _), rd@Deep(_, _, _, _)) => deepConcat[A](ld, mid, rd)
        }

    private def deepConcat[A](left: Deep[A], mid: List[A], right: Deep[A]): FingerTree[A] = {

        def nodes(xs: List[A]): List[Digit[A]] = (xs: @unchecked) match {
            case a :: b :: Nil => Two(a, b) :: Nil
            case a :: b :: c :: Nil => Three(a, b, c) :: Nil
            case a :: b :: c :: d :: Nil => Two(a, b) :: Two(c, d) :: Nil
            case a :: b :: c :: tail => Three(a, b, c) :: nodes(tail)
        }

        val prd = left.prefix
        val tr = concat(left.tree, nodes(left.suffix.toList ::: mid ::: right.prefix.toList), right.tree)
        val sf = right.suffix
        Deep(prd.measure + tr.measure + sf.measure, prd, tr, sf)
    }

    private def deepLeft[A](pr: MaybeDigit[A], tr: FingerTree[Digit[A]], sf: Digit[A]): FingerTree[A] = {
        if (pr.isEmpty) {
            tr.viewLeft match {
                case ViewLeftCons(a, tr1) => Deep(a.measure + tr1.measure + sf.measure, a, tr1, sf)
                case _ => sf.toTree
            }
        } else {
            val prd = pr.get
            Deep(prd.measure + tr.measure + sf.measure, prd, tr, sf)
        }
    }

    private def deepRight[A](pr: Digit[A], tr: FingerTree[Digit[A]], sf: MaybeDigit[A])
    : FingerTree[A] = {
        if (sf.isEmpty) {
            tr.viewRight match {
                case ViewRightCons(tr1, a) => Deep(pr.measure + tr1.measure + a.measure, pr, tr1, a)
                case _ => pr.toTree
            }
        } else {
            val sfd = sf.get
            Deep(pr.measure + tr.measure + sfd.measure, pr, tr, sfd)
        }
    }

    // ---- Trees ----

    final private case class Single[+A](a: A) extends FingerTree[A] {
        def head = a

        override def measure = 1

        def headOption: Option[A] = Some(a)

        def last = a

        def lastOption: Option[A] = Some(a)

        def tail: Tree = empty[A]

        def init: Tree = empty[A]

        def isEmpty = false

        def +:[A1 >: A](b: A1): FingerTree[A1] = {
            val prefix = One(b)
            val suffix = One(a)
            Deep(2, prefix, empty[Digit[A1]], suffix)
        }

        def :+[A1 >: A](b: A1): FingerTree[A1] = {
            val prefix = One(a)
            val suffix = One(b)
            Deep(2, prefix, empty[Digit[A1]], suffix)
        }

        def ++[A1 >: A](right: FingerTree[A1]): FingerTree[A1] = a +: right

        def viewLeft: ViewLeft[A] = ViewLeftCons[A](a, empty[A])

        def viewRight: ViewRight[A] = ViewRightCons[A](empty[A], a)

        def span(pred: Int => Boolean): (Tree, Tree) = {
            val e = empty[A]
            if (pred(1)) {
                (this, e)
            } else {
                (e, this)
            }
        }

        def takeWhile(pred: Int => Boolean): Tree = {
            if (pred(1)) this else empty[A]
        }

        def dropWhile(pred: Int => Boolean): Tree = {
            if (pred(1)) empty[A] else this
        }

        def span1(pred: Int => Boolean): (Tree, A, Tree) = {
            val e = empty[A]
            (e, a, e)
        }

        def span1(pred: Int => Boolean, init: Int): (Tree, A, Tree) = {
            val e = empty[A]
            (e, a, e)
        }

        def takeWhile1(pred: Int => Boolean, init: Int): (Tree, A) = {
            (empty[A], a) // correct???
        }

        def dropWhile1(pred: Int => Boolean, init: Int): (A, Tree) = {
            (a, empty[A]) // correct???
        }

        def find1(pred: Int => Boolean): (Int, A) = find1(pred, 0)

        private[fingertree] def find1(pred: Int => Boolean, init: Int): (Int, A) = {
            val v1 = init + measure
            (if (pred(v1)) init else v1, a)
        }

        def toList: List[A] = a :: Nil

        def iterator: Iterator[A] = Iterator.single(a)

//        def to[Col[_]](implicit cbf: CanBuildFrom[Nothing, A, Col[A@uV]]): Col[A@uV] = {
//            val b = cbf.apply()
//            b += a
//            b.result()
//        }

        override def toString = "(" + a + ")"
    }

    final private case class Deep[+A](measure: Int,
                                      prefix: Digit[A],
                                      tree: FingerTree[Digit[A]],
                                      suffix: Digit[A])
      extends FingerTree[A] {

        def isEmpty = false

        def head = prefix.head

        def headOption: Option[A] = Some(prefix.head)

        def last = suffix.last

        def lastOption: Option[A] = Some(suffix.last)

        def tail: Tree = viewLeft.tail

        def init: Tree = viewRight.init

        def +:[A1 >: A](b: A1): FingerTree[A1] = {
            val vb = 1
            val vNew = vb + measure
            prefix match {
                case Four(d, e, f, g) =>
                    val prefix = Two(b, d)
                    val treeNew = tree.+:[Digit[A1]](Three(e, f, g))
                    Deep(vNew, prefix, treeNew, suffix)

                case partial =>
                    Deep(vNew, b +: partial, tree, suffix)
            }
        }

        def :+[A1 >: A](b: A1): FingerTree[A1] = {
            val vb = 1
            val vNew = measure + vb
            suffix match {
                case Four(g, f, e, d) =>
                    val treeNew = tree.:+[Digit[A1]](Three(g, f, e))
                    val suffix = Two(d, b)
                    Deep(vNew, prefix, treeNew, suffix)
                case partial =>
                    Deep(vNew, prefix, tree, partial :+ b)
            }
        }

        // we could use app3 with an empty middle argument, as hinze/paterson suggest, but let's
        // keep the simplified polymorphic ++ here for faster handling of empty and single cats.
        def ++[A1 >: A](right: FingerTree[A1]): FingerTree[A1] = right match {
            case Empty(_) => this
            case Single(a) => this :+ a
            case rd@Deep(_, _, _, _) => deepConcat[A1](this, Nil, rd)
        }

        def viewLeft: ViewLeft[A] =
            ViewLeftCons(prefix.head, deepLeft(prefix.tail, tree, suffix))

        def viewRight: ViewRight[A] =
            ViewRightCons(deepRight(prefix, tree, suffix.init), suffix.last)

        def span(pred: Int => Boolean): (Tree, Tree) =
            if (pred(measure)) {
                // split point lies after the last element of this tree
                (this, empty[A])
            } else {
                // predicate turns true inside the tree
                val (left, elem, right) = span1(pred, 0)
                (left, elem +: right)
            }

        def takeWhile(pred: Int => Boolean): Tree =
            if (pred(measure)) {
                // split point lies after the last element of this tree
                this
            } else {
                // predicate turns true inside the tree
                val (left, _) = takeWhile1(pred, 0)
                left
            }

        def dropWhile(pred: Int => Boolean): Tree =
            if (pred(measure)) {
                // split point lies after the last element of this tree
                empty[A]
            } else {
                // predicate turns true inside the tree
                val (elem, right) = dropWhile1(pred, 0)
                elem +: right
            }

        def span1(pred: Int => Boolean): (Tree, A, Tree) = span1(pred, 0)

        private[fingertree] def span1(pred: Int => Boolean, init: Int): (Tree, A, Tree) = {
            val vPrefix = init + prefix.measure
            if (pred(vPrefix)) {
                val vTree = vPrefix + tree.measure
                if (pred(vTree)) {
                    // in suffix
                    val (l, x, r) = suffix.span1(pred, vTree)
                    (deepRight(prefix, tree, l), x, r.toTree)
                } else {
                    // split point found in middle
                    val (ml, xs, mr) = tree.span1(pred, vPrefix)
                    val (l, x, r) = xs.span1(pred, vPrefix + ml.measure)
                    (deepRight(prefix, ml, l), x, deepLeft(r, mr, suffix))
                }
            } else {
                // split point found in prefix
                val (l, x, r) = prefix.span1(pred, init)
                (l.toTree, x, deepLeft(r, tree, suffix))
            }
        }

        def takeWhile1(pred: Int => Boolean, init: Int): (Tree, A) = {
            val vPrefix = init + prefix.measure
            if (pred(vPrefix)) {
                val vTree = vPrefix + tree.measure
                if (pred(vTree)) {
                    // in suffix
                    val (l, x) = suffix.takeWhile1(pred, vTree)
                    (deepRight(prefix, tree, l), x)
                } else {
                    // split point found in middle
                    val (ml, xs) = tree.takeWhile1(pred, vPrefix)
                    val (l, x) = xs.takeWhile1(pred, vPrefix + ml.measure)
                    (deepRight(prefix, ml, l), x)
                }
            } else {
                // split point found in prefix
                val (l, x) = prefix.takeWhile1(pred, init)
                (l.toTree, x)
            }
        }

        def dropWhile1(pred: Int => Boolean, init: Int): (A, Tree) = {
            val vPrefix = init + prefix.measure
            if (pred(vPrefix)) {
                val vTree = vPrefix + tree.measure
                if (pred(vTree)) {
                    // in suffix
                    val (x, r) = suffix.dropWhile1(pred, vTree)
                    (x, r.toTree)
                } else {
                    // split point found in middle
                    val (ml, xs, mr) = tree.span1(pred, vPrefix)
                    val (x, r) = xs.dropWhile1(pred, vPrefix + ml.measure)
                    (x, deepLeft(r, mr, suffix))
                }
            } else {
                // split point found in prefix
                val (x, r) = prefix.dropWhile1(pred, init)
                (x, deepLeft(r, tree, suffix))
            }
        }

        def find1(pred: Int => Boolean): (Int, A) = find1(pred, 0)

        private[fingertree] def find1(pred: Int => Boolean, init: Int): (Int, A) = {
            val vPrefix = init + prefix.measure
            if (pred(vPrefix)) {
                // found in prefix
                prefix.find1(pred, init)
            } else {
                val vTree = vPrefix + tree.measure
                if (pred(vTree)) {
                    // found in middle
                    val (vTreeLeft, xs) = tree.find1(pred, vPrefix)
                    xs.find1(pred, vTreeLeft)
                } else {
                    // in suffix
                    suffix.find1(pred, vTree)
                }
            }
        }

        def toList: List[A] = iterator.toList

        //        def to[Col[_]](implicit cbf: CanBuildFrom[Nothing, A, Col[A@uV]]): Col[A@uV] = iterator.to[Col]

        def iterator: Iterator[A] = {
            // Iterators compose nicely, ++ and flatMap are still lazy
            prefix.iterator ++ tree.iterator.flatMap(_.iterator) ++ suffix.iterator
        }

        override def toString = "(" + prefix + ", " + tree + ", " + suffix + ")"
    }

    final private case class Empty(measure: Int) extends FingerTree[Nothing] {
        def isEmpty = true

        def head = throw new NoSuchElementException("head of empty finger tree")

        def headOption: Option[Nothing] = None

        def last = throw new NoSuchElementException("last of empty finger tree")

        def lastOption: Option[Nothing] = None

        def tail: Tree =
            throw new UnsupportedOperationException("tail of empty finger tree")

        def init: Tree =
            throw new UnsupportedOperationException("init of empty finger tree")

        def +:[A1](a1: A1): FingerTree[A1] = Single(a1)

        def :+[A1](a1: A1): FingerTree[A1] = Single(a1)

        def ++[A1 >: Nothing](right: FingerTree[A1]): FingerTree[A1] = right

        def viewLeft: ViewLeft[Nothing] = ViewNil()

        def viewRight: ViewRight[Nothing] = ViewNil()

        def span(pred: Int => Boolean): (Tree, Tree) = (this, this)

        def takeWhile(pred: Int => Boolean): Tree = this

        def dropWhile(pred: Int => Boolean): Tree = this

        def span1(pred: Int => Boolean): (Tree, Nothing, Tree) =
            throw new UnsupportedOperationException("span1 on empty finger tree")

        private[fingertree] def span1(pred: Int => Boolean, init: Int): (Tree, Nothing, Tree) =
            throw new UnsupportedOperationException("span1 on empty finger tree")

        def takeWhile1(pred: Int => Boolean, init: Int): (Tree, Nothing) =
            throw new UnsupportedOperationException("takeWhile1 on empty finger tree")

        def dropWhile1(pred: Int => Boolean, init: Int): (Nothing, Tree) =
            throw new UnsupportedOperationException("dropWhile1 on empty finger tree")

        def find1(pred: Int => Boolean): Nothing =
            throw new UnsupportedOperationException("find1 on empty finger tree")

        private[fingertree] def find1(pred: Int => Boolean, init: Int): (Int, Nothing) =
            throw new UnsupportedOperationException("find1 on empty finger tree")

        def toList: List[Nothing] = Nil

        def iterator: Iterator[Nothing] = Iterator.empty

        def to[Col[_]](implicit cbf: CanBuildFrom[Nothing, Nothing, Col[Nothing]]): Col[Nothing] = cbf().result()

        override def toString = "()"
    }

    // ---- Views ----

    sealed trait ViewLike {
        def isEmpty: Boolean
    }

    sealed trait ViewLeft[+A] extends ViewLike {
        def head: A

        def tail: FingerTree[A]
    }

    sealed trait ViewRight[+A] extends ViewLike {
        def init: FingerTree[A]

        def last: A
    }

    final case class ViewLeftCons[A](head: A, tail: FingerTree[A]) extends ViewLeft[A] {
        def isEmpty = false
    }

    final case class ViewRightCons[A](init: FingerTree[A], last: A) extends ViewRight[A] {
        def isEmpty = false
    }

    final case class ViewNil() extends ViewLeft[Nothing] with ViewRight[Nothing] {
        private def notSupported(what: String) = throw new NoSuchElementException(what + " of empty view")

        def isEmpty = true

        def head: Nothing = notSupported("head")

        def last: Nothing = notSupported("last")

        def tail: FingerTree[Nothing] = notSupported("tail")

        def init: FingerTree[Nothing] = notSupported("init")
    }

    // ---- Digits ----

    private sealed trait MaybeDigit[+A] {
        protected[this] type Tree = FingerTree[A]

        def isEmpty: Boolean

        def toTree: Tree

        def get: Digit[A]
    }

    private final case class Zero() extends MaybeDigit[Nothing] {
        def isEmpty = true

        def toTree(): Tree = empty[Nothing]()

        def get = throw new UnsupportedOperationException("get")
    }

    private sealed trait Digit[+A] extends MaybeDigit[A] {
        /** It is an open question whether caching the measurements of digits is preferable or not. As Hinze and
          * Paterson write: "Because the length of the buffer is bounded by a constant, the number of ‘⊕’ operations
          * is also bounded. Another possibility is to cache the measure of a digit, adding to the cost of digit
          * construction but yielding a saving when computing the measure. The choice between these strategies
          * would depend on the expected balance of query and modification operations, but they would differ only
          * by a constant factor."
          *
          * The advantage of having the measurement stored (as we currently do) is that there is essentially no
          * difference between `Two` and `Node2` and `Three` and `Node3`, thus we use digits where Hinze and Paterson
          * use distinguished nodes.
          */

        def measure: Int

        def head: A

        def last: A

        def tail: MaybeDigit[A]

        def init: MaybeDigit[A]

        def +:[A1 >: A](b: A1): Digit[A1]

        def :+[A1 >: A](b: A1): Digit[A1]

        def find1(pred: Int => Boolean, init: Int): (Int, A)

        def span1(pred: Int => Boolean, init: Int): (MaybeDigit[A], A, MaybeDigit[A])

        def takeWhile1(pred: Int => Boolean, init: Int): (MaybeDigit[A], A)

        def dropWhile1(pred: Int => Boolean, init: Int): (A, MaybeDigit[A])

        // def toTree( implicit m: Measure[ A, V ]) : Tree

        def toList: List[A]

        def iterator: Iterator[A]
    }

    final private case class One[A](a1: A) extends Digit[A] {
        def isEmpty = false

        def measure = 1

        def get: Digit[A] = this

        def head = a1

        def last = a1

        def tail: MaybeDigit[A] = Zero()

        def init: MaybeDigit[A] = Zero()

        def +:[A1 >: A](b: A1): Digit[A1] = Two(b, a1)

        def :+[A1 >: A](b: A1): Digit[A1] = Two(a1, b)

        def find1(pred: Int => Boolean, init: Int): (Int, A) = {
            val v1 = init + 1
            (if (pred(v1)) init else v1, a1)
        }

        def span1(pred: Int => Boolean, init: Int): (MaybeDigit[A], A, MaybeDigit[A]) = {
            val e = Zero()
            (e, a1, e)
        }

        def takeWhile1(pred: Int => Boolean, init: Int): (MaybeDigit[A], A) = {
            (Zero(), a1) // correct???
        }

        def dropWhile1(pred: Int => Boolean, init: Int): (A, MaybeDigit[A]) = {
            (a1, Zero()) // correct???
        }

        def toTree: Tree = Single(a1)

        def toList: List[A] = a1 :: Nil

        def iterator: Iterator[A] = Iterator.single(a1)

        override def toString = "(" + a1 + ")"
    }

    final private case class Two[A](a1: A, a2: A) extends Digit[A] {
        def isEmpty = false

        def measure = 2

        def get: Digit[A] = this

        def head = a1

        def last = a2

        def tail: MaybeDigit[A] = One(a2)

        def init: MaybeDigit[A] = One(a1)

        def +:[A1 >: A](b: A1): Digit[A1] = Three(b, a1, a2)

        def :+[A1 >: A](b: A1): Digit[A1] = Three(a1, a2, b)

        def find1(pred: Int => Boolean, init: Int): (Int, A) = {
            val v1 = init + 1
            if (pred(v1)) (init, a1)
            else {
                val v12 = init + 2
                (if (pred(v12)) v1 else v12, a2)
            }
        }

        def span1(pred: Int => Boolean, init: Int): (MaybeDigit[A], A, MaybeDigit[A]) = {
            val v1 = init + 1
            val e = Zero()
            if (pred(v1)) {
                (One(a1), a2, e) // (a1), a2, ()
            } else {
                (e, a1, One(a2)) // (), a1, (a2)
            }
        }

        def takeWhile1(pred: Int => Boolean, init: Int): (MaybeDigit[A], A) = {
            val v1 = init + 1
            if (pred(v1)) {
                (One(a1), a2) // (a1), a2
            } else {
                val e = Zero()
                (e, a1) // (), a1
            }
        }

        def dropWhile1(pred: Int => Boolean, init: Int): (A, MaybeDigit[A]) = {
            val v1 = init + 1
            if (pred(v1)) {
                val e = Zero()
                (a2, e) // a2, ()
            } else {
                (a1, One(a2)) // a1, (a2)
            }
        }

        def toTree: Tree = {
            Deep(2, One(a1), empty[Digit[A]], One(a2))
        }

        def toList: List[A] = a1 :: a2 :: Nil

        def iterator: Iterator[A] = toList.iterator

        override def toString = "(" + a1 + ", " + a2 + ")"
    }

    final private case class Three[A](a1: A, a2: A, a3: A) extends Digit[A] {
        def isEmpty = false

        def measure = 3

        def get: Digit[A] = this

        def head = a1

        def last = a3

        def tail: MaybeDigit[A] = Two(a2, a3)

        def init: MaybeDigit[A] = Two(a1, a2)

        def +:[A1 >: A](b: A1): Digit[A1] =
            Four(b, a1, a2, a3)

        def :+[A1 >: A](b: A1): Digit[A1] =
            Four(a1, a2, a3, b)

        def find1(pred: Int => Boolean, init: Int): (Int, A) = {
            val v1 = init + 1
            if (pred(v1)) (init, a1)
            else {
                val v12 = v1 + 1
                if (pred(v12)) (v1, a2)
                else {
                    val v123 = init + 3
                    (if (pred(v123)) v12 else v123, a3)
                }
            }
        }

        def span1(pred: Int => Boolean, init: Int): (MaybeDigit[A], A, MaybeDigit[A]) = {
            val v1 = init + 1
            if (pred(v1)) {
                if (pred(v1 + 1)) {
                    (Two(a1, a2), a3, Zero()) // (a1, a2), a3, ()
                } else {
                    (One(a1), a2, One(a3)) // (a1), a2, (a3)
                }
            } else {
                (Zero(), a1, Two(a2, a3)) // (), a1, (a2, a3)
            }
        }

        def takeWhile1(pred: Int => Boolean, init: Int): (MaybeDigit[A], A) = {
            val v1 = init + 1
            if (pred(v1)) {
                if (pred(v1 + 1)) {
                    (Two(a1, a2), a3) // (a1, a2), a3
                } else {
                    (One(a1), a2) // (a1), a2
                }
            } else {
                // (), a1
                (Zero(), a1)
            }
        }

        def dropWhile1(pred: Int => Boolean, init: Int): (A, MaybeDigit[A]) = {
            val v1 = init + 1
            if (pred(v1)) {
                if (pred(v1 + 1)) {
                    (a3, Zero()) // a3, ()
                } else {
                    (a2, One(a3)) // a2, (a3)
                }
            } else {
                (a1, Two(a2, a3)) // a1, (a2, a3)
            }
        }

        def toTree: Tree = {
            Deep(measure, Two(a1, a2), empty[Digit[A]], One(a3))
        }

        def toList: List[A] = a1 :: a2 :: a3 :: Nil

        def iterator: Iterator[A] = toList.iterator

        override def toString = "(" + a1 + ", " + a2 + ", " + a3 + ")"
    }

    final private case class Four[A](a1: A, a2: A, a3: A, a4: A) extends Digit[A] {
        def isEmpty = false

        def measure = 4

        def get: Digit[A] = this

        def head = a1

        def last = a4

        def tail: MaybeDigit[A] =
            Three(a2, a3, a4)

        def init: MaybeDigit[A] =
            Three(a1, a2, a3)

        def +:[A1 >: A](b: A1) =
            throw new UnsupportedOperationException("+: on digit four")

        def :+[A1 >: A](b: A1) =
            throw new UnsupportedOperationException(":+ on digit four")

        def find1(pred: Int => Boolean, init: Int): (Int, A) = {
            val v1 = init + 1
            if (pred(v1)) (init, a1)
            else {
                val v12 = v1 + 1
                if (pred(v12)) (v1, a2)
                else {
                    val v123 = v12 + 1
                    if (pred(v123)) (v12, a3)
                    else {
                        val v1234 = init + 4
                        (if (pred(v1234)) v123 else v1234, a4)
                    }
                }
            }
        }

        def span1(pred: Int => Boolean, init: Int): (MaybeDigit[A], A, MaybeDigit[A]) = {
            val v1 = init + 1
            if (pred(v1)) {
                val v12 = v1 + 1
                if (pred(v12)) {
                    if (pred(v12 + 1)) {
                        (Three(a1, a2, a3), // (a1, a2, a3), a4, ()
                          a4,
                          Zero())
                    } else {
                        (Two(a1, a2), // (a1, a2), a3, (a4)
                          a3,
                          One(a4))
                    }
                } else {
                    (One(a1), // (a1), a2, (a3, a4)
                      a2,
                      Two(a3, a4))
                }
            } else {
                (Zero(), // (), a1, (a2, a3, a4)
                  a1,
                  Three(a2, a3, a4))
            }
        }

        def takeWhile1(pred: Int => Boolean, init: Int): (MaybeDigit[A], A) = {
            val v1 = init + 1
            if (pred(v1)) {
                val v12 = v1 + 1
                if (pred(v12)) {
                    if (pred(v12 + 1)) {
                        (Three(a1, a2, a3), a4) // (a1, a2, a3), a4
                    } else {
                        (Two(a1, a2), a3) // (a1, a2), a3
                    }
                } else {
                    (One(a1), a2) // (a1), a2
                }
            } else {
                (Zero(), a1) // (), a1
            }
        }

        def dropWhile1(pred: Int => Boolean, init: Int): (A, MaybeDigit[A]) = {
            val v1 = init + 1
            if (pred(v1)) {
                val v12 = v1 + 1
                if (pred(v12)) {
                    if (pred(v12 + 1)) {
                        (a4, Zero()) // a4, ()
                    } else {
                        (a3, One(a4)) // a3, (a4)
                    }
                } else {
                    (a2, Two(a3, a4)) // a2, (a3, a4)
                }
            } else {
                (a1, Three(a2, a3, a4)) // a1, (a2, a3, a4)
            }
        }

        def toTree: Tree = {
            Deep(measure, Two(a1, a2), empty[Digit[A]], Two(a3, a4))
        }

        def toList: List[A] = a1 :: a2 :: a3 :: a4 :: Nil

        def iterator: Iterator[A] = toList.iterator

        override def toString = "(" + a1 + ", " + a2 + ", " + a3 + ", " + a4 + ")"
    }

}

sealed trait FingerTree[+A] {

    import FingerTree._

    protected[this] type Tree = FingerTree[A]

    def measure: Int

    /** Queries whether the tree is empty or not
      *
      * @return  `true` if the tree is empty
      */
    def isEmpty: Boolean

    /** Returns the first (left-most) element in the tree. Throws a runtime exception if performed on an empty tree.
      *
      * @return  the head element
      */
    def head: A

    /** Returns the first (left-most) element in the tree as an option.
      *
      * @return  the head element (`Some`), or `None` if the tree is empty
      */
    def headOption: Option[A]

    /** Returns a copy of the tree with the first (head) element removed. Throws a runtime exception if performed
      * on an empty tree.
      *
      * @return  the new tree with the first element removed
      */
    def tail: Tree

    /** Returns the last (right-most) element in the tree. Throws a runtime exception if performed on an empty tree.
      *
      * @return  the last element
      */
    def last: A

    /** Returns the last (right-most) element in the tree as an option.
      *
      * @return  the last element (`Some`), or `None` if the tree is empty
      */
    def lastOption: Option[A]

    /** Drops the last element of the tree.
      *
      * @return  the tree where the last element has been removed
      */
    def init: Tree

    /** Prepends an element to the tree.
      *
      * @param b the element to prepend
      * @return  the new tree with the element prepended
      */
    def +:[A1 >: A](b: A1): FingerTree[A1]

    /** Appends an element to the tree.
      *
      * @param b the element to append
      * @return  the new tree with the element appended
      */
    def :+[A1 >: A](b: A1): FingerTree[A1]

    def ++[A1 >: A](right: FingerTree[A1]): FingerTree[A1]

    def viewLeft: ViewLeft[A]

    def viewRight: ViewRight[A]

    /** Creates an `Iterator` over the elements of the tree
      *
      * @return  a fresh `Iterator` for the tree elements
      */
    def iterator: Iterator[A]

    /** Converts the tree to a `List` representation.
      *
      * @return  a `List` constructed from the elements in the tree
      */
    def toList: List[A]

    //    def to[Col[_]](implicit cbf: CanBuildFrom[Nothing, A, Col[A@uV]]): Col[A@uV]

    /** Same as `span1`, but prepends the discerning element to the right tree, returning the left and right tree.
      * Unlike `span1`, this is an allowed operation on an empty tree.
      *
      * @param pred a test function applied to the elements of the tree from left to right, until a
      *             the test returns `false`.
      * @return  the split tree, as a `Tuple2` with the left and the right tree
      */
    def span(pred: Int => Boolean): (Tree, Tree)

    /** Traverses the tree until a predicate on an element becomes `false`, and then splits the tree,
      * returning the elements before that element (the prefix for which the predicate holds),
      * the element itself (the first for which the predicate does not hold), and the remaining elements.
      *
      * This method is somewhat analogous to the `span` method in standard Scala collections, the difference
      * being that the predicate tests the tree's measure and not individual elements.
      *
      * Note that the returned discerning element corresponds to the last element in the tree, if
      * `pred` returns `true` for every element (rather than a runtime exception being thrown).
      *
      * If the tree is empty, this throws a runtime exception.
      *
      * @param pred a test function applied to the elements of the tree from left to right, until a
      *             the test returns `true`.
      * @return  the split tree, as a `Tuple3` with the left tree, the discerning element, and the right tree
      */
    def span1(pred: Int => Boolean): (Tree, A, Tree)

    private[fingertree] def span1(pred: Int => Boolean, init: Int): (Tree, A, Tree)

    /** Traverses the tree until a predicate on an element becomes `true`, and then returns that
      * element. Note that if `pred` returns `false` for every element, the last element in the
      * tree is returned (rather than a runtime exception being thrown).
      *
      * If the tree is empty, this throws a runtime exception.
      *
      * @param pred a test function applied to the elements of the tree from left to right, until a
      *             the test returns `true`.
      * @return  the discerning element
      */
    def find1(pred: Int => Boolean): (Int, A)

    private[fingertree] def find1(pred: Int => Boolean, init: Int): (Int, A)

    //   /**
    //    * Appends two elements to the tree.
    //    *
    //    * @param b1 the first element to append (this will become the before-last element in the tree)
    //    * @param b2 the second element to append (this will become the last element in the tree)
    //    * @param m the measure used to update the tree's structure
    //    * @return  the new tree with the elements appended
    //    */
    //   def append2[ A1 >: A ]( b1: A1, b2: A1 )( implicit m: Measure[ A1, V ]) : FingerTree[ V, A1 ]

    def takeWhile(pred: Int => Boolean): Tree

    def dropWhile(pred: Int => Boolean): Tree

    def takeWhile1(pred: Int => Boolean, init: Int): (Tree, A)

    def dropWhile1(pred: Int => Boolean, init: Int): (A, Tree)
}

trait FingerTreeLike[A, Repr <: FingerTreeLike[A, Repr]] {

    final def iterator: Iterator[A] = tree.iterator

    final def isEmpty: Boolean = tree.isEmpty

    final def nonEmpty: Boolean = !isEmpty

    final def head: A = tree.head

    final def headOption: Option[A] = tree.headOption

    final def last: A = tree.last

    final def lastOption: Option[A] = tree.lastOption

    final def init: Repr = wrap(tree.init)

    final def tail: Repr = wrap(tree.tail)

    // final def foreach[ U ]( f: A => U ) { tree.foreach( f )}

    final def toList: List[A] = tree.toList

    //    final def to[Col[_]](implicit cbf: CanBuildFrom[Nothing, A, Col[A]]): Col[A] = tree.to[Col]

    // def toStream : Stream[ A ] = tree.toStream

    protected def tree: FingerTree[A]

    protected def wrap(tree: FingerTree[A]): Repr
}

