package com.epam.service

import com.epam.data.aggregator.DataAggregator
import com.epam.domain.AggregationResult
import com.epam.serialization.Serializers.*
import io.circe.syntax.*
import zhttp.http.*

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.*

case class HistoricalDataAnalysisService(dataAggregator: DataAggregator):
  val app = Http.collect[Request] {
    case Method.GET -> !! / "result" =>
      val result = dataAggregator.aggregate("soccer", 2000 to 2003)
      Response.json(result.asJson.noSpaces)
  }
