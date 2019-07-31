package etl.pharma

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{DataType, StructType}
import org.elasticsearch.spark.sql.EsSparkSQL
import org.scalatest.FunSuite

import scala.io.Source
class Transaction extends FunSuite {

  //ElasticClient(ElasticProperties("http://34.204.76.119:9200"))

  val conf = new SparkConf()

  conf.set("es.nodes", "34.204.76.119")
    .set("es.index.auto.create", "true")
    .set("es.port", "9200")
    .set("es.nodes.discovery", "false")
    .set("es.nodes.wan.only", "true")
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .set("spark.es.net.http.auth.user","")
    .set("spark.es.net.http.auth.pass", "")


  val spark = SparkSession.builder().master("local").appName("Open Payments Transactions").config(conf).getOrCreate()




  //val fileName = "/Users/johnpoulin/IdeaProjects/DataThrasher/data/OP_DTL_GNRL_PGYR2017_P06292018.csv"
  val fileName = "/Users/johnpoulin/IdeaProjects/DataThrasher/data/OP_DTL_GNRL_PGYR2018_P06282019.csv"
  val transactionSchemaFileName = "/Users/johnpoulin/IdeaProjects/DataThrasher/src/main/scala/schema/open_payments/transaction.json"


  val transactionchemaJson = Source.fromFile(transactionSchemaFileName).getLines.mkString
  val transactionSchema = DataType.fromJson(transactionchemaJson).asInstanceOf[StructType]


  val parsed = spark.read.option("nullValue", "?").option("inferSchema", "false").option("header", "true").schema(transactionSchema).csv(fileName)

  //val parsed = spark.read.option("nullValue", "?").option("inferSchema", "true").option("header", "true").csv(fileName)


  //EsSparkSQL.saveToEs(parsed,"transactions2018_v14/docs", Map("es.nodes" -> "34.204.76.119",
  //                                                                      "es.nodes.discovery"->"false",
  //                                                                      "es.nodes.wan.only"->"true"))
  EsSparkSQL.saveToEs(parsed, "transactions2018_v16/docs")




}
