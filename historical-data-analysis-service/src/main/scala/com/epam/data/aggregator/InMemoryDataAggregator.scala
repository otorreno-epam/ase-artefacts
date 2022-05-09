package com.epam.data.aggregator

import com.epam.data.provider.DataProvider
import com.epam.domain.{AggregationResult, TeamValue}

case class InMemoryDataAggregator(dataProvider: DataProvider) extends DataAggregator {
  import TeamValue._

  override def aggregate(sport: String, years: Seq[Int]): AggregationResult = {
    val matches = years.flatMap(year => dataProvider.provide(sport, year))
    val winners = matches.flatMap { m =>
      if (m.homeScore > m.awayScore)
        Some(m.homeTeam)
      else if (m.homeScore > m.awayScore)
        Some(m.awayTeam)
      else
        None
    }

    val mostWins = winners
      .groupBy(identity)
      .map {
        case (team, wins) => team -> wins.length
      }
      .toSeq
      .sortBy {
        case (team, wins) => (-wins, team)
      }
      .headOption
      .map(TeamValue.apply)
      .getOrElse(Empty)

    val mostScoredPerGame = matches
      .flatMap { m =>
        Seq(m.homeTeam -> m.homeScore, m.awayTeam -> m.awayScore)
      }
      .sortBy {
        case (team, score) => (-score, team)
      }
      .headOption
      .map(TeamValue.apply)
      .getOrElse(Empty)

    val lessReceivedPerGame = matches
      .flatMap { m =>
        Seq(m.homeTeam -> m.awayScore, m.awayTeam -> m.homeScore)
      }
      .sortBy {
        case (team, received) => (received, team)
      }
      .headOption
      .map(TeamValue.apply)
      .getOrElse(Empty)

    AggregationResult(mostWins, mostScoredPerGame, lessReceivedPerGame)
  }
}
