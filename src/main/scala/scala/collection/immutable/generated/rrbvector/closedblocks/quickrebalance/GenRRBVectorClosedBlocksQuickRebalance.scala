package scala {
  package collection {
    package immutable {
      package generated {
        package rrbvector.closedblocks.quickrebalance {
          import scala.annotation.tailrec

          import scala.compat.Platform

          import scala.annotation.unchecked.uncheckedVariance

          import scala.collection.generic.{GenericCompanion, GenericTraversableTemplate, CanBuildFrom, IndexedSeqFactory}

          object GenRRBVectorClosedBlocksQuickRebalance extends scala.collection.generic.IndexedSeqFactory[GenRRBVectorClosedBlocksQuickRebalance] {
            def newBuilder[A]: mutable.Builder[A, GenRRBVectorClosedBlocksQuickRebalance[A]] = new GenRRBVectorClosedBlocksQuickRebalanceBuilder[A]();
            implicit def canBuildFrom[A]: scala.collection.generic.CanBuildFrom[Coll, A, GenRRBVectorClosedBlocksQuickRebalance[A]] = ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]];
            private val NIL = new GenRRBVectorClosedBlocksQuickRebalance[Nothing](0);
            override def empty[A]: GenRRBVectorClosedBlocksQuickRebalance[A] = NIL;
            final private[immutable] def singleton[A](value: A): GenRRBVectorClosedBlocksQuickRebalance[A] = {
              val vec = new GenRRBVectorClosedBlocksQuickRebalance[A](1);
              vec.display0 = new Array[AnyRef](1);
              vec.display0.update(0, value.asInstanceOf[AnyRef]);
              vec.depth = 1;
              vec.focusEnd = 1;
              vec.focusDepth = 1;
              vec
            }
          }

          final class GenRRBVectorClosedBlocksQuickRebalance[+A](private[immutable] val endIndex: Int) extends scala.collection.AbstractSeq[A] with scala.collection.immutable.IndexedSeq[A] with scala.collection.generic.GenericTraversableTemplate[A, GenRRBVectorClosedBlocksQuickRebalance] with scala.collection.IndexedSeqLike[A, GenRRBVectorClosedBlocksQuickRebalance[A]] with GenRRBVectorClosedBlocksQuickRebalancePointer[A @uncheckedVariance] with Serializable { self =>
            override def companion: scala.collection.generic.GenericCompanion[GenRRBVectorClosedBlocksQuickRebalance] = GenRRBVectorClosedBlocksQuickRebalance;
            def length(): Int = endIndex;
            override def lengthCompare(len: Int): Int = endIndex.-(len);
            private[collection] def initIterator[B >: A](it: GenRRBVectorClosedBlocksQuickRebalanceIterator[B]): scala.Unit = {
              it.initFrom(this);
              if (depth.>(0))
                it.resetIterator()
              else
                ()
            };
            private[collection] def initIterator[B >: A](it: GenRRBVectorClosedBlocksQuickRebalanceReverseIterator[B]): scala.Unit = {
              it.initFrom(this);
              if (depth.>(0))
                it.resetIterator()
              else
                ()
            };
            override def iterator: GenRRBVectorClosedBlocksQuickRebalanceIterator[A] = {
              val it = new GenRRBVectorClosedBlocksQuickRebalanceIterator[A](0, endIndex);
              this.initIterator(it);
              it
            };
            override def reverseIterator: GenRRBVectorClosedBlocksQuickRebalanceReverseIterator[A] = {
              val it = new GenRRBVectorClosedBlocksQuickRebalanceReverseIterator[A](0, endIndex);
              this.initIterator(it);
              it
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
                    display0(index.-(focusStart).&(31)).asInstanceOf[A]
                  }
                else
                  throw new IndexOutOfBoundsException(index.toString)
            };
            override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[GenRRBVectorClosedBlocksQuickRebalance[A], B, That]): That = if (bf.eq(IndexedSeq.ReusableCBF))
              appendedBack(elem).asInstanceOf[That]
            else
              super.:+(elem)(bf);
            override def isEmpty: Boolean = this.endIndex.==(0);
            override def head: A = if (this.endIndex.!=(0))
              apply(0)
            else
              throw new UnsupportedOperationException("empty.head");
            override def take(n: Int): GenRRBVectorClosedBlocksQuickRebalance[A] = if (n.<=(0))
              GenRRBVectorClosedBlocksQuickRebalance.empty
            else
              if (n.<(endIndex))
                takeFront0(n)
              else
                this;
            override def dropRight(n: Int): GenRRBVectorClosedBlocksQuickRebalance[A] = if (n.<=(0))
              this
            else
              if (n.<(endIndex))
                takeFront0(endIndex.-(n))
              else
                GenRRBVectorClosedBlocksQuickRebalance.empty;
            override def slice(from: Int, until: Int): GenRRBVectorClosedBlocksQuickRebalance[A] = take(until).drop(from);
            override def splitAt(n: Int): scala.Tuple2[GenRRBVectorClosedBlocksQuickRebalance[A], GenRRBVectorClosedBlocksQuickRebalance[A]] = scala.Tuple2(take(n), drop(n));
            override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[GenRRBVectorClosedBlocksQuickRebalance[A], B, That]): That = if (bf.eq(IndexedSeq.ReusableCBF))
              if (that.isEmpty)
                this.asInstanceOf[That]
              else
                if (that.isInstanceOf[GenRRBVectorClosedBlocksQuickRebalance[B]])
                  {
                    val vec = that.asInstanceOf[GenRRBVectorClosedBlocksQuickRebalance[B]];
                    if (this.isEmpty)
                      vec.asInstanceOf[That]
                    else
                      this.concatenated[B](vec).asInstanceOf[That]
                  }
                else
                  super.++(that)
            else
              super.++(that.seq);
            override def tail: GenRRBVectorClosedBlocksQuickRebalance[A] = if (this.endIndex.!=(0))
              this.drop(1)
            else
              throw new UnsupportedOperationException("empty.tail");
            override def last: A = if (this.endIndex.!=(0))
              this.apply(this.length.-(1))
            else
              throw new UnsupportedOperationException("empty.last");
            override def init: GenRRBVectorClosedBlocksQuickRebalance[A] = if (this.endIndex.!=(0))
              dropRight(1)
            else
              throw new UnsupportedOperationException("empty.init");
            private def appendedBack[B >: A](value: B): GenRRBVectorClosedBlocksQuickRebalance[B] = {
              if (this.endIndex.==(0))
                return GenRRBVectorClosedBlocksQuickRebalance.singleton[B](value)
              else
                ();
              val _endIndex = this.endIndex;
              val vec = new GenRRBVectorClosedBlocksQuickRebalance[B](_endIndex.+(1));
              vec.initFrom(this);
              vec.gotoIndex(_endIndex.-(1), _endIndex.-(1));
              val elemIndexInBlock = _endIndex.-(vec.focusStart).&(31);
              if (elemIndexInBlock.!=(0))
                vec.appendBackSetupCurrentBlock()
              else
                vec.appendBackSetupNewBlock();
              vec.display0.update(elemIndexInBlock, value.asInstanceOf[AnyRef]);
              vec
            };
            private def appendBackSetupCurrentBlock() = {
              focusEnd.+=(1);
              display0 = copyOf(display0, display0.length, display0.length.+(1));
              val _depth = depth;
              if (_depth.>(1))
                {
                  val stabilizationIndex = focus.|(focusRelax);
                  val displaySizes = allDisplaySizes();
                  copyDisplays(_depth, stabilizationIndex);
                  stabilize(_depth, stabilizationIndex);
                  var i = focusDepth;
                  while (i.<(_depth)) 
                    {
                      val oldSizes = displaySizes(i.-(1));
                      if (oldSizes.!=(null))
                        {
                          val newSizes = new Array[Int](oldSizes.length);
                          val lastIndex = oldSizes.length.-(1);
                          Platform.arraycopy(oldSizes, 0, newSizes, 0, lastIndex);
                          newSizes.update(lastIndex, oldSizes(lastIndex).+(1));
                          displaySizes.update(i.-(1), newSizes)
                        }
                      else
                        ();
                      i.+=(1)
                    }
                  ;
                  putDisplaySizes(displaySizes)
                }
              else
                ()
            };
            private def appendBackSetupNewBlock() = {
              ;
              val _depth = depth;
              val displaySizes = allDisplaySizes();
              copyDisplays(_depth, focus.|(focusRelax));
              val newRelaxedIndex = endIndex.-(focusStart).-(1).+(focusRelax);
              val xor = newRelaxedIndex.^(focus.|(focusRelax));
              setUpNextBlockNewBranchWritable(newRelaxedIndex, xor);
              stabilize(depth, newRelaxedIndex);
              if (_depth.!=(depth))
                if (endIndex.-(1).==((1).<<((5).*(depth.-(1)))))
                  displaySizes.update(depth.-(1), null)
                else
                  {
                    val newSizes = new Array[Int](2);
                    newSizes.update(0, endIndex.-(1));
                    newSizes.update(1, endIndex);
                    displaySizes.update(depth.-(1), newSizes)
                  }
              else
                {
                  val j = math.max(if (xor.<(1024))
                    1
                  else
                    if (xor.<(32768))
                      2
                    else
                      if (xor.<(1048576))
                        3
                      else
                        if (xor.<(33554432))
                          4
                        else
                          5, focusDepth);
                  var i = focusDepth;
                  while (i.<(j)) 
                    {
                      displaySizes.update(i.-(1), null);
                      i.+=(1)
                    }
                  ;
                  while (i.<(depth)) 
                    {
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
                        newSizes.update(newSizes.length.-(1), newSizes(newSizes.length.-(2)).+(1))
                      else
                        newSizes(newSizes.length.-(1)).+=(1);
                      displaySizes.update(i.-(1), newSizes);
                      i.+=(1)
                    }
                  ;
                  putDisplaySizes(displaySizes)
                };
              if (_depth.==(focusDepth))
                initFocus(endIndex.-(1), 0, endIndex, depth, 0)
              else
                initFocus(0, endIndex.-(1), endIndex, 1, newRelaxedIndex.&(-32))
            };
            private[immutable] def concatenated[B >: A](that: GenRRBVectorClosedBlocksQuickRebalance[B]): GenRRBVectorClosedBlocksQuickRebalance[B] = {
              this.gotoIndex(this.endIndex.-(1), this.endIndex);
              that.gotoIndex(0, that.endIndex);
              val newSize = this.endIndex.+(that.endIndex);
              val vec = new GenRRBVectorClosedBlocksQuickRebalance[B](newSize);
              math.max(this.depth, that.depth) match {
                case 1 => {
                  val concat1 = rebalancedLeafs(this.display0, that.display0, true);
                  vec.initFromRoot(concat1, if (newSize.<=(32))
                    1
                  else
                    2, newSize)
                }
                case 2 => {
                  val concat1 = rebalancedLeafs(this.display0, that.display0, false);
                  val concat2 = rebalanced(this.display1, concat1, that.display1, 2);
                  if (concat2.length.==(2))
                    vec.initFromRoot(concat2(0).asInstanceOf[Array[AnyRef]], 2, newSize)
                  else
                    vec.initFromRoot(withComputedSizes(concat2, 3), 3, newSize)
                }
                case 3 => {
                  val concat1 = rebalancedLeafs(this.display0, that.display0, false);
                  val concat2 = rebalanced(this.display1, concat1, that.display1, 2);
                  val concat3 = rebalanced(this.display2, concat2, that.display2, 3);
                  if (concat3.length.==(2))
                    vec.initFromRoot(concat3(0).asInstanceOf[Array[AnyRef]], 3, newSize)
                  else
                    vec.initFromRoot(withComputedSizes(concat3, 4), 4, newSize)
                }
                case 4 => {
                  val concat1 = rebalancedLeafs(this.display0, that.display0, false);
                  val concat2 = rebalanced(this.display1, concat1, that.display1, 2);
                  val concat3 = rebalanced(this.display2, concat2, that.display2, 3);
                  val concat4 = rebalanced(this.display3, concat3, that.display3, 4);
                  if (concat4.length.==(2))
                    vec.initFromRoot(concat4(0).asInstanceOf[Array[AnyRef]], 4, newSize)
                  else
                    vec.initFromRoot(withComputedSizes(concat4, 5), 5, newSize)
                }
                case 5 => {
                  val concat1 = rebalancedLeafs(this.display0, that.display0, false);
                  val concat2 = rebalanced(this.display1, concat1, that.display1, 2);
                  val concat3 = rebalanced(this.display2, concat2, that.display2, 3);
                  val concat4 = rebalanced(this.display3, concat3, that.display3, 4);
                  val concat5 = rebalanced(this.display4, concat4, that.display4, 5);
                  if (concat5.length.==(2))
                    vec.initFromRoot(concat5(0).asInstanceOf[Array[AnyRef]], 5, newSize)
                  else
                    vec.initFromRoot(withComputedSizes(concat5, 6), 6, newSize)
                }
                case 6 => {
                  val concat1 = rebalancedLeafs(this.display0, that.display0, false);
                  val concat2 = rebalanced(this.display1, concat1, that.display1, 2);
                  val concat3 = rebalanced(this.display2, concat2, that.display2, 3);
                  val concat4 = rebalanced(this.display3, concat3, that.display3, 4);
                  val concat5 = rebalanced(this.display4, concat4, that.display4, 5);
                  val concat6 = rebalanced(this.display5, concat5, that.display5, 6);
                  vec.initFromRoot(concat6, 6, newSize)
                }
                case _ => throw new IllegalStateException()
              };
              vec
            };
            private def rebalanced(displayLeft: Array[AnyRef], concat: Array[AnyRef], displayRight: Array[AnyRef], currentDepth: Int): Array[AnyRef] = {
              val leftLength = if (displayLeft.==(null))
                0
              else
                displayLeft.length.-(1);
              val concatLength = if (concat.==(null))
                0
              else
                concat.length.-(1);
              val rightLength = if (displayRight.==(null))
                0
              else
                displayRight.length.-(1);
              {
                val tup = computeNewSizes(displayLeft, concat, displayRight, currentDepth);
                val sizes: Array[Int] = tup._1;
                val nalen: Int = tup._2;
                val top = new Array[AnyRef](nalen.>>(5).+(if (nalen.&(31).==(0))
                  1
                else
                  2));
                var mid = new Array[AnyRef]((if (nalen.<=(32))
  nalen
else
  32).+(1));
                var bot: Array[AnyRef] = null;
                var iSizes = 0;
                var iTop = 0;
                var iMid = 0;
                var iBot = 0;
                var i = 0;
                var j = 0;
                var d = 0;
                var currentDisplay: Array[AnyRef] = null;
                var displayEnd = 0;
                do 
                  {
                    d match {
                      case 0 => if (displayLeft.!=(null))
                        {
                          currentDisplay = displayLeft;
                          if (concat.==(null))
                            displayEnd = leftLength
                          else
                            displayEnd = leftLength.-(1)
                        }
                      else
                        ()
                      case 1 => {
                        if (concat.==(null))
                          displayEnd = 0
                        else
                          {
                            currentDisplay = concat;
                            displayEnd = concatLength
                          };
                        i = 0
                      }
                      case 2 => if (displayRight.!=(null))
                        {
                          currentDisplay = displayRight;
                          displayEnd = rightLength;
                          if (concat.==(null))
                            i = 0
                          else
                            i = 1
                        }
                      else
                        ()
                    };
                    while (i.<(displayEnd)) 
                      {
                        val displayValue = currentDisplay(i).asInstanceOf[Array[AnyRef]];
                        val displayValueEnd = displayValue.length.-(if (currentDepth.==(2))
                          0
                        else
                          1);
                        if (iBot.==(0).&&(j.==(0)).&&(displayValueEnd.==(sizes(iSizes))))
                          {
                            if (currentDepth.!=(2).&&(bot.!=(null)))
                              {
                                withComputedSizes(bot, currentDepth.-(1));
                                bot = null
                              }
                            else
                              ();
                            mid.update(iMid, displayValue);
                            i.+=(1);
                            iMid.+=(1);
                            iSizes.+=(1)
                          }
                        else
                          {
                            val numElementsToCopy = math.min(displayValueEnd.-(j), sizes(iSizes).-(iBot));
                            if (iBot.==(0))
                              {
                                if (currentDepth.!=(2).&&(bot.!=(null)))
                                  withComputedSizes(bot, currentDepth.-(1))
                                else
                                  ();
                                bot = new Array[AnyRef](sizes(iSizes).+(if (currentDepth.==(2))
                                  0
                                else
                                  1));
                                mid.update(iMid, bot)
                              }
                            else
                              ();
                            Platform.arraycopy(displayValue, j, bot, iBot, numElementsToCopy);
                            j.+=(numElementsToCopy);
                            iBot.+=(numElementsToCopy);
                            if (j.==(displayValueEnd))
                              {
                                i.+=(1);
                                j = 0
                              }
                            else
                              ();
                            if (iBot.==(sizes(iSizes)))
                              {
                                iMid.+=(1);
                                iBot = 0;
                                iSizes.+=(1)
                              }
                            else
                              ()
                          };
                        if (iMid.==(32))
                          {
                            top.update(iTop, withComputedSizes(mid, currentDepth));
                            iTop.+=(1);
                            iMid = 0;
                            if (nalen.-(iTop.<<(5)).!=(0))
                              mid = new Array[AnyRef](math.min(nalen.-(iTop.<<(5)).+(1), 33))
                            else
                              mid = null
                          }
                        else
                          ()
                      }
                    ;
                    d.+=(1)
                  }
                 while (d.<(3)) ;
                if (currentDepth.!=(2).&&(bot.!=(null)))
                  withComputedSizes(bot, currentDepth.-(1))
                else
                  ();
                if (mid.!=(null))
                  top.update(iTop, withComputedSizes(mid, currentDepth))
                else
                  ();
                top
              }
            };
            private def rebalancedLeafs(displayLeft: Array[AnyRef], displayRight: Array[AnyRef], isTop: Boolean): Array[AnyRef] = {
              val leftLength = displayLeft.length;
              val rightLength = displayRight.length;
              if (leftLength.==(32))
                {
                  val top = new Array[AnyRef](3);
                  top.update(0, displayLeft);
                  top.update(1, displayRight);
                  top
                }
              else
                if (leftLength.+(rightLength).<=(32))
                  {
                    val mergedDisplay = new Array[AnyRef](leftLength.+(rightLength));
                    Platform.arraycopy(displayLeft, 0, mergedDisplay, 0, leftLength);
                    Platform.arraycopy(displayRight, 0, mergedDisplay, leftLength, rightLength);
                    if (isTop)
                      mergedDisplay
                    else
                      {
                        val top = new Array[AnyRef](2);
                        top.update(0, mergedDisplay);
                        top
                      }
                  }
                else
                  {
                    val top = new Array[AnyRef](3);
                    val arr0 = new Array[AnyRef](32);
                    val arr1 = new Array[AnyRef](leftLength.+(rightLength).-(32));
                    top.update(0, arr0);
                    top.update(1, arr1);
                    Platform.arraycopy(displayLeft, 0, arr0, 0, leftLength);
                    Platform.arraycopy(displayRight, 0, arr0, leftLength, (32).-(leftLength));
                    Platform.arraycopy(displayRight, (32).-(leftLength), arr1, 0, rightLength.-(32).+(leftLength));
                    top
                  }
            };
            private def computeNewSizes(displayLeft: Array[AnyRef], concat: Array[AnyRef], displayRight: Array[AnyRef], currentDepth: Int) = {
              val leftLength = if (displayLeft.==(null))
                0
              else
                displayLeft.length.-(1);
              val concatLength = if (concat.==(null))
                0
              else
                concat.length.-(1);
              val rightLength = if (displayRight.==(null))
                0
              else
                displayRight.length.-(1);
              {
                var szsLength = leftLength.+(concatLength).+(rightLength);
                if (leftLength.!=(0))
                  szsLength.-=(1)
                else
                  ();
                if (rightLength.!=(0))
                  szsLength.-=(1)
                else
                  ();
                val szs = new Array[Int](szsLength);
                var totalCount = 0;
                var i = 0;
                while (i.<(leftLength.-(1))) 
                  {
                    val sz = if (currentDepth.==(1))
                      1
                    else
                      if (currentDepth.==(2))
                        displayLeft(i).asInstanceOf[Array[AnyRef]].length
                      else
                        displayLeft(i).asInstanceOf[Array[AnyRef]].length.-(1);
                    szs.update(i, sz);
                    totalCount.+=(sz);
                    i.+=(1)
                  }
                ;
                val offset1 = i;
                i = 0;
                while (i.<(concatLength)) 
                  {
                    val sz = if (currentDepth.==(1))
                      1
                    else
                      if (currentDepth.==(2))
                        concat(i).asInstanceOf[Array[AnyRef]].length
                      else
                        concat(i).asInstanceOf[Array[AnyRef]].length.-(1);
                    szs.update(offset1.+(i), sz);
                    totalCount.+=(sz);
                    i.+=(1)
                  }
                ;
                val offset2 = offset1.+(i).-(1);
                i = 1;
                while (i.<(rightLength)) 
                  {
                    val sz = if (currentDepth.==(1))
                      1
                    else
                      if (currentDepth.==(2))
                        displayRight(i).asInstanceOf[Array[AnyRef]].length
                      else
                        displayRight(i).asInstanceOf[Array[AnyRef]].length.-(1);
                    szs.update(offset2.+(i), sz);
                    totalCount.+=(sz);
                    i.+=(1)
                  }
                ;
                val effectiveNumberOfSlots = totalCount./(32).+(1);
                val MinWidth = 31;
                val EXTRAS = 2;
                while (szsLength.>(effectiveNumberOfSlots.+(EXTRAS))) 
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
                    while (ix.<(szsLength.-(1))) 
                      {
                        szs.update(ix, szs(ix.+(1)));
                        ix.+=(1)
                      }
                    ;
                    szsLength.-=(1)
                  }
                ;
                scala.Tuple2(szs, szsLength)
              }
            };
            private def withComputedSizes(node: Array[AnyRef], currentDepth: Int): Array[AnyRef] = {
              var i = 0;
              var acc = 0;
              val end = node.length.-(1);
              val sizes = new Array[Int](end);
              if (currentDepth.>(1))
                {
                  while (i.<(end)) 
                    {
                      acc.+=(treeSize(node(i).asInstanceOf[Array[AnyRef]], currentDepth.-(1)));
                      sizes.update(i, acc);
                      i.+=(1)
                    }
                  ;
                  val last = node(end.-(1)).asInstanceOf[Array[AnyRef]];
                  if (end.>(1).&&(sizes(end.-(2)).!=(end.-(1).<<((5).*(currentDepth.-(1))))).||(currentDepth.>(2).&&(last(last.length.-(1)).!=(null))))
                    node.update(end, sizes)
                  else
                    ()
                }
              else
                {
                  while (i.<(end)) 
                    {
                      acc.+=(node(i).asInstanceOf[Array[AnyRef]].length);
                      sizes.update(i, acc);
                      i.+=(1)
                    }
                  ;
                  if (end.>(1).&&(sizes(end.-(2)).!=(end.-(1).<<(5))))
                    node.update(end, sizes)
                  else
                    ()
                };
              node
            };
            private def treeSize(tree: Array[AnyRef], currentDepth: Int): Int = if (currentDepth.==(1))
              tree.length
            else
              {
                val treeSizes = tree(tree.length.-(1)).asInstanceOf[Array[Int]];
                if (treeSizes.!=(null))
                  treeSizes(treeSizes.length.-(1))
                else
                  {
                    var _tree = tree;
                    var _currentDepth = currentDepth;
                    var acc = 0;
                    while (_currentDepth.>(1)) 
                      {
                        acc.+=(_tree.length.-(2).*((1).<<((5).*(_currentDepth.-(1)))));
                        _currentDepth.-=(1);
                        _tree = _tree(_tree.length.-(2)).asInstanceOf[Array[AnyRef]]
                      }
                    ;
                    acc.+(_tree.length)
                  }
              };
            private def takeFront0(n: Int): GenRRBVectorClosedBlocksQuickRebalance[A] = {
              val vec = new GenRRBVectorClosedBlocksQuickRebalance[A](n);
              vec.initFrom(this);
              if (depth.>(1))
                {
                  vec.gotoIndex(n.-(1), n);
                  val d0len = vec.focus.&(31).+(1);
                  if (d0len.!=(32))
                    {
                      val d0 = new Array[AnyRef](d0len);
                      Platform.arraycopy(vec.display0, 0, d0, 0, d0len);
                      vec.display0 = d0
                    }
                  else
                    ();
                  val cutIndex = vec.focus.|(vec.focusRelax);
                  vec.cleanTop(cutIndex);
                  vec.focusDepth = math.min(vec.depth, vec.focusDepth);
                  if (vec.depth.>(1))
                    {
                      val displaySizes = allDisplaySizes();
                      vec.copyDisplays(vec.depth, cutIndex);
                      if (vec.depth.>(2).||(d0len.!=(32)))
                        vec.stabilize(vec.depth, cutIndex)
                      else
                        ();
                      if (vec.focusDepth.<(vec.depth))
                        {
                          var offset = 0;
                          var i = vec.depth;
                          while (i.>(vec.focusDepth)) 
                            {
                              i.-=(1);
                              val oldSizes = displaySizes(i.-(1));
                              if (oldSizes.!=(null))
                                {
                                  val newLen = vec.focusRelax.>>((5).*(i)).+(1);
                                  val newSizes = new Array[Int](newLen);
                                  Platform.arraycopy(oldSizes, 0, newSizes, 0, newLen.-(1));
                                  newSizes.update(newLen.-(1), n.-(offset));
                                  offset.+=(newSizes(newLen.-(2)));
                                  displaySizes.update(i.-(1), newSizes)
                                }
                              else
                                ()
                            }
                          ;
                          vec.putDisplaySizes(displaySizes)
                        }
                      else
                        ()
                    }
                  else
                    ()
                }
              else
                if (n.!=(32))
                  {
                    val d0 = new Array[AnyRef](n);
                    Platform.arraycopy(vec.display0, 0, d0, 0, n);
                    vec.display0 = d0
                  }
                else
                  ();
              vec.focusEnd = n;
              vec
            }
          }

          final class GenRRBVectorClosedBlocksQuickRebalanceBuilder[A] extends mutable.Builder[A, GenRRBVectorClosedBlocksQuickRebalance[A]] with GenRRBVectorClosedBlocksQuickRebalancePointer[A @uncheckedVariance] {
            display0 = new Array[AnyRef](32);
            depth = 1;
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
            def result(): GenRRBVectorClosedBlocksQuickRebalance[A] = {
              val size = blockIndex.+(lo);
              if (size.==(0))
                return GenRRBVectorClosedBlocksQuickRebalance.empty
              else
                ();
              val resultVector = new GenRRBVectorClosedBlocksQuickRebalance[A](size);
              resultVector.initFrom(this);
              resultVector.display0 = copyOf(resultVector.display0, lo, lo);
              val _depth = depth;
              if (_depth.>(1))
                {
                  resultVector.copyDisplays(_depth, size.-(1));
                  resultVector.stabilize(_depth, size.-(1))
                }
              else
                ();
              resultVector.gotoPos(0, size.-(1));
              resultVector.focus = 0;
              resultVector.focusEnd = size;
              resultVector.focusDepth = _depth;
              resultVector
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
              lo = 0
            }
          }

          class GenRRBVectorClosedBlocksQuickRebalanceIterator[+A](startIndex: Int, endIndex: Int) extends AbstractIterator[A] with Iterator[A] with GenRRBVectorClosedBlocksQuickRebalancePointer[A @uncheckedVariance] {
            private var blockIndex: Int = _;
            private var lo: Int = _;
            private var endLo: Int = _;
            private var _hasNext: Boolean = startIndex.<(endIndex);
            final private[immutable] def resetIterator(): Unit = {
              if (focusStart.<=(startIndex).&&(startIndex.<(focusEnd)))
                gotoPos(startIndex, startIndex.^(focus))
              else
                gotoPosRelaxed(startIndex, 0, endIndex, depth);
              blockIndex = focusStart;
              lo = startIndex.-(focusStart);
              endLo = math.min(focusEnd.-(blockIndex), 32)
            };
            def hasNext = _hasNext;
            def next(): A = {
              val _lo = lo;
              val res = display0(_lo).asInstanceOf[A];
              lo = _lo.+(1);
              val _endLo = endLo;
              if (_lo.+(1).!=(_endLo))
                res
              else
                {
                  val oldBlockIndex = blockIndex;
                  val newBlockIndex = oldBlockIndex.+(_endLo);
                  blockIndex = newBlockIndex;
                  lo = 0;
                  if (newBlockIndex.<(focusEnd))
                    {
                      val _focusStart = focusStart;
                      val newBlockIndexInFocus = newBlockIndex.-(_focusStart);
                      gotoNextBlockStart(newBlockIndexInFocus, newBlockIndexInFocus.^(oldBlockIndex.-(_focusStart)))
                    }
                  else
                    if (newBlockIndex.<(endIndex))
                      gotoPosRelaxed(newBlockIndex, 0, endIndex, depth)
                    else
                      {
                        lo = focusEnd.-(newBlockIndex).-(1);
                        blockIndex = endIndex;
                        if (_hasNext)
                          _hasNext = false
                        else
                          throw new NoSuchElementException("reached iterator end")
                      };
                  endLo = math.min(focusEnd.-(newBlockIndex), 32);
                  res
                }
            }
          }

          class GenRRBVectorClosedBlocksQuickRebalanceReverseIterator[+A](startIndex: Int, endIndex: Int) extends AbstractIterator[A] with Iterator[A] with GenRRBVectorClosedBlocksQuickRebalancePointer[A @uncheckedVariance] {
            private var lastIndexOfBlock: Int = _;
            private var lo: Int = _;
            private var endLo: Int = _;
            private var _hasNext: Boolean = startIndex.<(endIndex);
            final private[immutable] def resetIterator(): Unit = {
              val idx = endIndex.-(1);
              if (focusStart.<=(idx).&&(idx.<(focusEnd)))
                gotoPos(idx, idx.^(focus))
              else
                gotoPosRelaxed(idx, 0, endIndex, depth);
              lastIndexOfBlock = idx;
              lo = idx.-(focusStart).&(31);
              endLo = math.max(startIndex.-(focusStart).-(lastIndexOfBlock), 0)
            };
            def hasNext = _hasNext;
            def next(): A = if (_hasNext)
              {
                val res = display0(lo).asInstanceOf[A];
                lo.-=(1);
                if (lo.<(endLo))
                  {
                    val newBlockIndex = lastIndexOfBlock.-(32);
                    if (focusStart.<=(newBlockIndex))
                      {
                        val _focusStart = focusStart;
                        val newBlockIndexInFocus = newBlockIndex.-(_focusStart);
                        gotoPrevBlockStart(newBlockIndexInFocus, newBlockIndexInFocus.^(lastIndexOfBlock.-(_focusStart)));
                        lastIndexOfBlock = newBlockIndex;
                        lo = 31;
                        endLo = math.max(startIndex.-(focusStart).-(focus), 0)
                      }
                    else
                      if (startIndex.<(focusStart))
                        {
                          val newIndex = focusStart.-(1);
                          gotoPosRelaxed(newIndex, 0, endIndex, depth);
                          lastIndexOfBlock = newIndex;
                          lo = newIndex.-(focusStart).&(31);
                          endLo = math.max(startIndex.-(focusStart).-(lastIndexOfBlock), 0)
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

          private[immutable] trait GenRRBVectorClosedBlocksQuickRebalancePointer[A] {
            private[immutable] var display0: Array[AnyRef] = _;
            private[immutable] var display1: Array[AnyRef] = _;
            private[immutable] var display2: Array[AnyRef] = _;
            private[immutable] var display3: Array[AnyRef] = _;
            private[immutable] var display4: Array[AnyRef] = _;
            private[immutable] var display5: Array[AnyRef] = _;
            private[immutable] var depth: Int = _;
            private[immutable] var dirty: Boolean = false;
            private[immutable] var focusStart: Int = 0;
            private[immutable] var focusEnd: Int = 0;
            private[immutable] var focusDepth: Int = 0;
            private[immutable] var focus: Int = 0;
            private[immutable] var focusRelax: Int = 0;
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
                case 1 => display0 = root
                case 2 => display1 = root
                case 3 => display2 = root
                case 4 => display3 = root
                case 5 => display4 = root
                case 6 => display5 = root
              };
              depth = _depth;
              gotoIndex(0, _endIndex)
            };
            private[immutable] def initFrom[U](that: GenRRBVectorClosedBlocksQuickRebalancePointer[U]): Unit = {
              initFocus(that.focus, that.focusStart, that.focusEnd, that.focusDepth, that.focusRelax);
              depth = that.depth;
              dirty = that.dirty;
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
                  if (xor.<(32))
                    ()
                  else
                    gotoPos(indexInFocus, xor);
                  focus = indexInFocus
                }
              else
                gotoPosRelaxed(index, 0, endIndex, this.depth)
            };
            private[immutable] def allDisplaySizes(): Array[Array[Int]] = {
              val allSizes: Array[Array[Int]] = new Array(5);
              var currentDepth = focusDepth;
              while (currentDepth.<(depth)) 
                {
                  allSizes.update(currentDepth.-(1), currentDepth match {
                    case 1 => display1(display1.length.-(1)).asInstanceOf[Array[Int]]
                    case 2 => display2(display2.length.-(1)).asInstanceOf[Array[Int]]
                    case 3 => display3(display3.length.-(1)).asInstanceOf[Array[Int]]
                    case 4 => display4(display4.length.-(1)).asInstanceOf[Array[Int]]
                    case 5 => display5(display5.length.-(1)).asInstanceOf[Array[Int]]
                    case _ => null
                  });
                  currentDepth.+=(1)
                }
              ;
              allSizes
            };
            private[immutable] def putDisplaySizes(allSizes: Array[Array[Int]]): Unit = {
              var _depth = focusDepth;
              while (_depth.<(depth)) 
                {
                  _depth match {
                    case 1 => display1.update(display1.length.-(1), allSizes(_depth.-(1)))
                    case 2 => display2.update(display2.length.-(1), allSizes(_depth.-(1)))
                    case 3 => display3.update(display3.length.-(1), allSizes(_depth.-(1)))
                    case 4 => display4.update(display4.length.-(1), allSizes(_depth.-(1)))
                    case 5 => display5.update(display5.length.-(1), allSizes(_depth.-(1)))
                    case _ => null
                  };
                  _depth.+=(1)
                }
              
            };
            @tailrec final private[immutable] def gotoPosRelaxed(index: Int, _startIndex: Int, _endIndex: Int, _depth: Int, _focusRelax: Int = 0): Unit = {
              val display = _depth match {
                case 0 => null
                case 1 => display0
                case 2 => display1
                case 3 => display2
                case 4 => display3
                case 5 => display4
                case 6 => display5
                case _ => throw new IllegalArgumentException()
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
                    case _ => throw new IllegalArgumentException()
                  };
                  gotoPosRelaxed(index, if (is.==(0))
                    _startIndex
                  else
                    _startIndex.+(sizes(is.-(1))), if (is.<(sizes.length.-(1)))
                    _startIndex.+(sizes(is))
                  else
                    _endIndex, _depth.-(1), _focusRelax.|(is.<<((5).*(_depth.-(1)))))
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
            final private[immutable] def gotoPos(index: Int, xor: Int): Unit = if (xor.<(32))
              ()
            else
              if (xor.<(1024))
                display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
              else
                if (xor.<(32768))
                  {
                    display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]];
                    display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
                  }
                else
                  if (xor.<(1048576))
                    {
                      display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]];
                      display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]];
                      display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
                    }
                  else
                    if (xor.<(33554432))
                      {
                        display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]];
                        display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]];
                        display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]];
                        display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
                      }
                    else
                      if (xor.<(1073741824))
                        {
                          display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]];
                          display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]];
                          display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]];
                          display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]];
                          display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
                        }
                      else
                        throw new IllegalArgumentException();
            final private[immutable] def gotoNextBlockStart(index: Int, xor: Int): Unit = if (xor.<(1024))
              display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
            else
              if (xor.<(32768))
                {
                  display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]];
                  display0 = display1(0).asInstanceOf[Array[AnyRef]]
                }
              else
                if (xor.<(1048576))
                  {
                    display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]];
                    display1 = display2(0).asInstanceOf[Array[AnyRef]];
                    display0 = display1(0).asInstanceOf[Array[AnyRef]]
                  }
                else
                  if (xor.<(33554432))
                    {
                      display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]];
                      display2 = display3(0).asInstanceOf[Array[AnyRef]];
                      display1 = display2(0).asInstanceOf[Array[AnyRef]];
                      display0 = display1(0).asInstanceOf[Array[AnyRef]]
                    }
                  else
                    if (xor.<(1073741824))
                      {
                        display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]];
                        display3 = display4(0).asInstanceOf[Array[AnyRef]];
                        display2 = display3(0).asInstanceOf[Array[AnyRef]];
                        display1 = display2(0).asInstanceOf[Array[AnyRef]];
                        display0 = display1(0).asInstanceOf[Array[AnyRef]]
                      }
                    else
                      throw new IllegalArgumentException();
            final private[immutable] def gotoPrevBlockStart(index: Int, xor: Int): Unit = if (xor.<(1024))
              display0 = display1(index.>>(5).&(31)).asInstanceOf[Array[AnyRef]]
            else
              if (xor.<(32768))
                {
                  display1 = display2(index.>>(10).&(31)).asInstanceOf[Array[AnyRef]];
                  display0 = display1(31).asInstanceOf[Array[AnyRef]]
                }
              else
                if (xor.<(1048576))
                  {
                    display2 = display3(index.>>(15).&(31)).asInstanceOf[Array[AnyRef]];
                    display1 = display2(31).asInstanceOf[Array[AnyRef]];
                    display0 = display1(31).asInstanceOf[Array[AnyRef]]
                  }
                else
                  if (xor.<(33554432))
                    {
                      display3 = display4(index.>>(20).&(31)).asInstanceOf[Array[AnyRef]];
                      display2 = display3(31).asInstanceOf[Array[AnyRef]];
                      display1 = display2(31).asInstanceOf[Array[AnyRef]];
                      display0 = display1(31).asInstanceOf[Array[AnyRef]]
                    }
                  else
                    if (xor.<(1073741824))
                      {
                        display4 = display5(index.>>(25).&(31)).asInstanceOf[Array[AnyRef]];
                        display3 = display4(31).asInstanceOf[Array[AnyRef]];
                        display2 = display3(31).asInstanceOf[Array[AnyRef]];
                        display1 = display2(31).asInstanceOf[Array[AnyRef]];
                        display0 = display1(31).asInstanceOf[Array[AnyRef]]
                      }
                    else
                      throw new IllegalArgumentException();
            final private[immutable] def setUpNextBlockNewBranchWritable(index: Int, xor: Int): Unit = if (xor.<(1024))
              {
                if (this.depth.==(1))
                  {
                    display1 = new Array(3);
                    display1.update(0, display0);
                    depth = 2
                  }
                else
                  {
                    val len = display1.length;
                    display1 = copyOf(display1, len, len.+(1))
                  };
                display0 = new Array(1)
              }
            else
              if (xor.<(32768))
                {
                  if (this.depth.==(2))
                    {
                      display2 = new Array(3);
                      display2.update(0, display1);
                      depth = 3
                    }
                  else
                    {
                      val len = display2.length;
                      display2 = copyOf(display2, len, len.+(1))
                    };
                  display0 = new Array(1);
                  display1 = new Array(2)
                }
              else
                if (xor.<(1048576))
                  {
                    if (this.depth.==(3))
                      {
                        display3 = new Array(3);
                        display3.update(0, display2);
                        depth = 4
                      }
                    else
                      {
                        val len = display3.length;
                        display3 = copyOf(display3, len, len.+(1))
                      };
                    display0 = new Array(1);
                    display1 = new Array(2);
                    display2 = new Array(2)
                  }
                else
                  if (xor.<(33554432))
                    {
                      if (this.depth.==(4))
                        {
                          display4 = new Array(3);
                          display4.update(0, display3);
                          depth = 5
                        }
                      else
                        {
                          val len = display4.length;
                          display4 = copyOf(display4, len, len.+(1))
                        };
                      display0 = new Array(1);
                      display1 = new Array(2);
                      display2 = new Array(2);
                      display3 = new Array(2)
                    }
                  else
                    if (xor.<(1073741824))
                      {
                        if (this.depth.==(5))
                          {
                            display5 = new Array(3);
                            display5.update(0, display4);
                            depth = 6
                          }
                        else
                          {
                            val len = display5.length;
                            display5 = copyOf(display5, len, len.+(1))
                          };
                        display0 = new Array(1);
                        display1 = new Array(2);
                        display2 = new Array(2);
                        display3 = new Array(2);
                        display4 = new Array(2)
                      }
                    else
                      throw new IllegalArgumentException();
            final private[immutable] def gotoNextBlockStartWritable(index: Int, xor: Int): Unit = if (xor.<(1024))
              {
                if (this.depth.==(1))
                  {
                    display1 = new Array(33);
                    display1.update(0, display0);
                    this.depth.+=(1)
                  }
                else
                  ();
                display0 = new Array(32);
                display1.update(index.>>(5).&(31), display0)
              }
            else
              if (xor.<(32768))
                {
                  if (this.depth.==(2))
                    {
                      display2 = new Array(33);
                      display2.update(0, display1);
                      this.depth.+=(1)
                    }
                  else
                    ();
                  display0 = new Array(32);
                  display1 = new Array(33);
                  display1.update(index.>>(5).&(31), display0);
                  display2.update(index.>>(10).&(31), display1)
                }
              else
                if (xor.<(1048576))
                  {
                    if (this.depth.==(3))
                      {
                        display3 = new Array(33);
                        display3.update(0, display2);
                        this.depth.+=(1)
                      }
                    else
                      ();
                    display0 = new Array(32);
                    display1 = new Array(33);
                    display2 = new Array(33);
                    display1.update(index.>>(5).&(31), display0);
                    display2.update(index.>>(10).&(31), display1);
                    display3.update(index.>>(15).&(31), display2)
                  }
                else
                  if (xor.<(33554432))
                    {
                      if (this.depth.==(4))
                        {
                          display4 = new Array(33);
                          display4.update(0, display3);
                          this.depth.+=(1)
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
                    if (xor.<(1073741824))
                      {
                        if (this.depth.==(5))
                          {
                            display5 = new Array(33);
                            display5.update(0, display4);
                            this.depth.+=(1)
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
            final private[immutable] def copyDisplays(_depth: Int, _focus: Int): Unit = _depth match {
              case 1 => ()
              case 2 => {
                val idx_1 = _focus.>>(5).&(31);
                display1 = copyOf(display1, idx_1.+(1), idx_1.+(2))
              }
              case 3 => {
                val idx_1 = _focus.>>(5).&(31);
                display1 = copyOf(display1, idx_1.+(1), idx_1.+(2));
                val idx_2 = _focus.>>(10).&(31);
                display2 = copyOf(display2, idx_2.+(1), idx_2.+(2))
              }
              case 4 => {
                val idx_1 = _focus.>>(5).&(31);
                display1 = copyOf(display1, idx_1.+(1), idx_1.+(2));
                val idx_2 = _focus.>>(10).&(31);
                display2 = copyOf(display2, idx_2.+(1), idx_2.+(2));
                val idx_3 = _focus.>>(15).&(31);
                display3 = copyOf(display3, idx_3.+(1), idx_3.+(2))
              }
              case 5 => {
                val idx_1 = _focus.>>(5).&(31);
                display1 = copyOf(display1, idx_1.+(1), idx_1.+(2));
                val idx_2 = _focus.>>(10).&(31);
                display2 = copyOf(display2, idx_2.+(1), idx_2.+(2));
                val idx_3 = _focus.>>(15).&(31);
                display3 = copyOf(display3, idx_3.+(1), idx_3.+(2));
                val idx_4 = _focus.>>(20).&(31);
                display4 = copyOf(display4, idx_4.+(1), idx_4.+(2))
              }
              case 6 => {
                val idx_1 = _focus.>>(5).&(31);
                display1 = copyOf(display1, idx_1.+(1), idx_1.+(2));
                val idx_2 = _focus.>>(10).&(31);
                display2 = copyOf(display2, idx_2.+(1), idx_2.+(2));
                val idx_3 = _focus.>>(15).&(31);
                display3 = copyOf(display3, idx_3.+(1), idx_3.+(2));
                val idx_4 = _focus.>>(20).&(31);
                display4 = copyOf(display4, idx_4.+(1), idx_4.+(2));
                val idx_5 = _focus.>>(25).&(31);
                display5 = copyOf(display5, idx_5.+(1), idx_5.+(2))
              }
            };
            final private[immutable] def copyDisplaysTop(currentDepth: Int, _focusRelax: Int): Unit = {
              currentDepth match {
                case 2 => {
                  val cutIndex = _focusRelax.>>(5).&(31);
                  display1 = copyOf(display1, cutIndex.+(1), cutIndex.+(2))
                }
                case 3 => {
                  val cutIndex = _focusRelax.>>(10).&(31);
                  display2 = copyOf(display2, cutIndex.+(1), cutIndex.+(2))
                }
                case 4 => {
                  val cutIndex = _focusRelax.>>(15).&(31);
                  display3 = copyOf(display3, cutIndex.+(1), cutIndex.+(2))
                }
                case 5 => {
                  val cutIndex = _focusRelax.>>(20).&(31);
                  display4 = copyOf(display4, cutIndex.+(1), cutIndex.+(2))
                }
                case 6 => {
                  val cutIndex = _focusRelax.>>(25).&(31);
                  display5 = copyOf(display5, cutIndex.+(1), cutIndex.+(2))
                }
                case _ => throw new IllegalStateException()
              };
              if (currentDepth.<(this.depth))
                copyDisplaysTop(currentDepth.+(1), focusRelax)
              else
                ()
            };
            final private[immutable] def stabilize(_depth: Int, _focus: Int): Unit = _depth match {
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
            };
            private[immutable] def cleanTop(cutIndex: Int): Unit = {
              @tailrec def cleanTopRec(_depth: Int): Unit = _depth match {
                case 2 => if (cutIndex.>>(5).==(0))
                  {
                    display1 = null;
                    this.depth = 1
                  }
                else
                  depth = 2
                case 3 => if (cutIndex.>>(10).==(0))
                  {
                    display2 = null;
                    cleanTopRec(_depth.-(1))
                  }
                else
                  depth = 3
                case 4 => if (cutIndex.>>(15).==(0))
                  {
                    display3 = null;
                    cleanTopRec(_depth.-(1))
                  }
                else
                  depth = 4
                case 5 => if (cutIndex.>>(20).==(0))
                  {
                    display4 = null;
                    cleanTopRec(_depth.-(1))
                  }
                else
                  depth = 5
                case 6 => if (cutIndex.>>(25).==(0))
                  {
                    display5 = null;
                    cleanTopRec(_depth.-(1))
                  }
                else
                  depth = 6
              };
              cleanTopRec(this.depth)
            };
            final private[immutable] def copyOf(array: Array[AnyRef], numElements: Int, newSize: Int) = {
              val newArray = new Array[AnyRef](newSize);
              Platform.arraycopy(array, 0, newArray, 0, numElements);
              newArray
            }
          }
        }
      }
    }
  }
}