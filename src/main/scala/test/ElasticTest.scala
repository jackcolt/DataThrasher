package test

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.{bulk => _, search => _, _}
import com.sksamuel.elastic4s.http.search.SearchResponse
import org.scalatest._


class ElasticTest extends FunSuite {


  test("es test") {

    val client = ElasticClient(ElasticProperties("http://localhost:9200"))

    client.execute {
      bulk(
        indexInto("myindex" / "mytype").fields("country" -> "Mongolia", "capital" -> "Ulaanbaatar"),
        indexInto("myindex" / "mytype").fields("country" -> "Namibia", "capital" -> "Windhoek")
      ).refresh(RefreshPolicy.WaitFor)
    }.await

    val response: Response[SearchResponse] = client.execute {
      search("myindex").matchQuery("capital", "ulaanbaatar")
    }.await

    // prints out the original json
    println(response.result.hits.hits.head.sourceAsString)

    client.close()
  }
}

