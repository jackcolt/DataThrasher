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

lazy val scalaXml = "org.scala-lang.modules" %% "scala-xml" % "1.0.2"
lazy val scalaParser = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1"
lazy val dispatchV = "0.11.3"
lazy val dispatch = "net.databinder.dispatch" %% "dispatch-core" % dispatchV

lazy val root = (project in file(".")).
  enablePlugins(ScalaxbPlugin).
  settings(inThisBuild(List(
    organization  := "com.example",
    scalaVersion  := "2.11.8"
  ))).
  settings(
    name          := "foo-project",
    libraryDependencies ++= Seq(dispatch),
    libraryDependencies ++= {
      if (scalaVersion.value startsWith "2.10") Seq()
      else Seq(scalaXml, scalaParser)
    }).
  settings(
    scalaxbDispatchVersion in (Compile, scalaxb) := dispatchV,
    scalaxbPackageName in (Compile, scalaxb)     := "generated"
     //scalaxbPackageNames in (Compile, scalaxb)    := Map(uri("http://schemas.microsoft.com/2003/10/Serialization/") -> "microsoft.serialization"),
    // logLevel in (Compile, scalaxb) := Level.Debug
  )