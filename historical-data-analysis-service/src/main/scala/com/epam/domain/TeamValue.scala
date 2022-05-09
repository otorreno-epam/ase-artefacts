package com.epam.domain

case class TeamValue(team: String, amount: Double)

object TeamValue {
  def apply(tuple: (String, Int)): TeamValue = TeamValue(tuple._1, tuple._2)

  val Empty: TeamValue = TeamValue("", 0.0)
}
