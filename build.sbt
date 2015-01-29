name := "scala-rrb-vector"

version := "0.1"

scalaVersion := "2.11.2"

resolvers += "Sonatype OSS Snapshots" at
  "https://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
    "com.storm-enroute" %% "scalameter" % "0.6",
    "de.sciss" %% "fingertree" % "1.5.2+"
)

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

parallelExecution in Test := false

// TODO: Try to attach collection docs
//scalacOptions in (Compile,doc) ++= Seq(
//    "-groups",
//    "-implicits",
//    s"-doc-external-doc:${scalaInstance.value.libraryJar}#http://www.scala-lang.org/api/${scalaVersion.value}/"
//)
//
//apiMappings += (scalaInstance.value.libraryJar -> url(s"http://www.scala-lang.org/api/${scalaVersion.value}/"))
//
//autoAPIMappings := true
//
//apiURL := Some(url(s"http://www.scala-lang.org/api/${scalaVersion.value}/"))
