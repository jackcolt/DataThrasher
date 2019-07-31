package etl.pharma

import java.io._
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import org.scalatest.FunSuite

import scala.util.Try


class PubMed extends FunSuite {

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

  val directoryName = "data/pubmed/"


  val outputFileName = directoryName + "pubmed_baseline.json.gz"

  val outputFile = new GZIPOutputStream(new FileOutputStream(outputFileName))

  val directory = new File(directoryName)

  var counter = 1
  if (directory.exists && directory.isDirectory) {

    val total_files = directory.listFiles.length / 2

    println("found " + total_files + " files....")

    for (file <- directory.listFiles if file.getName endsWith ".xml.gz") {
      if (counter < 3) {
        // process the file
        println("Processing file " + counter + " of " + total_files + "(" + file.getName + ") -" + file.getFreeSpace)
        counter = counter + 1
        //get the xml file
        //decompress the file

        val inputFileName = directoryName + file.getName

        val in = new GZIPInputStream(new FileInputStream(inputFileName))

        //val pm_xml = scala.xml.XML.loadFile(inputFileName)

        val pm_xml = scala.xml.XML.load(in)
        val articles = pm_xml \ "PubmedArticle"


        for (article <- articles) {
          try {
            val json_article = org.json.XML.toJSONObject(article.toString())

            outputFile.write(json_article.toString().getBytes)
            outputFile.write("\n".getBytes)

            //FileUtils.writeStringToFile(outputFile, json_article.toString, true)

            //FileUtils.writeStringToFile(outputFile, "\n", true)
          }
          catch {
            case unknown: Throwable => println("Got this unknown exception: " + unknown)
          }

        }
        outputFile.flush()

      }
    }
  }

  outputFile.close()

}
