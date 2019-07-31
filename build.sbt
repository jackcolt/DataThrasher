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

libraryDependencies +=   "org.elasticsearch" %% "elasticsearch-spark-20" % "7.0.0"

//libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-core" % "6.3.3"
//libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-http" % "6.3.3"

//libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-core" % "7.0.3"
//libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-http" % "6.8.0"

// https://mvnrepository.com/artifact/org.json/json
libraryDependencies += "org.json" % "json" % "20180813"

val elastic4sVersion = "7.0.3"
libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,

  // for the default http client
  "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % elastic4sVersion,

  // if you want to use reactive streams
  "com.sksamuel.elastic4s" %% "elastic4s-http-streams" % elastic4sVersion,

  // testing
  "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elastic4sVersion % "test"
)
