name := "fs2-practice"

version := "0.1"

scalaVersion := "2.13.3"
val circeVersion = "0.13.0"
libraryDependencies ++= Seq(
  "co.fs2" %% "fs2-core" % "2.4.0",
  "co.fs2" %% "fs2-io" % "2.4.0") ++ Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)