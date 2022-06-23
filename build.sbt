ThisBuild / tlBaseVersion := "0.0"

ThisBuild / organization := "com.armanbilge"
ThisBuild / organizationName := "Arman Bilge"
ThisBuild / developers += tlGitHubDev("armanbilge", "Arman Bilge")
ThisBuild / startYear := Some(2021)

ThisBuild / tlUntaggedAreSnapshots := false
ThisBuild / tlSonatypeUseLegacyHost := false

val Scala3 = "3.1.2"
ThisBuild / crossScalaVersions := Seq(Scala3)

val CatsVersion = "2.8.0"
val CatsEffectVersion = "3.3.12"
val DisciplineVersion = "1.5.1"
val RefinedVersion = "0.9.29"
val ScodecBitsVersion = "1.1.34"
val Specs2Version = "5.0.0"
val DisciplineSpecs2Version = "2.0-44-19f6d7f"

ThisBuild / scalacOptions ++= Seq("-new-syntax", "-indent", "-source:future")

lazy val root = tlCrossRootProject.aggregate(core, likelihood, likelihoodLaws)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "cheshire",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core" % CatsVersion,
      "org.typelevel" %%% "cats-laws" % CatsVersion % Test,
      "org.typelevel" %%% "discipline-specs2" % DisciplineSpecs2Version % Test,
      "org.specs2" %%% "specs2-core" % Specs2Version % Test,
      "org.specs2" %%% "specs2-scalacheck" % Specs2Version % Test
    )
  )
  .jvmSettings(
    fork := true
  )

lazy val likelihood = project
  .in(file("likelihood"))
  .settings(
    name := "cheshire-likelihood",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core" % CatsVersion,
      "org.scodec" %%% "scodec-bits" % ScodecBitsVersion,
      "org.typelevel" %%% "cats-effect-kernel" % CatsEffectVersion
    )
  )
  .dependsOn(core.jvm)

lazy val likelihoodLaws = project
  .in(file("likelihood-laws"))
  .settings(
    name := "cheshire-likelihood-laws",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "algebra" % CatsVersion,
      "org.typelevel" %%% "cats-kernel-laws" % CatsVersion,
      "org.typelevel" %%% "discipline-core" % DisciplineVersion,
      "org.typelevel" %%% "cats-effect-laws" % CatsEffectVersion,
      "eu.timepit" %%% "refined-scalacheck" % RefinedVersion
    )
  )
  .dependsOn(likelihood)
