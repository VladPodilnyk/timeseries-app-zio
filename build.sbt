ThisBuild / scalaVersion     := "3.1.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "vpodlnk"
ThisBuild / organizationName := "vpodlnk"

val zio         = "2.0.1"
val zioHttp     = "2.0.0-RC10"
val zioJson     = "0.3.0-RC5"
val zioLogging  = "2.1.0"
val zioCats     = "22.0.0.0"
val doobie      = "0.13.4"
val gprcVersion = "1.34.0"
val slf4j       = "1.7.36"
val requests    = "0.7.0"
val grpcNetty   = "1.34.0"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val protobuf = project
  .in(file("protobuf"))
  .settings(
    Compile / PB.targets := Seq(
      scalapb.gen(grpc = true) -> (Compile / sourceManaged).value
    ),
    Compile / PB.protoSources := Seq(
      (ThisBuild / baseDirectory).value / "protobuf" / "src" / "main" / "protobuf"
    ),
    libraryDependencies ++= Seq(
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
      "io.grpc" % "grpc-netty" % grpcNetty,
    ),
  )

lazy val backend = project
  .in(file("backend"))
  .settings(
    name := "timeseries-backend",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zio,
      "dev.zio" %% "zio-streams" % zio,
      "dev.zio" %% "zio-test" % zio % Test,
      "io.d11" %% "zhttp" % zioHttp,
      "dev.zio" %% "zio-logging-slf4j" % zioLogging,
      "org.slf4j" % "slf4j-api" % slf4j,
      "org.slf4j" % "slf4j-simple" % slf4j,
      "dev.zio" %% "zio-json" % zioJson,
      "dev.zio" %% "zio-interop-cats" % zioCats,
      "org.tpolecat" %% "doobie-core" % doobie,
      "org.tpolecat" %% "doobie-postgres" % doobie,
      "org.tpolecat" %% "doobie-hikari" % doobie,
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "utf8",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-unchecked",
      "-Xfatal-warnings",
    ),
    Test / fork := true,
  )
  .dependsOn(protobuf)

lazy val root = project
  .in(file("."))
  .aggregate(backend)
  .enablePlugins(DockerPlugin)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name            := "timeseries-app",
    dockerBaseImage := "adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.6_10",
    dockerBuildCommand := {
      if (sys.props("os.arch") != "amd64") {
        // use buildx with platform to build supported amd64 images on other CPU architectures
        // this may require that you have first run 'docker buildx create' to set docker buildx up
        dockerExecCommand.value ++ Seq("buildx", "build", "--platform=linux/amd64", "--load") ++ dockerBuildOptions.value :+ "."
      } else dockerBuildCommand.value
    },
    dockerExposedPorts := Seq(8080),
  )
