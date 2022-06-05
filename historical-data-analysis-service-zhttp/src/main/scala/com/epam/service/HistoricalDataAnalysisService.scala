package com.epam.service

import com.epam.data.aggregator.DataAggregator
import com.epam.domain.AggregationResult
import io.circe.generic.auto.*
import io.circe.syntax.*
import zhttp.http.*

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.*

case class HistoricalDataAnalysisService(dataAggregator: DataAggregator):
  val app = Http.collectZIO[Request] {
    case Method.GET -> !! / "result" =>
      for {
        result <- dataAggregator.aggregate("soccer", 2000 to 2003)
      } yield Response.json(result.asJson.noSpaces)
  }
