enablePlugins(JavaAppPackaging)

name := "historical-data-analysis-service"
organization := "com.epam"
version := "1.0"
scalaVersion := "3.1.2"

conflictWarning := ConflictWarning.disable

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val circeV    = "0.14.1"
  val zioV      = "2.0.0-RC6"
  val zioHttpV  = "2.0.0-RC7"

  Seq(
    "dev.zio" %% "zio" % zioV,
    "io.circe" %% "circe-core" % circeV,
    "io.circe" %% "circe-generic" % circeV,
    "io.circe" %% "circe-parser" % circeV,
    "io.d11"  %% "zhttp" % zioHttpV
  )//.map(_.cross(CrossVersion.for3Use2_13))
}

Revolver.settings
