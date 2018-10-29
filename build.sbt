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

libraryDependencies +=   "org.elasticsearch" %% "elasticsearch-spark-20" % "6.4.2"

libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-core" % "6.3.3"
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-http" % "6.3.3"

