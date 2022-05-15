package com.epam.domain

case class TeamValue(team: String, amount: Double)

object TeamValue:
  val Empty: TeamValue = TeamValue("", 0.0)
