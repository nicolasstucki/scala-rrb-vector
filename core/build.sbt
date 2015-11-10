name := "scala-rrb-vector"

version := "0.1.1-SNAPSHOT"

scalaVersion := "2.11.6"

resolvers += "Sonatype OSS Snapshots" at
  "https://oss.sonatype.org/content/repositories/releases"

organization := "io.github.nicolasstucki"

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/nicolasstucki/scala-rrb-vector</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:nicolasstucki/scala-rrb-vector.git</url>
    <connection>scm:git:git@github.com:nicolasstucki/scala-rrb-vector.git</connection>
  </scm>
  <developers>
    <developer>
      <id>nicolas.stucki</id>
      <name>Nicolas Stucki</name>
      <url>http://io.github.nicolasstucki</url>
    </developer>
  </developers>
)
