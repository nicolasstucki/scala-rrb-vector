package org.scalameter
package vectors


import Key._
import scala.collection.immutable.rrbvector

object RunVetcorBenchmarks extends VetcorBenchmarks

class VetcorBenchmarks extends PerformanceTest.OfflineReport with Collections {

    val minHeight = 1
    val maxHeight = 3
    val points = 64

    performance of "/temp/vectors" config(
      exec.benchRuns -> 32,
      exec.independentSamples -> 4
      ) in {

        for (height <- minHeight to maxHeight) {

            val from = math.pow(32, height - 1).toInt + 1
            val to = math.pow(32, height).toInt
            val by = math.max(math.pow(32, height).toInt / points, 1)
            var sideeffect = 0
            val v1 = Vector(42)


            measure method "apply" config(
              exec.minWarmupRuns -> (if (height <= 2) 1000 else 200).asInstanceOf[Int],
              exec.maxWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int]
              ) in {

                performance of "3x iterations" in {
                    performance of s"Height $height" in {

                        using(vectors(from, to, by)) curve ("Vector") in { xs =>
                            var i = 0
                            var sum = 0
                            val len = xs.length
                            val until = len * 3
                            while (i < len) {
                                sum += xs.apply(i % len)
                                i += 1
                            }
                            sideeffect = sum
                        }

                        using(rbvectors(from, to, by)) curve ("rbVector") in { xs =>
                            var i = 0
                            var sum = 0
                            val len = xs.length
                            val until = len * 3
                            while (i < until) {
                                sum += xs.apply(i % len)
                                i += 1
                            }
                            sideeffect = sum
                        }
                    }
                }

                performance of "3x reverse iteration" in {
                    performance of s"Height $height" in {
                        using(vectors(from, to, by)) curve ("Vector") in { xs =>
                            val len = xs.length
                            var i = len * 3 - 1
                            var sum = 0
                            while (i >= 0) {
                                sum += xs.apply(i % len)
                                i -= 1
                            }
                            sideeffect = sum
                        }

                        using(rbvectors(from, to, by)) curve ("rbVector") in { xs =>
                            val len = xs.length
                            var i = len * 3 - 1
                            var sum = 0
                            while (i >= 0) {
                                sum += xs.apply(i % len)
                                i -= 1
                            }
                            sideeffect = sum
                        }
                    }
                }

                performance of "iteration 1k elements" in {
                    val end = 1000
                    performance of s"Height $height" in {
                        using(vectors(from, to, by)) curve ("Vector") in { xs =>
                            var i = 0
                            var sum = 0
                            val len = xs.length
                            val until = end
                            while (i < until) {
                                sum += xs.apply(i % len)
                                i += 1
                            }
                            sideeffect = sum
                        }

                        using(rbvectors(from, to, by)) curve ("rbVector") in { xs =>
                            var i = 0
                            var sum = 0
                            val len = xs.length
                            val until = end
                            while (i < until) {
                                sum += xs.apply(i % len)
                                i += 1
                            }
                            sideeffect = sum
                        }
                    }
                }

            }


            /*
            measure method "updated" config(
              exec.minWarmupRuns -> (if (height <= 2) 1000 else 200).asInstanceOf[Int],
              exec.maxWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int]
              ) in {

                performance of s"Height $height" in {
                    using(vectors(from, to, by)) curve ("Vector") in { xs =>
                        var i = 0
                        var sum = 0
                        val len = xs.length
                        val until = len * 3
                        while (i < until) {
                            xs.updated(i % len, i)
                            i += 1
                        }
                        sideeffect = sum
                    }
                    // TODO rbVector updated is currently using the builder to reconstruct the whole vector
                    using(rbvectors(from, to, by)) curve ("rbVector") in { xs =>
                        var i = 0
                        var sum = 0
                        val len = xs.length
                        val until = len * 3
                        while (i < until) {
                            xs.updated(i % len, i)
                            i += 1
                        }
                        sideeffect = sum
                    }
                }
            }
            */

            measure method "append" config(
              exec.minWarmupRuns -> (if (height <= 2) 1000 else 200).asInstanceOf[Int],
              exec.maxWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int]
              ) in {

                performance of s"Height $height" in {

                    using(sizes(from, to, by)) curve ("Vector") in { len =>
                        var i = 0
                        var vector = Vector.empty[Int]
                        while (i < len) {
                            vector = vector :+ i
                            i += 1
                        }
                        sideeffect = i
                    }

                    using(sizes(from, to, by)) curve ("rbVector") in { len =>
                        var i = 0
                        var vector = rrbvector.Vector.empty[Int]
                        while (i < len) {
                            vector = vector :+ i
                            i += 1
                        }
                        sideeffect = i
                    }

                }
            }

            measure method "prepend" config(
              exec.minWarmupRuns -> (if (height <= 2) 1000 else 200).asInstanceOf[Int],
              exec.maxWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int]
              ) in {

                performance of s"Height $height" in {
                    using(sizes(from, to, by)) curve ("Vector") in { len =>
                        var i = 0
                        var vector = Vector.empty[Int]
                        while (i < len) {
                            vector = i +: vector
                            i += 1
                        }
                        sideeffect = i
                    }

                    using(sizes(from, to, by)) curve ("rbVector") in { len =>
                        var i = 0
                        var vector = rrbvector.Vector.empty[Int]
                        while (i < len) {
                            vector = i +: vector
                            i += 1
                        }
                        sideeffect = i
                    }
                }
            }



            measure method "foreach" config(
              exec.minWarmupRuns -> (if (height <= 2) 1000 else 200).asInstanceOf[Int],
              exec.maxWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int]
              ) in {
                performance of s"Height $height" in {
                    using(vectors(from, to, by)) curve ("Vector") in { xs =>
                        var sum = 0
                        xs.foreach(sum += _)
                    }

                    using(rbvectors(from, to, by)) curve ("rbVector") in { xs =>
                        var sum = 0
                        xs.foreach(sum += _)
                    }
                }
            }


            measure method "map" config(
              exec.minWarmupRuns -> (if (height <= 2) 1000 else 200).asInstanceOf[Int],
              exec.maxWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int]
              ) in {
                performance of s"Height $height" in {
                    using(vectors(from, to, by)) curve ("Vector") in {
                        _.map(_ % 2 == 0)
                    }

                    using(rbvectors(from, to, by)) curve ("rbVector") in {
                        _.map(_ % 2 == 0)
                    }
                }
            }

            measure method "++" config(
              exec.minWarmupRuns -> (if (height <= 2) 2000 else 500).asInstanceOf[Int],
              exec.maxWarmupRuns -> (if (height <= 2) 3000 else 1000).asInstanceOf[Int]
              ) in {

                performance of "concat same size" config(
                  exec.minWarmupRuns -> (if (height <= 2) 3000 else 1000).asInstanceOf[Int],
                  exec.maxWarmupRuns -> (if (height <= 2) 5000 else 3000).asInstanceOf[Int]
                  ) in {
                    performance of s"Height $height" in {
                        using(vectors(from, to, by) map (v => (v, Vector(v.iterator)))) curve ("Vector") in {
                            case (v1, v2) => v1 ++ v2
                        }

                        using(rbvectors(from, to, by) map (v => (v, rrbvector.Vector(v.iterator)))) curve ("rbVector") in {
                            case (v1, v2) => v1 ++ v2
                        }
                    }
                }

                performance of "concat _ ++ Vector(42)" in {
                    performance of s"Height $height" in {
                        using(vectors(from, to, by)) curve ("Vector") in {
                            _ ++ v1
                        }

                        using(rbvectors(from, to, by)) curve ("rbVector") in {
                            _ ++ v1
                        }
                    }
                }

                performance of "concat Vector(42) ++ _" in {
                    performance of s"Height $height" in {
                        using(vectors(from, to, by)) curve ("Vector") in {
                            v1 ++ _
                        }

                        using(rbvectors(from, to, by)) curve ("rbVector") in {
                            v1 ++ _
                        }
                    }
                }

            }

        }

    }
}