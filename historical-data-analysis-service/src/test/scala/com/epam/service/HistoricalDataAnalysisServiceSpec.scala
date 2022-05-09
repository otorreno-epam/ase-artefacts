package com.epam.service

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.epam.data.aggregator.DataAggregator
import com.epam.domain.AggregationResult
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

class HistoricalDataAnalysisServiceSpec extends AsyncFlatSpec with Matchers with ScalatestRouteTest {
  import com.epam.serialization.Serializers._
  override def testConfigSource = "akka.loglevel = WARNING"

  "Service" should "respond to get on /result" in {
    val aggregator = new DataAggregator {

      override def aggregate(sport: String, years: Seq[Int]): AggregationResult = AggregationResult.Empty
    }
    val service = HistoricalDataAnalysisService("0.0.0.0", 9000, aggregator)

    Get(s"/result") ~> service.routes ~> check {
      status                        shouldBe OK
      contentType                   shouldBe `application/json`
      responseAs[AggregationResult] shouldBe AggregationResult.Empty
    }
  }

  it should "respond with internal server error on aggregation exceptions" in {
    val aggregator = new DataAggregator {

      override def aggregate(sport: String, years: Seq[Int]): AggregationResult = throw new RuntimeException("boom!")
    }
    val service = HistoricalDataAnalysisService("0.0.0.0", 9000, aggregator)

    Get(s"/result") ~> service.routes ~> check {
      status shouldBe InternalServerError
    }
  }
}
