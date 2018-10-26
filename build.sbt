name := "DataThrasher"

version := "0.1"

scalaVersion := "2.11.8"

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5",
  "org.scalatest" % "scalatest_2.11" % "3.0.5" % "test")

libraryDependencies +=   "org.apache.spark" %% "spark-core" % "2.3.2"

libraryDependencies +=   "org.apache.spark" %% "spark-mllib" % "2.3.2"

libraryDependencies +=   "org.apache.spark" %% "spark-sql" % "2.3.2"
