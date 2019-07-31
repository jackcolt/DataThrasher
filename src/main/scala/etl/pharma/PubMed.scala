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

  val directory = new File(directoryName)

  var counter = 1
  if (directory.exists && directory.isDirectory) {

    val total_files = directory.listFiles.length / 2

    println("found " + total_files + " files....")

    for (file <- directory.listFiles if file.getName endsWith ".xml.gz") {
      // process the file
      println("Processing file " + counter + " of " + total_files + "(" + file.getName + ") -" + file.getFreeSpace)
      counter = counter + 1

      val inputFileName = directoryName + file.getName

      val in = new GZIPInputStream(new FileInputStream(inputFileName))

      val pm_xml = scala.xml.XML.load(in)

      scala.xml.XML.

      val articles = pm_xml \ "PubmedArticle"

      val outputFileName = inputFileName.replaceAll(".xml.gz", ".json.gz")

      val outputStream = new GZIPOutputStream(new FileOutputStream(outputFileName))

      for (article <- articles) {
        try {
          val json_article = org.json.XML.toJSONObject(article.toString())

          outputStream.write(json_article.toString().getBytes)
          outputStream.write("\n".getBytes)
        }
        catch {
          case unknown: Throwable => println("Got this unknown exception: " + unknown)
        }

      }
      outputStream.flush()
      outputStream.close()
    }
  }

}
