package io.allawala.httpclient

import io.allawala.config.BitlyConfiguration
import io.allawala.config.Configuration.BitlyConfig
import io.allawala.httpclient.ClientError.{ErrorFetchingUrl, RequestTimedOut}
import io.allawala.model.Shortened
import io.circe.generic.JsonCodec
import sttp.client._
import sttp.client.asynchttpclient.zio.SttpClient
import sttp.client.circe._
import sttp.model.{MediaType, Uri}
import zio.clock.Clock
import zio.duration._
import zio._

object BitlyClient {
  @JsonCodec
  case class Shorten(long_url: String, domain: String = "bit.ly")

  trait Service {
    def getShortUrl(longUrl: String): RIO[Clock, Shortened]
  }

  def getShortUrl(longUrl: String): RIO[BitlyClient with Clock, Shortened] = RIO.accessM(_.get.getShortUrl(longUrl))

  final case class SttpBitlyClient(client: SttpClientService, config: BitlyConfig) extends BitlyClient.Service {
    private val endpoint = Uri(config.protocol, config.host, config.port)
    private val basePath = "/v4"

    override def getShortUrl(longUrl: String): RIO[Clock, Shortened] = {
      val path = s"${basePath}/shorten"
      val request = basicRequest
        .post(endpoint.withPath(path))
        .auth
        .bearer(config.jwtToken)
        .contentType(MediaType.ApplicationJson)
        .body(Shorten(longUrl))
        .response(asJson[Shortened])

      client
        .send(request)
        .timeoutFail(RequestTimedOut(s"get short url $longUrl"))(30.seconds)
        .map(_.body)
        .absolve
        .bimap(err => ErrorFetchingUrl(longUrl, err), res => res)
    }
  }

  val live: URLayer[BitlyConfiguration with SttpClient, BitlyClient] =
    ZLayer.fromEffect(
      for {
        client <- ZIO.service[SttpClientService]
        config <- ZIO.service[BitlyConfig]
      } yield new SttpBitlyClient(client, config)
    )

  val mocked: ULayer[BitlyClient] = ZLayer.succeed(
    new Service {
      override def getShortUrl(longUrl: String): Task[Shortened] =
        Task.succeed(Shortened(longUrl))
    }
  )
}
