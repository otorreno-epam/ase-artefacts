package com.epam.serialization

import com.epam.domain.{AggregationResult, Match, TeamValue}
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.{Decoder, Encoder}

object Serializers extends ErrorAccumulatingCirceSupport {
  import io.circe.generic.semiauto._
  implicit val matchDecoder: Decoder[Match] = deriveDecoder
  implicit val matchEncoder: Encoder[Match] = deriveEncoder

  implicit val teamValueDecoder: Decoder[TeamValue] = deriveDecoder
  implicit val teamValueEncoder: Encoder[TeamValue] = deriveEncoder

  implicit val aggregationResultDecoder: Decoder[AggregationResult] = deriveDecoder
  implicit val aggregationResultEncoder: Encoder[AggregationResult] = deriveEncoder
}
