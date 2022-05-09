package com.epam.service

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.epam.data.aggregator.DataAggregator
import com.epam.serialization.Serializers._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

case class HistoricalDataAnalysisService(interface: String, port: Int, dataAggregator: DataAggregator)(
        implicit val system: ActorSystem) {
  private implicit val executor: ExecutionContext = system.dispatcher

  private val logger = Logging(system, "HistoricalDataAnalysisService")

  val routes: Route = {
    logRequestResult("historical-data-analysis-service") {
      (path("result") & get) {
        withRequestTimeout(1.minute) {
          complete {
            Future {
              val result = dataAggregator.aggregate("soccer", 2000 to 2003)
              logger.info(s"result: $result")
              result
            }.map[ToResponseMarshallable](a => a)
          }
        }
      }
    }
  }

  def start(): Unit =
    Http().newServerAt(interface, port).bindFlow(routes)
}
