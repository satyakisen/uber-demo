name := """uber-web-app"""
organization := "org.ssen"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.0",
  "org.webjars" % "vue" % "2.1.3",
  "org.webjars" % "ionicons" % "2.0.1",
  "org.webjars.npm" % "google-polyline" % "1.0.0"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "org.ssen.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "org.ssen.binders._"
