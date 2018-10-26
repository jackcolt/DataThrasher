package etl

import org.apache.spark.sql.SparkSession
import org.scalatest.FunSuite
import org.apache.spark.sql.types.{DataType, StructType}

import scala.io.Source
class LandRegistry extends FunSuite{

  /**
    * Transaction unique identifier	A reference number which is generated automatically recording each published sale. The number is unique and will change each time a sale is recorded.
    * Price	Sale price stated on the transfer deed.
    * Date of Transfer	Date when the sale was completed, as stated on the transfer deed.
    * Postcode	This is the postcode used at the time of the original transaction. Note that postcodes can be reallocated and these changes are not reflected in the Price Paid Dataset.
    * Property Type	D = Detached, S = Semi-Detached, T = Terraced, F = Flats/Maisonettes, O = Other
    * Note that:
    * - we only record the above categories to describe property type, we do not separately identify bungalows.
    * - end-of-terrace properties are included in the Terraced category above.
    * - ‘Other’ is only valid where the transaction relates to a property type that is not covered by existing values.
    * Old/New	Indicates the age of the property and applies to all price paid transactions, residential and non-residential.
    * Y = a newly built property, N = an established residential building
    * Duration	Relates to the tenure: F = Freehold, L= Leasehold etc.
    * Note that HM Land Registry does not record leases of 7 years or less in the Price Paid Dataset.
    * PAON	Primary Addressable Object Name. Typically the house number or name.
    * SAON	Secondary Addressable Object Name. Where a property has been divided into separate units (for example, flats), the PAON (above) will identify the building and a SAON will be specified that identifies the separate unit/flat.
    * Street
    * Locality
    * Town/City
    * District
    * County
    * PPD Category Type	Indicates the type of Price Paid transaction.
    * A = Standard Price Paid entry, includes single residential property sold for full market value.
    * B = Additional Price Paid entry including transfers under a power of sale/repossessions, buy-to-lets (where they can be identified by a Mortgage) and transfers to non-private individuals.
    *
    * Note that category B does not separately identify the transaction types stated.
    * HM Land Registry has been collecting information on Category A transactions from January 1995. Category B transactions were identified from October 2013.
    * Record Status - monthly file only	Indicates additions, changes and deletions to the records.(see guide below).
    * A = Addition
    * C = Change
    * D = Delete.
    *
    * Note that where a transaction changes category type due to misallocation (as above) it will be deleted from the original category type and added to the correct category with a new transaction unique identifier.
    */
  test ("land registry etl") {
    val spark = SparkSession.builder().master("local").appName("TokenTest").getOrCreate()

    val fileName = "/Users/johnpoulin/IdeaProjects/DataThrasher/data/pp-monthly-update-new-version_9_18.csv"
    val ppSchemaFileName = "/Users/johnpoulin/IdeaProjects/DataThrasher/src/main/scala/schema/price_paid.json"

    val sc = spark.sparkContext
    val land_rdd = sc.textFile(fileName)
    land_rdd.first()
    val head = land_rdd.take(10)
    head.foreach(println)

    val pricePaidSchemaJson = Source.fromFile(ppSchemaFileName).getLines.mkString
    val pricePaidSchema = DataType.fromJson(pricePaidSchemaJson).asInstanceOf[StructType]

    val parsed2 = spark.read.option("nullValue", "?").option("inferSchema", "false").schema(pricePaidSchema).csv(fileName)

    parsed2.printSchema()

    val head2 = parsed2.take(20)
    head2.foreach(println)

    import org.apache.spark.sql.functions._

    parsed2.groupBy("age").count.show()
    parsed2.groupBy("transfer_date").count.orderBy(desc("count")).show(100)
    parsed2.groupBy("sale_price").count.orderBy(desc("sale_price")).show(100)

  }
}
