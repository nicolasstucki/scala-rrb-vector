name := "scala-rrb-vector"

version := "0.1"

scalaVersion := "2.11.4"

resolvers ++= Seq(Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"))

libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
    "com.storm-enroute" %% "scalameter" % "0.6"
)

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

parallelExecution in Test := false

// miniboxing:

libraryDependencies += "org.scala-miniboxing.plugins" %% "miniboxing-runtime" % "0.4-SNAPSHOT"

addCompilerPlugin("org.scala-miniboxing.plugins" %% "miniboxing-plugin" % "0.4-SNAPSHOT")

scalacOptions ++= Seq("-P:minibox:warn", "-P:minibox:mark-all", /* temporary */ "-P:minibox:Yrewire-functionX-application")
