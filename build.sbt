ThisBuild / scalaVersion := "3.8.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "photoed",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect"       % "3.7.0",
      "org.scalameta" %% "munit"             % "1.3.3" % Test,
      "org.typelevel" %% "munit-cats-effect" % "2.2.0" % Test
    )
  )
