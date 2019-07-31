package test

//import com.sksamuel.elastic4s.http.ElasticDsl._
//import com.sksamuel.elastic4s.http.search.SearchResponse
//import com.sksamuel.elastic4s.http.{bulk => _, search => _, _}
import org.scalatest._


class ElasticTest extends FunSuite {

/**
  test("es test") {

    val client = ElasticClient(ElasticProperties("http://localhost:9200"))


    //{ "index" : { "_index" : "test", "_type" : "_doc", "_id" : "1" } }

    val response: Response[SearchResponse] = client.execute {
      search("transactions").matchQuery("Physician_First_Name", "j")
    }.await

    // prints out the original json
    response.result.hits.hits.foreach(println)

    client.close()
  }
  **/
}

