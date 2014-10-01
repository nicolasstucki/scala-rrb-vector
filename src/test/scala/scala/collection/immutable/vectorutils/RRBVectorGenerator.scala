package scala.collection.immutable
package vectorutils

/**
 * Created by nicolasstucki on 30/09/2014.
 */

import scala.util.Random
import rrbvector._

object RRBVectorGenerator {

    case class Config[A](rnd: Random, maxSplitSize: Int, tabFunc: Int => A)

    def vectorOfSize[A](n: Int)(implicit config: Config[A]): RRBVector[A] = n match {
        case 0 => RRBVector.empty[A]
        case n if n > 0 && config.maxSplitSize < n =>
            val mid = config.rnd.nextInt(n)
            val v1 = vectorOfSize(mid)
            val v2 = vectorOfSize(n - mid)
            val v3 = v1 ++ v2
            v3
        case n if n > 0 && config.maxSplitSize >= n => RRBVector.tabulate(n)(config.tabFunc)
        case _ => throw new IllegalArgumentException()
    }


}
