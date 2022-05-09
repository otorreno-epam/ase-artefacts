package com.epam.data.provider

import com.epam.domain.Match

import scala.collection.concurrent.TrieMap

/**
  * The intent of this data provider is to cache retrieved values to allow faster responses in subsequent calls
  */
case class CachingDataProvider(delegate: DataProvider) extends DataProvider {

  // TODO use a proper library like google cache to store the values
  private val cache = new TrieMap[(String, Int), Seq[Match]]()

  override def provide(sport: String, year: Int): Seq[Match] =
    cache.getOrElseUpdate((sport, year), delegate.provide(sport, year))
}
