ThisBuild / scalaVersion := "3.8.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "photoed",

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.7.0"
    )
  )

