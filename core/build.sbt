name := "scala-rrb-vector-core"

version := "0.1"

scalaVersion := "2.11.2"

resolvers += "Sonatype OSS Snapshots" at
  "https://oss.sonatype.org/content/repositories/releases"

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
