import Dependencies._
import sbt._
import sbt.Package.ManifestAttributes

lazy val dependencies =
  Circe.all ++ Enumeratum.all ++ Http4s.all ++ Logging.all ++ Refined.all ++ STTP.all ++ Tapir.all ++ ZIO.all

lazy val plugins = Seq(
  compilerPlugin("org.typelevel" %% "kind-projector"     % "0.11.0" cross CrossVersion.full),
  compilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
//  compilerPlugin("io.tryp"        % "splain"             % "0.5.6" cross CrossVersion.patch),
//  compilerPlugin(scalafixSemanticdb)
)

lazy val thisBuildSettings = inThisBuild(
  Seq(
    scalaVersion := "2.13.3",
    name := "zio-http4s",
    description := "Service to get a shortened url",
    version := "0.1.0-SNAPSHOT",
    organization := "io.allawala",
    organizationName := "allawala",
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    IntegrationTest / parallelExecution := false,
    packageOptions := Seq(ManifestAttributes(("Implementation-Version", (ThisProject / version).value))),
    libraryDependencies ++= dependencies ++ plugins,
    javacOptions ++= Seq("-source", "13", "-target", "11"),
    scalacOptions ++= CompilerOptions.all
  )
)

lazy val `zio-http4s` = project
  .in(file("."))
  .settings(thisBuildSettings)
  .settings(Compile / mainClass := Some("io.allawala.Main"))
  .settings(Defaults.itSettings)
  .configs(IntegrationTest extend Test)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .enablePlugins(AshScriptPlugin)


//**********//
//  DOCKER  //
//**********//

packageName in Docker := packageName.value
dockerBaseImage := "adoptopenjdk/openjdk11:alpine-jre"
dockerExposedPorts := Seq(8080)
dockerRepository := Some("814062369950.dkr.ecr.us-east-1.amazonaws.com")
