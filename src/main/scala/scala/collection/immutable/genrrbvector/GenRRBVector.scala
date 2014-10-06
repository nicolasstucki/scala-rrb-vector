package scala {
  package collection {
    package immutable {
      package genrrbvector {
        import scala.annotation.tailrec

        import scala.annotation.unchecked.uncheckedVariance

        import scala.collection.generic.{GenericCompanion, GenericTraversableTemplate, CanBuildFrom, IndexedSeqFactory}

        import scala.compat.Platform

        object GenRRBVector extends scala.collection.generic.IndexedSeqFactory[GenRRBVector] {
          def newBuilder[A]: mutable.Builder[A, GenRRBVector[A]] = new GenRRBVectorBuilder[A]();
          implicit def canBuildFrom[A]: scala.collection.generic.CanBuildFrom[Coll, A, GenRRBVector[A]] = ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]];
          private val NIL = new GenRRBVector[Nothing](0);
          override def empty[A]: GenRRBVector[A] = NIL;
          final private[immutable] def singleton[A](value: A): GenRRBVector[A] = {
            val vec = new GenRRBVector[A](1);
            vec.display0 = new Array[AnyRef](32);
            vec.display0.update(0, value.asInstanceOf[AnyRef]);
            vec.depth = 1;
            vec.focusEnd = 1;
            vec.focusDepth = 1;
            vec.hasWritableTail = true;
            vec
          };
          final private[immutable] val useAssertions = false
        }

        final class GenRRBVector[+A](val endIndex: Int) extends scala.collection.AbstractSeq[A] with scala.collection.immutable.IndexedSeq[A] with scala.collection.generic.GenericTraversableTemplate[A, GenRRBVector] with scala.collection.IndexedSeqLike[A, GenRRBVector[A]] with GenRRBVectorPointer[A @uncheckedVariance] with Serializable { self =>
          override def companion: scala.collection.generic.GenericCompanion[GenRRBVector] = GenRRBVector;
          def length(): Int = endIndex;
          override def lengthCompare(len: Int): Int = length.-(len);
          private[collection] def initIterator[B >: A](s: GenRRBVectorIterator[B]): scala.Unit = {
            s.initFrom(this);
            if (depth.>(0))
              s.resetIterator()
            else
              ()
          };
          private[collection] def initIterator[B >: A](s: GenRRBVectorReverseIterator[B]): scala.Unit = {
            s.initFrom(this);
            if (depth.>(0))
              s.initIterator()
            else
              ()
          };
          override def iterator: GenRRBVectorIterator[A] = {
            val s = new GenRRBVectorIterator[A](0, endIndex);
            initIterator(s);
            s
          };
          override def reverseIterator: GenRRBVectorReverseIterator[A] = {
            val s = new GenRRBVectorReverseIterator[A](0, endIndex);
            initIterator(s);
            s
          };
          def apply(index: Int): A = {
            val _focusStart = this.focusStart;
            if (_focusStart.<=(index).&&(index.<(focusEnd)))
              {
                val indexInFocus = index.-(_focusStart);
                getElement(indexInFocus, indexInFocus.^(focus)).asInstanceOf[A]
              }
            else
              if ((0).<=(index).&&(index.<(endIndex)))
                {
                  gotoPosRelaxed(index, 0, endIndex, depth);
                  display0(index.-(_focusStart).&(31)).asInstanceOf[A]
                }
              else
                throw new IndexOutOfBoundsException(index.toString)
          };
          override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[GenRRBVector[A], B, That]): That = if (bf.eq(IndexedSeq.ReusableCBF))
            appendedBack(elem).asInstanceOf[That]
          else
            super.:+(elem)(bf);
          override def isEmpty: Boolean = endIndex.==(0);
          override def head: A = {
            if (isEmpty)
              throw new UnsupportedOperationException("empty.head")
            else
              ();
            apply(0)
          };
          override def slice(from: Int, until: Int): GenRRBVector[A] = take(until).drop(from);
          override def splitAt(n: Int): scala.Tuple2[GenRRBVector[A], GenRRBVector[A]] = scala.Tuple2(take(n), drop(n));
          override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[GenRRBVector[A], B, That]): That = if (bf.eq(IndexedSeq.ReusableCBF))
            if (that.isEmpty)
              this.asInstanceOf[That]
            else
              if (that.isInstanceOf[GenRRBVector[B]])
                {
                  val vec = that.asInstanceOf[GenRRBVector[B]];
                  if (this.isEmpty)
                    vec.asInstanceOf[That]
                  else
                    this.concatenated[B](vec).asInstanceOf[That]
                }
              else
                super.++(that)
          else
            super.++(that.seq);
          override def tail: GenRRBVector[A] = {
            if (isEmpty)
              throw new UnsupportedOperationException("empty.tail")
            else
              ();
            drop(1)
          };
          override def last: A = {
            if (isEmpty)
              throw new UnsupportedOperationException("empty.last")
            else
              ();
            apply(length.-(1))
          };
          override def init: GenRRBVector[A] = {
            if (isEmpty)
              throw new UnsupportedOperationException("empty.init")
            else
              ();
            dropRight(1)
          };
          private def appendedBack[B >: A](value: B): GenRRBVector[B] = {
            val _endIndex = this.endIndex;
            if (_endIndex.==(0))
              return GenRRBVector.singleton[B](value)
            else
              ();
            val vec = new GenRRBVector[B](_endIndex.+(1));
            vec.initFrom(this);
            vec.gotoIndex(_endIndex.-(1), _endIndex.-(1));
            if (this.hasWritableTail)
              {
                this.hasWritableTail = false;
                val elemIndexInBlock = _endIndex.-(vec.focusStart).&(31);
                vec.display0.update(elemIndexInBlock, value.asInstanceOf[AnyRef]);
                vec.focusEnd.+=(1);
                if (elemIndexInBlock.<(31))
                  vec.hasWritableTail = true
                else
                  ()
              }
            else
              vec.appendBackNewTail(value);
            vec
          };
          private def appendBackNewTail[B >: A](value: B): Unit = {
            ();
            val elemIndexInBlock = endIndex.-(focusStart).-(1).&(31);
            val _depth = depth;
            if (elemIndexInBlock.!=(0))
              {
                val deltaSize = (32).-(display0.length);
                display0 = copyOf(display0, display0.length, 32);
                if (_depth.>(1))
                  {
                    val stabilizationIndex = focus.|(focusRelax);
                    val displaySizes = allDisplaySizes();
                    copyDisplays(_depth, stabilizationIndex);
                    stabilize(_depth, stabilizationIndex);
                    if (deltaSize.==(0))
                      putDisplaySizes(displaySizes)
                    else
                      {
                        focusDepth.until(depth).foreach(((i) => {
                          val oldSizes = displaySizes(i.-(1));
                          if (oldSizes.!=(null))
                            {
                              val newSizes = new Array[Int](oldSizes.length);
                              val lastIndex = oldSizes.length.-(1);
                              Platform.arraycopy(oldSizes, 0, newSizes, 0, lastIndex);
                              newSizes.update(lastIndex, oldSizes(lastIndex).+(deltaSize));
                              displaySizes.update(i.-(1), newSizes)
                            }
                          else
                            ()
                        }));
                        putDisplaySizes(displaySizes)
                      }
                  }
                else
                  ();
                display0.update(elemIndexInBlock, value.asInstanceOf[AnyRef]);
                focusEnd.+=(1);
                if (elemIndexInBlock.<(31))
                  hasWritableTail = true
                else
                  ()
              }
            else
              {
                val displaySizes = allDisplaySizes();
                copyDisplays(_depth, focus.|(focusRelax));
                val newRelaxedIndex = endIndex.-(focusStart).-(1).+(focusRelax);
                val xor = newRelaxedIndex.^(focus.|(focusRelax));
                setUpNextBlockStartTailWritable(newRelaxedIndex, xor);
                stabilize(depth, newRelaxedIndex);
                if (_depth.!=(depth))
                  if (endIndex.-(1).==((1).<<((5).*(depth).-(5))))
                    displaySizes.update(depth.-(1), null)
                  else
                    {
                      val newSizes = new Array[Int](2);
                      newSizes.update(0, endIndex.-(1));
                      newSizes.update(1, endIndex.+(31));
                      displaySizes.update(depth.-(1), newSizes)
                    }
                else
                  {
                    focusDepth.until(depth).foreach(((i) => {
                      val oldSizes = displaySizes(i.-(1));
                      val display = i match {
                        case 1 => display1
                        case 2 => display2
                        case 3 => display3
                        case 4 => display4
                        case 5 => display5
                      };
                      val newSizes = new Array[Int](display.length.-(1));
                      Platform.arraycopy(oldSizes, 0, newSizes, 0, oldSizes.length);
                      if (newSizes.length.!=(oldSizes.length))
                        newSizes.update(newSizes.length.-(1), newSizes(newSizes.length.-(2)).+(32))
                      else
                        newSizes(newSizes.length.-(1)).+=(32);
                      displaySizes.update(i.-(1), newSizes)
                    }));
                    putDisplaySizes(displaySizes)
                  };
                display0.update(0, value.asInstanceOf[AnyRef]);
                if (_depth.==(focusDepth))
                  initFocus(endIndex.-(1), 0, endIndex, depth, 0)
                else
                  initFocus(0, endIndex.-(1), endIndex, 1, newRelaxedIndex.&(-32));
                hasWritableTail = true
              }
          };
          private[immutable] def concatenated[B >: A](that: GenRRBVector[B]): GenRRBVector[B] = {
            ();
            ();
            ();
            ();
            this.gotoIndex(this.endIndex.-(1), this.endIndex);
            that.gotoIndex(0, that.endIndex);
            val newSize = this.length.+(that.length);
            def initVector(vec: GenRRBVector[B], concat: Array[AnyRef], depth: Int): Unit = if (concat.length.==(2))
              vec.initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], depth, newSize)
            else
              vec.initFromRoot(withComputedSizes(concat, depth), depth.+(1), newSize);
            val vec = new GenRRBVector[B](newSize);
            @inline def displayLength(display: Array[AnyRef]): Int = if (display.!=(null))
              display.length
            else
              0;
            math.max(this.depth, that.depth) match {
              case 1 => {
                val concat1 = rebalanced(this.display0, null, that.display0, this.endIndex, 0, that.endIndex, 1);
                initVector(vec, concat1, 1)
              }
              case 2 => {
                val concat1 = rebalanced(this.display0, null, that.display0, this.focus.&(31).+(1), 0, if (that.depth.==(1))
                  that.endIndex
                else
                  that.display0.length, 1);
                val concat2 = rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2);
                initVector(vec, concat2, 2)
              }
              case 3 => {
                val concat1 = rebalanced(this.display0, null, that.display0, this.focus.&(31).+(1), 0, if (that.depth.==(1))
                  that.endIndex
                else
                  that.display0.length, 1);
                val concat2 = rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2);
                val concat3 = rebalanced(this.display2, concat2, that.display2, displayLength(this.display2), concat2.length, displayLength(that.display2), 3);
                initVector(vec, concat3, 3)
              }
              case 4 => {
                val concat1 = rebalanced(this.display0, null, that.display0, this.focus.&(31).+(1), 0, if (that.depth.==(1))
                  that.endIndex
                else
                  that.display0.length, 1);
                val concat2 = rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2);
                val concat3 = rebalanced(this.display2, concat2, that.display2, displayLength(this.display2), concat2.length, displayLength(that.display2), 3);
                val concat4 = rebalanced(this.display3, concat3, that.display3, displayLength(this.display3), concat3.length, displayLength(that.display3), 4);
                initVector(vec, concat4, 4)
              }
              case 5 => {
                val concat1 = rebalanced(this.display0, null, that.display0, this.focus.&(31).+(1), 0, if (that.depth.==(1))
                  that.endIndex
                else
                  that.display0.length, 1);
                val concat2 = rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2);
                val concat3 = rebalanced(this.display2, concat2, that.display2, displayLength(this.display2), concat2.length, displayLength(that.display2), 3);
                val concat4 = rebalanced(this.display3, concat3, that.display3, displayLength(this.display3), concat3.length, displayLength(that.display3), 4);
                val concat5 = rebalanced(this.display4, concat4, that.display4, displayLength(this.display4), concat4.length, displayLength(that.display4), 5);
                initVector(vec, concat5, 5)
              }
              case 6 => {
                val concat1 = rebalanced(this.display0, null, that.display0, this.focus.&(31).+(1), 0, if (that.depth.==(1))
                  that.endIndex
                else
                  that.display0.length, 1);
                val concat2 = rebalanced(this.display1, concat1, that.display1, displayLength(this.display1), concat1.length, displayLength(that.display1), 2);
                val concat3 = rebalanced(this.display2, concat2, that.display2, displayLength(this.display2), concat2.length, displayLength(that.display2), 3);
                val concat4 = rebalanced(this.display3, concat3, that.display3, displayLength(this.display3), concat3.length, displayLength(that.display3), 4);
                val concat5 = rebalanced(this.display4, concat4, that.display4, displayLength(this.display4), concat4.length, displayLength(that.display4), 5);
                val concat6 = rebalanced(this.display5, concat5, that.display5, displayLength(this.display5), concat5.length, displayLength(that.display5), 6);
                initVector(vec, concat6, 6)
              }
              case _ => throw new IllegalStateException()
            };
            ();
            vec
          };
          private def rebalanced(displayLeft: Array[AnyRef], concat: Array[AnyRef], displayRight: Array[AnyRef], lengthLeft: Int, lengthConcat: Int, lengthRight: Int, _depth: Int): Array[AnyRef] = {
            var offset = 0;
            val alen = if (_depth.==(1))
              lengthLeft.+(lengthRight)
            else
              lengthLeft.+(lengthConcat).+(lengthRight).-(1).-(if (lengthLeft.==(0))
  0
else
  2).-(if (lengthRight.==(0))
                0
              else
                2);
            val all = new Array[AnyRef](alen);
            if (lengthLeft.>(0))
              {
                val s = lengthLeft.-(if (_depth.==(1))
                  0
                else
                  2);
                Platform.arraycopy(displayLeft, 0, all, 0, s);
                offset.+=(s)
              }
            else
              ();
            if (lengthConcat.>(0))
              {
                Platform.arraycopy(concat, 0, all, offset, concat.length.-(1));
                offset.+=(concat.length.-(1))
              }
            else
              ();
            if (lengthRight.>(0))
              {
                val s = lengthRight.-(if (_depth.==(1))
                  0
                else
                  2);
                Platform.arraycopy(displayRight, if (_depth.==(1))
                  0
                else
                  1, all, offset, s)
              }
            else
              ();
            val x$1 = (computeNewSizes(all, alen, _depth): @scala.unchecked) match {
              case scala.Tuple2((szs @ _), (nalen @ _)) => scala.Tuple2(szs, nalen)
            };
            val szs = x$1._1;
            val nalen = x$1._2;
            copiedAcross(all, szs, nalen, _depth)
          };
          private def computeNewSizes(all: Array[AnyRef], alen: Int, _depth: Int) = {
            val szs = new Array[Int](alen);
            var totalCount = 0;
            var i = 0;
            while (i.<(alen)) 
              {
                val sz = if (_depth.==(1))
                  1
                else
                  if (_depth.==(2))
                    all(i).asInstanceOf[Array[AnyRef]].length
                  else
                    all(i).asInstanceOf[Array[AnyRef]].length.-(1);
                szs.update(i, sz);
                totalCount.+=(sz);
                i.+=(1)
              }
            ;
            val effectiveNumberOfSlots = totalCount./(32).+(1);
            val MinWidth = 31;
            var nalen = alen;
            val EXTRAS = 2;
            while (nalen.>(effectiveNumberOfSlots.+(EXTRAS))) 
              {
                var ix = 0;
                while (szs(ix).>(MinWidth)) 
                  ix.+=(1)
                ;
                var el = szs(ix);
                do 
                  {
                    val msz = math.min(el.+(szs(ix.+(1))), 32);
                    szs.update(ix, msz);
                    el = el.+(szs(ix.+(1))).-(msz);
                    ix.+=(1)
                  }
                 while (el.>(0)) ;
                while (ix.<(nalen.-(1))) 
                  {
                    szs.update(ix, szs(ix.+(1)));
                    ix.+=(1)
                  }
                ;
                nalen.-=(1)
              }
            ;
            scala.Tuple2(szs, nalen)
          };
          private def withComputedSizes(node: Array[AnyRef], depth: Int): Array[AnyRef] = {
            ();
            ();
            ();
            if (depth.>(1))
              {
                var i = 0;
                var acc = 0;
                val end = node.length.-(1);
                val sizes = new Array[Int](end);
                while (i.<(end)) 
                  {
                    acc.+=(treeSize(node(i).asInstanceOf[Array[AnyRef]], depth.-(1)));
                    sizes.update(i, acc);
                    i.+=(1)
                  }
                ;
                node.update(end, sizes)
              }
            else
              {
                var i = 0;
                var acc = 0;
                val end = node.length.-(1);
                val sizes = new Array[Int](end);
                while (i.<(end)) 
                  {
                    acc.+=(node(i).asInstanceOf[Array[AnyRef]].length);
                    sizes.update(i, acc);
                    i.+=(1)
                  }
                ;
                node.update(end, sizes)
              };
            node
          };
          @tailrec private def treeSize(tree: Array[AnyRef], _depth: Int, acc: Int = 0): Int = {
            ();
            ();
            ();
            if (_depth.==(0))
              acc.+(tree.length)
            else
              {
                val treeSizes = tree(tree.length.-(1)).asInstanceOf[Array[Int]];
                if (treeSizes.!=(null))
                  treeSizes(treeSizes.length.-(1))
                else
                  treeSize(tree(tree.length.-(2)).asInstanceOf[Array[AnyRef]], _depth.-(1), acc.+(tree.length.-(2).*((1).<<((5).*(_depth)))))
              }
          };
          private def copiedAcross(all: Array[AnyRef], sizes: Array[Int], lengthSizes: Int, depth: Int): Array[AnyRef] = {
            ();
            ();
            ();
            if (depth.==(1))
              {
                val top = new Array[AnyRef](lengthSizes.+(1));
                var iTop = 0;
                var accSizes = 0;
                while (iTop.<(top.length.-(1))) 
                  {
                    val nodeSize = sizes(iTop);
                    val node = new Array[AnyRef](nodeSize);
                    Platform.arraycopy(all, accSizes, node, 0, nodeSize);
                    accSizes.+=(nodeSize);
                    top.update(iTop, node);
                    iTop.+=(1)
                  }
                ;
                top
              }
            else
              {
                var iAll = 0;
                var allSubNode = all(0).asInstanceOf[Array[AnyRef]];
                var jAll = 0;
                val top = new Array[AnyRef](lengthSizes.>>(5).+(if (lengthSizes.&(31).==(0))
                  1
                else
                  2));
                val topSizes = new Array[Int](top.length.-(1));
                top.update(top.length.-(1), topSizes);
                var iTop = 0;
                while (iTop.<(top.length.-(1))) 
                  {
                    val node = new Array[AnyRef](math.min(33, lengthSizes.+(1).-(iTop.<<(5))));
                    var iNode = 0;
                    while (iNode.<(node.length.-(1))) 
                      {
                        val sizeBottom = sizes(iTop.<<(5).+(iNode));
                        val bottom = new Array[AnyRef](sizeBottom.+(if (depth.==(2))
                          0
                        else
                          1));
                        node.update(iNode, bottom);
                        var iBottom = 0;
                        while (iBottom.<(bottom.length)) 
                          {
                            bottom.update(iBottom, allSubNode(jAll));
                            jAll.+=(1);
                            if (jAll.>=(allSubNode.length))
                              {
                                iAll.+=(1);
                                jAll = 0;
                                if (iAll.<(all.length))
                                  allSubNode = all(iAll).asInstanceOf[Array[AnyRef]]
                                else
                                  ()
                              }
                            else
                              ();
                            iBottom.+=(1)
                          }
                        ;
                        iNode.+=(1)
                      }
                    ;
                    top.update(iTop, withComputedSizes(node, depth.-(1)));
                    iTop.+=(1)
                  }
                ;
                top
              }
          };
          ()
        }

        class GenRRBVectorIterator[+A](startIndex: Int, endIndex: Int) extends AbstractIterator[A] with Iterator[A] with GenRRBVectorPointer[A @uncheckedVariance] {
          private var blockIndex: Int = _;
          private var lo: Int = _;
          private var endLo: Int = _;
          private var _hasNext: Boolean = startIndex.<(endIndex);
          def hasNext = _hasNext;
          final private[immutable] def resetIterator(): Unit = {
            if (focusStart.<=(startIndex).&&(startIndex.<(focusEnd)))
              gotoPos(startIndex, startIndex.^(focus))
            else
              gotoPosRelaxed(startIndex, 0, endIndex, depth);
            blockIndex = focusStart;
            lo = startIndex.-(focusStart);
            endLo = math.min(focusEnd.-(blockIndex), 32)
          };
          def next(): A = if (_hasNext)
            {
              val res = display0(lo).asInstanceOf[A];
              lo.+=(1);
              if (lo.==(endLo))
                {
                  val newBlockIndex = blockIndex.+(endLo);
                  if (newBlockIndex.<(focusEnd))
                    gotoNextBlockStart(newBlockIndex, blockIndex.^(newBlockIndex))
                  else
                    if (newBlockIndex.<(endIndex))
                      gotoPosRelaxed(newBlockIndex, 0, endIndex, depth)
                    else
                      _hasNext = false;
                  blockIndex = newBlockIndex;
                  lo = 0;
                  endLo = math.min(focusEnd.-(blockIndex), 32)
                }
              else
                ();
              res
            }
          else
            throw new NoSuchElementException("reached iterator end")
        }

        class GenRRBVectorReverseIterator[+A](startIndex: Int, endIndex: Int) extends AbstractIterator[A] with Iterator[A] with GenRRBVectorPointer[A @uncheckedVariance] {
          private var blockIndexInFocus: Int = _;
          private var lo: Int = _;
          private var endLo: Int = _;
          private var _hasNext: Boolean = startIndex.<(endIndex);
          def hasNext = _hasNext;
          final private[immutable] def initIterator(): Unit = {
            val idx = endIndex.-(1);
            if (focusStart.<=(idx).&&(idx.<(focusEnd)))
              gotoPos(idx, idx.^(focus))
            else
              gotoPosRelaxed(idx, 0, endIndex, depth);
            val indexInFocus = idx.-(focusStart);
            blockIndexInFocus = indexInFocus.&(-32);
            lo = indexInFocus.&(31);
            endLo = math.max(startIndex.-(focusStart).-(blockIndexInFocus), 0)
          };
          def next(): A = if (_hasNext)
            {
              val res = display0(lo).asInstanceOf[A];
              lo.-=(1);
              if (lo.<(endLo))
                {
                  val newBlockIndex = blockIndexInFocus.-(32);
                  if (focusStart.<=(newBlockIndex))
                    {
                      gotoPrevBlockStart(newBlockIndex, newBlockIndex.^(blockIndexInFocus));
                      blockIndexInFocus = newBlockIndex;
                      lo = 31;
                      endLo = math.max(startIndex.-(focusStart).-(focus), 0)
                    }
                  else
                    if (startIndex.<=(blockIndexInFocus.-(1)))
                      {
                        val newIndexInFocus = blockIndexInFocus.-(1);
                        gotoPosRelaxed(newIndexInFocus, 0, endIndex, depth);
                        blockIndexInFocus = newIndexInFocus.&(-32);
                        lo = newIndexInFocus.&(31);
                        endLo = math.max(startIndex.-(focusStart).-(blockIndexInFocus), 0)
                      }
                    else
                      _hasNext = false
                }
              else
                ();
              res
            }
          else
            throw new NoSuchElementException("reached iterator end")
        }

        final class GenRRBVectorBuilder[A] extends mutable.Builder[A, GenRRBVector[A]] with GenRRBVectorPointer[A @uncheckedVariance] {
          display0 = new Array[AnyRef](32);
          depth = 1;
          hasWritableTail = true;
          private var blockIndex = 0;
          private var lo = 0;
          def +=(elem: A): this.type = {
            if (lo.>=(32))
              {
                val newBlockIndex = blockIndex.+(32);
                gotoNextBlockStartWritable(newBlockIndex, newBlockIndex.^(blockIndex));
                blockIndex = newBlockIndex;
                lo = 0
              }
            else
              ();
            display0.update(lo, elem.asInstanceOf[AnyRef]);
            lo.+=(1);
            this
          };
          override def ++=(xs: TraversableOnce[A]): this.type = super.++=(xs);
          def result(): GenRRBVector[A] = {
            val size = blockIndex.+(lo);
            if (size.==(0))
              return GenRRBVector.empty
            else
              ();
            val vec = new GenRRBVector[A](size);
            vec.initFrom(this);
            val _depth = depth;
            if (_depth.>(1))
              {
                vec.copyDisplays(_depth, size.-(1));
                if (_depth.>(2))
                  vec.stabilize(_depth, size.-(1))
                else
                  ()
              }
            else
              ();
            vec.gotoPos(0, size.-(1));
            vec.focus = 0;
            vec.focusEnd = size;
            vec.focusDepth = _depth;
            ();
            vec
          };
          def clear(): Unit = {
            display0 = new Array[AnyRef](32);
            display1 = null;
            display2 = null;
            display3 = null;
            display4 = null;
            display5 = null;
            depth = 1;
            blockIndex = 0;
            lo = 0;
            hasWritableTail = true
          }
        }

        private[immutable] trait GenRRBVectorPointer[A] {
          private[immutable] var focusStart: Int = 0;
          private[immutable] var focusEnd: Int = 0;
          private[immutable] var focus: Int = 0;
          private[immutable] var focusDepth: Int = 0;
          private[immutable] var focusRelax: Int = 0;
          private[immutable] var depth: Int = _;
          private[immutable] var hasWritableTail: Boolean = false;
          private[immutable] var display0: Array[AnyRef] = _;
          private[immutable] var display1: Array[AnyRef] = _;
          private[immutable] var display2: Array[AnyRef] = _;
          private[immutable] var display3: Array[AnyRef] = _;
          private[immutable] var display4: Array[AnyRef] = _;
          private[immutable] var display5: Array[AnyRef] = _;
          private[immutable] def root(): AnyRef = depth match {
            case 0 => null
            case 1 => display0
            case 2 => display1
            case 3 => display2
            case 4 => display3
            case 5 => display4
            case _ => throw new IllegalStateException()
          };
          private[immutable] def initFromRoot(root: Array[AnyRef], _depth: Int, _endIndex: Int): Unit = {
            _depth match {
              case 0 => throw new IllegalArgumentException()
              case 1 => display0 = root
              case 2 => display1 = root
              case 3 => display2 = root
              case 4 => display3 = root
              case 5 => display4 = root
              case _ => throw new IllegalStateException()
            };
            depth = _depth;
            gotoIndex(0, _endIndex)
          };
          private[immutable] def initFrom[U](that: GenRRBVectorPointer[U]): Unit = {
            initFocus(that.focus, that.focusStart, that.focusEnd, that.focusDepth, that.focusRelax);
            depth = that.depth;
            that.depth match {
              case 0 => ()
              case 1 => display0 = that.display0
              case 2 => {
                display0 = that.display0;
                display1 = that.display1
              }
              case 3 => {
                display0 = that.display0;
                display1 = that.display1;
                display2 = that.display2
              }
              case 4 => {
                display0 = that.display0;
                display1 = that.display1;
                display2 = that.display2;
                display3 = that.display3
              }
              case 5 => {
                display0 = that.display0;
                display1 = that.display1;
                display2 = that.display2;
                display3 = that.display3;
                display4 = that.display4
              }
              case 6 => {
                display0 = that.display0;
                display1 = that.display1;
                display2 = that.display2;
                display3 = that.display3;
                display4 = that.display4;
                display5 = that.display5
              }
              case _ => throw new IllegalStateException()
            }
          };
          final private[immutable] def initFocus(_focus: Int, _focusStart: Int, _focusEnd: Int, _focusDepth: Int, _focusRelax: Int) = {
            this.focus = _focus;
            this.focusStart = _focusStart;
            this.focusEnd = _focusEnd;
            this.focusDepth = _focusDepth;
            this.focusRelax = _focusRelax
          };
          final private[immutable] def gotoIndex(index: Int, endIndex: Int) = {
            val focusStart = this.focusStart;
            if (focusStart.<=(index).&&(index.<(focusEnd)))
              {
                val indexInFocus = index.-(focusStart);
                val xor = indexInFocus.^(focus);
                if (xor.>=((1).<<(5)))
                  gotoPos(indexInFocus, xor)
                else
                  ();
                focus = indexInFocus
              }
            else
              gotoPosRelaxed(index, 0, endIndex, depth)
          };
          private[immutable] def allDisplaySizes(): Array[Array[Int]] = {
            val allSises: Array[Array[Int]] = new Array(5);
            focusDepth.until(depth).foreach(((i) => allSises.update(i.-(1), i match {
              case 1 => display1.last.asInstanceOf[Array[Int]]
              case 2 => display2.last.asInstanceOf[Array[Int]]
              case 3 => display3.last.asInstanceOf[Array[Int]]
              case 4 => display4.last.asInstanceOf[Array[Int]]
              case 5 => display5.last.asInstanceOf[Array[Int]]
              case _ => null
            })));
            allSises
          };
          private[immutable] def putDisplaySizes(allSizes: Array[Array[Int]]): Unit = focusDepth.until(depth).foreach(((_depth) => _depth match {
            case 0 => display0.update(display0.length.-(1), allSizes(depth.-(1)))
            case 1 => display1.update(display1.length.-(1), allSizes(depth.-(1)))
            case 2 => display2.update(display2.length.-(1), allSizes(depth.-(1)))
            case 3 => display3.update(display3.length.-(1), allSizes(depth.-(1)))
            case 4 => display4.update(display4.length.-(1), allSizes(depth.-(1)))
            case _ => null
          }));
          @tailrec final private[immutable] def gotoPosRelaxed(index: Int, _startIndex: Int, _endIndex: Int, _depth: Int, _focusRelax: Int = 0): Unit = {
            val display = _depth match {
              case 0 => null
              case 1 => display0
              case 2 => display1
              case 3 => display2
              case 4 => display3
              case 5 => display4
              case 6 => display5
              case _ => throw new IllegalArgumentException("depth=".+(_depth))
            };
            if (_depth.>(1).&&(display(display.length.-(1)).!=(null)))
              {
                val sizes = display(display.length.-(1)).asInstanceOf[Array[Int]];
                val indexInSubTree = index.-(_startIndex);
                var is = 0;
                while (sizes(is).<=(indexInSubTree)) 
                  is.+=(1)
                ;
                _depth match {
                  case 2 => display0 = display(is).asInstanceOf[Array[AnyRef]]
                  case 3 => display1 = display(is).asInstanceOf[Array[AnyRef]]
                  case 4 => display2 = display(is).asInstanceOf[Array[AnyRef]]
                  case 5 => display3 = display(is).asInstanceOf[Array[AnyRef]]
                  case 6 => display4 = display(is).asInstanceOf[Array[AnyRef]]
                  case _ => throw new IllegalArgumentException("depth=".+(_depth))
                };
                gotoPosRelaxed(index, if (is.==(0))
                  _startIndex
                else
                  _startIndex.+(sizes(is.-(1))), if (is.<(sizes.length.-(1)))
                  _startIndex.+(sizes(is))
                else
                  _endIndex, _depth.-(1), _focusRelax.|(is.<<((5).*(_depth).-(5))))
              }
            else
              {
                val indexInFocus = index.-(_startIndex);
                gotoPos(indexInFocus, (1).<<((5).*(_depth.-(1))));
                initFocus(indexInFocus, _startIndex, _endIndex, _depth, _focusRelax)
              }
          };
          final private[immutable] def getElement(index: Int, xor: Int): A = if (xor.<(32))
            display0(index.&(31)).asInstanceOf[A]
          else
            if (xor.<(1024))
              display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]](index.&(31)).asInstanceOf[A]
            else
              if (xor.<(32768))
                display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]](index.>>(5).&(31)).asInstanceOf[Array[AnyRef]](index.&(31)).asInstanceOf[A]
              else
                if (xor.<(1048576))
                  display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]](index.>>(10).&(31)).asInstanceOf[Array[AnyRef]](index.>>(5).&(31)).asInstanceOf[Array[AnyRef]](index.&(31)).asInstanceOf[A]
                else
                  if (xor.<(33554432))
                    display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]](index.>>(15).&(31)).asInstanceOf[Array[AnyRef]](index.>>(10).&(31)).asInstanceOf[Array[AnyRef]](index.>>(5).&(31)).asInstanceOf[Array[AnyRef]](index.&(31)).asInstanceOf[A]
                  else
                    if (xor.<(1073741824))
                      display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]](index.>>(20).&(31)).asInstanceOf[Array[AnyRef]](index.>>(15).&(31)).asInstanceOf[Array[AnyRef]](index.>>(10).&(31)).asInstanceOf[Array[AnyRef]](index.>>(5).&(31)).asInstanceOf[Array[AnyRef]](index.&(31)).asInstanceOf[A]
                    else
                      throw new IllegalArgumentException();
          final private[immutable] def gotoPos(index: Int, xor: Int): Unit = if (xor.<((1).<<(5)))
            ()
          else
            if (xor.<((1).<<(10)))
              display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
            else
              if (xor.<((1).<<(15)))
                {
                  val d1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]];
                  display1 = d1;
                  display0 = d1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
                }
              else
                if (xor.<((1).<<(20)))
                  {
                    val d2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]];
                    val d1 = d2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]];
                    display2 = d2;
                    display1 = d1;
                    display0 = d1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
                  }
                else
                  if (xor.<((1).<<(25)))
                    {
                      val d3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]];
                      val d2 = d3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]];
                      val d1 = d2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]];
                      display3 = d3;
                      display2 = d2;
                      display1 = d1;
                      display0 = d1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
                    }
                  else
                    if (xor.<((1).<<(30)))
                      {
                        val d4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]];
                        val d3 = d4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]];
                        val d2 = d3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]];
                        val d1 = d2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]];
                        display4 = d4;
                        display3 = d3;
                        display2 = d2;
                        display1 = d1;
                        display0 = d1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
                      }
                    else
                      throw new IllegalArgumentException();
          final private[immutable] def gotoNextBlockStart(index: Int, xor: Int): Unit = if (xor.<((1).<<(10)))
            display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
          else
            if (xor.<((1).<<(15)))
              {
                display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]];
                display0 = display1(0).asInstanceOf[Array[AnyRef]]
              }
            else
              if (xor.<((1).<<(20)))
                {
                  display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]];
                  display1 = display2(0).asInstanceOf[Array[AnyRef]];
                  display0 = display1(0).asInstanceOf[Array[AnyRef]]
                }
              else
                if (xor.<((1).<<(25)))
                  {
                    display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]];
                    display2 = display3(0).asInstanceOf[Array[AnyRef]];
                    display1 = display2(0).asInstanceOf[Array[AnyRef]];
                    display0 = display1(0).asInstanceOf[Array[AnyRef]]
                  }
                else
                  if (xor.<((1).<<(30)))
                    {
                      display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]];
                      display3 = display4(0).asInstanceOf[Array[AnyRef]];
                      display2 = display3(0).asInstanceOf[Array[AnyRef]];
                      display1 = display2(0).asInstanceOf[Array[AnyRef]];
                      display0 = display1(0).asInstanceOf[Array[AnyRef]]
                    }
                  else
                    throw new IllegalArgumentException();
          final private[immutable] def gotoPrevBlockStart(index: Int, xor: Int): Unit = if (xor.<((1).<<(10)))
            display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
          else
            if (xor.<((1).<<(15)))
              {
                display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]];
                display0 = display1(31).asInstanceOf[Array[AnyRef]]
              }
            else
              if (xor.<((1).<<(20)))
                {
                  display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]];
                  display1 = display2(31).asInstanceOf[Array[AnyRef]];
                  display0 = display1(31).asInstanceOf[Array[AnyRef]]
                }
              else
                if (xor.<((1).<<(25)))
                  {
                    display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]];
                    display2 = display3(31).asInstanceOf[Array[AnyRef]];
                    display1 = display2(31).asInstanceOf[Array[AnyRef]];
                    display0 = display1(31).asInstanceOf[Array[AnyRef]]
                  }
                else
                  if (xor.<((1).<<(30)))
                    {
                      display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]];
                      display3 = display4(31).asInstanceOf[Array[AnyRef]];
                      display2 = display3(31).asInstanceOf[Array[AnyRef]];
                      display1 = display2(31).asInstanceOf[Array[AnyRef]];
                      display0 = display1(31).asInstanceOf[Array[AnyRef]]
                    }
                  else
                    throw new IllegalArgumentException();
          final private[immutable] def setUpNextBlockStartTailWritable(_index: Int, _or: Int): Unit = if (_or.<(1024))
            {
              if (depth.==(1))
                {
                  display1 = new Array(3);
                  display1.update(0, display0);
                  depth.+=(1)
                }
              else
                {
                  val len = display1.length;
                  display1 = copyOf(display1, len, len.+(1))
                };
              display0 = new Array(32)
            }
          else
            if (_or.<(32768))
              {
                if (depth.==(2))
                  {
                    display2 = new Array(3);
                    display2.update(0, display1);
                    depth.+=(1)
                  }
                else
                  {
                    val len = display2.length;
                    display2 = copyOf(display2, len, len.+(1))
                  };
                display0 = new Array(32);
                display1 = new Array(33)
              }
            else
              if (_or.<(1048576))
                {
                  if (depth.==(3))
                    {
                      display3 = new Array(3);
                      display3.update(0, display2);
                      depth.+=(1)
                    }
                  else
                    {
                      val len = display3.length;
                      display3 = copyOf(display3, len, len.+(1))
                    };
                  display0 = new Array(32);
                  display1 = new Array(33);
                  display2 = new Array(33)
                }
              else
                if (_or.<(33554432))
                  {
                    if (depth.==(4))
                      {
                        display4 = new Array(3);
                        display4.update(0, display3);
                        depth.+=(1)
                      }
                    else
                      {
                        val len = display4.length;
                        display4 = copyOf(display4, len, len.+(1))
                      };
                    display0 = new Array(32);
                    display1 = new Array(33);
                    display2 = new Array(33);
                    display3 = new Array(33)
                  }
                else
                  if (_or.<(1073741824))
                    {
                      if (depth.==(5))
                        {
                          display5 = new Array(3);
                          display5.update(0, display4);
                          depth.+=(1)
                        }
                      else
                        {
                          val len = display5.length;
                          display5 = copyOf(display5, len, len.+(1))
                        };
                      display0 = new Array(32);
                      display1 = new Array(33);
                      display2 = new Array(33);
                      display3 = new Array(33);
                      display4 = new Array(33)
                    }
                  else
                    throw new IllegalArgumentException();
          final private[immutable] def gotoNextBlockStartWritable(index: Int, xor: Int): Unit = if (xor.<((1).<<(10)))
            {
              if (depth.==(1))
                {
                  display1 = new Array(33);
                  display1.update(0, display0);
                  depth.+=(1)
                }
              else
                ();
              display0 = new Array(32);
              display1.update(index.>>(5).&(31), display0)
            }
          else
            if (xor.<((1).<<(15)))
              {
                if (depth.==(2))
                  {
                    display2 = new Array(33);
                    display2.update(0, display1);
                    depth.+=(1)
                  }
                else
                  ();
                display0 = new Array(32);
                display1 = new Array(33);
                display1.update(index.>>(5).&(31), display0);
                display2.update(index.>>(10).&(31), display1)
              }
            else
              if (xor.<((1).<<(20)))
                {
                  if (depth.==(3))
                    {
                      display3 = new Array(33);
                      display3.update(0, display2);
                      depth.+=(1)
                    }
                  else
                    ();
                  display0 = new Array(32);
                  display1 = new Array(33);
                  display2 = new Array(3);
                  display1.update(index.>>(5).&(31), display0);
                  display2.update(index.>>(10).&(31), display1);
                  display3.update(index.>>(15).&(31), display2)
                }
              else
                if (xor.<((1).<<(25)))
                  {
                    if (depth.==(4))
                      {
                        display4 = new Array(33);
                        display4.update(0, display3);
                        depth.+=(1)
                      }
                    else
                      ();
                    display0 = new Array(32);
                    display1 = new Array(33);
                    display2 = new Array(33);
                    display3 = new Array(33);
                    display1.update(index.>>(5).&(31), display0);
                    display2.update(index.>>(10).&(31), display1);
                    display3.update(index.>>(15).&(31), display2);
                    display4.update(index.>>(20).&(31), display3)
                  }
                else
                  if (xor.<((1).<<(30)))
                    {
                      if (depth.==(5))
                        {
                          display5 = new Array(33);
                          display5.update(0, display4);
                          depth.+=(1)
                        }
                      else
                        ();
                      display0 = new Array(32);
                      display1 = new Array(33);
                      display2 = new Array(33);
                      display3 = new Array(33);
                      display4 = new Array(33);
                      display1.update(index.>>(5).&(31), display0);
                      display2.update(index.>>(10).&(31), display1);
                      display3.update(index.>>(15).&(31), display2);
                      display4.update(index.>>(20).&(31), display3);
                      display5.update(index.>>(25).&(31), display4)
                    }
                  else
                    throw new IllegalArgumentException();
          final private[immutable] def copyDisplays(_depth: Int, _focus: Int): Unit = {
            ();
            ();
            _depth match {
              case 1 => ()
              case 2 => display1 = {
                val idx_1 = _focus.>>(5);
                copyOf(display1, idx_1.+(1), idx_1.+(2))
              }
              case 3 => {
                display1 = {
                  val idx_1 = _focus.>>(5).&(31);
                  copyOf(display1, idx_1.+(1), idx_1.+(2))
                };
                display2 = {
                  val idx_2 = _focus.>>(10);
                  copyOf(display2, idx_2.+(1), idx_2.+(2))
                }
              }
              case 4 => {
                display1 = {
                  val idx_1 = _focus.>>(5).&(31);
                  copyOf(display1, idx_1.+(1), idx_1.+(2))
                };
                display2 = {
                  val idx_2 = _focus.>>(10).&(31);
                  copyOf(display2, idx_2.+(1), idx_2.+(2))
                };
                display3 = {
                  val idx_3 = _focus.>>(15);
                  copyOf(display3, idx_3.+(1), idx_3.+(2))
                }
              }
              case 5 => {
                display1 = {
                  val idx_1 = _focus.>>(5).&(31);
                  copyOf(display1, idx_1.+(1), idx_1.+(2))
                };
                display2 = {
                  val idx_2 = _focus.>>(10).&(31);
                  copyOf(display2, idx_2.+(1), idx_2.+(2))
                };
                display3 = {
                  val idx_3 = _focus.>>(15).&(31);
                  copyOf(display3, idx_3.+(1), idx_3.+(2))
                };
                display4 = {
                  val idx_4 = _focus.>>(20);
                  copyOf(display4, idx_4.+(1), idx_4.+(2))
                }
              }
              case 6 => {
                display1 = {
                  val idx_1 = _focus.>>(5).&(31);
                  copyOf(display1, idx_1.+(1), idx_1.+(2))
                };
                display2 = {
                  val idx_2 = _focus.>>(10).&(31);
                  copyOf(display2, idx_2.+(1), idx_2.+(2))
                };
                display3 = {
                  val idx_3 = _focus.>>(15).&(31);
                  copyOf(display3, idx_3.+(1), idx_3.+(2))
                };
                display4 = {
                  val idx_4 = _focus.>>(20).&(31);
                  copyOf(display4, idx_4.+(1), idx_4.+(2))
                };
                display5 = {
                  val idx_5 = _focus.>>(25);
                  copyOf(display5, idx_5.+(1), idx_5.+(2))
                }
              }
              case _ => throw new IllegalArgumentException()
            }
          };
          final private[immutable] def copyDisplaysTop(_currentDepth: Int, _focusRelax: Int): Unit = {
            _currentDepth match {
              case 2 => {
                val f1 = _focusRelax.>>(5).&(31);
                display1 = copyOf(display1, f1.+(1), f1.+(2))
              }
              case 3 => {
                val f2 = _focusRelax.>>(10).&(31);
                display2 = copyOf(display2, f2.+(1), f2.+(2))
              }
              case 4 => {
                val f3 = _focusRelax.>>(15).&(31);
                display3 = copyOf(display3, f3.+(1), f3.+(2))
              }
              case 5 => {
                val f4 = _focusRelax.>>(20).&(31);
                display4 = copyOf(display4, f4.+(1), f4.+(2))
              }
              case 6 => {
                val f5 = _focusRelax.>>(25).&(31);
                display5 = copyOf(display5, f5.+(1), f5.+(2))
              }
              case _ => throw new IllegalStateException()
            };
            if (_currentDepth.<(depth))
              copyDisplaysTop(_currentDepth.+(1), _focusRelax)
            else
              ()
          };
          final private[immutable] def stabilize(_depth: Int, _focus: Int): Unit = {
            ();
            ();
            _depth match {
              case 1 => ()
              case 2 => display1.update(_focus.>>(5).&(31), display0)
              case 3 => {
                display2.update(_focus.>>(10).&(31), display1);
                display1.update(_focus.>>(5).&(31), display0)
              }
              case 4 => {
                display3.update(_focus.>>(15).&(31), display2);
                display2.update(_focus.>>(10).&(31), display1);
                display1.update(_focus.>>(5).&(31), display0)
              }
              case 5 => {
                display4.update(_focus.>>(20).&(31), display3);
                display3.update(_focus.>>(15).&(31), display2);
                display2.update(_focus.>>(10).&(31), display1);
                display1.update(_focus.>>(5).&(31), display0)
              }
              case 6 => {
                display5.update(_focus.>>(25).&(31), display4);
                display4.update(_focus.>>(20).&(31), display3);
                display3.update(_focus.>>(15).&(31), display2);
                display2.update(_focus.>>(10).&(31), display1);
                display1.update(_focus.>>(5).&(31), display0)
              }
              case _ => throw new IllegalArgumentException()
            }
          };
          final private[immutable] def copyOf(a: Array[AnyRef], numElements: Int, newSize: Int) = {
            ;
            val b = new Array[AnyRef](newSize);
            Platform.arraycopy(a, 0, b, 0, numElements);
            b
          };
          final private[immutable] def mergeLeafs(leaf0: Array[AnyRef], length0: Int, leaf1: Array[AnyRef], length1: Int): Array[AnyRef] = {
            ;
            {
              val newLeaf = new Array[AnyRef](length0.+(length1));
              Platform.arraycopy(leaf0, 0, newLeaf, 0, length0);
              Platform.arraycopy(leaf1, 0, newLeaf, length0, length1);
              newLeaf
            }
          }
        }
      }
    }
  }
}