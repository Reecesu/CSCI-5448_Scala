name := """scala-backend"""
organization := "CU-boulder"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.10"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test"

// https://www.playframework.com/documentation/2.8.x/JavaDependencyInjection
libraryDependencies += guice
// Some machines get an error on first execution to the back end. believed to be due to a version of guice used
// https://stackoverflow.com/questions/12875685/how-to-install-guice-in-scala-sbt
libraryDependencies += "com.google.inject" % "guice" % "3.0"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "ch.megard" %% "akka-http-cors" % "1.2.0"
// // org.scalatestplus.play has a dependency already
// libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "CU-boulder.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "CU-boulder.binders._"

javacOptions ++= Seq("-source", "11") 



// mainClass in assembly := Some("play.core.server.ProdServerStart")
// fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)
// 
// assemblyMergeStrategy in assembly := {
//   case manifest if manifest.contains("MANIFEST.MF") =>
//     // We don't need manifest files since sbt-assembly will create
//     // one with the given settings
//     MergeStrategy.discard
//   case referenceOverrides if referenceOverrides.contains("reference-overrides.conf") =>
//     // Keep the content for all reference-overrides.conf files
//     MergeStrategy.concat
//   case x =>
//     // For all the other files, use the default sbt-assembly merge strategy
//     val oldStrategy = (assemblyMergeStrategy in assembly).value
//     oldStrategy(x)
// }
