package com.epam.data.provider

import com.epam.domain.Match
import com.epam.serialization.Serializers.*
import io.circe.parser.*
import zhttp.service.{ChannelFactory, Client, EventLoopGroup}
import zio.*

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.*
import scala.util.control.NonFatal

case class RESTDataProvider(host: String, port: Int) extends DataProvider:
  import RESTDataProvider.*

  override def provide(sport: String, year: Int): ZIO[EventLoopGroup & ChannelFactory, Throwable, Seq[Match]] = {
    for {
      res <- Client.request(s"$host:$port/$sport/$year").retry(Schedule.exponential(100.milliseconds))
      data <- res.bodyAsString
      matches <- ZIO.fromEither(decode[Seq[Match]](data))
    } yield (matches)
  }
