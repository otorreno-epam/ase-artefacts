package com.epam.domain

case class AggregationResult(mostWin: TeamValue, mostScoredPerGame: TeamValue, lessReceivedPerGame: TeamValue)

object AggregationResult {
  val Empty: AggregationResult = AggregationResult(TeamValue.Empty, TeamValue.Empty, TeamValue.Empty)
}
