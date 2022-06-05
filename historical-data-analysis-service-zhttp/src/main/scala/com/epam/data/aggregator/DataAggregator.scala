package com.epam.data.aggregator

import com.epam.domain.AggregationResult
import zhttp.service.{ChannelFactory, EventLoopGroup}
import zio.*

trait DataAggregator {

  /**
    * Aggregates statistics about matches for a given sport and sequence of years
    * @param sport type of sport (soccer, basketball, etc.)
    * @param years of the matches whose score we would like to aggregate
    * @return statistics about matches for a given sport and sequence of years
    */
  def aggregate(sport: String, years: Seq[Int]): ZIO[EventLoopGroup & ChannelFactory, Throwable, AggregationResult]
}
