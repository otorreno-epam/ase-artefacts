package com.epam

import akka.actor.ActorSystem
import com.epam.data.aggregator.InMemoryDataAggregator
import com.epam.data.provider.{CachingDataProvider, RESTDataProvider}
import com.epam.service.HistoricalDataAnalysisService
import com.typesafe.config.ConfigFactory

object MainApp extends App {
  private implicit val system: ActorSystem = ActorSystem()
  private val config                       = ConfigFactory.load()

  val dataProvider =
    CachingDataProvider(
        RESTDataProvider(config.getString("services.data-provider.host"), config.getInt("services.data-provider.port")))

  val dataAggregator = InMemoryDataAggregator(dataProvider)

  private val service =
    new HistoricalDataAnalysisService(config.getString("http.interface"), config.getInt("http.port"), dataAggregator)

  service.start()
}
