package scala.collection.immutable.oldrrbvector

import java.lang.Math._

import scala.annotation.unchecked.uncheckedVariance
import scala.collection._
import scala.collection.immutable.oldrrbvector.VectorProps._
import scala.compat.Platform

//import scala.collection.generic.GenTraversableFactory.GenericCanBuildFrom

import scala.collection.generic.{CanBuildFrom, GenericCompanion, GenericTraversableTemplate, IndexedSeqFactory}
import scala.collection.immutable.IndexedSeq
import scala.collection.mutable.Builder

import java.lang.Math.{max => mmax, min => mmin}

object Vector extends IndexedSeqFactory[Vector] {
    def newBuilder[A]: Builder[A, Vector[A]] = new VectorBuilder[A]

    @inline implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, Vector[A]] =
        ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]]

    private lazy val RRB_NIL = new Vector[Nothing]

    override def empty[A]: Vector[A] = RRB_NIL.asInstanceOf[Vector[A]]

    def apply[A]() = empty[A]

    // create a single element vector
    def apply[A](elem: A): Vector[A] = new Vector[A].initDisplay1(Array(elem.asInstanceOf[AnyRef]), 1)


}

final class Vector[+A] private[immutable]
  extends AbstractSeq[A]
  with IndexedSeq[A]
  with GenericTraversableTemplate[A, Vector]
  with IndexedSeqLike[A, Vector[A]]
  with RelaxedVectorPointer[A@uncheckedVariance]
  with Serializable
  /*with CustomParallelizable[A, ParVector[A]] */ {
    self =>

    override def companion: GenericCompanion[Vector] = Vector

    //    override def par = new ParVector(this)

    //    override def toVector: Vector[A] = this

    override def length = _length

    override def lengthCompare(len: Int): Int = length - len

    override def iterator: VectorIterator[A] = {
        val iterator = new VectorIterator[A]
        iterator.initIterator(root, height, length)
        iterator
    }

    // Function1 api

    override def apply(idx: Int): A = getElement(idx)


    // SeqLike api

    override /*SeqLike*/
    def reverseIterator: Iterator[A] = new AbstractIterator[A] {
        private var i = self.length

        def hasNext: Boolean = 0 < i

        def next(): A = {
            if (0 < i) {
                i -= 1
                self(i)
            } else Iterator.empty.next()
        }
    }

    override /*IterableLike*/ def head: A = {
        if (isEmpty) throw new UnsupportedOperationException("empty.head")
        apply(0)
    }

    override /*TraversableLike*/ def tail: Vector[A] = {
        if (isEmpty) throw new UnsupportedOperationException("empty.tail")
        drop(1)
    }

    override /*TraversableLike*/ def last: A = {
        if (isEmpty) throw new UnsupportedOperationException("empty.last")
        apply(length - 1)
    }

    override /*TraversableLike*/ def init: Vector[A] = {
        if (isEmpty) throw new UnsupportedOperationException("empty.init")
        dropRight(1)
    }

    //    override /*IterableLike*/ def slice(from: Int, until: Int): RRBVector[A] =
    //        take(until).drop(from)
    //
    //    override /*IterableLike*/ def splitAt(n: Int): (RRBVector[A], RRBVector[A]) = (take(n), drop(n))

    override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[Vector[A], B, That]): That = {
        that match {
            case vec: Vector[B] => this.concatenated[B](vec).asInstanceOf[That]
            case _ => super.++(that)
        }
    }

    @inline override def +:[B >: A, That](elem: B)(implicit bf: CanBuildFrom[Vector[A], B, That]): That = {
        (Vector(elem) ++ this).asInstanceOf[That]
    }

    override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[Vector[A], B, That]): That =
        if (bf eq IndexedSeq.ReusableCBF) appendedBack(elem).asInstanceOf[That]
        else super.:+(elem)(bf)

    //    override def patch[B >: A, That](from: Int, patch: GenSeq[B], replaced: Int)(implicit bf: CanBuildFrom[RRBVector[A], B, That]): That = {
    //        // just ignore bf
    //        val insert = patch.nonEmpty
    //        val delete = replaced != 0
    //        if (insert || delete) {
    //            val prefix = take(from)
    //            val rest = drop(from + replaced)
    //            ((prefix ++ patch).asInstanceOf[Vector[B]] ++ rest).asInstanceOf[That]
    //        } else this.asInstanceOf[That]
    //    }


    /* Private methods */

    private final def appendedBack[B >: A](elem: B): Vector[B] = {
        // TODO re-implement using an generalized version of gotoPosWritable
        height match {
            case 0 =>
                new Vector[B].initDisplay1(unitLeaf(elem.asInstanceOf[AnyRef]), 1)
            case 1 =>
                if (_length < WIDTH) {
                    val d0 = appendedToLeaf(display0, elem.asInstanceOf[AnyRef])
                    new Vector[B].initDisplay1(d0, _length + 1)
                } else {
                    val d0 = unitLeaf(elem.asInstanceOf[AnyRef])
                    val d1 = joinLeafs(display0, d0)
                    new Vector[B].initDisplay2(d0, d1, _length + 1, _length, 0, _length + 1)
                }
            case 2 =>
                // focusOnPosition(_length - 1)
                val d1 = display1
                val d0 = d1(d1.length - 1).asInstanceOf[Array[AnyRef]]
                if (d0.length < WIDTH) {
                    val n0 = appendedToLeaf(d0, elem.asInstanceOf[AnyRef])
                    val n1 = replacedLastWithBranch(d1, n0, 1)
                    // small focus on last block, could have a taller/wider focus
                    new Vector[B].initDisplay2(n0, n1, _length + 1, 0, _length + 1 - n0.length, _length + 1)
                } else if (d1.length < WIDTH + INVAR) {
                    val n0 = unitLeaf(elem.asInstanceOf[AnyRef])
                    val n1 = appendedToBranch(d1, n0, 1)
                    new Vector[B].initDisplay2(n0, n1, _length + 1, 0, _length + 1 - n0.length, _length + 1)
                } else {
                    val n0 = unitLeaf(elem.asInstanceOf[AnyRef])
                    val n1 = unitBranch(n0)
                    val n2 = joinBranches(d1, n1)
                    new Vector[B].initDisplay3(n0, n1, n2, _length + 1, 0, _length, _length + 1)
                }
            case 3 =>
                val d2 = display2
                val d1 = d2(d2.length - 1).asInstanceOf[Array[AnyRef]]
                val d0 = d1(d1.length - 1).asInstanceOf[Array[AnyRef]]
                if (d0.length < WIDTH) {
                    val n0 = appendedToLeaf(d0, elem.asInstanceOf[AnyRef])
                    val n1 = replacedLastWithBranch(d1, n0, 1)
                    val n2 = replacedLastWithBranch(d2, n1, 1)
                    // small focus on last block, could have a taller/wider focus
                    new Vector[B].initDisplay3(n0, n1, n2, _length + 1, 0, _length + 1 - n0.length, _length + 1)
                } else if (d1.length < WIDTH + INVAR) {
                    val n0 = unitLeaf(elem.asInstanceOf[AnyRef])
                    val n1 = appendedToBranch(d1, n0, 1)
                    val n2 = replacedLastWithBranch(d2, n1, 1)
                    new Vector[B].initDisplay3(n0, n1, n2, _length + 1, 0, _length + 1 - n0.length, _length + 1)
                } else if (d2.length < WIDTH + INVAR) {
                    val n0 = unitLeaf(elem.asInstanceOf[AnyRef])
                    val n1 = unitBranch(n0)
                    val n2 = appendedToBranch(d2, n1, 1)
                    new Vector[B].initDisplay3(n0, n1, n2, _length + 1, 0, _length + 1 - n0.length, _length + 1)
                } else {
                    val n0 = unitLeaf(elem.asInstanceOf[AnyRef])
                    val n1 = unitBranch(n0)
                    val n2 = unitBranch(n1)
                    val n3 = joinBranches(d2, n2)
                    new Vector[B].initDisplay4(n0, n1, n2, n3, _length + 1, 0, _length, _length + 1)
                }
            case 4 =>
                val d3 = display3
                val d2 = d3(d3.length - 1).asInstanceOf[Array[AnyRef]]
                val d1 = d2(d2.length - 1).asInstanceOf[Array[AnyRef]]
                val d0 = d1(d1.length - 1).asInstanceOf[Array[AnyRef]]
                if (d0.length < WIDTH) {
                    val n0 = appendedToLeaf(d0, elem.asInstanceOf[AnyRef])
                    val n1 = replacedLastWithBranch(d1, n0, 1)
                    val n2 = replacedLastWithBranch(d2, n1, 1)
                    val n3 = replacedLastWithBranch(d3, n2, 1)
                    // small focus on last block, could have a taller/wider focus
                    new Vector[B].initDisplay4(n0, n1, n2, n3, _length + 1, 0, _length + 1 - n0.length, _length + 1)
                } else if (d1.length < WIDTH + INVAR) {
                    val n0 = unitLeaf(elem.asInstanceOf[AnyRef])
                    val n1 = appendedToBranch(d1, n0, 1)
                    val n2 = replacedLastWithBranch(d2, n1, 1)
                    val n3 = replacedLastWithBranch(d3, n2, 1)
                    new Vector[B].initDisplay4(n0, n1, n2, n3, _length + 1, 0, _length + 1 - n0.length, _length + 1)
                } else if (d2.length < WIDTH + INVAR) {
                    val n0 = unitLeaf(elem.asInstanceOf[AnyRef])
                    val n1 = unitBranch(n0)
                    val n2 = appendedToBranch(d2, n1, 1)
                    val n3 = replacedLastWithBranch(d3, n2, 1)
                    new Vector[B].initDisplay4(n0, n1, n2, n3, _length + 1, 0, _length + 1 - n0.length, _length + 1)
                } else if (d3.length < WIDTH + INVAR) {
                    val n0 = unitLeaf(elem.asInstanceOf[AnyRef])
                    val n1 = unitBranch(n0)
                    val n2 = unitBranch(n1)
                    val n3 = appendedToBranch(d3, n2, 1)
                    new Vector[B].initDisplay4(n0, n1, n2, n3, _length + 1, 0, _length + 1 - n0.length, _length + 1)
                } else {
                    val n0 = unitLeaf(elem.asInstanceOf[AnyRef])
                    val n1 = unitBranch(n0)
                    val n2 = unitBranch(n1)
                    val n3 = unitBranch(n2)
                    val n4 = joinBranches(d3, n3)
                    new Vector[B].initDisplay5(n0, n1, n2, n3, n4, _length + 1, 0, _length, _length + 1)
                }
            case 5 =>
                // TODO
                throw new NotImplementedError()
            case 6 =>
                // TODO
                throw new NotImplementedError()
            case _ => throw new IllegalStateException("Illegal vector height: " + height)

        }
    }

    private final def concatenated[U >: A](that: Vector[U]): Vector[U] = {
        val thisLength = this._length
        if (thisLength == 0)
            return that

        val thatLength = that._length
        if (thatLength == 0)
            return this.asInstanceOf[Vector[U]]

        val newLength = thisLength + thatLength
        if (newLength <= WIDTH)
            return new Vector[U].initDisplay1(mergeLeafs(this.display0, that.display0), newLength)

        val thisHeight = this.height
        val thatHeight = that.height
        val thisRoot = this.root
        val thatRoot = that.root
        if (thisHeight == 1 && thatHeight == 1) {
            val leftLeaf = thisRoot.asInstanceOf[Array[AnyRef]]
            val arr = setSizes(araNewAbove(leftLeaf, thatRoot), 2)
            return new Vector[U].initDisplay2(leftLeaf, arr, newLength, 0, 0, leftLeaf.length)
        }

        val balancedBranch =
            if (thisHeight > thatHeight) {
                val leftBranch = thisRoot.asInstanceOf[Array[AnyRef]]
                val concatenatedBranch = concatenatedSubTree(leftBranch(leftBranch.length - 1), thisHeight - 1, thatRoot, thatHeight)
                rebalanced(leftBranch, concatenatedBranch, null, thisHeight, true)
            } else if (thisHeight < thatHeight) {
                val rightBranch = thatRoot.asInstanceOf[Array[AnyRef]]
                val concatenatedBranch = concatenatedSubTree(thisRoot, thisHeight, rightBranch(1), thatHeight - 1)
                rebalanced(null, concatenatedBranch, rightBranch, thatHeight, true)
            } else {
                val leftBranch = thisRoot.asInstanceOf[Array[AnyRef]]
                val rightBranch = thatRoot.asInstanceOf[Array[AnyRef]]
                val concatenatedBranch = concatenatedSubTree(leftBranch(leftBranch.length - 1), thisHeight - 1, rightBranch(1), thatHeight - 1)
                rebalanced(leftBranch, concatenatedBranch, rightBranch, thisHeight, true)
            }
        val height = balancedBranch(0).asInstanceOf[Int]
        val sizedBalancedBranch = setSizes(balancedBranch, height)
        new Vector[U].initDisplayWithoutFocus(sizedBalancedBranch, height, newLength)
    }

    private final def concatenatedSubTree(leftNode: AnyRef, leftHeight: Int, rightNode: AnyRef, rightHeight: Int): Array[AnyRef] = {
        if (leftHeight > rightHeight) {
            val leftBranch = leftNode.asInstanceOf[Array[AnyRef]]
            val concatenatedBranch = concatenatedSubTree(leftBranch(leftBranch.length - 1), leftHeight - 1, rightNode, rightHeight)
            val balancedBranch = rebalanced(leftBranch, concatenatedBranch, null, leftHeight, false)
            balancedBranch
        } else if (leftHeight < rightHeight) {
            val rightBranch = rightNode.asInstanceOf[Array[AnyRef]]
            val concatenatedBranch = concatenatedSubTree(leftNode, leftHeight, rightBranch(1), rightHeight - 1)
            val balancedBranch = rebalanced(null, concatenatedBranch, rightBranch, rightHeight, false)
            balancedBranch
        } else if (leftHeight == 1 /* && rightHeight == 1 */ ) {
            araNewAbove(leftNode, rightNode)
        } else {
            // two heights the same so move down both
            val leftBranch = leftNode.asInstanceOf[Array[AnyRef]]
            val rightBranch = rightNode.asInstanceOf[Array[AnyRef]]
            val concatenatedBranch = concatenatedSubTree(leftBranch(leftBranch.length - 1), leftHeight - 1, rightBranch(1), rightHeight - 1)
            val balancedBranch = rebalanced(leftBranch, concatenatedBranch, rightBranch, leftHeight, false)
            balancedBranch
        }
    }

    // From prototype
    private final def rebalanced(al: Array[AnyRef], ac: Array[AnyRef], ar: Array[AnyRef], height: Int, isTop: Boolean): Array[AnyRef] = {
        // Put all the slots at this level in one array ++Note:  This can be avoided by indexing the sub arrays as one
        // remember Ara(0) is Size
        val all = araNewJoin(al, ac, ar)

        // shuffle slot sizes to fit invariant
        val szs = shuffle(all, height)
        // TODO remove this second return value
        val slen = this._length // use object field to transport 2nd return value

        //println("shuffle: "+hw+ " " + (all map { (x:AnyRef) => x match {case a: Array[AnyRef] => a.mkString("{,",",","}") }} mkString))//TR
        //println("szs: "+szs.mkString)//TR


        // Now copy across according to model sizes in szs
        val nall = copyAcross(all, szs, slen, height)

        // nall.length = slen + 1 (accommodate size slot)

        // split across two nodes if greater than Width
        // This splitting/copying can be avoided by moving this logic into the copyAcross
        // and only creating one or two arrays as needed.
        if (slen <= WIDTH) {
            val na = araNewCopy(nall, 0, slen)
            if (isTop) {
                na(0) = height.asInstanceOf[AnyRef] // use na(0) to transport 2nd return value
                na
            } else
                araNewAbove(setSizes(na, height))

        } else {
            val nal = araNewCopy(nall, 0, WIDTH)
            val nar = araNewCopy(nall, WIDTH, slen - WIDTH)
            val arr = araNewAbove(setSizes(nal, height), setSizes(nar, height))
            if (isTop) {
                arr(0) = (height + 1).asInstanceOf[AnyRef] // use arr(0) to transport 2nd return value
                arr
            } else
                arr
        }

    }

    // From prototype
    private final def shuffle(all: Array[AnyRef], height: Int): Array[Int] = {
        // In writing up the description I realized that the shuffle cost can be reduced. When you look at the drawing and understand what we are doing it becomes obvious that we do not need to gather the pieces into one array. We can in fact calculate the size of the needed array and then do the shuffle only on the new edge array. Similarly the associated size array to do the shuffling can be the needed size array. Hence no wasted copying.

        // returns an array with the desired slot sizes.
        // This version allows an Extra number of slots however many slots.
        // if the slots are less than w then as many as Extra+1 could be small
        // while if the total number of slots at the level are as great as 2w
        // then still only Extra can be small

        // (Array[Int],Int) <--- 2nd return value transported via this.vSize
        val alen = all.length
        val szs = new Array[Int](alen)

        var tcnt = 0
        // find total slots in the two levels.
        var i = 0
        while (i < alen) {
            val sz = sizeSlot(all(i), height - 1)
            szs(i) = sz
            tcnt += sz
            i += 1
        }

        // szs(i) holds #slots of all(i), tcnt is sum
        // ---

        // Calculate the ideal or effective number of slots
        // used to limit number of extra slots.
        val effslt = tcnt / WIDTH + 1 // <-- "desired" number of slots???

        val MinWidth = WIDTH - INVAR // min number of slots allowed...

        var nalen = alen
        // note - this makes multiple passes, can be done in one.
        // redistribute the smallest slots until only the allowed extras remain
        while (nalen > effslt + EXTRAS) {
            // TR each loop iteration removes the first short block
            // TR what if no small one is found? doesn't check ix < szs.length,
            // TR we know there are small ones. what if the small ones are all at the right?
            // TR how do we know there is (enough) stuff right of them to balance?

            var ix = 0
            // skip over any blocks large enough
            while (szs(ix) > MinWidth) ix += 1

            // Found a short one so redistribute over following ones
            var el = szs(ix) // current size <= MinWidth
            do {
                val msz = mmin(el + szs(ix + 1), WIDTH)
                szs(ix) = msz
                el = el + szs(ix + 1) - msz

                ix += 1
            } while (el > 0)

            // shuffle up remaining slot sizes
            while (ix < nalen - 1) {
                szs(ix) = szs(ix + 1)
                ix += 1
            }
            nalen -= 1
        }

        //(szs,nalen)
        this._length = nalen // transport to caller
        szs
    }

    // From prototype
    // Takes the slot size model and copies across slots to match it.
    private final def copyAcross(all: Array[AnyRef], szs: Array[Int], slen: Int, height: Int): Array[AnyRef] = {

        val nall = new Array[AnyRef](slen + 1)
        var ix = 0 // index into the all input array
        var offset = 0 // offset into an individual slot array.
        // It points to the next sub tree in the array to be copied

        if (height == 2) {
            var i = 0
            while (i < slen) {
                val nsize = szs(i)
                val ge = all(ix).asInstanceOf[Array[AnyRef]]
                val asIs = (offset == 0) && (nsize == ge.length)

                if (asIs) {
                    ix += 1;
                    nall(i) = ge
                } else {
                    var fillcnt = 0
                    var offs = offset
                    var nix = ix
                    var rta: Array[AnyRef] = null

                    var ga: Array[AnyRef] = null
                    // collect enough slots together to match the size needed
                    while ((fillcnt < nsize) && (nix < all.length)) {
                        val gaa = all(nix).asInstanceOf[Array[AnyRef]]
                        ga = if (fillcnt == 0) new Array[AnyRef](nsize) else ga
                        val lena = gaa.length
                        if (nsize - fillcnt >= lena - offs) {
                            //for(i<-0 until lena-offs) ga(i+fillcnt)=gaa(i+offs)
                            System.arraycopy(gaa, offs, ga, fillcnt, lena - offs)
                            fillcnt += lena - offs
                            nix += 1
                            offs = 0
                        } else {
                            //for(i<-0 until nsize-fillcnt) ga(i+fillcnt)=gaa(i+offs)
                            System.arraycopy(gaa, offs, ga, fillcnt, nsize - fillcnt)
                            offs += nsize - fillcnt
                            fillcnt = nsize
                        }
                        rta = ga
                    }

                    ix = nix
                    offset = offs
                    nall(i) = rta
                }
                i += 1
            }

        } else {
            // not bottom

            var i = 0
            while (i < slen) {
                val nsize = szs(i)
                val ae = all(ix).asInstanceOf[Array[AnyRef]]
                val asIs = (offset == 0) && (nsize == ae.length - 1)

                if (asIs) {
                    ix += 1
                    nall(i) = ae
                } else {
                    var fillcnt = 0
                    var offs = offset
                    var nix = ix
                    var rta: Array[AnyRef] = null

                    var aa: Array[AnyRef] = null
                    // collect enough slots together to match the size needed
                    while ((fillcnt < nsize) && (nix < all.length)) {
                        val aaa = all(nix).asInstanceOf[Array[AnyRef]]
                        aa = if (fillcnt == 0) new Array[AnyRef](nsize + 1) else aa
                        val lena = aaa.length - 1
                        if (nsize - fillcnt >= lena - offs) {
                            //for(i<-0 until lena-offs) aa(i+fillcnt+1)=aaa(i+offs+1)
                            System.arraycopy(aaa, offs + 1, aa, fillcnt + 1, lena - offs)
                            nix += 1
                            fillcnt += lena - offs
                            offs = 0
                        } else {
                            //for(i<-0 until nsize-fillcnt) aa(i+fillcnt+1)=aaa(i+offs+1)
                            System.arraycopy(aaa, offs + 1, aa, fillcnt + 1, nsize - fillcnt)
                            offs += nsize - fillcnt
                            fillcnt = nsize
                        }
                        rta = aa
                    }

                    rta = setSizes(rta, height - 1)
                    ix = nix
                    offset = offs
                    nall(i) = rta
                }
                i += 1
            }
        } // end bottom
        nall
    }

    // From prototype
    private final def setSizes(a: Array[AnyRef], height: Int) = {
        var sigma = 0
        val lena = a.length - INVAR
        val szs = new Array[Int](lena)
        //cost+=lena
        var i = 0
        while (i < lena) {
            sigma += sizeSubTrie(a(i + 1), height - 1, 0)
            szs(i) = sigma
            i += 1
        }
        a(0) = szs
        a
    }

    // From prototype
    private final def sizeSubTrie(treeNode: AnyRef, height: Int, acc: Int): Int = {
        if (height > 1) {
            val treeBranch = treeNode.asInstanceOf[Array[AnyRef]]
            val len = treeBranch.length
            if (treeBranch(0) == null) {
                val sltsz = height - 1
                sizeSubTrie(treeBranch(len - 1), sltsz, acc + (1 << (WIDTH_SHIFT * sltsz)) * (len - 1 - INVAR))
            } else {
                val sn = treeBranch(0).asInstanceOf[Array[Int]]
                acc + sn(sn.length - 1)
            }
        } else {
            acc + treeNode.asInstanceOf[Array[AnyRef]].length
        }
    }

    // From prototype
    private final def sizeSlot(a: AnyRef, height: Int) = {
        if (a == null)
            throw new IllegalArgumentException("sizeSlot NULL")
        else if (height > 1)
            a.asInstanceOf[Array[AnyRef]].length - INVAR
        else
            a.asInstanceOf[Array[AnyRef]].length
    }

    // From prototype
    private final def araNewAbove(gal: AnyRef): Array[AnyRef] = {
        val na = new Array[AnyRef](1 + INVAR)
        na(1) = gal
        na
    }

    // From prototype
    private final def araNewAbove(til: AnyRef, tir: AnyRef): Array[AnyRef] = {
        val na = new Array[AnyRef](2 + INVAR)
        na(1) = til
        na(2) = tir
        na
    }

    // From prototype
    private final def araNewCopy(nall: Array[AnyRef], start: Int, len: Int) = {
        val na = new Array[AnyRef](len + INVAR)
        Platform.arraycopy(nall, start, na, 1, len)
        na
    }

    // From prototype
    private final def araNewJoin(al: Array[AnyRef], ac: Array[AnyRef], ar: Array[AnyRef]): Array[AnyRef] = {
        // result does not contain size slot!!!
        val lenl = if (al != null) al.length - 2 else 0
        val lenc = if (ac != null) ac.length - 1 else 0
        val lenr = if (ar != null) ar.length - 2 else 0
        var allx = 0
        val all = new Array[AnyRef](lenl + lenc + lenr)
        if (lenl > 0) {
            //for(i<-0 until lenl) all(i)=al(i+1)
            System.arraycopy(al, 1, all, 0, lenl)
            allx += lenl
        }
        //for(i<-0 until lenc) all(i+allx)=ac(i+1)
        System.arraycopy(ac, 1, all, allx, lenc)
        allx += lenc // <--- bug? wouldn't that exceed range of ac???
        if (lenr > 0) {
            //for(i<-0 until lenr)all(i+allx)=ar(i+2)
            System.arraycopy(ar, 2, all, allx, lenr)
        }
        all
    }


}


private[immutable] final object VectorProps {
    private[immutable] final val WIDTH_SHIFT = 5
    private[immutable] final val WIDTH = (1 << WIDTH_SHIFT)
    // sets min standard size for a slot ie w-invar
    private[immutable] final val INVAR = 1
    // sets number of extra slots allowed, ie linear search limit
    private[immutable] final val EXTRAS = 2

}