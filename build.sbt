import AssemblyKeys._

name := "Simple ThingSpan"

version := "1.0"

scalaVersion := "2.10.6"

mainClass in assembly := Some("com.objy.thingspan.examples.simple.SimpleAPI")

assemblySettings

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "3.0.0-M15" % "test"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1" 

libraryDependencies += "org.apache.spark" % "spark-sql_2.10" % "1.6.1"

libraryDependencies += "commons-cli" % "commons-cli" % "1.2"

libraryDependencies += "org.scalanlp" % "breeze_2.10" % "0.12"
