package com.epam.data.provider
import com.epam.domain.Match

trait DataProvider {

  /**
    *
    * @param sport type of sport (soccer, basketball, etc.)
    * @param year of the matches whose score we would like to retrieve
    * @return A sequence of matches
    */
  def provide(sport: String, year: Int): Seq[Match]
}
