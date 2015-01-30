import sbt._
import Keys._

object RRBVectorBuild extends Build {
 
 	lazy val root = Project(id = "scala-rrb-vector",
                            base = file(".")) dependsOn(coreRRBVector % "test->compile") aggregate(coreRRBVector)


    lazy val coreRRBVector = Project(id = "scala-rrb-vector-core",
                           base = file("core"))

}