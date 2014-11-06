package scala {
  package collection {
    package immutable {
      package generated {
        package rrbvector {
          import scala.annotation.tailrec

          import scala.compat.Platform

          import scala.annotation.unchecked.uncheckedVariance

          import scala.collection.generic.{GenericCompanion, GenericTraversableTemplate, CanBuildFrom, IndexedSeqFactory}

          import scala.collection.parallel.immutable.generated.rrbvector.ParRRBVector_quick_directLevel_64_splitbalanced

          object RRBVector_quick_directLevel_64_splitbalanced extends scala.collection.generic.IndexedSeqFactory[RRBVector_quick_directLevel_64_splitbalanced] {
            def newBuilder[A]: mutable.Builder[A, RRBVector_quick_directLevel_64_splitbalanced[A]] = new RRBVector_quick_directLevel_64_splitbalancedBuilder[A]();
            implicit def canBuildFrom[A]: scala.collection.generic.CanBuildFrom[Coll, A, RRBVector_quick_directLevel_64_splitbalanced[A]] = ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]];
            lazy private val EMPTY_VECTOR = new RRBVector_quick_directLevel_64_splitbalanced[Nothing](0);
            override def empty[A]: RRBVector_quick_directLevel_64_splitbalanced[A] = EMPTY_VECTOR
          }

          final class RRBVector_quick_directLevel_64_splitbalanced[+A](override private[immutable] val endIndex: Int) extends scala.collection.AbstractSeq[A] with scala.collection.immutable.IndexedSeq[A] with scala.collection.generic.GenericTraversableTemplate[A, RRBVector_quick_directLevel_64_splitbalanced] with scala.collection.IndexedSeqLike[A, RRBVector_quick_directLevel_64_splitbalanced[A]] with RRBVector_quick_directLevel_64_splitbalancedPointer[A @uncheckedVariance] with Serializable { self =>
            private[immutable] var dirty: Boolean = false;
            override def companion: scala.collection.generic.GenericCompanion[RRBVector_quick_directLevel_64_splitbalanced] = RRBVector_quick_directLevel_64_splitbalanced;
            def length(): Int = endIndex;
            override def lengthCompare(len: Int): Int = endIndex.-(len);
            override def par = new ParRRBVector_quick_directLevel_64_splitbalanced[A](this);
            override def iterator: RRBVector_quick_directLevel_64_splitbalancedIterator[A] = {
              if (this.dirty)
                {
                  this.stabilize();
                  this.dirty = false
                }
              else
                ();
              val it = new RRBVector_quick_directLevel_64_splitbalancedIterator[A](0, endIndex);
              it.initIteratorFrom(this);
              it
            };
            override def reverseIterator: RRBVector_quick_directLevel_64_splitbalancedReverseIterator[A] = {
              if (this.dirty)
                {
                  this.stabilize();
                  this.dirty = false
                }
              else
                ();
              val rit = new RRBVector_quick_directLevel_64_splitbalancedReverseIterator[A](0, endIndex);
              rit.initIteratorFrom(this);
              rit
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
                  getElementFromRoot(index).asInstanceOf[A]
                else
                  throw new IndexOutOfBoundsException(index.toString)
            };
            override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[RRBVector_quick_directLevel_64_splitbalanced[A], B, That]): That = if (bf.eq(IndexedSeq.ReusableCBF))
              (if (this.endIndex.==(0))
  {
    val resultVector = new RRBVector_quick_directLevel_64_splitbalanced[B](1);
    resultVector.initSingleton(elem);
    resultVector
  }
else
  {
    val _endIndex = this.endIndex;
    val resultVector = new RRBVector_quick_directLevel_64_splitbalanced[B](_endIndex.+(1));
    resultVector.initWithFocusFrom(this);
    resultVector.dirty = this.dirty;
    if (_endIndex.-(1).-(this.focusStart).^(this.focus).>=(64))
      {
        if (resultVector.dirty)
          {
            resultVector.stabilize();
            resultVector.dirty = false
          }
        else
          ();
        resultVector.focusOn(_endIndex.-(1))
      }
    else
      ();
    val elemIndexInBlock = _endIndex.-(resultVector.focusStart).&(63);
    if (elemIndexInBlock.==(0))
      {
        if (resultVector.dirty)
          {
            resultVector.stabilize();
            resultVector.dirty = false
          }
        else
          ();
        resultVector.appendBackSetupNewBlock();
        resultVector.display0.update(elemIndexInBlock, elem.asInstanceOf[AnyRef]);
        assert(resultVector.assertVectorInvariant());
        resultVector
      }
    else
      {
        resultVector.dirty = true;
        resultVector.focusEnd = resultVector.endIndex;
        val d0 = new Array[AnyRef](elemIndexInBlock.+(1));
        Platform.arraycopy(resultVector.display0, 0, d0, 0, elemIndexInBlock);
        d0.update(elemIndexInBlock, elem.asInstanceOf[AnyRef]);
        resultVector.display0 = d0;
        assert(resultVector.assertVectorInvariant());
        resultVector
      }
  }).asInstanceOf[That]
            else
              super.:+(elem)(bf);
            override def isEmpty: Boolean = this.endIndex.==(0);
            override def head: A = if (this.endIndex.!=(0))
              apply(0)
            else
              throw new UnsupportedOperationException("empty.head");
            override def take(n: Int): RRBVector_quick_directLevel_64_splitbalanced[A] = if (n.<=(0))
              RRBVector_quick_directLevel_64_splitbalanced.empty
            else
              if (n.<(endIndex))
                takeFront0(n)
              else
                this;
            override def dropRight(n: Int): RRBVector_quick_directLevel_64_splitbalanced[A] = if (n.<=(0))
              this
            else
              if (n.<(endIndex))
                takeFront0(endIndex.-(n))
              else
                RRBVector_quick_directLevel_64_splitbalanced.empty;
            override def slice(from: Int, until: Int): RRBVector_quick_directLevel_64_splitbalanced[A] = take(until).drop(from);
            override def splitAt(n: Int): scala.Tuple2[RRBVector_quick_directLevel_64_splitbalanced[A], RRBVector_quick_directLevel_64_splitbalanced[A]] = scala.Tuple2(take(n), drop(n));
            override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[RRBVector_quick_directLevel_64_splitbalanced[A], B, That]): That = if (bf.eq(IndexedSeq.ReusableCBF))
              if (that.isEmpty)
                this.asInstanceOf[That]
              else
                if (that.isInstanceOf[RRBVector_quick_directLevel_64_splitbalanced[B]])
                  {
                    val thatVec = that.asInstanceOf[RRBVector_quick_directLevel_64_splitbalanced[B]];
                    if (this.isEmpty)
                      thatVec.asInstanceOf[That]
                    else
                      {
                        val newVec = new RRBVector_quick_directLevel_64_splitbalanced(this.endIndex.+(thatVec.endIndex));
                        newVec.initWithFocusFrom(this);
                        newVec.concatenate(this.endIndex, thatVec);
                        newVec.asInstanceOf[That]
                      }
                  }
                else
                  super.++(that.seq)
            else
              super.++(that.seq);
            override def tail: RRBVector_quick_directLevel_64_splitbalanced[A] = if (this.endIndex.!=(0))
              this.drop(1)
            else
              throw new UnsupportedOperationException("empty.tail");
            override def last: A = if (this.endIndex.!=(0))
              this.apply(this.length.-(1))
            else
              throw new UnsupportedOperationException("empty.last");
            override def init: RRBVector_quick_directLevel_64_splitbalanced[A] = if (this.endIndex.!=(0))
              dropRight(1)
            else
              throw new UnsupportedOperationException("empty.init");
            private[immutable] def appendBackSetupNewBlock() = {
              assert(dirty.`unary_!`);
              val oldDepth = depth;
              val newRelaxedIndex = endIndex.-(1).-(focusStart).+(focusRelax);
              val xor = newRelaxedIndex.^(focus.|(focusRelax));
              setupNewBlockInNextBranch(newRelaxedIndex, xor);
              if (oldDepth.==(depth))
                {
                  var i = (31).-(java.lang.Integer.numberOfLeadingZeros(xor))./(6).+(1);
                  val _focusDepth = focusDepth;
                  while (i.<(oldDepth)) 
                    {
                      var display: Array[AnyRef] = null;
                      var newDisplay: Array[AnyRef] = null;
                      var newSizes: Array[Int] = null;
                      i match {
                        case 2 => display = display2
                        case 3 => display = display3
                        case 4 => display = display4
                        case 5 => display = display5
                      };
                      val displayLen = display.length.-(1);
                      if (i.>=(_focusDepth))
                        {
                          val oldSizes = display(displayLen).asInstanceOf[Array[Int]];
                          newSizes = new Array[Int](displayLen);
                          Platform.arraycopy(oldSizes, 0, newSizes, 0, displayLen.-(1));
                          newSizes.update(displayLen.-(1), oldSizes(displayLen.-(1)).+(1))
                        }
                      else
                        ();
                      newDisplay = new Array[AnyRef](display.length);
                      Platform.arraycopy(display, 0, newDisplay, 0, displayLen);
                      if (i.>=(_focusDepth))
                        newDisplay.update(displayLen, newSizes)
                      else
                        ();
                      i match {
                        case 2 => {
                          newDisplay.update(newRelaxedIndex.>>(12).&(63), display1);
                          display2 = newDisplay
                        }
                        case 3 => {
                          newDisplay.update(newRelaxedIndex.>>(18).&(63), display2);
                          display3 = newDisplay
                        }
                        case 4 => {
                          newDisplay.update(newRelaxedIndex.>>(24).&(63), display3);
                          display4 = newDisplay
                        }
                        case 5 => {
                          newDisplay.update(newRelaxedIndex.>>(30).&(63), display4);
                          display5 = newDisplay
                        }
                      };
                      i.+=(1)
                    }
                  
                }
              else
                ();
              if (oldDepth.==(focusDepth))
                initFocus(endIndex.-(1), 0, endIndex, depth, 0)
              else
                initFocus(endIndex.-(1), endIndex.-(1), endIndex, 1, newRelaxedIndex.&(-64))
            };
            private[immutable] def concatenate[B >: A](currentSize: Int, that: RRBVector_quick_directLevel_64_splitbalanced[B]): scala.Unit = {
              assert(that.assertVectorInvariant());
              assert((0).<(that.length));
              if (this.dirty)
                {
                  this.stabilize();
                  this.dirty = false
                }
              else
                ();
              if (that.dirty)
                {
                  that.stabilize();
                  that.dirty = false
                }
              else
                ();
              assert(that.assertVectorInvariant());
              this.focusOn(currentSize.-(1));
              math.max(this.depth, that.depth) match {
                case 1 => {
                  val concat = rebalancedLeafs(display0, that.display0, isTop = true);
                  initFromRoot(concat, if (endIndex.<=(64))
                    1
                  else
                    2);
                  assert(this.assertVectorInvariant())
                }
                case 2 => {
                  var d0: Array[AnyRef] = null;
                  var d1: Array[AnyRef] = null;
                  if (that.focus.&(-64).==(0))
                    {
                      d1 = that.display1;
                      d0 = that.display0
                    }
                  else
                    {
                      if (that.display1.!=(null))
                        d1 = that.display1
                      else
                        ();
                      if (d1.==(null))
                        d0 = that.display0
                      else
                        d0 = d1(0).asInstanceOf[Array[AnyRef]]
                    };
                  var concat: Array[AnyRef] = rebalancedLeafs(this.display0, d0, isTop = false);
                  concat = rebalanced(this.display1, concat, that.display1, 2);
                  if (concat.length.==(2))
                    initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], 2)
                  else
                    initFromRoot(withComputedSizes(concat, 3), 3);
                  assert(this.assertVectorInvariant())
                }
                case 3 => {
                  var d0: Array[AnyRef] = null;
                  var d1: Array[AnyRef] = null;
                  var d2: Array[AnyRef] = null;
                  if (that.focus.&(-64).==(0))
                    {
                      d2 = that.display2;
                      d1 = that.display1;
                      d0 = that.display0
                    }
                  else
                    {
                      if (that.display2.!=(null))
                        d2 = that.display2
                      else
                        ();
                      if (d2.==(null))
                        d1 = that.display1
                      else
                        d1 = d2(0).asInstanceOf[Array[AnyRef]];
                      if (d1.==(null))
                        d0 = that.display0
                      else
                        d0 = d1(0).asInstanceOf[Array[AnyRef]]
                    };
                  var concat: Array[AnyRef] = rebalancedLeafs(this.display0, d0, isTop = false);
                  concat = rebalanced(this.display1, concat, d1, 2);
                  concat = rebalanced(this.display2, concat, that.display2, 3);
                  if (concat.length.==(2))
                    initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], 3)
                  else
                    initFromRoot(withComputedSizes(concat, 4), 4);
                  assert(this.assertVectorInvariant())
                }
                case 4 => {
                  var d0: Array[AnyRef] = null;
                  var d1: Array[AnyRef] = null;
                  var d2: Array[AnyRef] = null;
                  var d3: Array[AnyRef] = null;
                  if (that.focus.&(-64).==(0))
                    {
                      d3 = that.display3;
                      d2 = that.display2;
                      d1 = that.display1;
                      d0 = that.display0
                    }
                  else
                    {
                      if (that.display3.!=(null))
                        d3 = that.display3
                      else
                        ();
                      if (d3.==(null))
                        d2 = that.display2
                      else
                        d2 = d3(0).asInstanceOf[Array[AnyRef]];
                      if (d2.==(null))
                        d1 = that.display1
                      else
                        d1 = d2(0).asInstanceOf[Array[AnyRef]];
                      if (d1.==(null))
                        d0 = that.display0
                      else
                        d0 = d1(0).asInstanceOf[Array[AnyRef]]
                    };
                  var concat: Array[AnyRef] = rebalancedLeafs(this.display0, d0, isTop = false);
                  concat = rebalanced(this.display1, concat, d1, 2);
                  concat = rebalanced(this.display2, concat, d2, 3);
                  concat = rebalanced(this.display3, concat, that.display3, 4);
                  if (concat.length.==(2))
                    initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], 4)
                  else
                    initFromRoot(withComputedSizes(concat, 5), 5);
                  assert(this.assertVectorInvariant())
                }
                case 5 => {
                  var d0: Array[AnyRef] = null;
                  var d1: Array[AnyRef] = null;
                  var d2: Array[AnyRef] = null;
                  var d3: Array[AnyRef] = null;
                  var d4: Array[AnyRef] = null;
                  if (that.focus.&(-64).==(0))
                    {
                      d4 = that.display4;
                      d3 = that.display3;
                      d2 = that.display2;
                      d1 = that.display1;
                      d0 = that.display0
                    }
                  else
                    {
                      if (that.display4.!=(null))
                        d4 = that.display4
                      else
                        ();
                      if (d4.==(null))
                        d3 = that.display3
                      else
                        d3 = d4(0).asInstanceOf[Array[AnyRef]];
                      if (d3.==(null))
                        d2 = that.display2
                      else
                        d2 = d3(0).asInstanceOf[Array[AnyRef]];
                      if (d2.==(null))
                        d1 = that.display1
                      else
                        d1 = d2(0).asInstanceOf[Array[AnyRef]];
                      if (d1.==(null))
                        d0 = that.display0
                      else
                        d0 = d1(0).asInstanceOf[Array[AnyRef]]
                    };
                  var concat: Array[AnyRef] = rebalancedLeafs(this.display0, d0, isTop = false);
                  concat = rebalanced(this.display1, concat, d1, 2);
                  concat = rebalanced(this.display2, concat, d2, 3);
                  concat = rebalanced(this.display3, concat, d3, 4);
                  concat = rebalanced(this.display4, concat, that.display4, 5);
                  if (concat.length.==(2))
                    initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], 5)
                  else
                    initFromRoot(withComputedSizes(concat, 6), 6);
                  assert(this.assertVectorInvariant())
                }
                case 6 => {
                  var d0: Array[AnyRef] = null;
                  var d1: Array[AnyRef] = null;
                  var d2: Array[AnyRef] = null;
                  var d3: Array[AnyRef] = null;
                  var d4: Array[AnyRef] = null;
                  var d5: Array[AnyRef] = null;
                  if (that.focus.&(-64).==(0))
                    {
                      d5 = that.display5;
                      d4 = that.display4;
                      d3 = that.display3;
                      d2 = that.display2;
                      d1 = that.display1;
                      d0 = that.display0
                    }
                  else
                    {
                      if (that.display5.!=(null))
                        d5 = that.display5
                      else
                        ();
                      if (d5.==(null))
                        d4 = that.display4
                      else
                        d4 = d5(0).asInstanceOf[Array[AnyRef]];
                      if (d4.==(null))
                        d3 = that.display3
                      else
                        d3 = d4(0).asInstanceOf[Array[AnyRef]];
                      if (d3.==(null))
                        d2 = that.display2
                      else
                        d2 = d3(0).asInstanceOf[Array[AnyRef]];
                      if (d2.==(null))
                        d1 = that.display1
                      else
                        d1 = d2(0).asInstanceOf[Array[AnyRef]];
                      if (d1.==(null))
                        d0 = that.display0
                      else
                        d0 = d1(0).asInstanceOf[Array[AnyRef]]
                    };
                  var concat: Array[AnyRef] = rebalancedLeafs(this.display0, d0, isTop = false);
                  concat = rebalanced(this.display1, concat, d1, 2);
                  concat = rebalanced(this.display2, concat, d2, 3);
                  concat = rebalanced(this.display3, concat, d3, 4);
                  concat = rebalanced(this.display4, concat, d4, 5);
                  concat = rebalanced(this.display5, concat, that.display5, 6);
                  if (concat.length.==(2))
                    initFromRoot(concat(0).asInstanceOf[Array[AnyRef]], 6)
                  else
                    initFromRoot(withComputedSizes(concat, 7), 7);
                  assert(this.assertVectorInvariant())
                }
                case _ => throw new IllegalStateException()
              }
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
              val tup = computeNewSizes(displayLeft, concat, displayRight, currentDepth);
              val sizes: Array[Int] = tup._1;
              val nalen: Int = tup._2;
              val top = new Array[AnyRef](nalen.>>(6).+(if (nalen.&(63).==(0))
                1
              else
                2));
              var mid = new Array[AnyRef]((if (nalen.<=(64))
  nalen
else
  64).+(1));
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
                      if (iBot.|(j).==(0).&&(displayValueEnd.==(sizes(iSizes))))
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
                      if (iMid.==(64))
                        {
                          top.update(iTop, withComputedSizes(mid, currentDepth));
                          iTop.+=(1);
                          iMid = 0;
                          if (nalen.-(iTop.<<(6)).!=(0))
                            mid = new Array[AnyRef](math.min(nalen.-(iTop.<<(6)).+(1), 65))
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
            };
            private def rebalancedLeafs(displayLeft: Array[AnyRef], displayRight: Array[AnyRef], isTop: Boolean): Array[AnyRef] = {
              val leftLength = displayLeft.length;
              val rightLength = displayRight.length;
              if (leftLength.==(64))
                {
                  val top = new Array[AnyRef](3);
                  top.update(0, displayLeft);
                  top.update(1, displayRight);
                  top
                }
              else
                if (leftLength.+(rightLength).<=(64))
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
                    val arr0 = new Array[AnyRef](64);
                    val arr1 = new Array[AnyRef](leftLength.+(rightLength).-(64));
                    top.update(0, arr0);
                    top.update(1, arr1);
                    Platform.arraycopy(displayLeft, 0, arr0, 0, leftLength);
                    Platform.arraycopy(displayRight, 0, arr0, leftLength, (64).-(leftLength));
                    Platform.arraycopy(displayRight, (64).-(leftLength), arr1, 0, rightLength.-(64).+(leftLength));
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
              val MinWidth = 63;
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
                      val msz = math.min(el.+(szs(ix.+(1))), 64);
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
            };
            private def withComputedSizes(node: Array[AnyRef], currentDepth: Int): Array[AnyRef] = {
              assert(node.!=(null));
              assert((0).<=(currentDepth));
              assert(currentDepth.<=(6));
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
                  if (end.>(1).&&(sizes(end.-(2)).!=(end.-(1).<<((6).*(currentDepth.-(1))))).||(currentDepth.>(2).&&(last(last.length.-(1)).!=(null))))
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
                  if (end.>(1).&&(sizes(end.-(2)).!=(end.-(1).<<(6))))
                    node.update(end, sizes)
                  else
                    ()
                };
              node
            };
            private def treeSize(tree: Array[AnyRef], currentDepth: Int): Int = {
              assert(tree.!=(null));
              assert((0).<=(currentDepth));
              assert(currentDepth.<=(6));
              if (currentDepth.==(1))
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
                          acc.+=(_tree.length.-(2).*((1).<<((6).*(_currentDepth.-(1)))));
                          _currentDepth.-=(1);
                          _tree = _tree(_tree.length.-(2)).asInstanceOf[Array[AnyRef]]
                        }
                      ;
                      acc.+(_tree.length)
                    }
                }
            };
            private def takeFront0(n: Int): RRBVector_quick_directLevel_64_splitbalanced[A] = {
              if (dirty)
                {
                  stabilize();
                  dirty = false
                }
              else
                ();
              val vec = new RRBVector_quick_directLevel_64_splitbalanced[A](n);
              vec.initWithFocusFrom(this);
              if (depth.>(1))
                {
                  vec.focusOn(n.-(1));
                  val d0len = vec.focus.&(63).+(1);
                  if (d0len.!=(64))
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
                      vec.copyDisplays(vec.focusDepth, cutIndex);
                      var i = vec.depth;
                      var offset = 0;
                      while (i.>(vec.focusDepth)) 
                        {
                          val display = i match {
                            case 2 => vec.display1
                            case 3 => vec.display2
                            case 4 => vec.display3
                            case 5 => vec.display4
                            case 6 => vec.display5
                          };
                          val oldSizes = display(display.length.-(1)).asInstanceOf[Array[Int]];
                          val newLen = vec.focusRelax.>>((6).*(i.-(1))).&(63).+(1);
                          val newSizes = new Array[Int](newLen);
                          Platform.arraycopy(oldSizes, 0, newSizes, 0, newLen.-(1));
                          newSizes.update(newLen.-(1), n.-(offset));
                          if (newLen.>(1))
                            offset.+=(newSizes(newLen.-(2)))
                          else
                            ();
                          val newDisplay = new Array[AnyRef](newLen.+(1));
                          Platform.arraycopy(display, 0, newDisplay, 0, newLen);
                          newDisplay.update(newLen.-(1), null);
                          newDisplay.update(newLen, newSizes);
                          i match {
                            case 2 => vec.display1 = newDisplay
                            case 3 => vec.display2 = newDisplay
                            case 4 => vec.display3 = newDisplay
                            case 5 => vec.display4 = newDisplay
                            case 6 => vec.display5 = newDisplay
                          };
                          i.-=(1)
                        }
                      ;
                      vec.stabilizeDisplayPath(vec.depth, cutIndex);
                      vec.focusEnd = n
                    }
                  else
                    vec.focusEnd = n
                }
              else
                if (n.!=(64))
                  {
                    val d0 = new Array[AnyRef](n);
                    Platform.arraycopy(vec.display0, 0, d0, 0, n);
                    vec.display0 = d0;
                    vec.initFocus(0, 0, n, 1, 0)
                  }
                else
                  ();
              assert(vec.assertVectorInvariant());
              vec
            };
            private[immutable] def assertVectorInvariant(): Boolean = {
              assert((0).<=(depth).&&(depth.<=(6)), depth);
              assert(isEmpty.==(depth.==(0)), scala.Tuple2(isEmpty, depth));
              assert(isEmpty.==(length.==(0)), scala.Tuple2(isEmpty, length));
              assert(length.==(endIndex), scala.Tuple2(length, endIndex));
              assert(depth.<=(0).&&(display0.==(null)).||(depth.>(0).&&(display0.!=(null))), ({
  val x$85 = depth.toString;
  "<=0 <==> display0==null ".+:(x$85)
}).:+(depth, display0));
              assert(depth.<=(1).&&(display1.==(null)).||(depth.>(0).&&(display1.!=(null))), ({
  val x$86 = depth.toString;
  "<=1 <==> display1==null ".+:(x$86)
}).:+(depth, display1));
              assert(depth.<=(2).&&(display2.==(null)).||(depth.>(0).&&(display2.!=(null))), ({
  val x$87 = depth.toString;
  "<=2 <==> display2==null ".+:(x$87)
}).:+(depth, display2));
              assert(depth.<=(3).&&(display3.==(null)).||(depth.>(0).&&(display3.!=(null))), ({
  val x$88 = depth.toString;
  "<=3 <==> display3==null ".+:(x$88)
}).:+(depth, display3));
              assert(depth.<=(4).&&(display4.==(null)).||(depth.>(0).&&(display4.!=(null))), ({
  val x$89 = depth.toString;
  "<=4 <==> display4==null ".+:(x$89)
}).:+(depth, display4));
              assert(depth.<=(5).&&(display5.==(null)).||(depth.>(0).&&(display5.!=(null))), ({
  val x$90 = depth.toString;
  "<=5 <==> display5==null ".+:(x$90)
}).:+(depth, display5));
              if (display5.!=(null))
                {
                  assert(display4.!=(null));
                  if (focusDepth.<=(5))
                    assert(display5(focusRelax.>>(30).&(63)).==(display4))
                  else
                    assert(display5(focus.>>(30).&(63)).==(display4))
                }
              else
                ();
              if (display4.!=(null))
                {
                  assert(display3.!=(null));
                  if (focusDepth.<=(4))
                    assert(display4(focusRelax.>>(24).&(63)).==(display3))
                  else
                    assert(display4(focus.>>(24).&(63)).==(display3))
                }
              else
                ();
              if (display3.!=(null))
                {
                  assert(display2.!=(null));
                  if (focusDepth.<=(3))
                    assert(display3(focusRelax.>>(18).&(63)).==(display2))
                  else
                    assert(display3(focus.>>(18).&(63)).==(display2))
                }
              else
                ();
              if (display2.!=(null))
                {
                  assert(display1.!=(null));
                  if (focusDepth.<=(2))
                    assert(display2(focusRelax.>>(12).&(63)).==(display1))
                  else
                    assert(display2(focus.>>(12).&(63)).==(display1))
                }
              else
                ();
              if (display1.!=(null))
                {
                  assert(display0.!=(null));
                  if (dirty.`unary_!`)
                    if (focusDepth.<=(1))
                      assert(display1(focusRelax.>>(6).&(63)).==(display0))
                    else
                      assert(display1(focus.>>(6).&(63)).==(display0))
                  else
                    ()
                }
              else
                ();
              assert((0).<=(focusStart).&&(focusStart.<=(focusEnd)).&&(focusEnd.<=(endIndex)), scala.Tuple3(focusStart, focusEnd, endIndex));
              assert(focusStart.==(focusEnd).||(focusEnd.!=(0)), "focusStart==focusEnd ==> focusEnd==0".+(focusStart, focusEnd));
              assert((0).<=(focusDepth).&&(focusDepth.<=(depth)), scala.Tuple2(focusDepth, depth));
              def checkSizes(node: Array[AnyRef], currentDepth: Int, _endIndex: Int): Unit = if (currentDepth.>(1))
                {
                  val sizes = node.last.asInstanceOf[Array[Int]];
                  if (sizes.!=(null))
                    {
                      assert(node.length.==(sizes.length.+(1)));
                      if (dirty.`unary_!`)
                        assert(sizes.last.==(_endIndex), scala.Tuple2(sizes.last, _endIndex))
                      else
                        ();
                      0.until(sizes.length.-(1)).foreach(((i) => checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth.-(1), sizes(i).-(if (i.==(0))
                        0
                      else
                        sizes(i.-(1))))));
                      checkSizes(node(node.length.-(2)).asInstanceOf[Array[AnyRef]], currentDepth.-(1), if (sizes.length.>(1))
                        sizes.last.-(sizes(sizes.length.-(2)))
                      else
                        sizes.last)
                    }
                  else
                    {
                      0.until(node.length.-(2)).foreach(((i) => checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth.-(1), (1).<<((6).*(currentDepth.-(1))))));
                      val expectedLast = _endIndex.-((1).<<((6).*(currentDepth.-(1))).*(node.length.-(2)));
                      assert((1).<=(expectedLast).&&(expectedLast.<=((1).<<((6).*(currentDepth)))));
                      checkSizes(node(node.length.-(2)).asInstanceOf[Array[AnyRef]], currentDepth.-(1), expectedLast)
                    }
                }
              else
                if (dirty.`unary_!`)
                  assert(node.length.==(_endIndex))
                else
                  ();
              depth match {
                case 1 => checkSizes(display0, 1, endIndex)
                case 2 => checkSizes(display1, 2, endIndex)
                case 3 => checkSizes(display2, 3, endIndex)
                case 4 => checkSizes(display3, 4, endIndex)
                case 5 => checkSizes(display4, 5, endIndex)
                case 6 => checkSizes(display5, 6, endIndex)
                case _ => ()
              };
              true
            }
          }

          final class RRBVector_quick_directLevel_64_splitbalancedBuilder[A] extends mutable.Builder[A, RRBVector_quick_directLevel_64_splitbalanced[A]] with RRBVector_quick_directLevel_64_splitbalancedPointer[A @uncheckedVariance] {
            display0 = new Array[AnyRef](64);
            depth = 1;
            private var blockIndex = 0;
            private var lo = 0;
            private var acc: RRBVector_quick_directLevel_64_splitbalanced[A] = null;
            def +=(elem: A): this.type = {
              if (lo.>=(64))
                {
                  val newBlockIndex = blockIndex.+(64);
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
            override def ++=(xs: TraversableOnce[A]): this.type = {
              if (xs.nonEmpty)
                if (xs.isInstanceOf[RRBVector_quick_directLevel_64_splitbalanced[A]])
                  {
                    val thatVec = xs.asInstanceOf[RRBVector_quick_directLevel_64_splitbalanced[A]];
                    if (endIndex.!=(0))
                      {
                        acc = this.result().++(xs);
                        this.clearCurrent()
                      }
                    else
                      if (acc.!=(null))
                        acc = acc.++(thatVec)
                      else
                        acc = thatVec
                  }
                else
                  super.++=(xs)
              else
                ();
              this
            };
            def result(): RRBVector_quick_directLevel_64_splitbalanced[A] = {
              val current = currentResult();
              if (acc.==(null))
                current
              else
                acc.++(current)
            };
            def clear(): Unit = {
              clearCurrent();
              acc = null
            };
            private[collection] def endIndex = {
              var sz = blockIndex.+(lo);
              if (acc.!=(null))
                sz.+=(acc.endIndex)
              else
                ();
              sz
            };
            private def currentResult(): RRBVector_quick_directLevel_64_splitbalanced[A] = {
              val size = blockIndex.+(lo);
              if (size.==(0))
                RRBVector_quick_directLevel_64_splitbalanced.empty
              else
                {
                  val resultVector = new RRBVector_quick_directLevel_64_splitbalanced[A](size);
                  resultVector.initFrom(this);
                  resultVector.display0 = copyOf(resultVector.display0, lo, lo);
                  val _depth = depth;
                  if (_depth.>(1))
                    {
                      resultVector.copyDisplays(_depth, size.-(1));
                      resultVector.stabilizeDisplayPath(_depth, size.-(1))
                    }
                  else
                    ();
                  resultVector.gotoPos(0, size.-(1));
                  resultVector.initFocus(0, 0, size, _depth, 0);
                  assert(resultVector.assertVectorInvariant());
                  resultVector
                }
            };
            private def clearCurrent(): Unit = {
              display0 = new Array[AnyRef](64);
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

          class RRBVector_quick_directLevel_64_splitbalancedIterator[+A](startIndex: Int, override private[immutable] val endIndex: Int) extends AbstractIterator[A] with Iterator[A] with RRBVector_quick_directLevel_64_splitbalancedPointer[A @uncheckedVariance] {
            private var blockIndex: Int = _;
            private var lo: Int = _;
            private var endLo: Int = _;
            private var _hasNext: Boolean = _;
            final private[collection] def initIteratorFrom[B >: A](that: RRBVector_quick_directLevel_64_splitbalancedPointer[B]): Unit = {
              if (_hasNext)
                {
                  focusOn(startIndex);
                  blockIndex = focusStart.+(focus.&(-64));
                  lo = focus.&(63);
                  endLo = math.min(focusEnd.-(blockIndex), 64)
                }
              else
                {
                  blockIndex = 0;
                  lo = 0;
                  endLo = 1;
                  display0 = new Array[AnyRef](1)
                };
              initWithFocusFrom(that);
              _hasNext = startIndex.<(endIndex);
              if (_hasNext)
                {
                  focusOn(startIndex);
                  blockIndex = focusStart.+(focus.&(-64));
                  lo = focus.&(63);
                  endLo = math.min(focusEnd.-(blockIndex), 64)
                }
              else
                {
                  blockIndex = 0;
                  lo = 0;
                  endLo = 1;
                  display0 = new Array[AnyRef](1)
                }
            };
            final def hasNext = _hasNext;
            def next(): A = {
              val _lo = lo;
              val res: A = display0(_lo).asInstanceOf[A];
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
                      focusOn(newBlockIndex)
                    else
                      {
                        lo = focusEnd.-(1).&(63);
                        blockIndex = endIndex;
                        if (_hasNext)
                          _hasNext = false
                        else
                          throw new NoSuchElementException("reached iterator end")
                      };
                  endLo = math.min(focusEnd.-(newBlockIndex), 64);
                  res
                }
            };
            private[collection] def remaining: Int = math.max(endIndex.-(blockIndex.+(lo)), 0)
          }

          class RRBVector_quick_directLevel_64_splitbalancedReverseIterator[+A](startIndex: Int, final override private[immutable] val endIndex: Int) extends AbstractIterator[A] with Iterator[A] with RRBVector_quick_directLevel_64_splitbalancedPointer[A @uncheckedVariance] {
            private var lastIndexOfBlock: Int = _;
            private var lo: Int = _;
            private var endLo: Int = _;
            private var _hasNext: Boolean = startIndex.<(endIndex);
            final private[collection] def initIteratorFrom[B >: A](that: RRBVector_quick_directLevel_64_splitbalancedPointer[B]): Unit = {
              assert((0).<=(startIndex));
              assert(startIndex.<=(endIndex));
              assert(endIndex.<=(that.endIndex));
              initWithFocusFrom(that);
              _hasNext = startIndex.<(endIndex);
              if (_hasNext)
                {
                  val idx = endIndex.-(1);
                  focusOn(idx);
                  lastIndexOfBlock = idx;
                  lo = idx.-(focusStart).&(63);
                  endLo = math.max(startIndex.-(focusStart).-(lastIndexOfBlock), 0)
                }
              else
                {
                  lastIndexOfBlock = 0;
                  lo = 0;
                  endLo = 0;
                  display0 = new Array[AnyRef](1)
                }
            };
            final def hasNext = _hasNext;
            def next(): A = if (_hasNext)
              {
                val res = display0(lo).asInstanceOf[A];
                lo.-=(1);
                if (lo.>=(endLo))
                  res
                else
                  {
                    val newBlockIndex = lastIndexOfBlock.-(64);
                    if (focusStart.<=(newBlockIndex))
                      {
                        val _focusStart = focusStart;
                        val newBlockIndexInFocus = newBlockIndex.-(_focusStart);
                        gotoPrevBlockStart(newBlockIndexInFocus, newBlockIndexInFocus.^(lastIndexOfBlock.-(_focusStart)));
                        lastIndexOfBlock = newBlockIndex;
                        lo = 63;
                        endLo = math.max(startIndex.-(focusStart).-(focus), 0);
                        res
                      }
                    else
                      if (startIndex.<(focusStart))
                        {
                          val newIndex = focusStart.-(1);
                          focusOn(newIndex);
                          lastIndexOfBlock = newIndex;
                          lo = newIndex.-(focusStart).&(63);
                          endLo = math.max(startIndex.-(focusStart).-(lastIndexOfBlock), 0);
                          res
                        }
                      else
                        {
                          _hasNext = false;
                          res
                        }
                  }
              }
            else
              throw new NoSuchElementException("reached iterator end")
          }

          private[immutable] trait RRBVector_quick_directLevel_64_splitbalancedPointer[A] {
            final private[immutable] var display0: Array[AnyRef] = _;
            final private[immutable] var display1: Array[AnyRef] = _;
            final private[immutable] var display2: Array[AnyRef] = _;
            final private[immutable] var display3: Array[AnyRef] = _;
            final private[immutable] var display4: Array[AnyRef] = _;
            final private[immutable] var display5: Array[AnyRef] = _;
            final private[immutable] var depth: Int = _;
            final private[immutable] var focusStart: Int = 0;
            final private[immutable] var focusEnd: Int = 0;
            final private[immutable] var focusDepth: Int = 0;
            final private[immutable] var focus: Int = 0;
            final private[immutable] var focusRelax: Int = 0;
            private[immutable] def endIndex: Int;
            final private[immutable] def initWithFocusFrom[U](that: RRBVector_quick_directLevel_64_splitbalancedPointer[U]): Unit = {
              initFocus(that.focus, that.focusStart, that.focusEnd, that.focusDepth, that.focusRelax);
              initFrom(that)
            };
            final private[immutable] def initFocus[U](focus: Int, focusStart: Int, focusEnd: Int, focusDepth: Int, focusRelax: Int): Unit = {
              this.focus = focus;
              this.focusStart = focusStart;
              this.focusEnd = focusEnd;
              this.focusDepth = focusDepth;
              this.focusRelax = focusRelax
            };
            final private[immutable] def initFromRoot(root: Array[AnyRef], depth: Int): Unit = {
              assert(root.!=(null));
              assert((0).<(depth));
              assert(depth.<=(6));
              depth match {
                case 1 => display0 = root
                case 2 => display1 = root
                case 3 => display2 = root
                case 4 => display3 = root
                case 5 => display4 = root
                case 6 => display5 = root
              };
              this.depth = depth;
              focusEnd = focusStart;
              focusOn(0)
            };
            final private[immutable] def initFrom[U](that: RRBVector_quick_directLevel_64_splitbalancedPointer[U]): Unit = {
              assert(that.!=(null));
              depth = that.depth;
              that.depth match {
                case 0 => ()
                case 1 => this.display0 = that.display0
                case 2 => {
                  this.display0 = that.display0;
                  this.display1 = that.display1
                }
                case 3 => {
                  this.display0 = that.display0;
                  this.display1 = that.display1;
                  this.display2 = that.display2
                }
                case 4 => {
                  this.display0 = that.display0;
                  this.display1 = that.display1;
                  this.display2 = that.display2;
                  this.display3 = that.display3
                }
                case 5 => {
                  this.display0 = that.display0;
                  this.display1 = that.display1;
                  this.display2 = that.display2;
                  this.display3 = that.display3;
                  this.display4 = that.display4
                }
                case 6 => {
                  this.display0 = that.display0;
                  this.display1 = that.display1;
                  this.display2 = that.display2;
                  this.display3 = that.display3;
                  this.display4 = that.display4;
                  this.display5 = that.display5
                }
                case _ => throw new IllegalStateException()
              }
            };
            final private[immutable] def initSingleton[B >: A](elem: B): Unit = {
              initFocus(0, 0, 1, 1, 0);
              val d0 = new Array[AnyRef](1);
              d0.update(0, elem.asInstanceOf[AnyRef]);
              display0 = d0;
              depth = 1
            };
            final private[immutable] def root(): AnyRef = depth match {
              case 0 => null
              case 1 => display0
              case 2 => display1
              case 3 => display2
              case 4 => display3
              case 5 => display4
              case 6 => display5
              case _ => throw new IllegalStateException()
            };
            final private[immutable] def focusOn(index: Int): Unit = if (focusStart.<=(index).&&(index.<(focusEnd)))
              {
                val indexInFocus = index.-(focusStart);
                val xor = indexInFocus.^(focus);
                if (xor.>=(64))
                  gotoPos(indexInFocus, xor)
                else
                  ();
                focus = index
              }
            else
              gotoPosFromRoot(index);
            final private[immutable] def getElementFromRoot(index: Int): A = {
              assert((2).<=(depth));
              assert(depth.<=(6));
              var indexInSubTree = index;
              var currentDepth = depth;
              var display: Array[AnyRef] = currentDepth match {
                case 2 => display1
                case 3 => display2
                case 4 => display3
                case 5 => display4
                case 6 => display5
              };
              while (currentDepth.>(1)) 
                {
                  val sizes = display(display.length.-(1)).asInstanceOf[Array[Int]];
                  if (sizes.==(null))
                    {
                      val depthShift = (6).*(currentDepth.-(1));
                      val idx = indexInSubTree.>>(depthShift);
                      indexInSubTree.-=(idx.<<(depthShift));
                      display = display(idx).asInstanceOf[Array[AnyRef]]
                    }
                  else
                    {
                      val sizesIdx = getIndexInSizes(sizes, indexInSubTree);
                      if (sizesIdx.!=(0))
                        indexInSubTree.-=(sizes(sizesIdx.-(1)))
                      else
                        ();
                      display = display(sizesIdx).asInstanceOf[Array[AnyRef]]
                    };
                  currentDepth.-=(1)
                }
              ;
              display(indexInSubTree).asInstanceOf[A]
            };
            final private def getIndexInSizes(sizes: Array[Int], indexInSubTree: Int): Int = {
              assert((0).<=(indexInSubTree));
              assert(indexInSubTree.<(sizes(sizes.length.-(1))));
              var is = 0;
              while (sizes(is).<=(indexInSubTree)) 
                is.+=(1)
              ;
              is
            };
            final private[immutable] def gotoPosFromRoot(index: Int): Unit = {
              var _startIndex: Int = 0;
              var _endIndex: Int = endIndex;
              var currentDepth: Int = depth;
              var _focusRelax: Int = 0;
              var continue: Boolean = currentDepth.>(1);
              while (continue) 
                {
                  assert((0).<=(_startIndex));
                  assert(_startIndex.<=(_endIndex));
                  assert((0).<(currentDepth));
                  if (currentDepth.<=(1))
                    continue = false
                  else
                    {
                      val display = currentDepth match {
                        case 2 => display1
                        case 3 => display2
                        case 4 => display3
                        case 5 => display4
                        case 6 => display5
                        case _ => throw new IllegalStateException()
                      };
                      val sizes = display(display.length.-(1)).asInstanceOf[Array[Int]];
                      if (sizes.==(null))
                        continue = false
                      else
                        {
                          val is = getIndexInSizes(sizes, index.-(_startIndex));
                          currentDepth match {
                            case 2 => display0 = display(is).asInstanceOf[Array[AnyRef]]
                            case 3 => display1 = display(is).asInstanceOf[Array[AnyRef]]
                            case 4 => display2 = display(is).asInstanceOf[Array[AnyRef]]
                            case 5 => display3 = display(is).asInstanceOf[Array[AnyRef]]
                            case 6 => display4 = display(is).asInstanceOf[Array[AnyRef]]
                          };
                          if (is.<(sizes.length.-(1)))
                            _endIndex = _startIndex.+(sizes(is))
                          else
                            ();
                          if (is.!=(0))
                            _startIndex.+=(sizes(is.-(1)))
                          else
                            ();
                          currentDepth.-=(1);
                          _focusRelax.|=(is.<<((6).*(currentDepth)))
                        }
                    }
                }
              ;
              val indexInFocus = index.-(_startIndex);
              gotoPos(indexInFocus, (1).<<((6).*(currentDepth.-(1))));
              initFocus(indexInFocus, _startIndex, _endIndex, currentDepth, _focusRelax)
            };
            final private[immutable] def setupNewBlockInNextBranch(index: Int, xor: Int): Unit = (31).-(java.lang.Integer.numberOfLeadingZeros(xor))./(6) match {
              case 1 => {
                if (depth.==(1))
                  {
                    val newRoot = new Array[AnyRef](3);
                    newRoot.update(0, display0);
                    ;
                    display1 = newRoot;
                    depth = 2
                  }
                else
                  {
                    val len = display1.length;
                    val newRoot = copyOf(display1, len, len.+(1));
                    val sizes = display1(len.-(1));
                    if (sizes.!=(null))
                      {
                        val newSizes = new Array[Int](len);
                        Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len.-(1));
                        newSizes.update(len.-(1), newSizes(len.-(2)).+(1));
                        newRoot.update(len, newSizes)
                      }
                    else
                      ();
                    display1 = newRoot
                  };
                display0 = new Array(1);
                display1.update(index.>>(6).&(63), display0)
              }
              case 2 => {
                if (depth.==(2))
                  {
                    val newRoot = new Array[AnyRef](3);
                    newRoot.update(0, display1);
                    {
                      val dLen = display1.length;
                      val dSizes = display1(dLen.-(1));
                      if (dSizes.!=(null))
                        {
                          val newRootSizes = new Array[Int](2);
                          val dSize = dSizes.asInstanceOf[Array[Int]](dLen.-(2));
                          newRootSizes.update(0, dSize);
                          newRootSizes.update(1, dSize.+(1));
                          newRoot.update(2, newRootSizes)
                        }
                      else
                        ()
                    };
                    display2 = newRoot;
                    depth = 3
                  }
                else
                  {
                    val len = display2.length;
                    val newRoot = copyOf(display2, len, len.+(1));
                    val sizes = display2(len.-(1));
                    if (sizes.!=(null))
                      {
                        val newSizes = new Array[Int](len);
                        Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len.-(1));
                        newSizes.update(len.-(1), newSizes(len.-(2)).+(1));
                        newRoot.update(len, newSizes)
                      }
                    else
                      ();
                    display2 = newRoot
                  };
                display0 = new Array(1);
                display1 = new Array(2);
                display1.update(index.>>(6).&(63), display0);
                display2.update(index.>>(12).&(63), display1)
              }
              case 3 => {
                if (depth.==(3))
                  {
                    val newRoot = new Array[AnyRef](3);
                    newRoot.update(0, display2);
                    {
                      val dLen = display2.length;
                      val dSizes = display2(dLen.-(1));
                      if (dSizes.!=(null))
                        {
                          val newRootSizes = new Array[Int](2);
                          val dSize = dSizes.asInstanceOf[Array[Int]](dLen.-(2));
                          newRootSizes.update(0, dSize);
                          newRootSizes.update(1, dSize.+(1));
                          newRoot.update(2, newRootSizes)
                        }
                      else
                        ()
                    };
                    display3 = newRoot;
                    depth = 4
                  }
                else
                  {
                    val len = display3.length;
                    val newRoot = copyOf(display3, len, len.+(1));
                    val sizes = display3(len.-(1));
                    if (sizes.!=(null))
                      {
                        val newSizes = new Array[Int](len);
                        Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len.-(1));
                        newSizes.update(len.-(1), newSizes(len.-(2)).+(1));
                        newRoot.update(len, newSizes)
                      }
                    else
                      ();
                    display3 = newRoot
                  };
                display0 = new Array(1);
                display1 = new Array(2);
                display2 = new Array(2);
                display1.update(index.>>(6).&(63), display0);
                display2.update(index.>>(12).&(63), display1);
                display3.update(index.>>(18).&(63), display2)
              }
              case 4 => {
                if (depth.==(4))
                  {
                    val newRoot = new Array[AnyRef](3);
                    newRoot.update(0, display3);
                    {
                      val dLen = display3.length;
                      val dSizes = display3(dLen.-(1));
                      if (dSizes.!=(null))
                        {
                          val newRootSizes = new Array[Int](2);
                          val dSize = dSizes.asInstanceOf[Array[Int]](dLen.-(2));
                          newRootSizes.update(0, dSize);
                          newRootSizes.update(1, dSize.+(1));
                          newRoot.update(2, newRootSizes)
                        }
                      else
                        ()
                    };
                    display4 = newRoot;
                    depth = 5
                  }
                else
                  {
                    val len = display4.length;
                    val newRoot = copyOf(display4, len, len.+(1));
                    val sizes = display4(len.-(1));
                    if (sizes.!=(null))
                      {
                        val newSizes = new Array[Int](len);
                        Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len.-(1));
                        newSizes.update(len.-(1), newSizes(len.-(2)).+(1));
                        newRoot.update(len, newSizes)
                      }
                    else
                      ();
                    display4 = newRoot
                  };
                display0 = new Array(1);
                display1 = new Array(2);
                display2 = new Array(2);
                display3 = new Array(2);
                display1.update(index.>>(6).&(63), display0);
                display2.update(index.>>(12).&(63), display1);
                display3.update(index.>>(18).&(63), display2);
                display4.update(index.>>(24).&(63), display3)
              }
              case 5 => {
                if (depth.==(5))
                  {
                    val newRoot = new Array[AnyRef](3);
                    newRoot.update(0, display4);
                    {
                      val dLen = display4.length;
                      val dSizes = display4(dLen.-(1));
                      if (dSizes.!=(null))
                        {
                          val newRootSizes = new Array[Int](2);
                          val dSize = dSizes.asInstanceOf[Array[Int]](dLen.-(2));
                          newRootSizes.update(0, dSize);
                          newRootSizes.update(1, dSize.+(1));
                          newRoot.update(2, newRootSizes)
                        }
                      else
                        ()
                    };
                    display5 = newRoot;
                    depth = 6
                  }
                else
                  {
                    val len = display5.length;
                    val newRoot = copyOf(display5, len, len.+(1));
                    val sizes = display5(len.-(1));
                    if (sizes.!=(null))
                      {
                        val newSizes = new Array[Int](len);
                        Platform.arraycopy(sizes.asInstanceOf[Array[Int]], 0, newSizes, 0, len.-(1));
                        newSizes.update(len.-(1), newSizes(len.-(2)).+(1));
                        newRoot.update(len, newSizes)
                      }
                    else
                      ();
                    display5 = newRoot
                  };
                display0 = new Array(1);
                display1 = new Array(2);
                display2 = new Array(2);
                display3 = new Array(2);
                display4 = new Array(2);
                display1.update(index.>>(6).&(63), display0);
                display2.update(index.>>(12).&(63), display1);
                display3.update(index.>>(18).&(63), display2);
                display4.update(index.>>(24).&(63), display3);
                display5.update(index.>>(30).&(63), display4)
              }
              case _ => throw new IllegalArgumentException()
            };
            final private[immutable] def getElement(index: Int, xor: Int): A = (31).-(java.lang.Integer.numberOfLeadingZeros(xor))./(6) match {
              case 0 => display0(index.&(63)).asInstanceOf[A]
              case 1 => display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]](index.&(63)).asInstanceOf[A]
              case 2 => display2(index.>>(12).&(63)).asInstanceOf[Array[AnyRef]](index.>>(6).&(63)).asInstanceOf[Array[AnyRef]](index.&(63)).asInstanceOf[A]
              case 3 => display3(index.>>(18).&(63)).asInstanceOf[Array[AnyRef]](index.>>(12).&(63)).asInstanceOf[Array[AnyRef]](index.>>(6).&(63)).asInstanceOf[Array[AnyRef]](index.&(63)).asInstanceOf[A]
              case 4 => display4(index.>>(24).&(63)).asInstanceOf[Array[AnyRef]](index.>>(18).&(63)).asInstanceOf[Array[AnyRef]](index.>>(12).&(63)).asInstanceOf[Array[AnyRef]](index.>>(6).&(63)).asInstanceOf[Array[AnyRef]](index.&(63)).asInstanceOf[A]
              case 5 => display5(index.>>(30).&(63)).asInstanceOf[Array[AnyRef]](index.>>(24).&(63)).asInstanceOf[Array[AnyRef]](index.>>(18).&(63)).asInstanceOf[Array[AnyRef]](index.>>(12).&(63)).asInstanceOf[Array[AnyRef]](index.>>(6).&(63)).asInstanceOf[Array[AnyRef]](index.&(63)).asInstanceOf[A]
              case _ => throw new IllegalArgumentException()
            };
            final private[immutable] def gotoPos(index: Int, xor: Int): Unit = (31).-(java.lang.Integer.numberOfLeadingZeros(xor))./(6) match {
              case 0 => ()
              case 1 => display0 = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]]
              case 2 => {
                display1 = display2(index.>>(12).&(63)).asInstanceOf[Array[AnyRef]];
                display0 = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]]
              }
              case 3 => {
                display2 = display3(index.>>(18).&(63)).asInstanceOf[Array[AnyRef]];
                display1 = display2(index.>>(12).&(63)).asInstanceOf[Array[AnyRef]];
                display0 = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]]
              }
              case 4 => {
                display3 = display4(index.>>(24).&(63)).asInstanceOf[Array[AnyRef]];
                display2 = display3(index.>>(18).&(63)).asInstanceOf[Array[AnyRef]];
                display1 = display2(index.>>(12).&(63)).asInstanceOf[Array[AnyRef]];
                display0 = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]]
              }
              case 5 => {
                display4 = display5(index.>>(30).&(63)).asInstanceOf[Array[AnyRef]];
                display3 = display4(index.>>(24).&(63)).asInstanceOf[Array[AnyRef]];
                display2 = display3(index.>>(18).&(63)).asInstanceOf[Array[AnyRef]];
                display1 = display2(index.>>(12).&(63)).asInstanceOf[Array[AnyRef]];
                display0 = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]]
              }
              case _ => throw new IllegalArgumentException()
            };
            final private[immutable] def gotoNextBlockStart(index: Int, xor: Int): Unit = (31).-(java.lang.Integer.numberOfLeadingZeros(xor))./(6) match {
              case 1 => display0 = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]]
              case 2 => {
                display1 = display2(index.>>(12).&(63)).asInstanceOf[Array[AnyRef]];
                display0 = display1(0).asInstanceOf[Array[AnyRef]]
              }
              case 3 => {
                display2 = display3(index.>>(18).&(63)).asInstanceOf[Array[AnyRef]];
                display1 = display2(0).asInstanceOf[Array[AnyRef]];
                display0 = display1(0).asInstanceOf[Array[AnyRef]]
              }
              case 4 => {
                display3 = display4(index.>>(24).&(63)).asInstanceOf[Array[AnyRef]];
                display2 = display3(0).asInstanceOf[Array[AnyRef]];
                display1 = display2(0).asInstanceOf[Array[AnyRef]];
                display0 = display1(0).asInstanceOf[Array[AnyRef]]
              }
              case 5 => {
                display4 = display5(index.>>(30).&(63)).asInstanceOf[Array[AnyRef]];
                display3 = display4(0).asInstanceOf[Array[AnyRef]];
                display2 = display3(0).asInstanceOf[Array[AnyRef]];
                display1 = display2(0).asInstanceOf[Array[AnyRef]];
                display0 = display1(0).asInstanceOf[Array[AnyRef]]
              }
              case _ => throw new IllegalArgumentException()
            };
            final private[immutable] def gotoPrevBlockStart(index: Int, xor: Int): Unit = (31).-(java.lang.Integer.numberOfLeadingZeros(xor))./(6) match {
              case 1 => display0 = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]]
              case 2 => {
                display1 = display2(index.>>(12).&(63)).asInstanceOf[Array[AnyRef]];
                display0 = display1(63).asInstanceOf[Array[AnyRef]]
              }
              case 3 => {
                display2 = display3(index.>>(18).&(63)).asInstanceOf[Array[AnyRef]];
                display1 = display2(63).asInstanceOf[Array[AnyRef]];
                display0 = display1(63).asInstanceOf[Array[AnyRef]]
              }
              case 4 => {
                display3 = display4(index.>>(24).&(63)).asInstanceOf[Array[AnyRef]];
                display2 = display3(63).asInstanceOf[Array[AnyRef]];
                display1 = display2(63).asInstanceOf[Array[AnyRef]];
                display0 = display1(63).asInstanceOf[Array[AnyRef]]
              }
              case 5 => {
                display4 = display5(index.>>(30).&(63)).asInstanceOf[Array[AnyRef]];
                display3 = display4(63).asInstanceOf[Array[AnyRef]];
                display2 = display3(63).asInstanceOf[Array[AnyRef]];
                display1 = display2(63).asInstanceOf[Array[AnyRef]];
                display0 = display1(63).asInstanceOf[Array[AnyRef]]
              }
              case _ => throw new IllegalArgumentException()
            };
            final private[immutable] def gotoNextBlockStartWritable(index: Int, xor: Int): Unit = (31).-(java.lang.Integer.numberOfLeadingZeros(xor))./(6) match {
              case 1 => {
                if (depth.==(1))
                  {
                    display1 = new Array(65);
                    display1.update(0, display0);
                    depth.+=(1)
                  }
                else
                  ();
                display0 = new Array(64);
                display1.update(index.>>(6).&(63), display0)
              }
              case 2 => {
                if (depth.==(2))
                  {
                    display2 = new Array(65);
                    display2.update(0, display1);
                    depth.+=(1)
                  }
                else
                  ();
                display0 = new Array(64);
                display1 = new Array(65);
                display1.update(index.>>(6).&(63), display0);
                display2.update(index.>>(12).&(63), display1)
              }
              case 3 => {
                if (depth.==(3))
                  {
                    display3 = new Array(65);
                    display3.update(0, display2);
                    depth.+=(1)
                  }
                else
                  ();
                display0 = new Array(64);
                display1 = new Array(65);
                display2 = new Array(65);
                display1.update(index.>>(6).&(63), display0);
                display2.update(index.>>(12).&(63), display1);
                display3.update(index.>>(18).&(63), display2)
              }
              case 4 => {
                if (depth.==(4))
                  {
                    display4 = new Array(65);
                    display4.update(0, display3);
                    depth.+=(1)
                  }
                else
                  ();
                display0 = new Array(64);
                display1 = new Array(65);
                display2 = new Array(65);
                display3 = new Array(65);
                display1.update(index.>>(6).&(63), display0);
                display2.update(index.>>(12).&(63), display1);
                display3.update(index.>>(18).&(63), display2);
                display4.update(index.>>(24).&(63), display3)
              }
              case 5 => {
                if (depth.==(5))
                  {
                    display5 = new Array(65);
                    display5.update(0, display4);
                    depth.+=(1)
                  }
                else
                  ();
                display0 = new Array(64);
                display1 = new Array(65);
                display2 = new Array(65);
                display3 = new Array(65);
                display4 = new Array(65);
                display1.update(index.>>(6).&(63), display0);
                display2.update(index.>>(12).&(63), display1);
                display3.update(index.>>(18).&(63), display2);
                display4.update(index.>>(24).&(63), display3);
                display5.update(index.>>(30).&(63), display4)
              }
              case _ => throw new IllegalArgumentException()
            };
            final private[immutable] def stabilize(): Unit = {
              val _depth = depth;
              if (_depth.>(1))
                {
                  val stabilizationIndex = focus.|(focusRelax);
                  val deltaSize = display0.length.-(display1(stabilizationIndex.>>(6).&(63)).asInstanceOf[Array[AnyRef]].length);
                  val _focusDepth = focusDepth;
                  copyDisplays(_focusDepth, stabilizationIndex);
                  stabilizeDisplayPath(_focusDepth, stabilizationIndex);
                  var currentDepth = _focusDepth.+(1);
                  while (currentDepth.<=(_depth)) 
                    {
                      val display = currentDepth match {
                        case 2 => display1
                        case 3 => display2
                        case 4 => display3
                        case 5 => display4
                        case 6 => display5
                      };
                      val oldSizes = display(display.length.-(1)).asInstanceOf[Array[Int]];
                      val newSizes = new Array[Int](oldSizes.length);
                      val lastSizesIndex = oldSizes.length.-(1);
                      Platform.arraycopy(oldSizes, 0, newSizes, 0, lastSizesIndex);
                      newSizes.update(lastSizesIndex, oldSizes(lastSizesIndex.+(deltaSize)));
                      val idx = stabilizationIndex.>>((6).*(currentDepth)).&(63);
                      val newDisplay = copyOf(display, idx, idx.+(2));
                      newDisplay.update(display1.length.-(1), newSizes);
                      currentDepth match {
                        case 2 => {
                          newDisplay.update(idx, display0);
                          display1.update(idx, newDisplay)
                        }
                        case 3 => {
                          newDisplay.update(idx, display1);
                          display2.update(idx, newDisplay)
                        }
                        case 4 => {
                          newDisplay.update(idx, display2);
                          display3.update(idx, newDisplay)
                        }
                        case 5 => {
                          newDisplay.update(idx, display3);
                          display4.update(idx, newDisplay)
                        }
                        case 6 => {
                          newDisplay.update(idx, display4);
                          display5.update(idx, newDisplay)
                        }
                      };
                      currentDepth.+=(1)
                    }
                  
                }
              else
                ()
            };
            final private[immutable] def copyDisplays(_depth: Int, _focus: Int): Unit = _depth match {
              case 1 => ()
              case 2 => {
                var idx = _focus.>>(6).&(63);
                display1 = copyOf(display1, idx.+(1), idx.+(2))
              }
              case 3 => {
                var idx = _focus.>>(6).&(63);
                display1 = copyOf(display1, idx.+(1), idx.+(2));
                idx = _focus.>>(12).&(63);
                display2 = copyOf(display2, idx.+(1), idx.+(2))
              }
              case 4 => {
                var idx = _focus.>>(6).&(63);
                display1 = copyOf(display1, idx.+(1), idx.+(2));
                idx = _focus.>>(12).&(63);
                display2 = copyOf(display2, idx.+(1), idx.+(2));
                idx = _focus.>>(18).&(63);
                display3 = copyOf(display3, idx.+(1), idx.+(2))
              }
              case 5 => {
                var idx = _focus.>>(6).&(63);
                display1 = copyOf(display1, idx.+(1), idx.+(2));
                idx = _focus.>>(12).&(63);
                display2 = copyOf(display2, idx.+(1), idx.+(2));
                idx = _focus.>>(18).&(63);
                display3 = copyOf(display3, idx.+(1), idx.+(2));
                idx = _focus.>>(24).&(63);
                display4 = copyOf(display4, idx.+(1), idx.+(2))
              }
              case 6 => {
                var idx = _focus.>>(6).&(63);
                display1 = copyOf(display1, idx.+(1), idx.+(2));
                idx = _focus.>>(12).&(63);
                display2 = copyOf(display2, idx.+(1), idx.+(2));
                idx = _focus.>>(18).&(63);
                display3 = copyOf(display3, idx.+(1), idx.+(2));
                idx = _focus.>>(24).&(63);
                display4 = copyOf(display4, idx.+(1), idx.+(2));
                idx = _focus.>>(30).&(63);
                display5 = copyOf(display5, idx.+(1), idx.+(2))
              }
            };
            final private[immutable] def copyDisplaysTop(currentDepth: Int, _focusRelax: Int): Unit = {
              var _currentDepth = currentDepth;
              while (_currentDepth.<(this.depth)) 
                _currentDepth match {
                  case 2 => {
                    val cutIndex = _focusRelax.>>(6).&(63);
                    display1 = copyOf(display1, cutIndex.+(1), cutIndex.+(2))
                  }
                  case 3 => {
                    val cutIndex = _focusRelax.>>(12).&(63);
                    display2 = copyOf(display2, cutIndex.+(1), cutIndex.+(2))
                  }
                  case 4 => {
                    val cutIndex = _focusRelax.>>(18).&(63);
                    display3 = copyOf(display3, cutIndex.+(1), cutIndex.+(2))
                  }
                  case 5 => {
                    val cutIndex = _focusRelax.>>(24).&(63);
                    display4 = copyOf(display4, cutIndex.+(1), cutIndex.+(2))
                  }
                  case 6 => {
                    val cutIndex = _focusRelax.>>(30).&(63);
                    display5 = copyOf(display5, cutIndex.+(1), cutIndex.+(2))
                  }
                  case _ => throw new IllegalStateException()
                }
              
            };
            final private[immutable] def stabilizeDisplayPath(_depth: Int, _focus: Int): Unit = {
              assert((0).<(_depth));
              assert(_depth.<=(this.depth));
              _depth match {
                case 1 => ()
                case 2 => display1.update(_focus.>>(6).&(63), display0)
                case 3 => {
                  display2.update(_focus.>>(12).&(63), display1);
                  display1.update(_focus.>>(6).&(63), display0)
                }
                case 4 => {
                  display3.update(_focus.>>(18).&(63), display2);
                  display2.update(_focus.>>(12).&(63), display1);
                  display1.update(_focus.>>(6).&(63), display0)
                }
                case 5 => {
                  display4.update(_focus.>>(24).&(63), display3);
                  display3.update(_focus.>>(18).&(63), display2);
                  display2.update(_focus.>>(12).&(63), display1);
                  display1.update(_focus.>>(6).&(63), display0)
                }
                case 6 => {
                  display5.update(_focus.>>(30).&(63), display4);
                  display4.update(_focus.>>(24).&(63), display3);
                  display3.update(_focus.>>(18).&(63), display2);
                  display2.update(_focus.>>(12).&(63), display1);
                  display1.update(_focus.>>(6).&(63), display0)
                }
              }
            };
            private[immutable] def cleanTop(cutIndex: Int): Unit = this.depth match {
              case 2 => if (cutIndex.>>(6).==(0))
                {
                  display1 = null;
                  this.depth = 1
                }
              else
                this.depth = 2
              case 3 => if (cutIndex.>>(12).==(0))
                {
                  display2 = null;
                  if (cutIndex.>>(6).==(0))
                    {
                      display1 = null;
                      this.depth = 1
                    }
                  else
                    this.depth = 2
                }
              else
                this.depth = 3
              case 4 => if (cutIndex.>>(18).==(0))
                {
                  display3 = null;
                  if (cutIndex.>>(12).==(0))
                    {
                      display2 = null;
                      if (cutIndex.>>(6).==(0))
                        {
                          display1 = null;
                          this.depth = 1
                        }
                      else
                        this.depth = 2
                    }
                  else
                    this.depth = 3
                }
              else
                this.depth = 4
              case 5 => if (cutIndex.>>(24).==(0))
                {
                  display4 = null;
                  if (cutIndex.>>(18).==(0))
                    {
                      display3 = null;
                      if (cutIndex.>>(12).==(0))
                        {
                          display2 = null;
                          if (cutIndex.>>(6).==(0))
                            {
                              display1 = null;
                              this.depth = 1
                            }
                          else
                            this.depth = 2
                        }
                      else
                        this.depth = 3
                    }
                  else
                    this.depth = 4
                }
              else
                this.depth = 5
              case 6 => if (cutIndex.>>(30).==(0))
                {
                  display5 = null;
                  if (cutIndex.>>(24).==(0))
                    {
                      display4 = null;
                      if (cutIndex.>>(18).==(0))
                        {
                          display3 = null;
                          if (cutIndex.>>(12).==(0))
                            {
                              display2 = null;
                              if (cutIndex.>>(6).==(0))
                                {
                                  display1 = null;
                                  this.depth = 1
                                }
                              else
                                this.depth = 2
                            }
                          else
                            this.depth = 3
                        }
                      else
                        this.depth = 4
                    }
                  else
                    this.depth = 5
                }
              else
                this.depth = 6
            };
            final private[immutable] def copyOf(array: Array[AnyRef], numElements: Int, newSize: Int) = {
              assert(array.!=(null));
              assert(numElements.<=(newSize));
              assert(numElements.<=(array.length));
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

package scala {
  package collection {
    package parallel {
      package immutable {
        package generated {
          package rrbvector {
            import scala.collection.immutable.generated.rrbvector._

            import scala.collection.generic.{GenericParTemplate, CanCombineFrom, ParFactory}

            import scala.collection.parallel.{ParSeqLike, Combiner, SeqSplitter}

            import scala.collection.mutable.ArrayBuffer

            class ParRRBVector_quick_directLevel_64_splitbalanced[+A](vector: RRBVector_quick_directLevel_64_splitbalanced[A]) extends ParSeq[A] with GenericParTemplate[A, ParRRBVector_quick_directLevel_64_splitbalanced] with ParSeqLike[A, ParRRBVector_quick_directLevel_64_splitbalanced[A], RRBVector_quick_directLevel_64_splitbalanced[A]] with Serializable {
              override def companion = ParRRBVector_quick_directLevel_64_splitbalanced;
              def this() = {
                this(RRBVector_quick_directLevel_64_splitbalanced.empty[A]);
                ()
              };
              def apply(idx: Int) = vector.apply(idx);
              def length = vector.length;
              def splitter: SeqSplitter[A] = {
                val pit = new ParRRBVector_quick_directLevel_64_splitbalancedIterator(0, vector.length);
                pit.initIteratorFrom(vector);
                pit
              };
              override def seq: RRBVector_quick_directLevel_64_splitbalanced[A] = vector;
              override def toVector: Vector[A] = vector.toVector;
              class ParRRBVector_quick_directLevel_64_splitbalancedIterator(_start: Int, _end: Int) extends RRBVector_quick_directLevel_64_splitbalancedIterator[A](_start, _end) with SeqSplitter[A] {
                final override def remaining: Int = super.remaining;
                def dup: SeqSplitter[A] = {
                  val pit = new ParRRBVector_quick_directLevel_64_splitbalancedIterator(_end.-(remaining), _end);
                  pit.initIteratorFrom(this);
                  pit
                };
                def split: Seq[ParRRBVector_quick_directLevel_64_splitbalancedIterator] = {
                  val rem = remaining;
                  if (rem.>=(2))
                    {
                      val splitSize = (1).<<((5).*((31).-(java.lang.Integer.numberOfLeadingZeros(rem))./(6).-(1)));
                      val splitted = new ArrayBuffer[ParRRBVector_quick_directLevel_64_splitbalancedIterator]();
                      var currentPos = _end.-(remaining);
                      while (currentPos.<(rem)) 
                        {
                          val pit = new ParRRBVector_quick_directLevel_64_splitbalancedIterator(currentPos, math.min(currentPos.+(splitSize), _end));
                          pit.initIteratorFrom(this);
                          splitted.+=(pit);
                          currentPos.+=(splitSize)
                        }
                      ;
                      splitted
                    }
                  else
                    Seq(this)
                };
                def psplit(sizes: Int*): Seq[ParRRBVector_quick_directLevel_64_splitbalancedIterator] = {
                  val splitted = new ArrayBuffer[ParRRBVector_quick_directLevel_64_splitbalancedIterator]();
                  var currentPos = _end.-(remaining);
                  sizes.foreach(((sz) => {
                    val pit = new ParRRBVector_quick_directLevel_64_splitbalancedIterator(currentPos, currentPos.+(sz));
                    pit.initIteratorFrom(this);
                    splitted.+=(pit);
                    currentPos.+=(sz)
                  }));
                  splitted
                }
              }
            }

            object ParRRBVector_quick_directLevel_64_splitbalanced extends ParFactory[ParRRBVector_quick_directLevel_64_splitbalanced] {
              implicit def canBuildFrom[A]: CanCombineFrom[Coll, A, ParRRBVector_quick_directLevel_64_splitbalanced[A]] = new GenericCanCombineFrom[A]();
              def newBuilder[A]: Combiner[A, ParRRBVector_quick_directLevel_64_splitbalanced[A]] = newCombiner[A];
              def newCombiner[A]: Combiner[A, ParRRBVector_quick_directLevel_64_splitbalanced[A]] = new ParRRBVector_quick_directLevel_64_splitbalancedCombinator[A]()
            }

            private[immutable] class ParRRBVector_quick_directLevel_64_splitbalancedCombinator[A] extends Combiner[A, ParRRBVector_quick_directLevel_64_splitbalanced[A]] {
              val builder: RRBVector_quick_directLevel_64_splitbalancedBuilder[A] = new RRBVector_quick_directLevel_64_splitbalancedBuilder[A]();
              override def size = builder.endIndex;
              override def result() = new ParRRBVector_quick_directLevel_64_splitbalanced[A](builder.result());
              override def clear() = builder.clear();
              override def +=(elem: A) = {
                builder.+=(elem);
                this
              };
              override def ++=(xs: TraversableOnce[A]) = {
                builder.++=(xs);
                this
              };
              def combine[B <: A, NewTo >: ParRRBVector_quick_directLevel_64_splitbalanced[A]](other: Combiner[B, NewTo]) = if (other.eq(this))
                this
              else
                {
                  val that = other.asInstanceOf[ParRRBVector_quick_directLevel_64_splitbalancedCombinator[B]];
                  builder.++=(that.builder.result());
                  this
                }
            }
          }
        }
      }
    }
  }
}