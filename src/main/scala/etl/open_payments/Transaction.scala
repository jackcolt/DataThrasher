package etl.open_payments

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{DataType, StructType}
import org.elasticsearch.spark.sql.EsSparkSQL
import org.scalatest.FunSuite

import scala.io.Source
class Transaction extends FunSuite {

  //val client = ElasticClient(ElasticProperties("http://localhost:9200"))

  val spark = SparkSession.builder().master("local").appName("Open Payments Transactions").getOrCreate()
  spark.conf.set("es.index.auto.create", "true")


  val fileName = "/Users/johnpoulin/IdeaProjects/DataThrasher/data/OP_DTL_GNRL_PGYR2017_P06292018.csv"
  val transactionSchemaFileName = "/Users/johnpoulin/IdeaProjects/DataThrasher/src/main/scala/schema/open_payments/transaction.json"


  val transactionchemaJson = Source.fromFile(transactionSchemaFileName).getLines.mkString
  val transactionSchema = DataType.fromJson(transactionchemaJson).asInstanceOf[StructType]



  val parsed = spark.read.option("nullValue", "?").option("inferSchema", "false").option("header", "true").schema(transactionSchema).csv(fileName)


  EsSparkSQL.saveToEs(parsed,"transactions2/docs")

}
