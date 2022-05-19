package com.epam.data.aggregator

import com.epam.data.provider.DataProvider
import com.epam.domain.{AggregationResult, Match, TeamValue}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class InMemoryDataAggregatorSpec extends AsyncWordSpec with Matchers {

  case class TestDataProvider(matches: Map[(String, Int), Seq[Match]]) extends DataProvider {

    override def provide(sport: String, year: Int): Seq[Match] = matches.getOrElse((sport, year), Seq.empty)
  }

  def `match`(homeTeam: String, awayTeam: String, homeScore: Int, awayScore: Int): Match =
    Match(homeTeam, awayTeam, homeScore, awayScore, "testTournament", "testCity", "testCountry")

  "InMemoryDataAggregator" should {
    val soccer   = "soccer"
    val testYear = 2022

    "return empty team if data provider returns no data" in {
      val aggregator = InMemoryDataAggregator(TestDataProvider(Map.empty))
      aggregator.aggregate(soccer, Seq(testYear)) shouldBe AggregationResult.Empty
    }

    "return empty team if there are no wins" in {
      val matches    = Map((soccer, testYear) -> Seq(`match`("teamA", "teamB", 2, 2)))
      val aggregator = InMemoryDataAggregator(TestDataProvider(matches))
      val statistics = aggregator.aggregate(soccer, Seq(testYear))
      statistics.mostWin shouldBe TeamValue.Empty
    }

    "return the first alphabetically sorted team with most wins" in {
      val mostWinsTeam = "teamA"
      val matches =
        Map((soccer, testYear) -> Seq(`match`(mostWinsTeam, "teamB", 3, 2), `match`(mostWinsTeam, "teamB", 1, 2)))
      val aggregator = InMemoryDataAggregator(TestDataProvider(matches))
      val statistics = aggregator.aggregate(soccer, Seq(testYear))
      statistics.mostWin shouldBe TeamValue(mostWinsTeam, 1.0d)
    }

    "return the team with most wins" in {
      val mostWinsTeam = "teamA"
      val matches = Map(
          (soccer, testYear) -> Seq(
              `match`("teamC", "teamD", 3, 2),
              `match`(mostWinsTeam, "teamB", 4, 2),
              `match`(mostWinsTeam, "teamC", 1, 0)))
      val aggregator = InMemoryDataAggregator(TestDataProvider(matches))
      val statistics = aggregator.aggregate(soccer, Seq(testYear))
      statistics.mostWin shouldBe TeamValue(mostWinsTeam, 2.0d)
    }

    "return the first alphabetically sorted team with most scored game" in {
      val mostScoredPerGame = "teamA"
      val matches = Map(
          (soccer, testYear) -> Seq(
              `match`("teamC", "teamD", 1, 4),
              `match`(mostScoredPerGame, "teamB", 4, 2),
              `match`(mostScoredPerGame, "teamC", 1, 0)))
      val aggregator = InMemoryDataAggregator(TestDataProvider(matches))
      val statistics = aggregator.aggregate(soccer, Seq(testYear))
      statistics.mostScoredPerGame shouldBe TeamValue(mostScoredPerGame, 4.0d)
    }

    "return the first alphabetically sorted team with less received per game" in {
      val lessReceivedPerGame = "teamA"
      val matches = Map(
          (soccer, testYear) -> Seq(
              `match`("teamC", "teamD", 0, 4),
              `match`(lessReceivedPerGame, "teamB", 4, 0),
              `match`(lessReceivedPerGame, "teamC", 1, 0)))
      val aggregator = InMemoryDataAggregator(TestDataProvider(matches))
      val statistics = aggregator.aggregate(soccer, Seq(testYear))
      statistics.lessReceivedPerGame shouldBe TeamValue(lessReceivedPerGame, 0.0d)
    }
  }
}
