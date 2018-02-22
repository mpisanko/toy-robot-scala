import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "rea.robot",
      scalaVersion := "2.12.4",
      version      := "0.0.1",
      assemblyJarName in assembly := "toy-robot.jar"
    )),
    name := "toy-robot",
    libraryDependencies += scalaTest % Test
  )
