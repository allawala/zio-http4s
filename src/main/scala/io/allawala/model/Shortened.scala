package io.allawala.model

import io.circe.generic.JsonCodec

@JsonCodec
case class Shortened(url: String)
