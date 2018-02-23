import Dependencies._
lazy val IntegrationTest = config("it") extend(Test)
lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(
    inThisBuild(List(
      organization := "rea.robot",
      scalaVersion := "2.12.4",
      version      := "0.0.1",
      assemblyJarName in assembly := "toy-robot.jar"
    )),
    name := "toy-robot",
    libraryDependencies += scalaTest % "it,test",
    Defaults.itSettings
  )
