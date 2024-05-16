ThisBuild / tlBaseVersion := "0.0"

ThisBuild / organization := "com.armanbilge"
ThisBuild / organizationName := "Arman Bilge"
ThisBuild / developers += tlGitHubDev("armanbilge", "Arman Bilge")
ThisBuild / startYear := Some(2024)
ThisBuild / tlSonatypeUseLegacyHost := false

ThisBuild / tlJdkRelease := Some(22)
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("22"))

ThisBuild / githubWorkflowBuild ~= { steps =>
  steps.flatMap {
    case step: WorkflowStep.Sbt if step.commands == List("test") =>
      List(WorkflowStep.Sbt(List("Test/compile"), name = Some("Compile")))
    case step                                                       => List(step)
  }
}

ThisBuild / githubWorkflowPublishPreamble +=
  WorkflowStep.Use(
    UseRef.Public("typelevel", "await-cirrus", "main"),
    name = Some("Wait for Cirrus CI")
  )

name := "cheshire"
libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test
// Java library
crossPaths := false
autoScalaLibrary := false
Compile / compileOrder := CompileOrder.JavaThenScala
