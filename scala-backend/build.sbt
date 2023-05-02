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
// // org.scalatestplus.play has a dependency already
// libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",

libraryDependencies += "ch.megard" %% "akka-http-cors" % "1.2.0"


// https://users.scala-lang.org/t/telling-sbt-to-use-different-jdk-version/4608/3
javacOptions ++= Seq("-source", "11") 


