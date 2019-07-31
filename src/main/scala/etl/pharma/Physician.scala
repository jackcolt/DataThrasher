package etl.pharma

import org.apache.spark.sql.SparkSession

import org.apache.spark.sql.types.{DataType, StructType}

import scala.io.Source
import org.scalatest.FunSuite

import org.apache.spark.sql.functions._

class Physician extends FunSuite {

  val spark = SparkSession.builder().master("local").appName("Open Payments").getOrCreate()

  val fileName = "/Users/johnpoulin/IdeaProjects/DataThrasher/data/OP_PH_PRFL_SPLMTL_P06292018.csv"
  val physicianSchemaFileName = "/Users/johnpoulin/IdeaProjects/DataThrasher/src/main/scala/schema/open_payments/physician.json"


  val physicianSchemaJson = Source.fromFile(physicianSchemaFileName).getLines.mkString
  val physicianSchema = DataType.fromJson(physicianSchemaJson).asInstanceOf[StructType]


  val parsed2 = spark.read.option("nullValue", "?").option("inferSchema", "false").option("header", "true").schema(physicianSchema).csv(fileName)

  println(parsed2.schema.json)

  val head2 = parsed2.take(20)
  head2.foreach(println)

  parsed2.groupBy("Physician_Profile_Zipcode").count.orderBy(desc("count")).show(100)

  parsed2.groupBy("Physician_Profile_State").count.orderBy(desc("count")).show(100)


  /*


  parsed2.groupBy("age").count.show()

  parsed2.groupBy("sale_price").count.orderBy(desc("sale_price")).show(100)

*/
}
