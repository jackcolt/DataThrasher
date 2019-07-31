package etl.pharma

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.elasticsearch.spark.sql.EsSparkSQL
import org.scalatest.FunSuite



class ClinicalTrial extends FunSuite {



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


  //val fileName = "/Users/johnpoulin/IdeaProjects/DataThrasher/data/clinical_trials/NCT04030052.xml"

  val fileName = "test3.json"

  //val trial_xml = scala.xml.XML.loadFile(fileName)

  //val trial_json = toJson(trial_xml)

  //println(trial_json)

  //val file = new File( "test3.json")

  //val json_obj = org.json.XML.toJSONObject(trial_xml.toString())

  //System.out.println(json_obj.toString)

  //FileUtils.write(file, json_obj.toString)

  val parsed = spark.read.option("nullValue", "?").option("inferSchema", "false").option("header", "true").json(fileName)

  //val parsed = spark.read.option("nullValue", "?").option("inferSchema", "true").option("header", "true").csv(fileName)


  //EsSparkSQL.saveToEs(parsed,"transactions2018_v14/docs", Map("es.nodes" -> "34.204.76.119",
  //                                                                      "es.nodes.discovery"->"false",
  //                                                                      "es.nodes.wan.only"->"true"))
  EsSparkSQL.saveToEs(parsed, "clinical_trials_v1/docs")


}
