package scala.collection.immutable.vectorbenchmarks.genericbenchmarks

import scala.collection.parallel.ForkJoinTaskSupport

object ParSupport {

    lazy val pool1 = new scala.concurrent.forkjoin.ForkJoinPool(1)
    lazy val pool2 = new scala.concurrent.forkjoin.ForkJoinPool(2)
    lazy val pool4 = new scala.concurrent.forkjoin.ForkJoinPool(4)
    lazy val pool8 = new scala.concurrent.forkjoin.ForkJoinPool(8)

    def getTaskSupport(n: Int) = n match {
        case 1 => new ForkJoinTaskSupport(pool1)
        case 2 => new ForkJoinTaskSupport(pool2)
        case 4 => new ForkJoinTaskSupport(pool4)
        case 8 => new ForkJoinTaskSupport(pool8)
    }

}
