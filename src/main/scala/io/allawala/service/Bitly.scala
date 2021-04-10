package io.allawala.service

import io.allawala.httpclient.BitlyClient
import io.allawala.model.Shortened
import zio.clock.Clock
import zio.{RIO, ULayer, ZLayer}

object Bitly {

  trait Service {
    def shortenUrl(longUrl: String): RIO[BitlyClient with Clock, Shortened]
  }

  def shortenUrl(longUrl: String): RIO[Bitly with BitlyClient with Clock, Shortened] = RIO.accessM(_.get.shortenUrl(longUrl))

  val live: ULayer[Bitly] = ZLayer.succeed(
    new Service {
      override def shortenUrl(longUrl: String): RIO[BitlyClient with Clock, Shortened] = {
        BitlyClient.getShortUrl(longUrl)
      }
    }
  )
}
