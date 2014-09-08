name := """kcal"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(MustacheKeys.playSupport := true)

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  ws,
  "com.github.nscala-time" %% "nscala-time" % "1.4.0"
)
