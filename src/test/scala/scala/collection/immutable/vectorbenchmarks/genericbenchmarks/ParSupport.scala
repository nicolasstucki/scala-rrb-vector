package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import scala.collection.parallel.ForkJoinTaskSupport

object ParSupport {

    lazy val pool1 = new scala.concurrent.forkjoin.ForkJoinPool(1)
    lazy val pool2 = new scala.concurrent.forkjoin.ForkJoinPool(2)
    lazy val pool4 = new scala.concurrent.forkjoin.ForkJoinPool(4)
    lazy val pool8 = new scala.concurrent.forkjoin.ForkJoinPool(8)
    lazy val pool16 = new scala.concurrent.forkjoin.ForkJoinPool(16)
    lazy val pool32 = new scala.concurrent.forkjoin.ForkJoinPool(32)
    lazy val pool64 = new scala.concurrent.forkjoin.ForkJoinPool(64)

    def getTaskSupport(n: Int) = n match {
        case 1 => new ForkJoinTaskSupport(pool1)
        case 2 => new ForkJoinTaskSupport(pool2)
        case 4 => new ForkJoinTaskSupport(pool4)
        case 8 => new ForkJoinTaskSupport(pool8)
        case 16 => new ForkJoinTaskSupport(pool16)
        case 32 => new ForkJoinTaskSupport(pool32)
        case 64 => new ForkJoinTaskSupport(pool64)
    }

}
