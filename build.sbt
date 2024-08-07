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
    case step => List(step)
  }
}

ThisBuild / tlCiJavafmtCheck := true

ThisBuild / githubWorkflowPublishPreamble +=
  WorkflowStep.Use(
    UseRef.Public("typelevel", "await-cirrus", "main"),
    name = Some("Wait for Cirrus CI")
  )

name := "cheshire"

libraryDependencies ++= Seq(
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
  "org.junit.jupiter" % "junit-jupiter-api" % "5.8.2" % Test,
  "org.junit.jupiter" % "junit-jupiter-engine" % "5.8.2" % Test
)

// Java library
crossPaths := false
autoScalaLibrary := false
javacOptions ++= Seq("--enable-preview", "--release", "22")
Compile / compileOrder := CompileOrder.JavaThenScala
Compile / doc / javacOptions -= "-Xlint:all"
