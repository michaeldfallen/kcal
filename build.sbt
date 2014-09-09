name := """kcal"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(MustacheKeys.playSupport := true)

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  ws,
  cache,
  "com.github.nscala-time" %% "nscala-time" % "1.4.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)
