package etl.pharma

import java.io._
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import org.json4s._
import org.json4s.jackson.JsonMethods._
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

      val articles = pm_xml \ "PubmedArticle" \ "MedlineCitation" \ "Article"


      val outputFileName = inputFileName.replaceAll(".xml.gz", ".json.gz")

      val outputStream = new GZIPOutputStream(new FileOutputStream(outputFileName))

      for (article <- articles) {
        try {
          //val elements = article \
          val json_article = org.json.XML.toJSONObject(article.toString())
          // outputStream.write(json_article.toString().getBytes)
          // outputStream.write("\n".getBytes)


          implicit val formats = DefaultFormats
          val parsed_article = parse(json_article.toString()).asInstanceOf[JObject]

          //val authorListRaw = parsed_article \ "Article" \ "AuthorList" \ "Author"
          val authorListRaw = parsed_article \ "Article" \ "AuthorList"
          val title = parsed_article \ "Article" \ "ArticleTitle"
          val journal_raw = parsed_article \ "Article" \ "Journal"
          val journal = journal_raw.extract[Journal]
          println("ISSN: " + journal.ISSN.content)
          println("Journal Title: " + journal.Title)
          print(" Title: ")
          println(title.extract[String])
          if (journal.ISSN.content == "1543-706X") {
            println("here...")
          }

          try {
            val authorList = authorListRaw.extract[AuthorList]
            for (author <- authorList.Author) {
                //val author = authorRaw.extract[Author]
                println("   Last name: " + author.LastName)
                println("   First name: " + author.ForeName)
                println("   Affiliation: " + author.AffiliationInfo.Affiliation)
            }
          }
          catch {
            case unknown: Throwable => println("Got this unknown exception parsing authors: " + unknown)
             print("   Last name:")
              println((authorListRaw \ "Author" \ "LastName").extract[String])
              print("   First name:")
              println((authorListRaw \"Author" \ "ForeName").extract[String])
          }

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
