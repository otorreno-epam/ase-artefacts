package com.epam.data.provider

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.epam.domain.Match
import com.epam.serialization.Serializers._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

case class RESTDataProvider(host: String, port: Int)(implicit val system: ActorSystem) extends DataProvider {
  import RESTDataProvider._

  private lazy implicit val executionContext: ExecutionContext = system.dispatcher

  private lazy val apiFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(host, port)

  override def provide(sport: String, year: Int): Seq[Match] = {
    val matches = Source.single(RequestBuilding.Get(s"/$sport/$year")).via(apiFlow).runWith(Sink.head).flatMap {
      response =>
        response.status match {
          case OK => Unmarshal(response.entity).to[Seq[Match]]
          case _ =>
            Unmarshal(response.entity).to[String].flatMap { entity =>
              val error =
                s"request to obtain the data for sport: $sport and year: $year, failed with status code ${response.status} and entity $entity"
              throw new RuntimeException(error)
            }
        }
    }
    Await.result(matches, FetchTimeout)
  }
}

object RESTDataProvider {
  private val FetchTimeout = 20.seconds
}
