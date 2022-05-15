package com.epam.data.provider

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.epam.domain.Match
import com.epam.serialization.Serializers.*

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.*
import scala.util.control.NonFatal

case class RESTDataProvider(host: String, port: Int)(using system: ActorSystem) extends DataProvider:
  import RESTDataProvider.*

  given ExecutionContext = system.dispatcher

  private lazy val apiFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(host, port)

  def retriableFetch(currentIteration: Int, maxIterations: Int)(sport: String, year: Int): Future[Seq[Match]] =
    Source.single(RequestBuilding.Get(s"/$sport/$year")).via(apiFlow).runWith(Sink.head).flatMap { response =>
      response.status match {
        case OK =>
          Unmarshal(response.entity).to[Seq[Match]].recoverWith {
            case NonFatal(_) if currentIteration < maxIterations =>
              Thread.sleep(1000)
              retriableFetch(currentIteration + 1, maxIterations)(sport, year)
            case NonFatal(_) =>
              Future {
                Seq.empty
              }
          }
        case _ =>
          Unmarshal(response.entity).to[String].flatMap { entity =>
            val error =
              s"request to obtain the data for sport: $sport and year: $year, failed with status code ${response.status} and entity $entity"
            throw new RuntimeException(error)
          }
      }
    }

  override def provide(sport: String, year: Int): Seq[Match] = {
    val matches = retriableFetch(0, 10)(sport, year)
    Await.result(matches, FetchTimeout)
  }

object RESTDataProvider:
  private val FetchTimeout = 60.seconds
