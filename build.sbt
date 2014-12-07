name := "scala-rrb-vector"

version := "0.1"

scalaVersion := "2.11.2"

resolvers += "Sonatype OSS Snapshots" at
  "https://oss.sonatype.org/content/repositories/releases"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
    "com.storm-enroute" %% "scalameter" % "0.6"
    , "org.scala-miniboxing.plugins" %% "miniboxing-runtime" % "0.4-SNAPSHOT"
)

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

parallelExecution in Test := false

addCompilerPlugin("org.scala-miniboxing.plugins" %% "miniboxing-plugin" % "0.4-SNAPSHOT")

scalacOptions ++= Seq("-optimize", "-P:minibox:warn")
