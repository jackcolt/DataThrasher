package etl.pharma

import java.io._
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import org.apache.commons.io.FileUtils
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{DataType, StructType}
import org.elasticsearch.spark.sql.EsSparkSQL
import org.scalatest.FunSuite

import scala.io.Source
import scala.util.Try


class PubMedES extends FunSuite {

  def compress(input: Array[Byte]): Array[Byte] = {
    val bos = new ByteArrayOutputStream(input.length)
    val gzip = new GZIPOutputStream(bos)
    gzip.write(input)
    gzip.close()
    val compressed = bos.toByteArray
    bos.close()
    compressed
  }

  def decompress(compressed: Array[Byte]): Option[String] =
    Try {
      val inputStream = new GZIPInputStream(new ByteArrayInputStream(compressed))
      scala.io.Source.fromInputStream(inputStream).mkString
    }.toOption

  val conf = new SparkConf()

  conf.set("es.nodes", "helioinsights.com")
    .set("es.index.auto.create", "true")
    .set("es.port", "9200")
    .set("es.nodes.discovery", "false")
    .set("es.nodes.wan.only", "true")
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .set("spark.es.net.http.auth.user","")
    .set("spark.es.net.http.auth.pass", "")


  val spark = SparkSession.builder().master("local").appName("Open Payments Transactions").config(conf).getOrCreate()


  val directoryName = "data/pubmed/"

  val directory = new File(directoryName)

  var counter = 1
  if (directory.exists && directory.isDirectory) {

    val total_files = directory.listFiles.length / 2

    println("found " + total_files + " files....")

    for (file <- directory.listFiles if file.getName endsWith ".json.gz") {
      // process the file
      println("Processing file " + counter + " of " + total_files + "(" + file.getName + ") -" + file.getFreeSpace)
      counter = counter + 1

      val inputFileName = directoryName + file.getName

      val in = new GZIPInputStream(new FileInputStream(inputFileName))


      val tempOutFile = new File("temp.json")

      FileUtils.writeStringToFile(tempOutFile, scala.io.Source.fromInputStream(in).mkString, false)

      val transactionSchemaFileName = "/Users/johnpoulin/IdeaProjects/DataThrasher/src/main/scala/schema/pubmed_schema.json"


      val pubmedSchemaJson = Source.fromFile(transactionSchemaFileName).getLines.mkString
      val pubmedSchema = DataType.fromJson(pubmedSchemaJson).asInstanceOf[StructType]

      val parsed = spark.read.option("primitivesAsString",true).option("mode","PERMISSIVE").json("temp.json")

      //FileUtils.writeStringToFile(schemaFile, parsed.schema.prettyJson, false)
      EsSparkSQL.saveToEs(parsed, "pubmed_v1/docs")

    }
  }

}
