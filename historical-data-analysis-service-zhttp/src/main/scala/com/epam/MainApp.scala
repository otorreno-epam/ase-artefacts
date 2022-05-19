package com.epam

import com.epam.data.aggregator.InMemoryDataAggregator
import com.epam.data.provider.RESTDataProvider
import com.epam.service.HistoricalDataAnalysisService
import zhttp.http.*
import zhttp.service.Server
import zio.*

object MainApp extends ZIOAppDefault {

  val dataProvider = RESTDataProvider("services.data-provider.host", 8080)

  val dataAggregator = InMemoryDataAggregator(dataProvider)

  private val service =
    HistoricalDataAnalysisService(dataAggregator)

  override val run = Server.start(8090, service.app).exitCode
}
