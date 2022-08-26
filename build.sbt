ThisBuild / scalaVersion := "3.1.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "vpodlnk"
ThisBuild / organizationName := "vpodlnk"

val zio = "2.0.1"
val zioHttp = "2.0.0-RC5"
val zioJson = "0.3.0-RC5"
//val zioLogging = "2.1.0"
val doobie = "1.0.0-RC1"
val gprcVersion = "1.34.0"
val slf4j = "1.7.36"
val requests = "0.7.0"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val protobuf = project
  .in(file("protobuf"))
  .settings(
    Compile / PB.targets := Seq(
      scalapb.gen(grpc = true) -> (Compile / sourceManaged).value,
    ),
    Compile / PB.protoSources := Seq(
      (ThisBuild / baseDirectory).value / "protobuf" / "src" / "main" / "protobuf"
    ),
    libraryDependencies ++= Seq(
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
      "io.grpc" % "grpc-netty" % "1.34.0"
    )
  )

lazy val backend = project
  .in(file("backend"))
  .settings(
    name := "timeseries-backend",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zio,
      "dev.zio" %% "zio-streams" % zio,
      //("dev.zio" %% "zio-macros" % zio).cross(CrossVersion.for3Use2_13),
      //"dev.zio" %% "zio.test" % zio % Test,
      // "dev.zio" %% "zio.test-sbt" % zio % Test,
      // "dev.zio" %% "zio-test-magnolia" % zio % Test,
      "io.d11" %% "zhttp" % zioHttp,
//      "dev.zio" %% "zio-logging-slf4j" % zioLogging,
//      "org.slf4j" % "slf4j-api" % slf4j,
//      "org.slf4j" % "slf4j-simple" % slf4j,
      "dev.zio" %% "zio-json" % zioJson,
      //"com.lihaoyi" %% "requests" % V.requests,
      // "org.tpolecat" %% "doobie-core" % V.doobie,
      // "org.tpolecat" %% "doobie-postgres" % V.doobie,
      // "org.tpolecat" %% "doobie-hikari" % V.doobie
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
    //testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(protobuf)

lazy val root = project
  .in(file("."))
  .aggregate(protobuf, backend)
  .settings(name := "timeseries-app")