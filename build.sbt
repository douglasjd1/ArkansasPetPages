name := """continents"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice

libraryDependencies += javaJpa
libraryDependencies += javaJdbc
libraryDependencies += "org.hibernate" % "hibernate-core" % "5.3.1.Final"
libraryDependencies += "org.mariadb.jdbc" % "mariadb-java-client" % "2.2.5"

libraryDependencies += "org.webjars" % "bootstrap" % "4.1.0"
libraryDependencies += "org.webjars.bowergithub.chartjs" % "chart.js" % "2.7.2"
libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.11.351"

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.196"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))