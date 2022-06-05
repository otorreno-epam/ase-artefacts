package com.epam.data.aggregator

import com.epam.data.provider.DataProvider
import com.epam.domain.{AggregationResult, TeamValue}
import zhttp.service.{ChannelFactory, EventLoopGroup}
import zio.*

case class InMemoryDataAggregator(dataProvider: DataProvider) extends DataAggregator {
  import TeamValue.*

  override def aggregate(sport: String, years: Seq[Int]): ZIO[EventLoopGroup & ChannelFactory, Throwable, AggregationResult] = {
    for {
      matchesPerYear <- ZIO.foreachPar(years)(year => dataProvider.provide(sport, year))
      matches = matchesPerYear.flatten
      winners = matches.flatMap { m =>
        if (m.homeScore > m.awayScore)
          Some(m.homeTeam)
        else if (m.homeScore > m.awayScore)
          Some(m.awayTeam)
        else
          None
      }
      mostWins = winners
        .groupBy(identity)
        .map {
          case (team, wins) => team -> wins.length.toDouble
        }
        .toSeq
        .sortBy {
          case (team, wins) => (-wins, team)
        }
        .headOption
        .map(TeamValue.apply.tupled)
        .getOrElse(Empty)
      mostScoredPerGame = matches
        .flatMap { m =>
          Seq(m.homeTeam -> m.homeScore, m.awayTeam -> m.awayScore)
        }
        .sortBy {
          case (team, score) => (-score, team)
        }
        .headOption
        .map(TeamValue.apply.tupled)
        .getOrElse(Empty)
      lessReceivedPerGame = matches
        .flatMap { m =>
          Seq(m.homeTeam -> m.awayScore, m.awayTeam -> m.homeScore)
        }
        .sortBy {
          case (team, received) => (received, team)
        }
        .headOption
        .map(TeamValue.apply.tupled)
        .getOrElse(Empty)
    } yield AggregationResult(mostWins, mostScoredPerGame, lessReceivedPerGame)
  }
}
