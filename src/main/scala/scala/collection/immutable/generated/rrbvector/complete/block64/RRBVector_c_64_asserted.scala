package scala {
  package collection {
    package immutable {
      package generated {
        package rrbvector.complete.block64 {
          import scala.annotation.tailrec

          import scala.compat.Platform

          import scala.annotation.unchecked.uncheckedVariance

          import scala.collection.generic.{GenericCompanion, GenericTraversableTemplate, CanBuildFrom, IndexedSeqFactory}

          import scala.collection.parallel.immutable.generated.rrbvector.complete.block64.ParRRBVector_c_64_asserted

          object RRBVector_c_64_asserted extends scala.collection.generic.IndexedSeqFactory[RRBVector_c_64_asserted] {
            def newBuilder[A]: mutable.Builder[A, RRBVector_c_64_asserted[A]] = new RRBVectorBuilder_c_64_asserted[A]();
            implicit def canBuildFrom[A]: scala.collection.generic.CanBuildFrom[Coll, A, RRBVector_c_64_asserted[A]] = ReusableCBF.asInstanceOf[GenericCanBuildFrom[A]];
            lazy private val EMPTY_VECTOR = new RRBVector_c_64_asserted[Nothing](0);
            override def empty[A]: RRBVector_c_64_asserted[A] = EMPTY_VECTOR;
            final lazy private[immutable] val emptyTransientBlock = new Array[AnyRef](2)
          }

          final class RRBVector_c_64_asserted[+A](override private[immutable] val endIndex: Int) extends scala.collection.AbstractSeq[A] with scala.collection.immutable.IndexedSeq[A] with scala.collection.generic.GenericTraversableTemplate[A, RRBVector_c_64_asserted] with scala.collection.IndexedSeqLike[A, RRBVector_c_64_asserted[A]] with RRBVectorPointer_c_64_asserted[A @uncheckedVariance] with Serializable { self =>
            private[immutable] var transient: Boolean = false;
            override def companion: scala.collection.generic.GenericCompanion[RRBVector_c_64_asserted] = RRBVector_c_64_asserted;
            def length(): Int = endIndex;
            override def lengthCompare(len: Int): Int = endIndex.-(len);
            override def par = new ParRRBVector_c_64_asserted[A](this);
            override def iterator: RRBVectorIterator_c_64_asserted[A] = {
              if (this.transient)
                {
                  this.normalize(this.depth);
                  this.transient = false;
                  assert(this.assertVectorInvariant())
                }
              else
                ();
              val it = new RRBVectorIterator_c_64_asserted[A](0, endIndex);
              it.initIteratorFrom(this);
              it
            };
            override def reverseIterator: RRBVectorReverseIterator_c_64_asserted[A] = {
              if (this.transient)
                {
                  this.normalize(this.depth);
                  this.transient = false;
                  assert(this.assertVectorInvariant())
                }
              else
                ();
              val rit = new RRBVectorReverseIterator_c_64_asserted[A](0, endIndex);
              rit.initIteratorFrom(this);
              rit
            };
            def apply(index: Int): A = {
              val _focusStart = this.focusStart;
              if (_focusStart.<=(index).&&(index.<(focusEnd)))
                {
                  val indexInFocus = index.-(_focusStart);
                  getElem(indexInFocus, indexInFocus.^(focus)).asInstanceOf[A]
                }
              else
                if ((0).<=(index).&&(index.<(endIndex)))
                  getElementFromRoot(index).asInstanceOf[A]
                else
                  throw new IndexOutOfBoundsException(index.toString)
            };
            override def :+[B >: A, That](elem: B)(implicit bf: CanBuildFrom[RRBVector_c_64_asserted[A], B, That]): That = if (bf.eq(IndexedSeq.ReusableCBF))
              {
                val _endIndex = this.endIndex;
                if (_endIndex.!=(0))
                  {
                    val resultVector = new RRBVector_c_64_asserted[B](_endIndex.+(1));
                    resultVector.transient = this.transient;
                    resultVector.initWithFocusFrom(this);
                    resultVector.append(elem, _endIndex);
                    assert(resultVector.assertVectorInvariant());
                    resultVector.asInstanceOf[That]
                  }
                else
                  createSingletonVector(elem).asInstanceOf[That]
              }
            else
              super.:+(elem)(bf);
            override def +:[B >: A, That](elem: B)(implicit bf: CanBuildFrom[RRBVector_c_64_asserted[A], B, That]): That = if (bf.eq(IndexedSeq.ReusableCBF))
              {
                val _endIndex = this.endIndex;
                if (_endIndex.!=(0))
                  {
                    val resultVector = new RRBVector_c_64_asserted[B](_endIndex.+(1));
                    resultVector.transient = this.transient;
                    resultVector.initWithFocusFrom(this);
                    resultVector.prepend(elem);
                    assert(resultVector.assertVectorInvariant());
                    resultVector.asInstanceOf[That]
                  }
                else
                  createSingletonVector(elem).asInstanceOf[That]
              }
            else
              super.:+(elem)(bf);
            override def isEmpty: Boolean = this.endIndex.==(0);
            override def head: A = if (this.endIndex.!=(0))
              apply(0)
            else
              throw new UnsupportedOperationException("empty.head");
            override def take(n: Int): RRBVector_c_64_asserted[A] = if (n.<=(0))
              RRBVector_c_64_asserted.empty
            else
              if (n.<(endIndex))
                takeFront0(n)
              else
                this;
            override def dropRight(n: Int): RRBVector_c_64_asserted[A] = if (n.<=(0))
              this
            else
              if (n.<(endIndex))
                takeFront0(endIndex.-(n))
              else
                RRBVector_c_64_asserted.empty;
            override def slice(from: Int, until: Int): RRBVector_c_64_asserted[A] = take(until).drop(from);
            override def splitAt(n: Int): scala.Tuple2[RRBVector_c_64_asserted[A], RRBVector_c_64_asserted[A]] = scala.Tuple2(take(n), drop(n));
            override def ++[B >: A, That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[RRBVector_c_64_asserted[A], B, That]): That = if (bf.eq(IndexedSeq.ReusableCBF))
              if (that.isEmpty)
                this.asInstanceOf[That]
              else
                if (that.isInstanceOf[RRBVector_c_64_asserted[B]])
                  {
                    val thatVec = that.asInstanceOf[RRBVector_c_64_asserted[B]];
                    if (this.endIndex.==(0))
                      thatVec.asInstanceOf[That]
                    else
                      {
                        val newVec = new RRBVector_c_64_asserted(this.endIndex.+(thatVec.endIndex));
                        newVec.initWithFocusFrom(this);
                        newVec.transient = this.transient;
                        newVec.concatenate(this.endIndex, thatVec);
                        newVec.asInstanceOf[That]
                      }
                  }
                else
                  super.++(that.seq)
            else
              super.++(that.seq);
            override def tail: RRBVector_c_64_asserted[A] = if (this.endIndex.!=(0))
              this.drop(1)
            else
              throw new UnsupportedOperationException("empty.tail");
            override def last: A = if (this.endIndex.!=(0))
              this.apply(this.length.-(1))
            else
              throw new UnsupportedOperationException("empty.last");
            override def init: RRBVector_c_64_asserted[A] = if (this.endIndex.!=(0))
              dropRight(1)
            else
              throw new UnsupportedOperationException("empty.init");
            private[immutable] def append[B](elem: B, _endIndex: Int): scala.Unit = {
              if (focusStart.+(focus).^(_endIndex.-(1)).>=(32))
                normalizeAndFocusOn(_endIndex.-(1))
              else
                ();
              val elemIndexInBlock = _endIndex.-(focusStart).&(63);
              if (elemIndexInBlock.!=(0))
                appendOnCurrentBlock(elem, elemIndexInBlock)
              else
                appendBackNewBlock(elem, elemIndexInBlock)
            };
            private def appendOnCurrentBlock[B](elem: B, elemIndexInBlock: Int): scala.Unit = {
              focusEnd = endIndex;
              val d0 = new Array[AnyRef](elemIndexInBlock.+(1));
              System.arraycopy(display0, 0, d0, 0, elemIndexInBlock);
              d0.update(elemIndexInBlock, elem.asInstanceOf[AnyRef]);
              display0 = d0;
              makeTransientIfNeeded();
              assert(assertVectorInvariant())
            };
            private def appendBackNewBlock[B](elem: B, elemIndexInBlock: Int): scala.Unit = {
              val oldDepth = depth;
              val newRelaxedIndex = endIndex.-(1).-(focusStart).+(focusRelax);
              val focusJoined = focus.|(focusRelax);
              val xor = newRelaxedIndex.^(focusJoined);
              val _transient = transient;
              setupNewBlockInNextBranch(xor, _transient);
              if (oldDepth.==(depth))
                {
                  var i = if (xor.<(4096))
                    2
                  else
                    if (xor.<(262144))
                      3
                    else
                      if (xor.<(16777216))
                        4
                      else
                        if (xor.<(1073741824))
                          5
                        else
                          if (xor.<(16))
                            6
                          else
                            6;
                  if (i.<(oldDepth))
                    {
                      val _focusDepth = focusDepth;
                      var display: Array[AnyRef] = i match {
                        case 2 => display2
                        case 3 => display3
                        case 4 => display4
                        case 5 => display5
                      };
                      do 
                        {
                          val displayLen = display.length.-(1);
                          val newSizes: Array[Int] = if (i.>=(_focusDepth))
                            makeTransientSizes(display(displayLen).asInstanceOf[Array[Int]], displayLen.-(1))
                          else
                            null;
                          val newDisplay = new Array[AnyRef](display.length);
                          System.arraycopy(display, 0, newDisplay, 0, displayLen.-(1));
                          if (i.>=(_focusDepth))
                            newDisplay.update(displayLen, newSizes)
                          else
                            ();
                          i match {
                            case 2 => {
                              display2 = newDisplay;
                              display = display3
                            }
                            case 3 => {
                              display3 = newDisplay;
                              display = display4
                            }
                            case 4 => {
                              display4 = newDisplay;
                              display = display5
                            }
                            case 5 => display5 = newDisplay
                          };
                          i.+=(1)
                        }
                       while (i.<(oldDepth)) 
                    }
                  else
                    ()
                }
              else
                ();
              if (oldDepth.==(focusDepth))
                initFocus(endIndex.-(1), 0, endIndex, depth, 0)
              else
                initFocus(endIndex.-(1), endIndex.-(1), endIndex, 1, newRelaxedIndex.&(-64));
              display0.update(elemIndexInBlock, elem.asInstanceOf[AnyRef]);
              transient = true;
              assert(this.assertVectorInvariant())
            };
            private[immutable] def prepend[B](elem: B): scala.Unit = {
              if (focusStart.!=(0).||(focus.&(-64).!=(0)))
                normalizeAndFocusOn(0)
              else
                ();
              val d0 = display0;
              if (d0.length.<(64))
                prependOnCurrentBlock(elem, d0)
              else
                prependFrontNewBlock(elem);
              assert(this.assertVectorInvariant())
            };
            private def prependOnCurrentBlock[B](elem: B, oldD0: Array[AnyRef]): scala.Unit = {
              val newLen = oldD0.length.+(1);
              focusEnd = newLen;
              val newD0 = new Array[AnyRef](newLen);
              newD0.update(0, elem.asInstanceOf[AnyRef]);
              System.arraycopy(oldD0, 0, newD0, 1, newLen.-(1));
              display0 = newD0;
              makeTransientIfNeeded();
              assert(this.assertVectorInvariant())
            };
            private def prependFrontNewBlock[B](elem: B): scala.Unit = {
              assert(display0.length.==(64));
              var currentDepth = focusDepth;
              if (currentDepth.==(1))
                currentDepth.+=(1)
              else
                ();
              var display = currentDepth match {
                case 1 => {
                  currentDepth = 2;
                  display1
                }
                case 2 => display1
                case 3 => display2
                case 4 => display3
                case 5 => display4
                case 6 => display5
              };
              while (display.!=(null).&&(display.length.==(65))) 
                {
                  currentDepth.+=(1);
                  currentDepth match {
                    case 2 => display = display1
                    case 3 => display = display2
                    case 4 => display = display3
                    case 5 => display = display4
                    case 6 => display = display5
                    case _ => throw new IllegalStateException()
                  }
                }
              ;
              val oldDepth = depth;
              val _transient = transient;
              setupNewBlockInInitBranch(currentDepth, _transient);
              if (oldDepth.==(depth))
                {
                  var i = currentDepth;
                  if (i.<(oldDepth))
                    {
                      val _focusDepth = focusDepth;
                      var display: Array[AnyRef] = i match {
                        case 2 => display2
                        case 3 => display3
                        case 4 => display4
                        case 5 => display5
                      };
                      do 
                        {
                          val displayLen = display.length.-(1);
                          val newSizes: Array[Int] = if (i.>=(_focusDepth))
                            makeTransientSizes(display(displayLen).asInstanceOf[Array[Int]], 1)
                          else
                            null;
                          val newDisplay = new Array[AnyRef](display.length);
                          System.arraycopy(display, 0, newDisplay, 0, displayLen.-(1));
                          if (i.>=(_focusDepth))
                            newDisplay.update(displayLen, newSizes)
                          else
                            ();
                          i match {
                            case 2 => {
                              display2 = newDisplay;
                              display = display3
                            }
                            case 3 => {
                              display3 = newDisplay;
                              display = display4
                            }
                            case 4 => {
                              display4 = newDisplay;
                              display = display5
                            }
                            case 5 => display5 = newDisplay
                          };
                          i.+=(1)
                        }
                       while (i.<(oldDepth)) 
                    }
                  else
                    ()
                }
              else
                ();
              initFocus(0, 0, 1, 1, 0);
              display0.update(0, elem.asInstanceOf[AnyRef]);
              transient = true
            };
            private def createSingletonVector[B](elem: B) = {
              val resultVector = new RRBVector_c_64_asserted[B](1);
              resultVector.initSingleton(elem);
              assert(resultVector.assertVectorInvariant());
              resultVector
            };
            private[immutable] def normalizeAndFocusOn(index: Int) = {
              if (transient)
                {
                  normalize(depth);
                  transient = false
                }
              else
                ();
              focusOn(index)
            };
            private[immutable] def makeTransientIfNeeded() = {
              val _depth = depth;
              if (_depth.>(1).&&(transient.`unary_!`))
                {
                  copyDisplaysAndNullFocusedBranch(_depth, focus.|(focusRelax));
                  transient = true
                }
              else
                ()
            };
            private[immutable] def concatenate[B >: A](currentSize: Int, that: RRBVector_c_64_asserted[B]): scala.Unit = {
              assert(that.assertVectorInvariant());
              assert((0).<(that.length));
              if (this.transient)
                {
                  this.normalize(this.depth);
                  this.transient = false
                }
              else
                ();
              if (that.transient)
                {
                  that.normalize(that.depth);
                  that.transient = false
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
                    2)
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
                    initFromRoot(withComputedSizes(concat, 3), 3)
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
                    initFromRoot(withComputedSizes(concat, 4), 4)
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
                    initFromRoot(withComputedSizes(concat, 5), 5)
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
                    initFromRoot(withComputedSizes(concat, 6), 6)
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
                    initFromRoot(withComputedSizes(concat, 7), 7)
                }
                case _ => throw new IllegalStateException()
              };
              assert(this.assertVectorInvariant())
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
              val branching = computeBranching(displayLeft, concat, displayRight, currentDepth);
              val top = new Array[AnyRef](branching.>>(12).+(if (branching.&(4095).==(0))
                1
              else
                2));
              var mid = new Array[AnyRef](if (branching.>>(12).==(0))
                branching.+(63).>>(6).+(1)
              else
                65);
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
                      val displayValueEnd = if (currentDepth.==(2))
                        displayValue.length
                      else
                        displayValue.length.-(1);
                      if (iBot.|(j).==(0).&&(displayValueEnd.==(64)))
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
                          val numElementsToCopy = math.min(displayValueEnd.-(j), (64).-(iBot));
                          if (iBot.==(0))
                            {
                              if (currentDepth.!=(2).&&(bot.!=(null)))
                                withComputedSizes(bot, currentDepth.-(1))
                              else
                                ();
                              bot = new Array[AnyRef](math.min(branching.-(iTop.<<(12)).-(iMid.<<(6)), 64).+(if (currentDepth.==(2))
                                0
                              else
                                1));
                              mid.update(iMid, bot)
                            }
                          else
                            ();
                          System.arraycopy(displayValue, j, bot, iBot, numElementsToCopy);
                          j.+=(numElementsToCopy);
                          iBot.+=(numElementsToCopy);
                          if (j.==(displayValueEnd))
                            {
                              i.+=(1);
                              j = 0
                            }
                          else
                            ();
                          if (iBot.==(64))
                            {
                              iMid.+=(1);
                              iBot = 0;
                              iSizes.+=(1);
                              if (currentDepth.!=(2).&&(bot.!=(null)))
                                withComputedSizes(bot, currentDepth.-(1))
                              else
                                ()
                            }
                          else
                            ()
                        };
                      if (iMid.==(64))
                        {
                          top.update(iTop, if (currentDepth.==(1))
                            withComputedSizes1(mid)
                          else
                            withComputedSizes(mid, currentDepth));
                          iTop.+=(1);
                          iMid = 0;
                          val remainingBranches = branching.-(iTop.<<(6).|(iMid).<<(6).|(iBot));
                          if (remainingBranches.>(0))
                            mid = new Array[AnyRef](if (remainingBranches.>>(12).==(0))
                              remainingBranches.+(127).>>(6)
                            else
                              65)
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
                top.update(iTop, if (currentDepth.==(1))
                  withComputedSizes1(mid)
                else
                  withComputedSizes(mid, currentDepth))
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
                    System.arraycopy(displayLeft, 0, mergedDisplay, 0, leftLength);
                    System.arraycopy(displayRight, 0, mergedDisplay, leftLength, rightLength);
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
                    System.arraycopy(displayLeft, 0, arr0, 0, leftLength);
                    System.arraycopy(displayRight, 0, arr0, leftLength, (64).-(leftLength));
                    System.arraycopy(displayRight, (64).-(leftLength), arr1, 0, rightLength.-(64).+(leftLength));
                    top
                  }
            };
            private def computeBranching(displayLeft: Array[AnyRef], concat: Array[AnyRef], displayRight: Array[AnyRef], currentDepth: Int) = {
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
              var branching = 0;
              if (currentDepth.==(1))
                {
                  branching = leftLength.+(concatLength).+(rightLength);
                  if (leftLength.!=(0))
                    branching.-=(1)
                  else
                    ();
                  if (rightLength.!=(0))
                    branching.-=(1)
                  else
                    ()
                }
              else
                {
                  var i = 0;
                  while (i.<(leftLength.-(1))) 
                    {
                      branching.+=(displayLeft(i).asInstanceOf[Array[AnyRef]].length);
                      i.+=(1)
                    }
                  ;
                  i = 0;
                  while (i.<(concatLength)) 
                    {
                      branching.+=(concat(i).asInstanceOf[Array[AnyRef]].length);
                      i.+=(1)
                    }
                  ;
                  i = 1;
                  while (i.<(rightLength)) 
                    {
                      branching.+=(displayRight(i).asInstanceOf[Array[AnyRef]].length);
                      i.+=(1)
                    }
                  ;
                  if (currentDepth.!=(2))
                    {
                      branching.-=(leftLength.+(concatLength).+(rightLength));
                      if (leftLength.!=(0))
                        branching.+=(1)
                      else
                        ();
                      if (rightLength.!=(0))
                        branching.+=(1)
                      else
                        ()
                    }
                  else
                    ()
                };
              branching
            };
            private def takeFront0(n: Int): RRBVector_c_64_asserted[A] = {
              if (transient)
                {
                  normalize(depth);
                  transient = false
                }
              else
                ();
              val vec = new RRBVector_c_64_asserted[A](n);
              vec.initWithFocusFrom(this);
              if (depth.>(1))
                {
                  vec.focusOn(n.-(1));
                  val d0len = vec.focus.&(63).+(1);
                  if (d0len.!=(64))
                    {
                      val d0 = new Array[AnyRef](d0len);
                      System.arraycopy(vec.display0, 0, d0, 0, d0len);
                      vec.display0 = d0
                    }
                  else
                    ();
                  val cutIndex = vec.focus.|(vec.focusRelax);
                  vec.cleanTopTake(cutIndex);
                  vec.focusDepth = math.min(vec.depth, vec.focusDepth);
                  if (vec.depth.>(1))
                    {
                      vec.copyDisplays(vec.focusDepth, cutIndex);
                      var i = vec.depth;
                      var offset = 0;
                      var display: Array[AnyRef] = null;
                      while (i.>(vec.focusDepth)) 
                        {
                          i match {
                            case 2 => display = vec.display1
                            case 3 => display = vec.display2
                            case 4 => display = vec.display3
                            case 5 => display = vec.display4
                            case 6 => display = vec.display5
                          };
                          val oldSizes = display(display.length.-(1)).asInstanceOf[Array[Int]];
                          val newLen = vec.focusRelax.>>((6).*(i.-(1))).&(63).+(1);
                          val newSizes = new Array[Int](newLen);
                          System.arraycopy(oldSizes, 0, newSizes, 0, newLen.-(1));
                          newSizes.update(newLen.-(1), n.-(offset));
                          if (newLen.>(1))
                            offset.+=(newSizes(newLen.-(2)))
                          else
                            ();
                          val newDisplay = new Array[AnyRef](newLen.+(1));
                          System.arraycopy(display, 0, newDisplay, 0, newLen);
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
                    System.arraycopy(vec.display0, 0, d0, 0, n);
                    vec.display0 = d0;
                    vec.initFocus(0, 0, n, 1, 0)
                  }
                else
                  ();
              assert(vec.assertVectorInvariant());
              vec
            };
            private def dropFront0(n: Int): RRBVector_c_64_asserted[A] = {
              if (transient)
                {
                  normalize(depth);
                  transient = false
                }
              else
                ();
              val vec = new RRBVector_c_64_asserted[A](this.endIndex.-(n));
              vec.initWithFocusFrom(this);
              if (vec.depth.>(1))
                {
                  vec.focusOn(n);
                  val cutIndex = vec.focus.|(vec.focusRelax);
                  val d0Start = cutIndex.&(63);
                  if (d0Start.!=(0))
                    {
                      val d0len = vec.display0.length.-(d0Start);
                      val d0 = new Array[AnyRef](d0len);
                      System.arraycopy(vec.display0, d0Start, d0, 0, d0len);
                      vec.display0 = d0
                    }
                  else
                    ();
                  vec.cleanTopDrop(cutIndex);
                  if (vec.depth.>(1))
                    {
                      var i = 2;
                      var display = vec.display1;
                      while (i.<=(vec.depth)) 
                        {
                          val splitStart = cutIndex.>>((6).*(i.-(1))).&(63);
                          val newLen = display.length.-(splitStart).-(1);
                          val newDisplay = new Array[AnyRef](newLen.+(1));
                          System.arraycopy(display, splitStart.+(1), newDisplay, 1, newLen.-(1));
                          i match {
                            case 2 => {
                              newDisplay.update(0, vec.display0);
                              vec.display1 = withComputedSizes(newDisplay, 2);
                              display = vec.display2
                            }
                            case 3 => {
                              newDisplay.update(0, vec.display1);
                              vec.display2 = withComputedSizes(newDisplay, 3);
                              display = vec.display3
                            }
                            case 4 => {
                              newDisplay.update(0, vec.display2);
                              vec.display3 = withComputedSizes(newDisplay, 4);
                              display = vec.display4
                            }
                            case 5 => {
                              newDisplay.update(0, vec.display3);
                              vec.display4 = withComputedSizes(newDisplay, 5);
                              display = vec.display5
                            }
                            case 6 => {
                              newDisplay.update(0, vec.display4);
                              vec.display5 = withComputedSizes(newDisplay, 6)
                            }
                          };
                          i.+=(1)
                        }
                      
                    }
                  else
                    ();
                  vec.initFocus(0, 0, vec.display0.length, 1, 0)
                }
              else
                {
                  val newLen = vec.display0.length.-(n);
                  val d0 = new Array[AnyRef](newLen);
                  System.arraycopy(vec.display0, n, d0, 0, newLen);
                  vec.display0 = d0;
                  vec.initFocus(0, 0, newLen, 1, 0)
                };
              assert(vec.assertVectorInvariant());
              vec
            };
            private[immutable] def assertVectorInvariant(): Boolean = {
              assert((0).<=(depth).&&(depth.<=(6)), depth);
              assert(isEmpty.==(depth.==(0)), scala.Tuple2(isEmpty, depth));
              assert(isEmpty.==(length.==(0)), scala.Tuple2(isEmpty, length));
              assert(length.==(endIndex), scala.Tuple2(length, endIndex));
              assert(depth.<=(0).&&(display0.==(null)).||(depth.>(0).&&(display0.!=(null))), ({
  val x$8 = depth.toString;
  "<=0 <==> display0==null ".+:(x$8)
}).:+(depth, display0));
              assert(depth.<=(1).&&(display1.==(null)).||(depth.>(0).&&(display1.!=(null))), ({
  val x$9 = depth.toString;
  "<=1 <==> display1==null ".+:(x$9)
}).:+(depth, display1));
              assert(depth.<=(2).&&(display2.==(null)).||(depth.>(0).&&(display2.!=(null))), ({
  val x$10 = depth.toString;
  "<=2 <==> display2==null ".+:(x$10)
}).:+(depth, display2));
              assert(depth.<=(3).&&(display3.==(null)).||(depth.>(0).&&(display3.!=(null))), ({
  val x$11 = depth.toString;
  "<=3 <==> display3==null ".+:(x$11)
}).:+(depth, display3));
              assert(depth.<=(4).&&(display4.==(null)).||(depth.>(0).&&(display4.!=(null))), ({
  val x$12 = depth.toString;
  "<=4 <==> display4==null ".+:(x$12)
}).:+(depth, display4));
              assert(depth.<=(5).&&(display5.==(null)).||(depth.>(0).&&(display5.!=(null))), ({
  val x$13 = depth.toString;
  "<=5 <==> display5==null ".+:(x$13)
}).:+(depth, display5));
              if (transient.`unary_!`)
                {
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
                      if (focusDepth.<=(1))
                        assert(display1(focusRelax.>>(6).&(63)).==(display0))
                      else
                        assert(display1(focus.>>(6).&(63)).==(display0))
                    }
                  else
                    ()
                }
              else
                {
                  assert(depth.>(1));
                  if (display5.!=(null))
                    {
                      assert(display4.!=(null));
                      if (focusDepth.<=(5))
                        assert(display5(focusRelax.>>(30).&(63)).==(null))
                      else
                        assert(display5(focus.>>(30).&(63)).==(null))
                    }
                  else
                    ();
                  if (display4.!=(null))
                    {
                      assert(display3.!=(null));
                      if (focusDepth.<=(4))
                        assert(display4(focusRelax.>>(24).&(63)).==(null))
                      else
                        assert(display4(focus.>>(24).&(63)).==(null))
                    }
                  else
                    ();
                  if (display3.!=(null))
                    {
                      assert(display2.!=(null));
                      if (focusDepth.<=(3))
                        assert(display3(focusRelax.>>(18).&(63)).==(null))
                      else
                        assert(display3(focus.>>(18).&(63)).==(null))
                    }
                  else
                    ();
                  if (display2.!=(null))
                    {
                      assert(display1.!=(null));
                      if (focusDepth.<=(2))
                        assert(display2(focusRelax.>>(12).&(63)).==(null))
                      else
                        assert(display2(focus.>>(12).&(63)).==(null))
                    }
                  else
                    ();
                  if (display1.!=(null))
                    {
                      assert(display0.!=(null));
                      if (focusDepth.<=(1))
                        assert(display1(focusRelax.>>(6).&(63)).==(null))
                      else
                        assert(display1(focus.>>(6).&(63)).==(null))
                    }
                  else
                    ()
                };
              assert((0).<=(focusStart).&&(focusStart.<=(focusEnd)).&&(focusEnd.<=(endIndex)), scala.Tuple3(focusStart, focusEnd, endIndex));
              assert(focusStart.==(focusEnd).||(focusEnd.!=(0)), "focusStart==focusEnd ==> focusEnd==0".+(focusStart, focusEnd));
              assert((0).<=(focusDepth).&&(focusDepth.<=(depth)), scala.Tuple2(focusDepth, depth));
              def checkSizes(node: Array[AnyRef], currentDepth: Int, _endIndex: Int): Unit = if (currentDepth.>(1))
                if (node.!=(null))
                  {
                    val sizes = node(node.length.-(1)).asInstanceOf[Array[Int]];
                    if (sizes.!=(null))
                      {
                        assert(node.length.==(sizes.length.+(1)));
                        if (transient.`unary_!`)
                          assert(sizes(sizes.length.-(1)).==(_endIndex), scala.Tuple2(sizes(sizes.length.-(1)), _endIndex))
                        else
                          ();
                        var i = 0;
                        while (i.<(sizes.length.-(1))) 
                          {
                            checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth.-(1), sizes(i).-(if (i.==(0))
                              0
                            else
                              sizes(i.-(1))));
                            i.+=(1)
                          }
                        ;
                        checkSizes(node(node.length.-(2)).asInstanceOf[Array[AnyRef]], currentDepth.-(1), if (sizes.length.>(1))
                          sizes(sizes.length.-(1)).-(sizes(sizes.length.-(2)))
                        else
                          sizes(sizes.length.-(1)))
                      }
                    else
                      {
                        var i = 0;
                        while (i.<(node.length.-(2))) 
                          {
                            checkSizes(node(i).asInstanceOf[Array[AnyRef]], currentDepth.-(1), (1).<<((6).*(currentDepth.-(1))));
                            i.+=(1)
                          }
                        ;
                        val expectedLast = _endIndex.-((1).<<((6).*(currentDepth.-(1))).*(node.length.-(2)));
                        assert((1).<=(expectedLast).&&(expectedLast.<=((1).<<((6).*(currentDepth)))));
                        checkSizes(node(node.length.-(2)).asInstanceOf[Array[AnyRef]], currentDepth.-(1), expectedLast)
                      }
                  }
                else
                  assert(transient)
              else
                if (node.!=(null))
                  assert(node.length.==(_endIndex))
                else
                  assert(transient);
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
            };
            private[immutable] def debugToString(): String = {
              val sb = new StringBuilder();
              sb.append("RRBVector (");
              sb.append("\t".+("display0").+(" = ").+(display0).+(if (display0.!=(null))
  display0.mkString("[", ", ", "]")
else
  "").+("\n"));
              sb.append("\t".+("display1").+(" = ").+(display1).+(if (display1.!=(null))
  display1.mkString("[", ", ", "]")
else
  "").+("\n"));
              sb.append("\t".+("display2").+(" = ").+(display2).+(if (display2.!=(null))
  display2.mkString("[", ", ", "]")
else
  "").+("\n"));
              sb.append("\t".+("display3").+(" = ").+(display3).+(if (display3.!=(null))
  display3.mkString("[", ", ", "]")
else
  "").+("\n"));
              sb.append("\t".+("display4").+(" = ").+(display4).+(if (display4.!=(null))
  display4.mkString("[", ", ", "]")
else
  "").+("\n"));
              sb.append("\t".+("display5").+(" = ").+(display5).+(if (display5.!=(null))
  display5.mkString("[", ", ", "]")
else
  "").+("\n"));
              sb.append("\tdepth = ".+(depth).+("\n"));
              sb.append("\tendIndex = ".+(endIndex).+("\n"));
              sb.append("\tfocus = ".+(focus).+("\n"));
              sb.append("\tfocusStart = ".+(focusStart).+("\n"));
              sb.append("\tfocusEnd = ".+(focusEnd).+("\n"));
              sb.append("\tfocusRelax = ".+(focusRelax).+("\n"));
              sb.append("\ttransient = ".+(transient).+("\n"));
              sb.append(")");
              sb.toString
            }
          }

          final class RRBVectorBuilder_c_64_asserted[A] extends mutable.Builder[A, RRBVector_c_64_asserted[A]] with RRBVectorPointer_c_64_asserted[A @uncheckedVariance] {
            display0 = new Array[AnyRef](64);
            depth = 1;
            private var blockIndex = 0;
            private var lo = 0;
            private var acc: RRBVector_c_64_asserted[A] = null;
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
                if (xs.isInstanceOf[RRBVector_c_64_asserted[A]])
                  {
                    val thatVec = xs.asInstanceOf[RRBVector_c_64_asserted[A]];
                    if (thatVec.length.>(1024))
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
                    else
                      super.++=(xs)
                  }
                else
                  super.++=(xs)
              else
                ();
              this
            };
            def result(): RRBVector_c_64_asserted[A] = {
              val current = currentResult();
              val resultVector = if (acc.==(null))
                current
              else
                acc.++(current);
              assert(resultVector.assertVectorInvariant());
              resultVector
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
            private def currentResult(): RRBVector_c_64_asserted[A] = {
              val size = blockIndex.+(lo);
              if (size.==(0))
                RRBVector_c_64_asserted.empty
              else
                {
                  val resultVector = new RRBVector_c_64_asserted[A](size);
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

          class RRBVectorIterator_c_64_asserted[+A](startIndex: Int, override private[immutable] val endIndex: Int) extends AbstractIterator[A] with Iterator[A] with RRBVectorPointer_c_64_asserted[A @uncheckedVariance] {
            private var blockIndex: Int = _;
            private var lo: Int = _;
            private var endLo: Int = _;
            private var _hasNext: Boolean = _;
            final private[collection] def initIteratorFrom[B >: A](that: RRBVectorPointer_c_64_asserted[B]): Unit = {
              initWithFocusFrom(that);
              _hasNext = startIndex.<(endIndex);
              if (_hasNext)
                {
                  focusOn(startIndex);
                  blockIndex = focusStart.+(focus.&(-64));
                  lo = focus.&(63);
                  if (endIndex.<(focusEnd))
                    focusEnd = endIndex
                  else
                    ();
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
                      {
                        focusOn(newBlockIndex);
                        if (endIndex.<(focusEnd))
                          focusEnd = endIndex
                        else
                          ()
                      }
                    else
                      {
                        lo = 0;
                        blockIndex = endIndex;
                        endLo = 1;
                        if (_hasNext)
                          {
                            _hasNext = false;
                            return res
                          }
                        else
                          throw new NoSuchElementException("reached iterator end")
                      };
                  endLo = math.min(focusEnd.-(newBlockIndex), 64);
                  res
                }
            };
            private[collection] def remaining: Int = math.max(endIndex.-(blockIndex.+(lo)), 0)
          }

          class RRBVectorReverseIterator_c_64_asserted[+A](startIndex: Int, final override private[immutable] val endIndex: Int) extends AbstractIterator[A] with Iterator[A] with RRBVectorPointer_c_64_asserted[A @uncheckedVariance] {
            private var lastIndexOfBlock: Int = _;
            private var lo: Int = _;
            private var endLo: Int = _;
            private var _hasNext: Boolean = startIndex.<(endIndex);
            final private[collection] def initIteratorFrom[B >: A](that: RRBVectorPointer_c_64_asserted[B]): Unit = {
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

          private[immutable] trait RRBVectorPointer_c_64_asserted[A] {
            final private[immutable] var display0: Array[AnyRef] = _;
            final private[immutable] var display1: Array[AnyRef] = _;
            final private[immutable] var display2: Array[AnyRef] = _;
            final private[immutable] var display3: Array[AnyRef] = _;
            final private[immutable] var display4: Array[AnyRef] = _;
            final private[immutable] var display5: Array[AnyRef] = _;
            final private[immutable] var display6: Array[AnyRef] = _;
            final private[immutable] var depth: Int = _;
            final private[immutable] var focusStart: Int = 0;
            final private[immutable] var focusEnd: Int = 0;
            final private[immutable] var focusDepth: Int = 0;
            final private[immutable] var focus: Int = 0;
            final private[immutable] var focusRelax: Int = 0;
            private[immutable] def endIndex: Int;
            final private[immutable] def initWithFocusFrom[U](that: RRBVectorPointer_c_64_asserted[U]): Unit = {
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
            final private[immutable] def initFrom[U](that: RRBVectorPointer_c_64_asserted[U]): Unit = {
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
              assert((0).<=(index));
              assert((1).<(depth));
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
              var sizes = display(display.length.-(1)).asInstanceOf[Array[Int]];
              do 
                {
                  val sizesIdx = getIndexInSizes(sizes, indexInSubTree);
                  if (sizesIdx.!=(0))
                    indexInSubTree.-=(sizes(sizesIdx.-(1)))
                  else
                    ();
                  display = display(sizesIdx).asInstanceOf[Array[AnyRef]];
                  if (currentDepth.>(2))
                    sizes = display(display.length.-(1)).asInstanceOf[Array[Int]]
                  else
                    sizes = null;
                  currentDepth.-=(1)
                }
               while (sizes.!=(null)) ;
              currentDepth match {
                case 2 => getElem1(display, indexInSubTree)
                case 3 => getElem2(display, indexInSubTree)
                case 4 => getElem3(display, indexInSubTree)
                case 5 => getElem4(display, indexInSubTree)
                case 6 => getElem5(display, indexInSubTree)
                case _ => throw new IllegalStateException()
              }
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
              assert((0).<=(index));
              var _startIndex: Int = 0;
              var _endIndex: Int = endIndex;
              var currentDepth: Int = depth;
              var _focusRelax: Int = 0;
              var continue: Boolean = currentDepth.>(1);
              if (continue)
                {
                  var display = currentDepth match {
                    case 2 => display1
                    case 3 => display2
                    case 4 => display3
                    case 5 => display4
                    case 6 => display5
                    case _ => throw new IllegalStateException()
                  };
                  do 
                    {
                      val sizes = display(display.length.-(1)).asInstanceOf[Array[Int]];
                      if (sizes.==(null))
                        continue = false
                      else
                        {
                          val is = getIndexInSizes(sizes, index.-(_startIndex));
                          display = display(is).asInstanceOf[Array[AnyRef]];
                          currentDepth match {
                            case 2 => {
                              display0 = display;
                              continue = false
                            }
                            case 3 => display1 = display
                            case 4 => display2 = display
                            case 5 => display3 = display
                            case 6 => display4 = display
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
                   while (continue) 
                }
              else
                ();
              val indexInFocus = index.-(_startIndex);
              gotoPos(indexInFocus, (1).<<((6).*(currentDepth.-(1))));
              initFocus(indexInFocus, _startIndex, _endIndex, currentDepth, _focusRelax)
            };
            final private[immutable] def setupNewBlockInNextBranch(xor: Int, transient: Boolean): Unit = if (xor.<(4096))
              {
                if (depth.==(1))
                  {
                    depth = 2;
                    {
                      val newRoot = new Array[AnyRef](3);
                      newRoot.update(0, display0);
                      display1 = newRoot
                    }
                  }
                else
                  {
                    val newRoot = copyAndIncRightRoot(display1, transient, 1);
                    if (transient)
                      {
                        val oldTransientBranch = newRoot.length.-(3);
                        withRecomputedSizes(newRoot, 2, oldTransientBranch);
                        newRoot.update(oldTransientBranch, display0)
                      }
                    else
                      ();
                    display1 = newRoot
                  };
                display0 = new Array(1);
                
              }
            else
              if (xor.<(262144))
                {
                  if (transient)
                    normalize(2)
                  else
                    ();
                  if (depth.==(2))
                    {
                      depth = 3;
                      display2 = makeNewRoot0(display1)
                    }
                  else
                    {
                      val newRoot = copyAndIncRightRoot(display2, transient, 2);
                      if (transient)
                        {
                          val oldTransientBranch = newRoot.length.-(3);
                          withRecomputedSizes(newRoot, 3, oldTransientBranch);
                          newRoot.update(oldTransientBranch, display1)
                        }
                      else
                        ();
                      display2 = newRoot
                    };
                  display0 = new Array(1);
                  val _emptyTransientBlock = RRBVector_c_64_asserted.emptyTransientBlock;
                  display1 = _emptyTransientBlock
                }
              else
                if (xor.<(16777216))
                  {
                    if (transient)
                      normalize(3)
                    else
                      ();
                    if (depth.==(3))
                      {
                        depth = 4;
                        display3 = makeNewRoot0(display2)
                      }
                    else
                      {
                        val newRoot = copyAndIncRightRoot(display3, transient, 3);
                        if (transient)
                          {
                            val oldTransientBranch = newRoot.length.-(3);
                            withRecomputedSizes(newRoot, 4, oldTransientBranch);
                            newRoot.update(oldTransientBranch, display2)
                          }
                        else
                          ();
                        display3 = newRoot
                      };
                    display0 = new Array(1);
                    val _emptyTransientBlock = RRBVector_c_64_asserted.emptyTransientBlock;
                    display1 = _emptyTransientBlock;
                    display2 = _emptyTransientBlock
                  }
                else
                  if (xor.<(1073741824))
                    {
                      if (transient)
                        normalize(4)
                      else
                        ();
                      if (depth.==(4))
                        {
                          depth = 5;
                          display4 = makeNewRoot0(display3)
                        }
                      else
                        {
                          val newRoot = copyAndIncRightRoot(display4, transient, 4);
                          if (transient)
                            {
                              val oldTransientBranch = newRoot.length.-(3);
                              withRecomputedSizes(newRoot, 5, oldTransientBranch);
                              newRoot.update(oldTransientBranch, display3)
                            }
                          else
                            ();
                          display4 = newRoot
                        };
                      display0 = new Array(1);
                      val _emptyTransientBlock = RRBVector_c_64_asserted.emptyTransientBlock;
                      display1 = _emptyTransientBlock;
                      display2 = _emptyTransientBlock;
                      display3 = _emptyTransientBlock
                    }
                  else
                    if (xor.<(16))
                      {
                        if (transient)
                          normalize(5)
                        else
                          ();
                        if (depth.==(5))
                          {
                            depth = 6;
                            display5 = makeNewRoot0(display4)
                          }
                        else
                          {
                            val newRoot = copyAndIncRightRoot(display5, transient, 5);
                            if (transient)
                              {
                                val oldTransientBranch = newRoot.length.-(3);
                                withRecomputedSizes(newRoot, 6, oldTransientBranch);
                                newRoot.update(oldTransientBranch, display4)
                              }
                            else
                              ();
                            display5 = newRoot
                          };
                        display0 = new Array(1);
                        val _emptyTransientBlock = RRBVector_c_64_asserted.emptyTransientBlock;
                        display1 = _emptyTransientBlock;
                        display2 = _emptyTransientBlock;
                        display3 = _emptyTransientBlock;
                        display4 = _emptyTransientBlock
                      }
                    else
                      throw new IllegalArgumentException();
            final private[immutable] def setupNewBlockInInitBranch(insertionDepth: Int, transient: Boolean): Unit = insertionDepth match {
              case 2 => {
                if (transient)
                  normalize(1)
                else
                  ();
                if (depth.==(1))
                  {
                    depth = 2;
                    {
                      val sizes = new Array[Int](2);
                      sizes.update(1, display0.length);
                      val newRoot = new Array[AnyRef](3);
                      newRoot.update(1, display0);
                      newRoot.update(2, sizes);
                      display1 = newRoot
                    }
                  }
                else
                  {
                    val newRoot = copyAndIncLeftRoot(display1, transient, 1);
                    if (transient)
                      {
                        withRecomputedSizes(newRoot, 2, 1);
                        newRoot.update(1, display0)
                      }
                    else
                      ();
                    display1 = newRoot
                  };
                display0 = new Array(1);
                
              }
              case 3 => {
                if (transient)
                  normalize(2)
                else
                  ();
                if (depth.==(2))
                  {
                    depth = 3;
                    display2 = makeNewRoot1(display1, 3)
                  }
                else
                  {
                    val newRoot = copyAndIncLeftRoot(display2, transient, 2);
                    if (transient)
                      {
                        withRecomputedSizes(newRoot, 3, 1);
                        newRoot.update(1, display1)
                      }
                    else
                      ();
                    display2 = newRoot
                  };
                display0 = new Array(1);
                val _emptyTransientBlock = RRBVector_c_64_asserted.emptyTransientBlock;
                display1 = _emptyTransientBlock
              }
              case 4 => {
                if (transient)
                  normalize(3)
                else
                  ();
                if (depth.==(3))
                  {
                    depth = 4;
                    display3 = makeNewRoot1(display2, 4)
                  }
                else
                  {
                    val newRoot = copyAndIncLeftRoot(display3, transient, 3);
                    if (transient)
                      {
                        withRecomputedSizes(newRoot, 4, 1);
                        newRoot.update(1, display2)
                      }
                    else
                      ();
                    display3 = newRoot
                  };
                display0 = new Array(1);
                val _emptyTransientBlock = RRBVector_c_64_asserted.emptyTransientBlock;
                display1 = _emptyTransientBlock;
                display2 = _emptyTransientBlock
              }
              case 5 => {
                if (transient)
                  normalize(4)
                else
                  ();
                if (depth.==(4))
                  {
                    depth = 5;
                    display4 = makeNewRoot1(display3, 5)
                  }
                else
                  {
                    val newRoot = copyAndIncLeftRoot(display4, transient, 4);
                    if (transient)
                      {
                        withRecomputedSizes(newRoot, 5, 1);
                        newRoot.update(1, display3)
                      }
                    else
                      ();
                    display4 = newRoot
                  };
                display0 = new Array(1);
                val _emptyTransientBlock = RRBVector_c_64_asserted.emptyTransientBlock;
                display1 = _emptyTransientBlock;
                display2 = _emptyTransientBlock;
                display3 = _emptyTransientBlock
              }
              case 6 => {
                if (transient)
                  normalize(5)
                else
                  ();
                if (depth.==(5))
                  {
                    depth = 6;
                    display5 = makeNewRoot1(display4, 6)
                  }
                else
                  {
                    val newRoot = copyAndIncLeftRoot(display5, transient, 5);
                    if (transient)
                      {
                        withRecomputedSizes(newRoot, 6, 1);
                        newRoot.update(1, display4)
                      }
                    else
                      ();
                    display5 = newRoot
                  };
                display0 = new Array(1);
                val _emptyTransientBlock = RRBVector_c_64_asserted.emptyTransientBlock;
                display1 = _emptyTransientBlock;
                display2 = _emptyTransientBlock;
                display3 = _emptyTransientBlock;
                display4 = _emptyTransientBlock
              }
              case _ => throw new IllegalStateException()
            };
            final private[immutable] def gotoPos(index: Int, xor: Int): Unit = if (xor.<(64))
              ()
            else
              if (xor.<(4096))
                display0 = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]]
              else
                if (xor.<(262144))
                  {
                    display1 = display2(index.>>(12).&(63)).asInstanceOf[Array[AnyRef]];
                    display0 = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]]
                  }
                else
                  if (xor.<(16777216))
                    {
                      display2 = display3(index.>>(18).&(63)).asInstanceOf[Array[AnyRef]];
                      display1 = display2(index.>>(12).&(63)).asInstanceOf[Array[AnyRef]];
                      display0 = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]]
                    }
                  else
                    if (xor.<(1073741824))
                      {
                        display3 = display4(index.>>(24).&(63)).asInstanceOf[Array[AnyRef]];
                        display2 = display3(index.>>(18).&(63)).asInstanceOf[Array[AnyRef]];
                        display1 = display2(index.>>(12).&(63)).asInstanceOf[Array[AnyRef]];
                        display0 = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]]
                      }
                    else
                      if (xor.<(16))
                        {
                          display4 = display5(index.>>(30).&(63)).asInstanceOf[Array[AnyRef]];
                          display3 = display4(index.>>(24).&(63)).asInstanceOf[Array[AnyRef]];
                          display2 = display3(index.>>(18).&(63)).asInstanceOf[Array[AnyRef]];
                          display1 = display2(index.>>(12).&(63)).asInstanceOf[Array[AnyRef]];
                          display0 = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]]
                        }
                      else
                        throw new IllegalArgumentException();
            final private[immutable] def gotoNextBlockStart(index: Int, xor: Int): Unit = {
              var idx = 0;
              if (xor.>=(4096))
                {
                  if (xor.>=(262144))
                    {
                      if (xor.>=(16777216))
                        {
                          if (xor.>=(1073741824))
                            {
                              if (xor.>=(16))
                                throw new IllegalArgumentException()
                              else
                                display4 = display5(index.>>(30).&(63)).asInstanceOf[Array[AnyRef]];
                              idx = 0
                            }
                          else
                            idx = index.>>(24).&(63);
                          display3 = display4(idx).asInstanceOf[Array[AnyRef]];
                          idx = 0
                        }
                      else
                        idx = index.>>(18).&(63);
                      display2 = display3(idx).asInstanceOf[Array[AnyRef]];
                      idx = 0
                    }
                  else
                    idx = index.>>(12).&(63);
                  display1 = display2(idx).asInstanceOf[Array[AnyRef]];
                  idx = 0
                }
              else
                idx = index.>>(6).&(63);
              display0 = display1(idx).asInstanceOf[Array[AnyRef]]
            };
            final private[immutable] def gotoPrevBlockStart(index: Int, xor: Int): Unit = {
              var idx = 63;
              if (xor.>=(4096))
                {
                  if (xor.>=(262144))
                    {
                      if (xor.>=(16777216))
                        {
                          if (xor.>=(1073741824))
                            {
                              if (xor.>=(16))
                                throw new IllegalArgumentException()
                              else
                                display4 = display5(index.>>(30).&(63)).asInstanceOf[Array[AnyRef]];
                              idx = 63
                            }
                          else
                            idx = index.>>(24).&(63);
                          display3 = display4(idx).asInstanceOf[Array[AnyRef]];
                          idx = 63
                        }
                      else
                        idx = index.>>(18).&(63);
                      display2 = display3(idx).asInstanceOf[Array[AnyRef]];
                      idx = 63
                    }
                  else
                    idx = index.>>(12).&(63);
                  display1 = display2(idx).asInstanceOf[Array[AnyRef]];
                  idx = 63
                }
              else
                idx = index.>>(6).&(63);
              display0 = display1(idx).asInstanceOf[Array[AnyRef]]
            };
            final private[immutable] def gotoNextBlockStartWritable(index: Int, xor: Int): Unit = if (xor.<(4096))
              {
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
            else
              if (xor.<(262144))
                {
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
              else
                if (xor.<(16777216))
                  {
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
                else
                  if (xor.<(1073741824))
                    {
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
                  else
                    if (xor.<(16))
                      {
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
                    else
                      throw new IllegalArgumentException();
            final private[immutable] def normalize(_depth: Int): Unit = {
              assert((1).<(_depth));
              val _focusDepth = focusDepth;
              val stabilizationIndex = focus.|(focusRelax);
              copyDisplaysAndStabilizeDisplayPath(_focusDepth, stabilizationIndex);
              var currentLevel = _focusDepth;
              if (currentLevel.<(_depth))
                {
                  var display = currentLevel match {
                    case 1 => display1
                    case 2 => display2
                    case 3 => display3
                    case 4 => display4
                    case 5 => display5
                  };
                  do 
                    {
                      val newDisplay = copyOf(display);
                      val idx = stabilizationIndex.>>((6).*(currentLevel)).&(31);
                      currentLevel match {
                        case 1 => {
                          newDisplay.update(idx, display0);
                          display1 = withRecomputedSizes(newDisplay, 2, idx);
                          display = display2
                        }
                        case 2 => {
                          newDisplay.update(idx, display1);
                          display2 = withRecomputedSizes(newDisplay, 3, idx);
                          display = display3
                        }
                        case 3 => {
                          newDisplay.update(idx, display2);
                          display3 = withRecomputedSizes(newDisplay, 4, idx);
                          display = display4
                        }
                        case 4 => {
                          newDisplay.update(idx, display3);
                          display4 = withRecomputedSizes(newDisplay, 5, idx);
                          display = display5
                        }
                        case 5 => {
                          newDisplay.update(idx, display4);
                          display5 = withRecomputedSizes(newDisplay, 6, idx)
                        }
                      };
                      currentLevel.+=(1)
                    }
                   while (currentLevel.<(_depth)) 
                }
              else
                ()
            };
            final private[immutable] def copyDisplays(_depth: Int, _focus: Int): Unit = if ((2).<=(_depth))
              {
                if ((3).<=(_depth))
                  {
                    if ((4).<=(_depth))
                      {
                        if ((5).<=(_depth))
                          {
                            if ((6).<=(_depth))
                              {
                                val idx5 = _focus.>>(30).&(63).+(1);
                                display5 = copyOf(display5, idx5, idx5.+(1))
                              }
                            else
                              ();
                            val idx4 = _focus.>>(24).&(63).+(1);
                            display4 = copyOf(display4, idx4, idx4.+(1))
                          }
                        else
                          ();
                        val idx3 = _focus.>>(18).&(63).+(1);
                        display3 = copyOf(display3, idx3, idx3.+(1))
                      }
                    else
                      ();
                    val idx2 = _focus.>>(12).&(63).+(1);
                    display2 = copyOf(display2, idx2, idx2.+(1))
                  }
                else
                  ();
                val idx1 = _focus.>>(6).&(63).+(1);
                display1 = copyOf(display1, idx1, idx1.+(1))
              }
            else
              ();
            final private[immutable] def copyDisplaysAndNullFocusedBranch(_depth: Int, _focus: Int): Unit = _depth match {
              case 2 => display1 = copyOfAndNull(display1, _focus.>>(6).&(63))
              case 3 => {
                display1 = copyOfAndNull(display1, _focus.>>(6).&(63));
                display2 = copyOfAndNull(display2, _focus.>>(12).&(63))
              }
              case 4 => {
                display1 = copyOfAndNull(display1, _focus.>>(6).&(63));
                display2 = copyOfAndNull(display2, _focus.>>(12).&(63));
                display3 = copyOfAndNull(display3, _focus.>>(18).&(63))
              }
              case 5 => {
                display1 = copyOfAndNull(display1, _focus.>>(6).&(63));
                display2 = copyOfAndNull(display2, _focus.>>(12).&(63));
                display3 = copyOfAndNull(display3, _focus.>>(18).&(63));
                display4 = copyOfAndNull(display4, _focus.>>(24).&(63))
              }
              case 6 => {
                display1 = copyOfAndNull(display1, _focus.>>(6).&(63));
                display2 = copyOfAndNull(display2, _focus.>>(12).&(63));
                display3 = copyOfAndNull(display3, _focus.>>(18).&(63));
                display4 = copyOfAndNull(display4, _focus.>>(24).&(63));
                display5 = copyOfAndNull(display5, _focus.>>(30).&(63))
              }
            };
            final private[immutable] def copyDisplaysAndStabilizeDisplayPath(_depth: Int, _focus: Int): Unit = _depth match {
              case 1 => ()
              case 2 => {
                val d1: Array[AnyRef] = copyOf(display1);
                d1.update(_focus.>>(6).&(63), display0);
                display1 = d1
              }
              case 3 => {
                val d1: Array[AnyRef] = copyOf(display1);
                d1.update(_focus.>>(6).&(63), display0);
                display1 = d1;
                val d2: Array[AnyRef] = copyOf(display2);
                d2.update(_focus.>>(12).&(63), d1);
                display2 = d2
              }
              case 4 => {
                val d1: Array[AnyRef] = copyOf(display1);
                d1.update(_focus.>>(6).&(63), display0);
                display1 = d1;
                val d2: Array[AnyRef] = copyOf(display2);
                d2.update(_focus.>>(12).&(63), d1);
                display2 = d2;
                val d3: Array[AnyRef] = copyOf(display3);
                d3.update(_focus.>>(18).&(63), d2);
                display3 = d3
              }
              case 5 => {
                val d1: Array[AnyRef] = copyOf(display1);
                d1.update(_focus.>>(6).&(63), display0);
                display1 = d1;
                val d2: Array[AnyRef] = copyOf(display2);
                d2.update(_focus.>>(12).&(63), d1);
                display2 = d2;
                val d3: Array[AnyRef] = copyOf(display3);
                d3.update(_focus.>>(18).&(63), d2);
                display3 = d3;
                val d4: Array[AnyRef] = copyOf(display4);
                d4.update(_focus.>>(24).&(63), d3);
                display4 = d4
              }
              case 6 => {
                val d1: Array[AnyRef] = copyOf(display1);
                d1.update(_focus.>>(6).&(63), display0);
                display1 = d1;
                val d2: Array[AnyRef] = copyOf(display2);
                d2.update(_focus.>>(12).&(63), d1);
                display2 = d2;
                val d3: Array[AnyRef] = copyOf(display3);
                d3.update(_focus.>>(18).&(63), d2);
                display3 = d3;
                val d4: Array[AnyRef] = copyOf(display4);
                d4.update(_focus.>>(24).&(63), d3);
                display4 = d4;
                val d5: Array[AnyRef] = copyOf(display5);
                d5.update(_focus.>>(30).&(63), d4);
                display5 = d5
              }
            };
            final private[immutable] def copyDisplaysTop(currentDepth: Int, _focusRelax: Int): Unit = {
              var _currentDepth = currentDepth;
              while (_currentDepth.<(this.depth)) 
                {
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
                  };
                  _currentDepth.+=(1)
                }
              
            };
            final private[immutable] def stabilizeDisplayPath(_depth: Int, _focus: Int): Unit = if ((1).<(_depth))
              {
                val d1 = display1;
                d1.update(_focus.>>(6).&(63), display0);
                if ((2).<(_depth))
                  {
                    val d2 = display2;
                    d2.update(_focus.>>(12).&(63), d1);
                    if ((3).<(_depth))
                      {
                        val d3 = display3;
                        d3.update(_focus.>>(18).&(63), d2);
                        if ((4).<(_depth))
                          {
                            val d4 = display4;
                            d4.update(_focus.>>(24).&(63), d3);
                            if (_depth.==(6))
                              display5.update(_focus.>>(30).&(63), d4)
                            else
                              ()
                          }
                        else
                          ()
                      }
                    else
                      ()
                  }
                else
                  ()
              }
            else
              ();
            private[immutable] def cleanTopTake(cutIndex: Int): Unit = this.depth match {
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
            private[immutable] def cleanTopDrop(cutIndex: Int): Unit = this.depth match {
              case 2 => if (cutIndex.>>(6).==(display1.length.-(2)))
                {
                  display1 = null;
                  this.depth = 1
                }
              else
                this.depth = 2
              case 3 => if (cutIndex.>>(12).==(display2.length.-(2)))
                {
                  display2 = null;
                  if (cutIndex.>>(6).==(display1.length.-(2)))
                    {
                      display1 = null;
                      this.depth = 1
                    }
                  else
                    this.depth = 2
                }
              else
                this.depth = 3
              case 4 => if (cutIndex.>>(18).==(display3.length.-(2)))
                {
                  display3 = null;
                  if (cutIndex.>>(12).==(display2.length.-(2)))
                    {
                      display2 = null;
                      if (cutIndex.>>(6).==(display1.length.-(2)))
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
              case 5 => if (cutIndex.>>(24).==(display4.length.-(2)))
                {
                  display4 = null;
                  if (cutIndex.>>(18).==(display3.length.-(2)))
                    {
                      display3 = null;
                      if (cutIndex.>>(12).==(display2.length.-(2)))
                        {
                          display2 = null;
                          if (cutIndex.>>(6).==(display1.length.-(2)))
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
              case 6 => if (cutIndex.>>(30).==(display5.length.-(2)))
                {
                  display5 = null;
                  if (cutIndex.>>(24).==(display4.length.-(2)))
                    {
                      display4 = null;
                      if (cutIndex.>>(18).==(display3.length.-(2)))
                        {
                          display3 = null;
                          if (cutIndex.>>(12).==(display2.length.-(2)))
                            {
                              display2 = null;
                              if (cutIndex.>>(6).==(display1.length.-(2)))
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
            final private[immutable] def copyOf(array: Array[AnyRef]) = {
              assert(array.!=(null));
              val len = array.length;
              val newArray = new Array[AnyRef](len);
              System.arraycopy(array, 0, newArray, 0, len);
              newArray
            };
            final private[immutable] def copyOf(array: Array[AnyRef], numElements: Int, newSize: Int) = {
              assert(array.!=(null));
              assert((0).<=(numElements));
              assert(numElements.<=(newSize));
              assert(numElements.<=(array.length));
              val newArray = new Array[AnyRef](newSize);
              System.arraycopy(array, 0, newArray, 0, numElements);
              newArray
            };
            final private[immutable] def copyOfAndNull(array: Array[AnyRef], nullIndex: Int) = {
              assert(array.!=(null));
              assert((0).<=(nullIndex));
              assert(nullIndex.<=(array.length));
              val len = array.length;
              val newArray = new Array[AnyRef](len);
              System.arraycopy(array, 0, newArray, 0, len.-(1));
              newArray.update(nullIndex, null);
              val sizes = array(len.-(1)).asInstanceOf[Array[Int]];
              if (sizes.!=(null))
                newArray.update(len.-(1), makeTransientSizes(sizes, nullIndex))
              else
                ();
              newArray
            };
            final private def makeNewRoot0(node: Array[AnyRef]) = {
              val newRoot = new Array[AnyRef](3);
              newRoot.update(0, node);
              val dLen = node.length;
              val dSizes = node(dLen.-(1));
              if (dSizes.!=(null))
                {
                  val newRootSizes = new Array[Int](2);
                  val dSize = dSizes.asInstanceOf[Array[Int]](dLen.-(2));
                  newRootSizes.update(0, dSize);
                  newRootSizes.update(1, dSize);
                  newRoot.update(2, newRootSizes)
                }
              else
                ();
              newRoot
            };
            final private def makeNewRoot1(node: Array[AnyRef], currentDepth: Int) = {
              val dSize = treeSize(node, currentDepth.-(1));
              val newRootSizes = new Array[Int](2);
              newRootSizes.update(1, dSize);
              val newRoot = new Array[AnyRef](3);
              newRoot.update(1, node);
              newRoot.update(2, newRootSizes);
              newRoot
            };
            final private[immutable] def makeTransientSizes(oldSizes: Array[Int], transientBranchIndex: Int) = {
              val newSizes = new Array[Int](oldSizes.length);
              var delta = oldSizes(transientBranchIndex);
              if (transientBranchIndex.>(0))
                {
                  delta.-=(oldSizes(transientBranchIndex.-(1)));
                  if (oldSizes.eq(newSizes).`unary_!`)
                    System.arraycopy(oldSizes, 0, newSizes, 0, transientBranchIndex)
                  else
                    ()
                }
              else
                ();
              var i = transientBranchIndex;
              val len = newSizes.length;
              while (i.<(len)) 
                {
                  newSizes.update(i, oldSizes(i).-(delta));
                  i.+=(1)
                }
              ;
              newSizes
            };
            final private def copyAndIncRightRoot(node: Array[AnyRef], transient: Boolean, currentLevel: Int) = {
              val len = node.length;
              val newRoot = copyOf(node, len.-(1), len.+(1));
              val oldSizes = node(len.-(1)).asInstanceOf[Array[Int]];
              if (oldSizes.!=(null))
                {
                  val newSizes = new Array[Int](len);
                  System.arraycopy(oldSizes, 0, newSizes, 0, len.-(1));
                  if (transient)
                    newSizes.update(len.-(1), (1).<<((6).*(currentLevel)))
                  else
                    ();
                  newSizes.update(len.-(1), newSizes(len.-(2)));
                  newRoot.update(len, newSizes)
                }
              else
                ();
              newRoot
            };
            final private def copyAndIncLeftRoot(node: Array[AnyRef], transient: Boolean, currentLevel: Int) = {
              val len = node.length;
              val newRoot = new Array[AnyRef](len.+(1));
              System.arraycopy(node, 0, newRoot, 1, len.-(1));
              val oldSizes = node(len.-(1));
              val newSizes = new Array[Int](len);
              if (oldSizes.!=(null))
                if (transient)
                  System.arraycopy(oldSizes, 1, newSizes, 2, len.-(2))
                else
                  System.arraycopy(oldSizes, 0, newSizes, 1, len.-(1))
              else
                {
                  val subTreeSize = (1).<<((6).*(currentLevel));
                  var acc = 0;
                  var i = 1;
                  while (i.<(len.-(1))) 
                    {
                      acc.+=(subTreeSize);
                      newSizes.update(i, acc);
                      i.+=(1)
                    }
                  ;
                  newSizes.update(i, acc.+(treeSize(node(node.length.-(2)).asInstanceOf[Array[AnyRef]], currentLevel)))
                };
              newRoot.update(len, newSizes);
              newRoot
            };
            final private[immutable] def withComputedSizes1(node: Array[AnyRef]) = {
              var i = 0;
              var acc = 0;
              val end = node.length.-(1);
              if (end.>(1))
                {
                  val sizes = new Array[Int](end);
                  while (i.<(end)) 
                    {
                      acc.+=(node(i).asInstanceOf[Array[AnyRef]].length);
                      sizes.update(i, acc);
                      i.+=(1)
                    }
                  ;
                  if (sizes(end.-(2)).!=(end.-(1).<<(6)))
                    node.update(end, sizes)
                  else
                    ()
                }
              else
                ();
              node
            };
            final private[immutable] def withComputedSizes(node: Array[AnyRef], currentDepth: Int) = {
              assert(node.!=(null));
              assert((1).<(currentDepth));
              var i = 0;
              var acc = 0;
              val end = node.length.-(1);
              if (end.>(1))
                {
                  val sizes = new Array[Int](end);
                  while (i.<(end)) 
                    {
                      acc.+=(treeSize(node(i).asInstanceOf[Array[AnyRef]], currentDepth.-(1)));
                      sizes.update(i, acc);
                      i.+=(1)
                    }
                  ;
                  if (notBalanced(node, sizes, currentDepth, end))
                    node.update(end, sizes)
                  else
                    ()
                }
              else
                if (end.==(1).&&(currentDepth.>(2)))
                  {
                    val child = node(0).asInstanceOf[Array[AnyRef]];
                    val childSizes = child(child.length.-(1)).asInstanceOf[Array[Int]];
                    if (childSizes.!=(null))
                      if (childSizes.length.!=(1))
                        {
                          val sizes = new Array[Int](1);
                          sizes.update(0, childSizes(childSizes.length.-(1)));
                          node.update(end, sizes)
                        }
                      else
                        node.update(end, childSizes)
                    else
                      ()
                  }
                else
                  ();
              node
            };
            final private def withRecomputedSizes(node: Array[AnyRef], currentDepth: Int, branchToUpdate: Int) = {
              assert(node.!=(null));
              assert((1).<(currentDepth));
              val end = node.length.-(1);
              val oldSizes = node(end).asInstanceOf[Array[Int]];
              if (oldSizes.!=(null))
                {
                  val newSizes = new Array[Int](end);
                  val delta = treeSize(node(branchToUpdate).asInstanceOf[Array[AnyRef]], currentDepth.-(1));
                  if (branchToUpdate.>(0))
                    System.arraycopy(oldSizes, 0, newSizes, 0, branchToUpdate)
                  else
                    ();
                  var i = branchToUpdate;
                  while (i.<(end)) 
                    {
                      newSizes.update(i, oldSizes(i).+(delta));
                      i.+=(1)
                    }
                  ;
                  if (notBalanced(node, newSizes, currentDepth, end))
                    node.update(end, newSizes)
                  else
                    ()
                }
              else
                ();
              node
            };
            @inline final private def notBalanced(node: Array[AnyRef], sizes: Array[Int], currentDepth: Int, end: Int) = sizes(end.-(2)).!=(end.-(1).<<((6).*(currentDepth.-(1)))).||(currentDepth.>(2).&&({
              val last = node(end.-(1)).asInstanceOf[Array[AnyRef]];
              last(last.length.-(1)).!=(null)
            }));
            final private def treeSize(tree: Array[AnyRef], currentDepth: Int) = {
              def treeSizeRec(node: Array[AnyRef], currentDepth: Int, acc: Int): Int = if (currentDepth.==(1))
                acc.+(node.length)
              else
                {
                  val treeSizes = node(node.length.-(1)).asInstanceOf[Array[Int]];
                  if (treeSizes.!=(null))
                    acc.+(treeSizes(treeSizes.length.-(1)))
                  else
                    {
                      val len = node.length;
                      treeSizeRec(node(len.-(2)).asInstanceOf[Array[AnyRef]], currentDepth.-(1), acc.+(len.-(2).*((1).<<((6).*(currentDepth.-(1))))))
                    }
                };
              treeSizeRec(tree, currentDepth, 0)
            };
            final private[immutable] def getElem(index: Int, xor: Int): A = if (xor.<(64))
              getElem0(display0, index)
            else
              if (xor.<(4096))
                getElem1(display1, index)
              else
                if (xor.<(262144))
                  getElem2(display2, index)
                else
                  if (xor.<(16777216))
                    getElem3(display3, index)
                  else
                    if (xor.<(1073741824))
                      getElem4(display4, index)
                    else
                      if (xor.<(16))
                        getElem5(display5, index)
                      else
                        throw new IllegalArgumentException();
            final private def getElem0(block: Array[AnyRef], index: Int): A = display0(index.&(63)).asInstanceOf[A];
            final private def getElem1(block: Array[AnyRef], index: Int): A = display1(index.>>(6).&(63)).asInstanceOf[Array[AnyRef]](index.&(63)).asInstanceOf[A];
            final private def getElem2(block: Array[AnyRef], index: Int): A = display2(index.>>(12).&(63)).asInstanceOf[Array[AnyRef]](index.>>(6).&(63)).asInstanceOf[Array[AnyRef]](index.&(63)).asInstanceOf[A];
            final private def getElem3(block: Array[AnyRef], index: Int): A = display3(index.>>(18).&(63)).asInstanceOf[Array[AnyRef]](index.>>(12).&(63)).asInstanceOf[Array[AnyRef]](index.>>(6).&(63)).asInstanceOf[Array[AnyRef]](index.&(63)).asInstanceOf[A];
            final private def getElem4(block: Array[AnyRef], index: Int): A = display4(index.>>(24).&(63)).asInstanceOf[Array[AnyRef]](index.>>(18).&(63)).asInstanceOf[Array[AnyRef]](index.>>(12).&(63)).asInstanceOf[Array[AnyRef]](index.>>(6).&(63)).asInstanceOf[Array[AnyRef]](index.&(63)).asInstanceOf[A];
            final private def getElem5(block: Array[AnyRef], index: Int): A = display5(index.>>(30).&(63)).asInstanceOf[Array[AnyRef]](index.>>(24).&(63)).asInstanceOf[Array[AnyRef]](index.>>(18).&(63)).asInstanceOf[Array[AnyRef]](index.>>(12).&(63)).asInstanceOf[Array[AnyRef]](index.>>(6).&(63)).asInstanceOf[Array[AnyRef]](index.&(63)).asInstanceOf[A]
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
          package rrbvector.complete.block64 {
            import scala.collection.immutable.generated.rrbvector.complete.block64._

            import scala.collection.generic.{GenericParTemplate, CanCombineFrom, ParFactory}

            import scala.collection.parallel.{ParSeqLike, Combiner, SeqSplitter}

            import scala.collection.mutable.ArrayBuffer

            class ParRRBVector_c_64_asserted[+A](vector: RRBVector_c_64_asserted[A]) extends ParSeq[A] with GenericParTemplate[A, ParRRBVector_c_64_asserted] with ParSeqLike[A, ParRRBVector_c_64_asserted[A], RRBVector_c_64_asserted[A]] with Serializable {
              override def companion = ParRRBVector_c_64_asserted;
              def this() = {
                this(RRBVector_c_64_asserted.empty[A]);
                ()
              };
              def apply(idx: Int) = vector.apply(idx);
              def length = vector.length;
              def splitter: SeqSplitter[A] = {
                val pit = new ParRRBVectorIterator_c_64_asserted(0, vector.length);
                pit.initIteratorFrom(vector);
                pit
              };
              override def seq: RRBVector_c_64_asserted[A] = vector;
              override def toVector: Vector[A] = vector.toVector;
              class ParRRBVectorIterator_c_64_asserted(_start: Int, _end: Int) extends RRBVectorIterator_c_64_asserted[A](_start, _end) with SeqSplitter[A] {
                final override def remaining: Int = super.remaining;
                def dup: SeqSplitter[A] = {
                  val pit = new ParRRBVectorIterator_c_64_asserted(_end.-(remaining), _end);
                  pit.initIteratorFrom(this);
                  pit
                };
                def split: Seq[ParRRBVectorIterator_c_64_asserted] = {
                  val rem = remaining;
                  if (rem.>=(2))
                    {
                      val _half = rem./(2);
                      val _splitModulo = if (rem.<=(64))
                        1
                      else
                        if (rem.<=(4096))
                          64
                        else
                          if (rem.<=(262144))
                            4096
                          else
                            if (rem.<=(16777216))
                              262144
                            else
                              if (rem.<=(1073741824))
                                16777216
                              else
                                1073741824;
                      val _halfAdjusted = if (_half.>(_splitModulo))
                        _half.-(_half.%(_splitModulo))
                      else
                        if (_splitModulo.<(_end))
                          _splitModulo
                        else
                          _half;
                      psplit(_halfAdjusted, rem.-(_halfAdjusted))
                    }
                  else
                    Seq(this)
                };
                def psplit(sizes: Int*): Seq[ParRRBVectorIterator_c_64_asserted] = {
                  val splitted = new ArrayBuffer[ParRRBVectorIterator_c_64_asserted]();
                  var currentPos = _end.-(remaining);
                  sizes.foreach(((sz) => {
                    val pit = new ParRRBVectorIterator_c_64_asserted(currentPos, currentPos.+(sz));
                    pit.initIteratorFrom(this);
                    splitted.+=(pit);
                    currentPos.+=(sz)
                  }));
                  splitted
                }
              }
            }

            object ParRRBVector_c_64_asserted extends ParFactory[ParRRBVector_c_64_asserted] {
              implicit def canBuildFrom[A]: CanCombineFrom[Coll, A, ParRRBVector_c_64_asserted[A]] = new GenericCanCombineFrom[A]();
              def newBuilder[A]: Combiner[A, ParRRBVector_c_64_asserted[A]] = newCombiner[A];
              def newCombiner[A]: Combiner[A, ParRRBVector_c_64_asserted[A]] = new ParRRBVectorCombinator_c_64_asserted[A]()
            }

            private[immutable] class ParRRBVectorCombinator_c_64_asserted[A] extends Combiner[A, ParRRBVector_c_64_asserted[A]] {
              val builder: RRBVectorBuilder_c_64_asserted[A] = new RRBVectorBuilder_c_64_asserted[A]();
              override def size = builder.endIndex;
              override def result() = new ParRRBVector_c_64_asserted[A](builder.result());
              override def clear() = builder.clear();
              override def +=(elem: A) = {
                builder.+=(elem);
                this
              };
              override def ++=(xs: TraversableOnce[A]) = {
                builder.++=(xs);
                this
              };
              def combine[B <: A, NewTo >: ParRRBVector_c_64_asserted[A]](other: Combiner[B, NewTo]) = if (other.eq(this))
                this
              else
                {
                  val newCombiner = new ParRRBVectorCombinator_c_64_asserted[A]();
                  newCombiner.++=(this.builder.result());
                  newCombiner.++=(other.asInstanceOf[ParRRBVectorCombinator_c_64_asserted[A]].builder.result());
                  newCombiner
                }
            }
          }
        }
      }
    }
  }
}