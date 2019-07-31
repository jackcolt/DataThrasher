package etl.pharma

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{DataType, StructType}
import org.elasticsearch.spark.sql.EsSparkSQL
import org.scalatest.FunSuite

import scala.io.Source


class PubMed extends FunSuite {



  //ElasticClient(ElasticProperties("http://34.204.76.119:9200"))

  val conf = new SparkConf()

  conf.set("es.nodes", "localhost")
    .set("es.index.auto.create", "true")
    .set("es.port", "9200")
    .set("es.nodes.discovery", "false")
    .set("es.nodes.wan.only", "true")
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .set("spark.es.net.http.auth.user","")
    .set("spark.es.net.http.auth.pass", "")


  val spark = SparkSession.builder().master("local").appName("Clinical Trials Data").config(conf).getOrCreate()




  val outputFileName = "pubmed_all.json"

  val inputFileName = "/Users/johnpoulin/IdeaProjects/DataThrasher/data/pubmed19n1358.xml"



  val pm_xml = scala.xml.XML.loadFile(inputFileName)


  val articles = pm_xml \ "PubmedArticle"

  //val json_obj = org.json.XML.toJSONObject(articles.toString())



  //FileUtils.write(file, json_obj.toString)

  val schemaFileName="/Users/johnpoulin/IdeaProjects/DataThrasher/src/main/scala/schema/pubmed_schema.json"

  val pubmedSchemaJson = Source.fromFile(schemaFileName).getLines.mkString
  val pubmedSchema = DataType.fromJson(pubmedSchemaJson).asInstanceOf[StructType]

  val file = new File(outputFileName)

  val schemaFile = new File(schemaFileName)


  for (article <- articles)
    {
      try {
        val json_article = org.json.XML.toJSONObject(article.toString())

        FileUtils.writeStringToFile(file, json_article.toString, true)

        FileUtils.writeStringToFile(file, "\n", true)
      }
      catch
      {
        case unknown => println("Got this unknown exception: " + unknown)
      }

    }

  val parsed = spark.read.schema(pubmedSchema).option("nullValue", "?").option("mode","DROPMALFORMED").json(outputFileName)

  //FileUtils.writeStringToFile(schemaFile, parsed.schema.prettyJson, false)
  EsSparkSQL.saveToEs(parsed, "pubmed_v13/docs")


  //EsSparkSQL.saveToEs(parsed,"transactions2018_v14/docs", Map("es.nodes" -> "34.204.76.119",
  //                                                                      "es.nodes.discovery"->"false",
  //                                                                      "es.nodes.wan.only"->"true"))



}
