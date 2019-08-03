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


      val outputFileName = inputFileName.replaceAll(".xml.gz", ".csv.gz")

      val outputStream = new GZIPOutputStream(new FileOutputStream(outputFileName))

      //6 columnms - ISSN, JournalTitle, ArticleTitle, AuthorLastName, AuthorFirstName, AuthorAffiliation
      //write the header
      outputStream.write("ISSN, JournalTitle, ArticleTitle, AuthorLastName, AuthorFirstName, AuthorAffiliation".getBytes)
      outputStream.write("\n".getBytes)

      for (article <- articles) {
        try {
          //val elements = article \
          val json_article = org.json.XML.toJSONObject(article.toString())

          implicit val formats = DefaultFormats
          val parsed_article = parse(json_article.toString()).asInstanceOf[JObject]

          val authorListRaw = parsed_article \ "Article" \ "AuthorList"
          val title = parsed_article \ "Article" \ "ArticleTitle"
          val journal_raw = parsed_article \ "Article" \ "Journal"
          val journal = journal_raw.extract[Journal]

          val issn = journal.ISSN.content
          val journalTitle = journal.Title
          val articleTitle = title.extract[String]

          var lastName=""
          var firstName=""
          var affiliation=""

          try {
            val authorList = authorListRaw.extract[AuthorList]
            for (author <- authorList.Author) {
                lastName= author.LastName
                firstName= author.ForeName
                affiliation= author.AffiliationInfo.Affiliation

              //6 columnms - ISSN, JournalTitle, ArticleTitle, AuthorLastName, AuthorFirstName, AuthorAffiliation
              //write the content

              //issn
              outputStream.write(("\"" + issn + "\"").getBytes)
              outputStream.write(",".getBytes())
              //JournalTitle
              outputStream.write(("\"" + journalTitle + "\"").getBytes)
              outputStream.write(",".getBytes())
              //ArticleTitle
              outputStream.write(("\"" + articleTitle + "\"").getBytes)
              outputStream.write(",".getBytes())
              //AuthorLastName
              outputStream.write(("\"" + lastName + "\"").getBytes)
              outputStream.write(",".getBytes())
              //AuthorFirstName
              outputStream.write(("\"" + firstName + "\"").getBytes)
              outputStream.write(",".getBytes())
              //AuthorAffiliation
              outputStream.write(("\"" + affiliation + "\"").getBytes)
              //EOL
              outputStream.write("\n".getBytes)

            }
          }
          catch {
            case unknown: Throwable =>
              val author = (authorListRaw \ "Author").extract[Author]
              lastName= author.LastName
              firstName= author.ForeName
              affiliation= author.AffiliationInfo.Affiliation

              //6 columnms - ISSN, JournalTitle, ArticleTitle, AuthorLastName, AuthorFirstName, AuthorAffiliation
              //write the content

              //issn
              outputStream.write(("\"" + issn + "\"").getBytes)
              outputStream.write(",".getBytes())
              //JournalTitle
              outputStream.write(("\"" + journalTitle + "\"").getBytes)
              outputStream.write(",".getBytes())
              //ArticleTitle
              outputStream.write(("\"" + articleTitle + "\"").getBytes)
              outputStream.write(",".getBytes())
              //AuthorLastName
              outputStream.write(("\"" + lastName + "\"").getBytes)
              outputStream.write(",".getBytes())
              //AuthorFirstName
              outputStream.write(("\"" + firstName + "\"").getBytes)
              outputStream.write(",".getBytes())
              //AuthorAffiliation
              outputStream.write(("\"" + affiliation + "\"").getBytes)
              //EOL
              outputStream.write("\n".getBytes)

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
