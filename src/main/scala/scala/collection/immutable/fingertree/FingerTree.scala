package scala.collection.immutable.fingertree

/**
 *
 * Specialized version of https://github.com/Sciss/FingerTree
 * Removed abstractions to focus on indexed sequences
 */


import annotation.unchecked.{uncheckedVariance => uV}
import language.higherKinds

/** Variant of a finger tree which adds a measure. */
object FingerTree {

    def empty[A](implicit m: Measure[A]): FingerTree[A] = Empty

    def apply[A](elems: A*)(implicit m: Measure[A]): FingerTree[A] = {
        // TODO make this more efficient?
        // Maybe not worth the effort, the best we could do is
        // improve O(N logN) to become O(N).
        // However, it might be good for small trees of a few elements, saving some constant factor.
        // (We could overload apply with one, two, three and four element versions)
        var res = empty[A]
        elems.foreach(res :+= _)
        res
    }

    def one[V, A](a: A)(implicit m: Measure[A]): FingerTree[A] = Single(m(a), a)

    def two[V, A](a: A, b: A)(implicit m: Measure[A]): FingerTree[A] = {
        val vPrefix = m(a)
        val prefix = One(vPrefix, a)
        val vSuffix = m(b)
        val suffix = One(vSuffix, b)
        Deep(vPrefix + vSuffix, prefix, empty[Digit[A]], suffix)
    }

    implicit private def digitMeasure[A](implicit m: Measure[A]): Measure[Digit[A]] = new DigitMeasure(m)

    private final class DigitMeasure[A](m: Measure[A]) extends Measure[Digit[A]] {

        def apply(n: Digit[A]): Int = n.measure
    }

    // ---- functions ----

    private def concat[A](left: FingerTree[A], mid: List[A], right: FingerTree[A])
                         (implicit m: Measure[A]): FingerTree[A] =
        (left, right) match {
            case (Empty, _) => mid.foldRight(right)((a, b) => a +: b)
            case (_, Empty) => mid.foldLeft(left)((b, a) => b :+ a)
            case (Single(_, x), _) => x +: mid.foldRight(right)((a, b) => a +: b)
            case (_, Single(_, x)) => mid.foldLeft(left)((b, a) => b :+ a) :+ x
            case (ld@Deep(_, _, _, _), rd@Deep(_, _, _, _)) => deepConcat[A](ld, mid, rd)
        }

    private def deepConcat[A](left: Deep[A], mid: List[A], right: Deep[A])
                             (implicit m: Measure[A]): FingerTree[A] = {

        def nodes(xs: List[A]): List[Digit[A]] = (xs: @unchecked) match {
            case a :: b :: Nil => Two((m(a) + m(b)), a, b) :: Nil
            case a :: b :: c :: Nil => Three((m(a) + m(b) + m(c)), a, b, c) :: Nil
            case a :: b :: c :: d :: Nil => Two((m(a) + m(b)), a, b) ::
              Two((m(c) + m(d)), c, d) :: Nil
            case a :: b :: c :: tail => Three((m(a) + m(b) + m(c)), a, b, c) :: nodes(tail)
        }

        val prd = left.prefix
        val tr = concat(left.tree, nodes(left.suffix.toList ::: mid ::: right.prefix.toList), right.tree)
        val sf = right.suffix
        Deep((prd.measure + tr.measure + sf.measure), prd, tr, sf)
    }

    private def deepLeft[A](pr: MaybeDigit[A], tr: FingerTree[Digit[A]], sf: Digit[A])
                           (implicit m: Measure[A]): FingerTree[A] = {
        if (pr.isEmpty) {
            tr.viewLeft match {
                case ViewLeftCons(a, tr1) => Deep((a.measure + tr1.measure + sf.measure), a, tr1, sf)
                case _ => sf.toTree
            }
        } else {
            val prd = pr.get
            Deep((prd.measure + tr.measure + sf.measure), prd, tr, sf)
        }
    }

    private def deepRight[A](pr: Digit[A], tr: FingerTree[Digit[A]], sf: MaybeDigit[A])
                            (implicit m: Measure[A]): FingerTree[A] = {
        if (sf.isEmpty) {
            tr.viewRight match {
                case ViewRightCons(tr1, a) => Deep((pr.measure + tr1.measure + a.measure), pr, tr1, a)
                case _ => pr.toTree
            }
        } else {
            val sfd = sf.get
            Deep((pr.measure + tr.measure + sfd.measure), pr, tr, sfd)
        }
    }

    // ---- Trees ----

    final private case class Single[A](measure: Int, a: A) extends FingerTree[A] {
        def head = a

        def headOption: Option[A] = Some(a)

        def last = a

        def lastOption: Option[A] = Some(a)

        def tail(implicit m: Measure[A]): Tree = empty[A]

        def init(implicit m: Measure[A]): Tree = empty[A]

        def isEmpty = false

        def +:[A1 >: A](b: A1)(implicit m: Measure[A1]): FingerTree[A1] = {
            val vPrefix = m(b)
            val prefix = One(vPrefix, b)
            val suffix = One(measure, a)
            Deep((vPrefix + measure), prefix, empty[Digit[A1]], suffix)
        }

        def :+[A1 >: A](b: A1)(implicit m: Measure[A1]): FingerTree[A1] = {
            val prefix = One(measure, a)
            val vSuffix = m(b)
            val suffix = One(vSuffix, b)
            Deep((measure + vSuffix), prefix, empty[Digit[A1]], suffix)
        }

        def ++[A1 >: A](right: FingerTree[A1])(implicit m: Measure[A1]): FingerTree[A1] = a +: right

        def viewLeft(implicit m: Measure[A]): ViewLeft[A] = ViewLeftCons[A](a, empty[A])

        def viewRight(implicit m: Measure[A]): ViewRight[A] = ViewRightCons[A](empty[A], a)

        def span(pred: Int => Boolean)(implicit m: Measure[A]): (Tree, Tree) = {
            val e = empty[A]
            if (pred(m(a))) {
                (this, e)
            } else {
                (e, this)
            }
        }

        def takeWhile(pred: Int => Boolean)(implicit m: Measure[A]): Tree = {
            if (pred(m(a))) this else empty[A]
        }

        def dropWhile(pred: Int => Boolean)(implicit m: Measure[A]): Tree = {
            if (pred(m(a))) empty[A] else this
        }

        def span1(pred: Int => Boolean)(implicit m: Measure[A]): (Tree, A, Tree) = {
            val e = empty[A]
            (e, a, e)
        }

        def span1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Tree, A, Tree) = {
            val e = empty[A]
            (e, a, e)
        }

        def takeWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Tree, A) = {
            (empty[A], a) // correct???
        }

        def dropWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (A, Tree) = {
            (a, empty[A]) // correct???
        }

        def find1(pred: Int => Boolean)(implicit m: Measure[A]): (Int, A) = find1(pred, 0)

        private[fingertree] def find1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Int, A) = {
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

        def tail(implicit m: Measure[A]): Tree = viewLeft.tail

        def init(implicit m: Measure[A]): Tree = viewRight.init

        def +:[A1 >: A](b: A1)(implicit m: Measure[A1]): FingerTree[A1] = {
            val vb = m(b)
            val vNew = vb + measure
            prefix match {
                case Four(_, d, e, f, g) =>
                    val prefix = Two(vb + m(d), b, d)
                    val vTreePrefix = m(e) + m(f) + m(g)
                    val treeNew = tree.+:[Digit[A1]](Three(vTreePrefix, e, f, g))
                    Deep(vNew, prefix, treeNew, suffix)

                case partial =>
                    Deep(vNew, b +: partial, tree, suffix)
            }
        }

        def :+[A1 >: A](b: A1)(implicit m: Measure[A1]): FingerTree[A1] = {
            val vb = m(b)
            val vNew = measure + vb
            suffix match {
                case Four(_, g, f, e, d) =>
                    val vTreeSuffix = m(g) + m(f) + m(e)
                    val treeNew = tree.:+[Digit[A1]](Three(vTreeSuffix, g, f, e))
                    val suffix = Two(m(d) + vb, d, b)
                    Deep(vNew, prefix, treeNew, suffix)
                case partial =>
                    Deep(vNew, prefix, tree, partial :+ b)
            }
        }

        // we could use app3 with an empty middle argument, as hinze/paterson suggest, but let's
        // keep the simplified polymorphic ++ here for faster handling of empty and single cats.
        def ++[A1 >: A](right: FingerTree[A1])(implicit m: Measure[A1]): FingerTree[A1] = right match {
            case Empty => this
            case Single(_, a) => this :+ a
            case rd@Deep(_, _, _, _) => deepConcat[A1](this, Nil, rd)
        }

        def viewLeft(implicit m: Measure[A]): ViewLeft[A] =
            ViewLeftCons(prefix.head, deepLeft(prefix.tail, tree, suffix))

        def viewRight(implicit m: Measure[A]): ViewRight[A] =
            ViewRightCons(deepRight(prefix, tree, suffix.init), suffix.last)

        def span(pred: Int => Boolean)(implicit m: Measure[A]): (Tree, Tree) =
            if (pred(measure)) {
                // split point lies after the last element of this tree
                (this, empty[A])
            } else {
                // predicate turns true inside the tree
                val (left, elem, right) = span1(pred, 0)
                (left, elem +: right)
            }

        def takeWhile(pred: Int => Boolean)(implicit m: Measure[A]): Tree =
            if (pred(measure)) {
                // split point lies after the last element of this tree
                this
            } else {
                // predicate turns true inside the tree
                val (left, _) = takeWhile1(pred, 0)
                left
            }

        def dropWhile(pred: Int => Boolean)(implicit m: Measure[A]): Tree =
            if (pred(measure)) {
                // split point lies after the last element of this tree
                empty[A]
            } else {
                // predicate turns true inside the tree
                val (elem, right) = dropWhile1(pred, 0)
                elem +: right
            }

        def span1(pred: Int => Boolean)(implicit m: Measure[A]): (Tree, A, Tree) = span1(pred, 0)

        private[fingertree] def span1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Tree, A, Tree) = {
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

        def takeWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Tree, A) = {
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

        def dropWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (A, Tree) = {
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

        def find1(pred: Int => Boolean)(implicit m: Measure[A]): (Int, A) = find1(pred, 0)

        private[fingertree] def find1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Int, A) = {
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

    private case object Empty extends FingerTree[Nothing] {
        def isEmpty = true

        def measure = 0

        def head = throw new NoSuchElementException("head of empty finger tree")

        def headOption: Option[Nothing] = None

        def last = throw new NoSuchElementException("last of empty finger tree")

        def lastOption: Option[Nothing] = None

        def tail(implicit m: Measure[Nothing]): Tree =
            throw new UnsupportedOperationException("tail of empty finger tree")

        def init(implicit m: Measure[Nothing]): Tree =
            throw new UnsupportedOperationException("init of empty finger tree")

        def +:[A1](a1: A1)(implicit m: Measure[A1]): FingerTree[A1] = Single(m(a1), a1)

        def :+[A1](a1: A1)(implicit m: Measure[A1]): FingerTree[A1] = Single(m(a1), a1)

        def ++[A1 >: Nothing](right: FingerTree[A1])(implicit m: Measure[A1]): FingerTree[A1] = right

        def viewLeft(implicit m: Measure[Nothing]): ViewLeft[Nothing] = ViewNil()

        def viewRight(implicit m: Measure[Nothing]): ViewRight[Nothing] = ViewNil()

        def span(pred: Int => Boolean)(implicit m: Measure[Nothing]): (Tree, Tree) = (this, this)

        def takeWhile(pred: Int => Boolean)(implicit m: Measure[Nothing]): Tree = this

        def dropWhile(pred: Int => Boolean)(implicit m: Measure[Nothing]): Tree = this

        def span1(pred: Int => Boolean)(implicit m: Measure[Nothing]): (Tree, Nothing, Tree) =
            throw new UnsupportedOperationException("span1 on empty finger tree")

        private[fingertree] def span1(pred: Int => Boolean, init: Int)(implicit m: Measure[Nothing]): (Tree, Nothing, Tree) =
            throw new UnsupportedOperationException("span1 on empty finger tree")

        def takeWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[Nothing]): (Tree, Nothing) =
            throw new UnsupportedOperationException("takeWhile1 on empty finger tree")

        def dropWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[Nothing]): (Nothing, Tree) =
            throw new UnsupportedOperationException("dropWhile1 on empty finger tree")

        def find1(pred: Int => Boolean)(implicit m: Measure[Nothing]): Nothing =
            throw new UnsupportedOperationException("find1 on empty finger tree")

        private[fingertree] def find1(pred: Int => Boolean, init: Int)(implicit m: Measure[Nothing]): (Int, Nothing) =
            throw new UnsupportedOperationException("find1 on empty finger tree")

        def toList: List[Nothing] = Nil

        def iterator: Iterator[Nothing] = Iterator.empty

        //def to[Col[_]](implicit cbf: CanBuildFrom[Nothing, Nothing, Col[Nothing]]): Col[Nothing] = cbf().result()

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

        def toTree(implicit m: Measure[A]): Tree

        def get: Digit[A]
    }

    private final case class Zero() extends MaybeDigit[Nothing] {
        def isEmpty = true

        def toTree(implicit m: Measure[Nothing]): Tree = empty[Nothing](m)

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

        def tail(implicit m: Measure[A]): MaybeDigit[A]

        def init(implicit m: Measure[A]): MaybeDigit[A]

        def +:[A1 >: A](b: A1)(implicit m: Measure[A1]): Digit[A1]

        def :+[A1 >: A](b: A1)(implicit m: Measure[A1]): Digit[A1]

        def find1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Int, A)

        def span1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (MaybeDigit[A], A, MaybeDigit[A])

        def takeWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (MaybeDigit[A], A)

        def dropWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (A, MaybeDigit[A])

        // def toTree( implicit m: Measure[ A, V ]) : Tree

        def toList: List[A]

        def iterator: Iterator[A]
    }

    final private case class One[A](measure: Int, a1: A) extends Digit[A] {
        def isEmpty = false

        def get: Digit[A] = this

        def head = a1

        def last = a1

        def tail(implicit m: Measure[A]): MaybeDigit[A] = Zero()

        def init(implicit m: Measure[A]): MaybeDigit[A] = Zero()

        def +:[A1 >: A](b: A1)(implicit m: Measure[A1]): Digit[A1] = Two(m(b) + measure, b, a1)

        def :+[A1 >: A](b: A1)(implicit m: Measure[A1]): Digit[A1] = Two(measure + m(b), a1, b)

        def find1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Int, A) = {
            val v1 = init + measure
            (if (pred(v1)) init else v1, a1)
        }

        def span1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (MaybeDigit[A], A, MaybeDigit[A]) = {
            val e = Zero()
            (e, a1, e)
        }

        def takeWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (MaybeDigit[A], A) = {
            (Zero(), a1) // correct???
        }

        def dropWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (A, MaybeDigit[A]) = {
            (a1, Zero()) // correct???
        }

        def toTree(implicit m: Measure[A]): Tree = Single(measure, a1)

        def toList: List[A] = a1 :: Nil

        def iterator: Iterator[A] = Iterator.single(a1)

        override def toString = "(" + a1 + ")"
    }

    final private case class Two[A](measure: Int, a1: A, a2: A) extends Digit[A] {
        def isEmpty = false

        def get: Digit[A] = this

        def head = a1

        def last = a2

        def tail(implicit m: Measure[A]): MaybeDigit[A] = One(m(a2), a2)

        def init(implicit m: Measure[A]): MaybeDigit[A] = One(m(a1), a1)

        def +:[A1 >: A](b: A1)(implicit m: Measure[A1]): Digit[A1] = Three(m(b) + measure, b, a1, a2)

        def :+[A1 >: A](b: A1)(implicit m: Measure[A1]): Digit[A1] = Three(measure + m(b), a1, a2, b)

        def find1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Int, A) = {
            val v1 = init + m(a1)
            if (pred(v1)) (init, a1)
            else {
                val v12 = init + measure
                (if (pred(v12)) v1 else v12, a2)
            }
        }

        def span1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (MaybeDigit[A], A, MaybeDigit[A]) = {
            val va1 = m(a1)
            val v1 = init + va1
            val e = Zero()
            if (pred(v1)) {
                (One(va1, a1), a2, e) // (a1), a2, ()
            } else {
                (e, a1, One(m(a2), a2)) // (), a1, (a2)
            }
        }

        def takeWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (MaybeDigit[A], A) = {
            val va1 = m(a1)
            val v1 = init + va1
            if (pred(v1)) {
                (One(va1, a1), a2) // (a1), a2
            } else {
                val e = Zero()
                (e, a1) // (), a1
            }
        }

        def dropWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (A, MaybeDigit[A]) = {
            val va1 = m(a1)
            val v1 = init + va1
            if (pred(v1)) {
                val e = Zero()
                (a2, e) // a2, ()
            } else {
                (a1, One(m(a2), a2)) // a1, (a2)
            }
        }

        def toTree(implicit m: Measure[A]): Tree = {
            Deep(measure, One(m(a1), a1), empty[Digit[A]], One(m(a2), a2))
        }

        def toList: List[A] = a1 :: a2 :: Nil

        def iterator: Iterator[A] = toList.iterator

        override def toString = "(" + a1 + ", " + a2 + ")"
    }

    final private case class Three[A](measure: Int, a1: A, a2: A, a3: A) extends Digit[A] {
        def isEmpty = false

        def get: Digit[A] = this

        def head = a1

        def last = a3

        def tail(implicit m: Measure[A]): MaybeDigit[A] = Two(m(a2) + m(a3), a2, a3)

        def init(implicit m: Measure[A]): MaybeDigit[A] = Two(m(a1) + m(a2), a1, a2)

        def +:[A1 >: A](b: A1)(implicit m: Measure[A1]): Digit[A1] =
            Four(m(b) + measure, b, a1, a2, a3)

        def :+[A1 >: A](b: A1)(implicit m: Measure[A1]): Digit[A1] =
            Four(measure + m(b), a1, a2, a3, b)

        def find1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Int, A) = {
            val v1 = init + m(a1)
            if (pred(v1)) (init, a1)
            else {
                val v12 = v1 + m(a2)
                if (pred(v12)) (v1, a2)
                else {
                    val v123 = init + measure
                    (if (pred(v123)) v12 else v123, a3)
                }
            }
        }

        def span1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (MaybeDigit[A], A, MaybeDigit[A]) = {
            val va1 = m(a1)
            val va2 = m(a2)
            val v1 = init + va1
            if (pred(v1)) {
                if (pred(v1 + va2)) {
                    (Two(va1 + va2, a1, a2), a3, Zero()) // (a1, a2), a3, ()
                } else {
                    (One(va1, a1), a2, One(m(a3), a3)) // (a1), a2, (a3)
                }
            } else {
                (Zero(), a1, Two(va2 + m(a3), a2, a3)) // (), a1, (a2, a3)
            }
        }

        def takeWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (MaybeDigit[A], A) = {
            val va1 = m(a1)
            val v1 = init + va1
            if (pred(v1)) {
                val va2 = m(a2)
                if (pred(v1 + va2)) {
                    (Two(va1 + va2, a1, a2), a3) // (a1, a2), a3
                } else {
                    (One(va1, a1), a2) // (a1), a2
                }
            } else {
                // (), a1
                (Zero(), a1)
            }
        }

        def dropWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (A, MaybeDigit[A]) = {
            val va1 = m(a1)
            val va2 = m(a2)
            val v1 = init + va1
            if (pred(v1)) {
                if (pred(v1 + va2)) {
                    (a3, Zero()) // a3, ()
                } else {
                    (a2, One(m(a3), a3)) // a2, (a3)
                }
            } else {
                (a1, Two(va2 + m(a3), a2, a3)) // a1, (a2, a3)
            }
        }

        def toTree(implicit m: Measure[A]): Tree = {
            Deep(measure, Two(m(a1) + m(a2), a1, a2), empty[Digit[A]], One(m(a3), a3))
        }

        def toList: List[A] = a1 :: a2 :: a3 :: Nil

        def iterator: Iterator[A] = toList.iterator

        override def toString = "(" + a1 + ", " + a2 + ", " + a3 + ")"
    }

    final private case class Four[A](measure: Int, a1: A, a2: A, a3: A, a4: A) extends Digit[A] {
        def isEmpty = false

        def get: Digit[A] = this

        def head = a1

        def last = a4

        def tail(implicit m: Measure[A]): MaybeDigit[A] =
            Three(m(a2) + m(a3) + m(a4), a2, a3, a4)

        def init(implicit m: Measure[A]): MaybeDigit[A] =
            Three(m(a1) + m(a2) + m(a3), a1, a2, a3)

        def +:[A1 >: A](b: A1)(implicit m: Measure[A1]) =
            throw new UnsupportedOperationException("+: on digit four")

        def :+[A1 >: A](b: A1)(implicit m: Measure[A1]) =
            throw new UnsupportedOperationException(":+ on digit four")

        def find1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Int, A) = {
            val v1 = init + m(a1)
            if (pred(v1)) (init, a1)
            else {
                val v12 = v1 + m(a2)
                if (pred(v12)) (v1, a2)
                else {
                    val v123 = v12 + m(a3)
                    if (pred(v123)) (v12, a3)
                    else {
                        val v1234 = init + measure
                        (if (pred(v1234)) v123 else v1234, a4)
                    }
                }
            }
        }

        def span1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (MaybeDigit[A], A, MaybeDigit[A]) = {
            val va1 = m(a1)
            val va2 = m(a2)
            val v1 = init + va1
            if (pred(v1)) {
                val v12 = v1 + va2
                val va3 = m(a3)
                if (pred(v12)) {
                    val va12 = va1 + va2
                    if (pred(v12 + va3)) {
                        (Three(va12 + va3, a1, a2, a3), // (a1, a2, a3), a4, ()
                          a4,
                          Zero())
                    } else {
                        (Two(va12, a1, a2), // (a1, a2), a3, (a4)
                          a3,
                          One(m(a4), a4))
                    }
                } else {
                    (One(va1, a1), // (a1), a2, (a3, a4)
                      a2,
                      Two(va3 + m(a4), a3, a4))
                }
            } else {
                (Zero(), // (), a1, (a2, a3, a4)
                  a1,
                  Three(va2 + m(a3) + m(a4), a2, a3, a4))
            }
        }

        def takeWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (MaybeDigit[A], A) = {
            val va1 = m(a1)
            val v1 = init + va1
            if (pred(v1)) {
                val va2 = m(a2)
                val v12 = v1 + va2
                if (pred(v12)) {
                    val va3 = m(a3)
                    val va12 = va1 + va2
                    if (pred(v12 + va3)) {
                        (Three(va12 + va3, a1, a2, a3), a4) // (a1, a2, a3), a4
                    } else {
                        (Two(va12, a1, a2), a3) // (a1, a2), a3
                    }
                } else {
                    (One(va1, a1), a2) // (a1), a2
                }
            } else {
                (Zero(), a1) // (), a1
            }
        }

        def dropWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (A, MaybeDigit[A]) = {
            val va1 = m(a1)
            val va2 = m(a2)
            val v1 = init + va1
            if (pred(v1)) {
                val v12 = v1 + va2
                val va3 = m(a3)
                if (pred(v12)) {
                    if (pred(v12 + va3)) {
                        (a4, Zero()) // a4, ()
                    } else {
                        (a3, One(m(a4), a4)) // a3, (a4)
                    }
                } else {
                    (a2, Two(va3 + m(a4), a3, a4)) // a2, (a3, a4)
                }
            } else {
                (a1, Three(va2 + m(a3) + m(a4), a2, a3, a4)) // a1, (a2, a3, a4)
            }
        }

        def toTree(implicit m: Measure[A]): Tree = {
            Deep(measure, Two(m(a1) + m(a2), a1, a2), empty[Digit[A]], Two(m(a3) + m(a4), a3, a4))
        }

        def toList: List[A] = a1 :: a2 :: a3 :: a4 :: Nil

        def iterator: Iterator[A] = toList.iterator

        override def toString = "(" + a1 + ", " + a2 + ", " + a3 + ", " + a4 + ")"
    }

}

sealed trait FingerTree[+A] {

    import FingerTree._

    protected[this] type Tree = FingerTree[A]

    /** Queries whether the tree is empty or not
      *
      * @return  `true` if the tree is empty
      */
    def isEmpty: Boolean

    /** Queries the measure of the tree, which might be its size or sum
      *
      * @return  the measure of the tree
      */
    def measure: Int

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
      * @param m the measure used to update the tree's structure
      * @return  the new tree with the first element removed
      */
    def tail(implicit m: Measure[A]): Tree

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
    def init(implicit m: Measure[A]): Tree

    /** Prepends an element to the tree.
      *
      * @param b the element to prepend
      * @param m the measure used to update the tree's measure
      * @return  the new tree with the element prepended
      */
    def +:[A1 >: A](b: A1)(implicit m: Measure[A1]): FingerTree[A1]

    /** Appends an element to the tree.
      *
      * @param b the element to append
      * @param m the measure used to update the tree's structure
      * @return  the new tree with the element appended
      */
    def :+[A1 >: A](b: A1)(implicit m: Measure[A1]): FingerTree[A1]

    def ++[A1 >: A](right: FingerTree[A1])(implicit m: Measure[A1]): FingerTree[A1]

    def viewLeft(implicit m: Measure[A]): ViewLeft[A]

    def viewRight(implicit m: Measure[A]): ViewRight[A]

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

    //def to[Col[_]](implicit cbf: CanBuildFrom[Nothing, A, Col[A@uV]]): Col[A@uV]

    /** Same as `span1`, but prepends the discerning element to the right tree, returning the left and right tree.
      * Unlike `span1`, this is an allowed operation on an empty tree.
      *
      * @param pred a test function applied to the elements of the tree from left to right, until a
      *             the test returns `false`.
      * @return  the split tree, as a `Tuple2` with the left and the right tree
      */
    def span(pred: Int => Boolean)(implicit m: Measure[A]): (Tree, Tree)

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
    def span1(pred: Int => Boolean)(implicit m: Measure[A]): (Tree, A, Tree)

    private[fingertree] def span1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Tree, A, Tree)

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
    def find1(pred: Int => Boolean)(implicit m: Measure[A]): (Int, A)

    private[fingertree] def find1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Int, A)

    //   /**
    //    * Appends two elements to the tree.
    //    *
    //    * @param b1 the first element to append (this will become the before-last element in the tree)
    //    * @param b2 the second element to append (this will become the last element in the tree)
    //    * @param m the measure used to update the tree's structure
    //    * @return  the new tree with the elements appended
    //    */
    //   def append2[ A1 >: A ]( b1: A1, b2: A1 )( implicit m: Measure[ A1, V ]) : FingerTree[ V, A1 ]

    def takeWhile(pred: Int => Boolean)(implicit m: Measure[A]): Tree

    def dropWhile(pred: Int => Boolean)(implicit m: Measure[A]): Tree

    def takeWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (Tree, A)

    def dropWhile1(pred: Int => Boolean, init: Int)(implicit m: Measure[A]): (A, Tree)
}

import language.higherKinds

trait FingerTreeLike[A, Repr <: FingerTreeLike[A, Repr]] {
    protected implicit def m: Measure[A]

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

    //final def to[Col[_]](implicit cbf: CanBuildFrom[Nothing, A, Col[A]]): Col[A] = tree.to[Col]

    // def toStream : Stream[ A ] = tree.toStream

    protected def tree: FingerTree[A]

    protected def wrap(tree: FingerTree[A]): Repr
}


object Measure {

    object Indexed extends Measure[Any] {
        override def toString = "Indexed"

        def apply(c: Any) = 1
    }

}

trait Measure[-C] {

    def apply(c: C): Int

}

object FingerTreeIndexedSeq {
    private implicit val measure = Measure.Indexed

    def empty[A]: FingerTreeIndexedSeq[A] = new Impl[A](FingerTree.empty[A])

    def apply[A](elems: A*): FingerTreeIndexedSeq[A] = new Impl[A](FingerTree.apply[A](elems: _*))

    private final class Impl[A](protected val tree: FingerTree[A]) extends FingerTreeIndexedSeq[A] {
        protected def m: Measure[A] = measure

        protected def wrap(tree: FingerTree[A]): FingerTreeIndexedSeq[A] = new Impl(tree)

        protected def isSizeGtPred(i: Int) = _ > i

        protected def isSizeLteqPred(i: Int) = _ <= i

        def size: Int = tree.measure

        override def toString = tree.iterator.mkString("Seq(", ", ", ")")
    }

}

sealed trait FingerTreeIndexedSeq[A] extends FingerTreeIndexedSeqLike[A, FingerTreeIndexedSeq[A]]


trait FingerTreeIndexedSeqLike[A, Repr <: FingerTreeIndexedSeqLike[A, Repr]] extends FingerTreeLike[A, Repr] {
    final def :+(x: A): Repr = wrap(tree :+ x)

    final def +:(x: A): Repr = wrap(x +: tree)

    final def ++(xs: Repr): Repr = wrap(tree ++ xs.tree)

    final def apply(idx: Int): A = {
        if (idx < 0 || idx >= size) throw new IndexOutOfBoundsException(idx.toString)
        tree.find1(isSizeGtPred(idx))._2
    }

    // final def size : Int = sizeMeasure( tree.measure )
    def size: Int

    final def drop(n: Int): Repr = wrap(dropTree(n))

    final def dropRight(n: Int): Repr = wrap(takeTree(size - n))

    final def slice(from: Int, until: Int): Repr = take(until).drop(from)

    final def splitAt(idx: Int): (Repr, Repr) = {
        val (l, r) = tree.span(isSizeLteqPred(idx))
        (wrap(l), wrap(r))
    }

    final def take(n: Int): Repr = wrap(takeTree(n))

    final def takeRight(n: Int): Repr = wrap(dropTree(size - n))

    //   final def updated( index: Int, elem: A ) : Repr = {
    //      if( index < 0 || index >= size ) throw new IndexOutOfBoundsException( index.toString )
    //      val (l, _, r) = splitTree1( index )
    //      wrap( l.:+( elem ).<++>( r ))  // XXX most efficient?
    //   }

    /**
     * For a given value `i`, returns a test function that when passed a measure,
     * compare's the measure's size component against `i` using `f(m) <= i`
     */
    protected def isSizeLteqPred(idx: Int): Int => Boolean

    /**
     * For a given value `i`, returns a test function that when passed a measure,
     * compare's the measure's size component against `i` using `f(m) > i`
     */
    protected def isSizeGtPred(idx: Int): Int => Boolean

    private def takeTree(idx: Int) = tree.takeWhile(isSizeLteqPred(idx))

    private def dropTree(idx: Int) = tree.dropWhile(isSizeLteqPred(idx))

    // private def splitTree1(  i: Int ) = tree.split1( indexPred( i ))
}
