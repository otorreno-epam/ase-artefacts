package com.epam.serialization

import com.epam.domain.{AggregationResult, Match, TeamValue}
import io.circe.{Decoder, Encoder}

object Serializers:
  import io.circe.generic.semiauto.*
  implicit val matchDecoder: Decoder[Match] = deriveDecoder
  implicit val matchEncoder: Encoder[Match] = deriveEncoder

  implicit val teamValueDecoder: Decoder[TeamValue] = deriveDecoder
  implicit val teamValueEncoder: Encoder[TeamValue] = deriveEncoder

  implicit val aggregationResultDecoder: Decoder[AggregationResult] = deriveDecoder
  implicit val aggregationResultEncoder: Encoder[AggregationResult] = deriveEncoder
