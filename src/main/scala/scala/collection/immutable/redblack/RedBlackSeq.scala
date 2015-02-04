package scala
package collection
package immutable
package redblack


import scala.collection.generic.{GenericCompanion, GenericTraversableTemplate, CanBuildFrom, IndexedSeqFactory}
import scala.collection.mutable.Builder

/**
 * Created by nicolasstucki on 01/02/15.
 */
final class RedBlackSeq[+A] private[immutable](private[immutable] val tree: RedBlackTree.Tree[A], private[immutable] val startIndex: Int, private[immutable] val endIndex: Int)
  extends AbstractSeq[A]
  with IndexedSeq[A]
  with GenericTraversableTemplate[A, RedBlackSeq]
  with IndexedSeqLike[A, RedBlackSeq[A]] {

    override def companion: GenericCompanion[RedBlackSeq] = RedBlackSeq

    override def length = endIndex - startIndex


    override def iterator = RedBlackTree.valuesIterator(tree)

    override def apply(idx: Int) = {
        val index = startIndex + idx
        if (startIndex <= index && index < endIndex)
            RedBlackTree.lookup(tree, index).value
        else
            throw new IndexOutOfBoundsException(idx.toString)
    }

    override def updated[B >: A, That](index: Int, elem: B)(implicit bf: CanBuildFrom[RedBlackSeq[A], B, That]) = {
        if (bf eq IndexedSeq.ReusableCBF)
            new RedBlackSeq[B](
                RedBlackTree.update(tree, startIndex + index, elem, overwrite = false),
                startIndex,
                endIndex
            ).asInstanceOf[That]
        else
            super.updated(index, elem)
    }

    override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[RedBlackSeq[A], B, That]) = {
        if (bf eq IndexedSeq.ReusableCBF)
            new RedBlackSeq[B](
                RedBlackTree.update(tree, endIndex, elem, overwrite = false),
                startIndex,
                endIndex + 1
            ).asInstanceOf[That]
        else
            super.:+(elem)
    }

    override def +:[B >: A, That](elem: B)(implicit bf: CanBuildFrom[RedBlackSeq[A], B, That]) = {
        if (bf eq IndexedSeq.ReusableCBF)
            new RedBlackSeq[B](
                RedBlackTree.update(tree, startIndex - 1, elem, overwrite = false),
                startIndex - 1,
                endIndex
            ).asInstanceOf[That]
        else
            super.+:(elem)
    }

    override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[RedBlackSeq[A], B, That]): That = that match {
        case _that: RedBlackSeq[B] =>
            if (this.length < _that.length) {
                // prepend elements
                val it = RedBlackTree.valuesIterator[B](this.tree)
                if (it.hasNext) {
                    val newStart = _that.startIndex - this.length
                    var i = newStart
                    var newTree: RedBlackTree.Tree[B] = RedBlackTree.update(tree, i, it.next(), false)
                    i += 1
                    while (it.hasNext) {
                        newTree = RedBlackTree.update(newTree, i, it.next(), true)
                        i += 1
                    }
                    new RedBlackSeq[B](
                        newTree,
                        newStart,
                        _that.endIndex
                    ).asInstanceOf[That]
                } else _that.asInstanceOf[That]
            } else {
                // append elements
                val it = RedBlackTree.valuesIterator[B](_that.tree)
                if (it.hasNext) {
                    var _endIndex = this.endIndex
                    var newTree: RedBlackTree.Tree[B] = RedBlackTree.update(tree, _endIndex, it.next(), false)
                    _endIndex += 1
                    while (it.hasNext) {
                        newTree = RedBlackTree.update(newTree, _endIndex, it.next(), true)
                        _endIndex += 1
                    }
                    new RedBlackSeq[B](
                        newTree,
                        startIndex,
                        _endIndex
                    ).asInstanceOf[That]
                } else this.asInstanceOf[That]
            }
        case _ => super.++(that)
    }

    override def head = {
        if (isEmpty) throw new UnsupportedOperationException
        super.head
    }

    override def last = {
        if (isEmpty) throw new UnsupportedOperationException
        super.last
    }

    override def lengthCompare(len: Int) = length - len
}

final class RedBlackSeqBuilder[A]() extends Builder[A, RedBlackSeq[A]] {
    var tree: RedBlackTree.Tree[A] = null
    var index = 0

    override def +=(elem: A) = {
        val idx = index
        tree = RedBlackTree.update(tree, idx, elem, overwrite = true)
        index = idx + 1
        this
    }

    override def result() = new RedBlackSeq[A](tree, 0, index)

    override def clear() = {
        tree = null
        index = 0
    }
}

object RedBlackSeq extends IndexedSeqFactory[RedBlackSeq] {
    def newBuilder[A]: Builder[A, RedBlackSeq[A]] = new RedBlackSeqBuilder[A]

    implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, RedBlackSeq[A]] =
        ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

    override def empty[A]: RedBlackSeq[A] = new RedBlackSeq[A](null, 0, 0)

}