package scala.collection.immutable.rrbprototype

import java.lang.Math.{max => mmax, min => mmin}

import scala.collection._
import scala.collection.generic._
import scala.collection.immutable.IndexedSeq
import scala.collection.mutable.Builder


// companion object

object Vector extends SeqFactory[Vector] {
    @inline implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, Vector[A]] =
        ReusableCBF.asInstanceOf[CanBuildFrom[Coll, A, Vector[A]]]

    def newBuilder[A]: Builder[A, Vector[A]] = new VectorBuilder[A]

    private[rrbprototype] val NIL = new Vector[Nothing](null, 0, 0)

    @inline override def empty[A]: Vector[A] = NIL


    // zero element vector
    def apply() = NIL

    // create a single element vector
    def apply[A](elem: A): Vector[A] = {
        val data = new Array[AnyRef](1)
        data(0) = elem.asInstanceOf[AnyRef]
        new Vector[A](data, 1, 32)
    }

    /*
      def vec[A:ClassManifest](elem: A): _root_.Vector.vec[A] = {
        val data = new Array[A](1)
        data(0) = elem
        val v = new _root_.Vector.vec[A]
        v.root = data
        v.vHw = 32
        v.vSize = 1
        v
      }
    */

}


// builder class, using concat -- has not been optimized!

final class VectorBuilder[A]() extends Builder[A, Vector[A]] {

    var acc = Vector.empty[A]

    def +=(elem: A): this.type = {
        acc = acc ++ Vector(elem)
        this
    }

    override def ++=(xs: TraversableOnce[A]): this.type = xs match {
        case v: Vector[A] => acc = acc ++ v; this
        case _ => super.++=(xs)
    }

    def result: Vector[A] = acc

    def clear(): Unit = {
        acc = Vector.empty[A]
    }
}


// vector collection class

final class Vector[+A] private
  extends /*AbstractSeq[A]
   with*/ IndexedSeq[A]
  with GenericTraversableTemplate[A, Vector]
  with IndexedSeqLike[A, Vector[A]] {
    // constants and type aliases

    private final val Width = 32
    private final val Invar = 1
    // sets min standard size for a slot ie w-invar
    private final val Extras = 2 // sets number of extra slots allowed, ie linear search limit

    type TreeNode = AnyRef

    type Ara = Array[AnyRef]
    type GTa = Array[AnyRef] // could use Array[T], but then accessors need to box/unbox internally (barring @specialization)

    // instance fields

    //var cost = 0
    //val t = false

    /*private*/ var root: TreeNode = null

    /*private*/ var vSize = 0
    // size of vector
    /*private*/ var vHw = 0 // height of vector w, w^2, w^3, ...

    // constructor
    private def this(r: AnyRef, s: Int, hw: Int) = {
        this()
        root = r
        vSize = s
        vHw = hw
    }

    // collection interface
    override def companion: GenericCompanion[Vector] = Vector

    def length = vSize


    // index and update

    override def lengthCompare(len: Int): Int = vSize - len

    def apply(index: Int): A = {
        indexAt(index).asInstanceOf[A]
    }

    @inline override def updated[B >: A, That](index: Int, elem: B)(implicit bf: CanBuildFrom[Vector[A], B, That]): That = {
        // just ignore bf
        updateAt(index, elem).asInstanceOf[That]
    }

    // slices (take and drop)

    override def take(n: Int): Vector[A] = {
        if (n <= 0)
            Vector.empty
        else if (n < vSize)
            sliceR(n)
        else
            this
    }

    override def drop(n: Int): Vector[A] = {
        if (n <= 0)
            this
        else if (n < vSize)
            sliceL(n)
        else
            Vector.empty
    }

    override def takeRight(n: Int): Vector[A] = {
        if (n <= 0)
            Vector.empty
        else if (vSize - n > 0)
            sliceL(vSize - n)
        else
            this
    }

    override def dropRight(n: Int): Vector[A] = {
        if (n <= 0)
            this
        else if (vSize - n > 0)
            sliceR(vSize - n)
        else
            Vector.empty
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

    override /*IterableLike*/ def slice(from: Int, until: Int): Vector[A] =
        take(until).drop(from)

    override /*IterableLike*/ def splitAt(n: Int): (Vector[A], Vector[A]) = (take(n), drop(n))


    // concat interface

    def ++[U >: A](b: Vector[U]): Vector[U] = {
        val a = this
        if (a.vSize == 0) b
        else if (b.vSize == 0) a
        else {
            // create new vector
            val nvec = new Vector[U]
            // vSize and vHw fields left uninitialized
            val tnca = nvec.concatSubTree(a.root, a.vHw, b.root, b.vHw, true)
            nvec.root = if ((a.vHw == Width) && (b.vHw == Width) && (a.vSize + b.vSize <= Width))
                tnca.asInstanceOf[Ara](1)
            else
                setSizes(tnca, mmax(a.vHw, b.vHw))
            nvec.vSize = a.vSize + b.vSize
            nvec.vHw = findhw(nvec.root) //TR: can we do without the findhw?
            nvec
        }
    }

    override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[Vector[A], B, That]): That = {
        that match {
            case v: Vector[B] => this.++[B](v).asInstanceOf[That]
            case _ => super.++(that)
        }
    }

    @inline override def +:[B >: A, That](elem: B)(implicit bf: CanBuildFrom[Vector[A], B, That]): That = {
        // just ignore bf
        (Vector(elem) ++ this).asInstanceOf[That]
    }

    @inline override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[Vector[A], B, That]): That = {
        // just ignore bf
        (this ++ Vector(elem)).asInstanceOf[That]
    }


    // patch: split + concat

    override def patch[B >: A, That](from: Int, patch: GenSeq[B], replaced: Int)(implicit bf: CanBuildFrom[Vector[A], B, That]): That = {
        // just ignore bf
        val insert = patch.nonEmpty
        val delete = replaced != 0
        if (insert || delete) {
            val prefix = take(from)
            val rest = drop(from + replaced)
            ((prefix ++ patch).asInstanceOf[Vector[B]] ++ rest).asInstanceOf[That]
        } else this.asInstanceOf[That]
    }


    // #### concat implementation ####

    // IsTop
    private def concatSubTree(til: TreeNode, hwl: Int, tir: TreeNode, hwr: Int, isTop: Boolean): Ara = {
        if (hwl > hwr) {
            // left vector higher than right
            val tnla = til.asInstanceOf[Ara]
            val tnca = concatSubTree(tnla(tnla.length - 1), hwl / Width, tir, hwr, false)
            rebalance(tnla, tnca, null, hwl, isTop)
        } else if (hwl < hwr) {
            // right vector higher than left
            val tnra = tir.asInstanceOf[Ara]
            val tnca = concatSubTree(til, hwl, tnra(1), hwr / Width, false)
            rebalance(null, tnca, tnra, hwr, isTop)
        } else {
            // same height
            if (hwl == Width) {
                // height = w so at bottom
                val gnla = til.asInstanceOf[GTa]
                val gnra = tir.asInstanceOf[GTa]
                val lenl = gtaLength(gnla)
                val lenr = gtaLength(gnra)
                if (isTop && (lenr + lenl <= Width)) {
                    // sum of two less than w so copy into one if at top of vectors
                    araNewAbove(gtaNewJoin(gnla, gnra))
                } else {
                    // else simply return the two subtrees as they will be balanced
                    // at the next level up
                    araNewAbove(til, tir)
                }
            } else {
                // two heights the same so move down both
                val tnla = til.asInstanceOf[Ara]
                val tnra = tir.asInstanceOf[Ara]
                val tnca = concatSubTree(tnla(tnla.length - 1), hwl / Width, tnra(1), hwr / Width, false)
                rebalance(tnla, tnca, tnra, hwl, isTop)
            }
        }
    }

    private def gtaLength(a: GTa) = a.length

    private def gtaNewJoin(gnla: GTa, gnra: GTa): GTa = {
        val lenl = gnla.length
        val lenr = gnra.length
        val gal = new GTa(lenr + lenl)
        //for(i<-0 until lenl)gal(i)=gnla(i)
        System.arraycopy(gnla, 0, gal, 0, lenl)
        //for(i<-0 until lenr)gal(i+lenl)=gnra(i)
        System.arraycopy(gnra, 0, gal, lenl, lenr)
        gal
    }

    private def araNewAbove(gal: TreeNode): Ara = {
        val na = new Ara(2)
        na(0) = null;
        na(1) = gal
        na
    }

    private def araNewAbove(til: TreeNode, tir: TreeNode): Ara = {
        val na = new Ara(3);
        na(0) = null
        //cost+=3
        na(1) = til
        na(2) = tir
        na
    }

    private def araNewJoin(al: Ara, ac: Ara, ar: Ara): Ara = {
        // result does not contain size slot!!!
        val lenl = if (al != null) al.length - 2 else 0
        val lenc = if (ac != null) ac.length - 1 else 0
        val lenr = if (ar != null) ar.length - 2 else 0
        var allx = 0
        val all = new Ara(lenl + lenc + lenr)
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

    private def araNewCopy(nall: Ara, start: Int, len: Int) = {
        val na = new Ara(len + 1)
        //for(i<-0 until len)na(i+1)=nall(start+i+1)
        System.arraycopy(nall, start + 1, na, 1, len)
        na
    }

    /*
    CONTRACT:

    al,ac,ar  Tree_h, how many slots each? --> algorithm bounds ac <= 2

    result Tree_{h+1}, 1 or 2 slots

    may be Tree_h if isTop (in that sense, result has 0 slots)

    */

    private def rebalance(al: Ara, ac: Ara, ar: Ara, hw: Int, IsTop: Boolean): Ara = {
        // Put all the slots at this level in one array ++Note:  This can be avoided by indexing the sub arrays as one
        // remember Ara(0) is Size
        val all = araNewJoin(al, ac, ar)

        // shuffle slot sizes to fit invariant
        val szs = shuffle(all, hw)
        val slen = this.vSize // use object field to transport 2nd return value

        //println("shuffle: "+hw+ " " + (all map { (x:AnyRef) => x match {case a: Array[AnyRef] => a.mkString("{,",",","}") }} mkString))//TR
        //println("szs: "+szs.mkString)//TR


        // Now copy across according to model sizes in szs
        val nall = copyAcross(all, szs, slen, hw)

        // nall.length = slen + 1 (accommodate size slot)

        // split across two nodes if greater than Width
        // This splitting/copying can be avoided by moving this logic into the copyAcross
        // and only creating one or two arrays as needed.
        if (slen <= Width) {
            if (!IsTop) {
                araNewAbove(setSizes(nall, hw))
            } else {
                nall
            }
        } else {
            val nal = araNewCopy(nall, 0, Width)
            val nar = araNewCopy(nall, Width, slen - Width)
            araNewAbove(setSizes(nal, hw), setSizes(nar, hw))
        }
    }


    /*
    In writing up the description I realized that the shuffle cost can be reduced. When you look at the drawing and understand what we are doing it becomes obvious that we do not need to gather the pieces into one array. We can in fact calculate the size of the needed array and then do the shuffle only on the new edge array. Similarly the associated size array to do the shuffling can be the needed size array. Hence no wasted copying.
    */

    // returns an array with the desired slot sizes.
    // This version allows an Extra number of slots however many slots.
    // if the slots are less than w then as many as Extra+1 could be small
    // while if the total number of slots at the level are as great as 2w
    // then still only Extra can be small

    private def shuffle(all: Ara, hw: Int): Array[Int] = {
        // (Array[Int],Int) <--- 2nd return value transported via this.vSize
        val alen = all.length
        val szs = new Array[Int](alen)

        var tcnt = 0
        // find total slots in the two levels.
        var i = 0
        while (i < alen) {
            val sz = sizeSlot(all(i), hw / Width)
            szs(i) = sz
            tcnt += sz
            i += 1
        }

        // szs(i) holds #slots of all(i), tcnt is sum
        // ---

        // Calculate the ideal or effective number of slots
        // used to limit number of extra slots.
        val effslt = tcnt / Width + 1 // <-- "desired" number of slots???

        val MinWidth = Width - Invar // min number of slots allowed...

        var nalen = alen
        // note - this makes multiple passes, can be done in one.
        // redistribute the smallest slots until only the allowed extras remain
        while (nalen > effslt + Extras) {
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
                val msz = mmin(el + szs(ix + 1), Width)
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
        this.vSize = nalen // transport to caller
        szs
    }

    // Takes the slot size model and copies across slots to match it.
    private def copyAcross(all: Ara, szs: Array[Int], slen: Int, hw: Int): Ara = {

        val nall = new Ara(slen + 1)
        var ix = 0 // index into the all input array
        var offset = 0 // offset into an individual slot array.
        // It points to the next sub tree in the array to be copied

        val isOneAboveBottom = hw == Width * Width
        if (isOneAboveBottom) {
            var i = 0
            while (i < slen) {
                val nsize = szs(i)
                val ge = all(ix).asInstanceOf[GTa]
                val asIs = (offset == 0) && (nsize == ge.length)

                if (asIs) {
                    ix += 1;
                    nall(i + 1) = ge
                } else {
                    var fillcnt = 0
                    var offs = offset
                    var nix = ix
                    var rta: GTa = null

                    var ga: GTa = null
                    // collect enough slots together to match the size needed
                    while ((fillcnt < nsize) && (nix < all.length)) {
                        val gaa = all(nix).asInstanceOf[GTa]
                        ga = if (fillcnt == 0) new GTa(nsize) else ga
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
                    nall(i + 1) = rta
                }
                i += 1
            }

        } else {
            // not bottom

            var i = 0
            while (i < slen) {
                val nsize = szs(i)
                val ae = all(ix).asInstanceOf[Ara]
                val asIs = (offset == 0) && (nsize == ae.length - 1)

                if (asIs) {
                    ix += 1;
                    nall(i + 1) = ae
                } else {
                    var fillcnt = 0
                    var offs = offset
                    var nix = ix
                    var rta: Ara = null

                    var aa: Ara = null
                    // collect enough slots together to match the size needed
                    while ((fillcnt < nsize) && (nix < all.length)) {
                        val aaa = all(nix).asInstanceOf[Ara]
                        aa = if (fillcnt == 0) new Ara(nsize + 1) else aa
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

                    rta = setSizes(rta, hw / Width)
                    ix = nix
                    offset = offs
                    nall(i + 1) = rta
                }
                i += 1
            }
        } // end bottom
        nall
    }


    private def findhw(n: TreeNode): Int = n match {
        // FIXME: can't match!!
        case a: Array[AnyRef] if (a.length > 0 && ((a(0) eq null) || a(0).isInstanceOf[Array[Int]])) => findhw(a(1)) * Width
        case _ => Width
    }

    /*
      private def findhw(n:TreeNode):Int= n match { // FIXME: can't match!!
        case a:Ara=>findhw(a(1))*Width
        case g:GTa=>Width
      }
    */

    // Finds the sizes of all the sub trees
    private def setSizes(a: Ara, hw: Int) = {
        var sigma = 0
        val lena = a.length - 1
        val szs = new Array[Int](lena)
        //cost+=lena
        var i = 0
        while (i < lena) {
            sigma += sizeSubTrie(a(i + 1), hw / Width)
            //if(t)println("i,lena,sigma",i,lena,sigma)
            szs(i) = sigma
            i += 1
        }
        a(0) = szs
        a
    }

    // Find the size of one array
    private def sizeSlot(a: TreeNode, hw: Int) = {
        if (a == null) {
            throw new IllegalArgumentException("sizeSlot NULL");
            0
        }
        else {
            /*      a match { // FIXME
                    case aa:Ara=>aa.length-1 // allow for size in aa(0)
                    case at:GTa=>at.length
                    }
            */
            if (hw > Width) // Ara case
                a.asInstanceOf[Ara].length - 1 // allow for size in aa(0)
            else
                a.asInstanceOf[GTa].length
        }
    }

    // returns the top weight for a given size
    private def sztohw(sz: Int) = {
        var hw = Width
        while (sz > hw) hw *= Width
        hw
    }

    private def sizeSubTrie(tn: TreeNode, hw: Int): Int = {
        if (hw > Width) {
            val in = tn.asInstanceOf[Ara]
            if (in(0) == null) {
                val len = in.length
                val sltsz = hw / Width
                sltsz * (len - 2) + sizeSubTrie(in(len - 1), sltsz)
            } else {
                val sn = in(0).asInstanceOf[Array[Int]]
                sn(sn.length - 1)
            }
        } else {
            val vn = tn.asInstanceOf[GTa]
            vn.length
        }
    }


    // ### implementation ###
    /*   def slice(left:Int,right:Int):vec[T]={
        val s=sliceR(right)
        //s.printTrie
        s.sliceL(left)
        }*/

    private def sliceR(right: Int): Vector[A] = {
        if ((right < vSize) && (right >= 0) && (root != null)) {
            val nv = new Vector[A]
            val n = nv.rSliceDown2(root, right - 1, vHw, false)
            //nv.vHw=hw set by callee
            nv.vSize = right
            nv.root = n
            nv
        } else this
    }

    private def rSliceDown2(n: AnyRef, right: Int, hw: Int, hasLeft: Boolean): AnyRef = {
        // (AnyRef,Int) <-- second item transported via this.vHw
        // Works but can be simplified
        val sw = hw / Width
        var is = right / sw
        if (hw > Width) {
            val in = n.asInstanceOf[Ara]
            val len = in.length - 1
            if (in(0) == null) {
                // Aligned vector
                val rhn = rSliceDown2(in(is + 1), right - is * sw, hw / Width, (is != 0) || hasLeft)
                val hwr = this.vHw
                if (is == 0) {
                    if (hasLeft) {
                        // has left above so return add level and return right node/height
                        val rcnodes = new Array[AnyRef](2)
                        //cost+=2
                        rcnodes(1) = rhn
                        rcnodes(0) = null
                        this.vHw = hw
                        rcnodes
                    } else {
                        this.vHw = hwr
                        rhn // nothing on left above so just return right node and height
                    }
                } else {
                    // Make copy of remaining left node
                    val cnodes = new Array[AnyRef](is + 2)
                    //cost+=is+2
                    //for(i<-0 until is)cnodes(i+1)=in(i+1)
                    System.arraycopy(in, 1, cnodes, 1, is)
                    cnodes(is + 1) = rhn
                    cnodes(0) = null
                    this.vHw = hw
                    cnodes
                }
            } else {
                val szs = in(0).asInstanceOf[Array[Int]]
                var ix = right

                while (szs(is) <= ix) is += 1
                ix = ix - (if (is == 0) 0 else szs(is - 1))
                val nn = in.asInstanceOf[Ara](is + 1)

                val rhn = rSliceDown2(nn, ix, hw / Width, (is != 0) || hasLeft)
                val hwr = this.vHw
                if (is == 0) {
                    if (hasLeft) {
                        val rcnodes = new Array[AnyRef](2)
                        val rsizes = new Array[Int](1)
                        //cost+=3
                        rcnodes(1) = rhn
                        rsizes(0) = right + 1 //++++
                        rcnodes(0) = rsizes
                        this.vHw = hw
                        rcnodes
                    } else {
                        this.vHw = hwr
                        rhn // nothing on left so return right node and height
                    }
                } else {
                    val cnodes = new Array[AnyRef](is + 2)
                    val sizes = new Array[Int](is + 1)
                    //cost+=2*is+1
                    /*for(i<-0 until is){
                       cnodes(i+1)=in(i+1)
                       sizes(i)=szs(i)
                    }*/
                    System.arraycopy(in, 1, cnodes, 1, is)
                    System.arraycopy(szs, 0, sizes, 0, is)
                    cnodes(0) = sizes
                    sizes(is) = right + 1
                    cnodes(is + 1) = rhn
                    this.vHw = hw
                    cnodes
                }
            }
        } else {
            val vn = n.asInstanceOf[GTa]
            // copy up to is
            var lvals = new GTa(is + 1)
            //cost+=is+1
            //for(i<-0 to is)lvals(i)=vn(i).asInstanceOf[AnyRef]
            System.arraycopy(vn, 0, lvals, 0, is + 1)
            this.vHw = hw
            lvals
        }
    }

    private def sliceL(left: Int): Vector[A] = {
        if (left >= vSize) new Vector[A]
        else if ((left > 0) && (root != null)) {
            val nv = new Vector[A]
            val n = nv.lSliceDown2(root, left, vHw, false)
            //nv.vHw=hw done by callee
            nv.vSize = vSize - left
            nv.root = n
            nv
        } else this
    }

    // hasRight flags there are more slots to the right of this one
    private def lSliceDown2(n: AnyRef, left: Int, hw: Int, hasRight: Boolean): AnyRef = {
        // (AnyRef,Int) <-- second item transported via this.vHw
        val sw = hw / Width
        var is = left / sw
        if (hw > Width) {
            val in = n.asInstanceOf[Ara]
            val len = in.length - 1
            var inl = null: AnyRef
            var ist = 0
            var ix = 0
            if (in(0) != null) {
                // is a sized node so find index position
                val szs = in(0).asInstanceOf[Array[Int]]
                ix = left
                ist = is
                while (szs(ist) <= ix) ist += 1
                ix = ix - (if (ist == 0) 0 else szs(ist - 1))
                inl = in.asInstanceOf[Ara](ist + 1)
            } else {
                inl = in(is + 1)
                ist = is
                ix = left - is * sw
            }

            val lastslt = len - 1
            val lhn = lSliceDown2(inl, ix, hw / Width, (ist != lastslt) || hasRight)
            val hwr = this.vHw
            if (ist == lastslt) {
                // no more slots to left
                if (hasRight) {
                    val rcnodes = new Array[AnyRef](2)
                    //cost+=2
                    rcnodes(1) = lhn
                    rcnodes(0) = null
                    this.vHw = hw
                    rcnodes
                } else {
                    this.vHw = hwr
                    lhn // nothing on left so return right node and height
                }
            } else {
                // has slots on left so copy them across
                val cnodes = new Array[AnyRef](len - ist + 1)
                //for(i<-0 until len-ist-1)cnodes(i+2)=in(ist+2+i)
                System.arraycopy(in, ist + 2, cnodes, 2, len - ist - 1)
                val szs = in(0).asInstanceOf[Array[Int]]
                val rsizes = new Array[Int](len - ist)
                //cost+=2*(len-ist)+1
                /*for(i<-0 until len-ist){
                      val sz=if(in(0)!=null)szs(ist+i) else sw*(ist+1+i)
                      rsizes(i)=sz-left
                    }*/
                if (in(0) != null) {
                    var i = 0
                    while (i < (len - ist)) {
                        rsizes(i) = szs(ist + i) - left
                        i += 1
                    }
                } else {
                    var i = 0
                    while (i < (len - ist)) {
                        rsizes(i) = sw * (ist + 1 + i)
                        i += 1
                    }
                }
                cnodes(0) = rsizes
                cnodes(1) = lhn
                this.vHw = hw
                cnodes
            }
        } else {
            val vn = n.asInstanceOf[GTa]
            // copy from is to end
            val lenv = vn.length
            var lvals = new GTa(lenv - is)
            //cost+=lenv-is
            //for(i<-is until lenv)lvals(i-is)=vn(i).asInstanceOf[AnyRef]
            System.arraycopy(vn, is, lvals, 0, lenv - is)
            this.vHw = hw
            lvals
        }
    }


    // #### update implementation ####

    private def updateAt[B >: A](index: Int, value: B): Vector[B] = {
        if ((index < 0) || (index >= vSize) || (root == null)) this
        else {
            //val hw=sztohw(vSize)
            val nvec = new Vector[B]
            nvec.root = updateTrie(root, index, value.asInstanceOf[AnyRef], vHw)
            nvec.vSize = vSize
            nvec.vHw = vHw
            nvec
        }
    }

    private def updateTrie(tn: TreeNode, ix: Int, value: AnyRef /* A */ , hw: Int): AnyRef = {
        val sw = hw / Width
        var is = ix / sw
        if (hw > Width) {
            val in = tn.asInstanceOf[Ara]
            val subn = if (in(0) == null) updateTrie(in(is), ix - is * sw, value, hw / Width)
            else {
                val szs = in(0).asInstanceOf[Array[Int]]
                while (szs(is) <= ix) is += 1
                val nix = ix - (if (is == 0) 0 else szs(is - 1))
                updateTrie(in(1 + is), nix, value, hw / Width)
            }
            val len = in.length - 1
            val cnodes = new Ara(len + 1)
            //for(i<- 0 to len)cnodes(i)=in(i)
            System.arraycopy(in, 0, cnodes, 0, len + 1)
            cnodes(is + 1) = subn
            cnodes
        } else {
            val vn = tn.asInstanceOf[GTa]
            val len = vn.length
            val lvals = new GTa(len)
            //for(i<- 0 until len)lvals(i)=vn(i)
            System.arraycopy(vn, 0, lvals, 0, len)
            lvals(is) = value
            lvals
        }
    }


    // #### index implementation ####

    final val S5 = 1 << 5
    final val S10 = 1 << 10
    final val S15 = 1 << 15
    final val S20 = 1 << 20
    final val S25 = 1 << 25
    final val S30 = 1 << 30

    // Index method optimised for regular vectors

    private def indexAt(index: Int): AnyRef = {
        //if(Width!=32)indexAll(index)  // Only used for test purposes with vectors not based on 32.
        //else{                         // Can be removed for vector implementation
        var ix = index
        def sized(ia: AnyRef, sp: Int): AnyRef = {
            val szs = ia.asInstanceOf[Ara](0).asInstanceOf[Array[Int]]

            var is = ix >> sp
            while (szs(is) <= ix) is += 1
            ix = ix - (if (is == 0) 0 else szs(is - 1))
            ia.asInstanceOf[Ara](is + 1)
            /*
                          // To eveluate bin or linear search use commented code below here

                         var l = 0
                         var r=szs.length-1
                         var m=r/2
                         while(r-l>1){
                           if(szs(m)>ix)r=m
                           else l=m
                           m=(r+l)/2
                         }
                         val ps=if(ix<szs(l))l else r
                         ix=ix-(if(ps==0)0 else szs(ps-1))
                         ia.asInstanceOf[Ara](ps+1)
            */
        }
        if ((ix < 0) || (ix >= vSize)) throw new IndexOutOfBoundsException(ix.toString)
        else {
            vHw match {
                case S5 => root.asInstanceOf[GTa](ix)
                case S10 =>
                    val n1 = if (root.asInstanceOf[Ara](0) == null) root.asInstanceOf[Ara]((ix >> 5) + 1) else sized(root, 5)
                    n1.asInstanceOf[GTa](ix & 31)
                case S15 =>
                    val n1 = if (root.asInstanceOf[Ara](0) == null) root.asInstanceOf[Ara]((ix >> 10) + 1) else sized(root, 10)
                    val n2 = if (n1.asInstanceOf[Ara](0) == null) n1.asInstanceOf[Ara](((ix >> 5) & 31) + 1) else sized(n1, 5)
                    n2.asInstanceOf[GTa](ix & 31)
                case S20 =>
                    val n1 = if (root.asInstanceOf[Ara](0) == null) root.asInstanceOf[Ara]((ix >> 15) + 1) else sized(root, 15)
                    val n2 = if (n1.asInstanceOf[Ara](0) == null) n1.asInstanceOf[Ara](((ix >> 10) & 31) + 1) else sized(n1, 10)
                    val n3 = if (n2.asInstanceOf[Ara](0) == null) n2.asInstanceOf[Ara](((ix >> 5) & 31) + 1) else sized(n2, 5)
                    n3.asInstanceOf[GTa](ix & 31)
                case S25 =>
                    val n1 = if (root.asInstanceOf[Ara](0) == null) root.asInstanceOf[Ara]((ix >> 20) + 1) else sized(root, 20)
                    val n2 = if (n1.asInstanceOf[Ara](0) == null) n1.asInstanceOf[Ara](((ix >> 15) & 31) + 1) else sized(n1, 15)
                    val n3 = if (n2.asInstanceOf[Ara](0) == null) n2.asInstanceOf[Ara](((ix >> 10) & 31) + 1) else sized(n2, 10)
                    val n4 = if (n3.asInstanceOf[Ara](0) == null) n3.asInstanceOf[Ara](((ix >> 5) & 31) + 1) else sized(n3, 5)
                    n4.asInstanceOf[GTa](ix & 31)
                case S30 =>
                    val n1 = if (root.asInstanceOf[Ara](0) == null) root.asInstanceOf[Ara]((ix >> 25) + 1) else sized(root, 25)
                    val n2 = if (n1.asInstanceOf[Ara](0) == null) n1.asInstanceOf[Ara](((ix >> 20) & 31) + 1) else sized(n1, 20)
                    val n3 = if (n2.asInstanceOf[Ara](0) == null) n2.asInstanceOf[Ara](((ix >> 15) & 31) + 1) else sized(n2, 15)
                    val n4 = if (n3.asInstanceOf[Ara](0) == null) n3.asInstanceOf[Ara](((ix >> 10) & 31) + 1) else sized(n3, 10)
                    val n5 = if (n4.asInstanceOf[Ara](0) == null) n4.asInstanceOf[Ara](((ix >> 5) & 31) + 1) else sized(n4, 5)
                    n5.asInstanceOf[GTa](ix & 31)
                case _ => throw new IllegalArgumentException()
            }
        }
    }

}