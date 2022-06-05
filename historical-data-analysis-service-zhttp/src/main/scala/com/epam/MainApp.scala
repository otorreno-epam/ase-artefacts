package com.epam

import com.epam.data.aggregator.InMemoryDataAggregator
import com.epam.data.provider.RESTDataProvider
import com.epam.service.HistoricalDataAnalysisService
import zhttp.http.*
import zhttp.service.{ChannelFactory, EventLoopGroup, Server}
import zio.*

object MainApp extends ZIOAppDefault {

  private val dataProvider = RESTDataProvider("services.data-provider.host", 8080)

  private val dataAggregator = InMemoryDataAggregator(dataProvider)

  private val service =
    HistoricalDataAnalysisService(dataAggregator)

  override val run = Server.start(8090, service.app.provideLayer(ChannelFactory.auto ++ EventLoopGroup.auto(2))).exitCode
}
