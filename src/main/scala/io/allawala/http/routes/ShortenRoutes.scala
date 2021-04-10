package io.allawala.http.routes

import io.allawala.BitlyEnv
import io.allawala.EvenMoreTapir._
import io.allawala.http.ErrorInfo._
import io.allawala.http.{ErrorInfo, ReqCtx}
import io.allawala.httpclient.BitlyClient
import io.allawala.model.Shortened
import io.allawala.service.Bitly
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.ztapir.ZEndpoint
import zio.clock.Clock
import zio.interop.catz._
import zio.logging.Logging
import zio.{RIO, Task, URIO, ZIO}

object ShortenRoutes extends Routes {

  object ShortenApi extends Api {
    override def basePath: EndpointInput[Unit] = "api" / "v1.0"

    val getShortenEndpoint: ZEndpoint[(ReqCtx, String), ErrorInfo, Shortened] =
      endpoint.get
        .in(basePath / "shorten")
        .in(extractFromRequest(ReqCtx(_)))
        .in(query[String]("url").example("www.google.com"))
        .out(jsonBody[Shortened])
        .errorOut(httpErrors)
        .description("Get shortened url")

    override def endpoints: Seq[ZEndpoint[_, _, _]] = Seq(getShortenEndpoint)
  }

  private val shortenRoute: URIO[BitlyEnv, HttpRoutes[Task]] =
    ShortenApi.getShortenEndpoint.toRoutesR {
      case (reqCtx, url) =>
        Bitly.shortenUrl(url).mapToClientError(reqCtx)
    }

  override def routes: URIO[BitlyEnv, HttpRoutes[Task]] = shortenRoute
}
