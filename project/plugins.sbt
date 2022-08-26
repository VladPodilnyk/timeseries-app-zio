// gRPC related stuff
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.2")
libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.11"

// other useful things
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.9")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.10.1")


