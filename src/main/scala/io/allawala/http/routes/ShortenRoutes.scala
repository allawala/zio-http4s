package io.allawala.http.routes

import io.allawala.BitlyEnv
import io.allawala.EvenMoreTapir._
import io.allawala.http.ErrorInfo._
import io.allawala.http.{ErrorInfo, ReqCtx}
import io.allawala.model.Shortened
import io.allawala.service.Bitly
import sttp.tapir.generic.auto._

object ShortenRoutes extends Routes[BitlyEnv] {

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

  val shortenEndpoint: ZServerEndpoint[BitlyEnv, (ReqCtx, String), ErrorInfo, Shortened] =
    ShortenApi.getShortenEndpoint.zServerLogic {
      case (reqCtx, url) =>
        Bitly.shortenUrl(url).mapToClientError(reqCtx)
    }

  override def endpoints = List(shortenEndpoint)
}
