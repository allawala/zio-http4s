package io.allawala.http.routes

import io.allawala.BitlyEnv
import io.allawala.EvenMoreTapir._
import io.allawala.http.ErrorInfo._
import io.allawala.http.{ErrorInfo, ReqCtx}
import io.allawala.model.Shortened
import io.allawala.service.Bitly
import org.http4s.HttpRoutes
import sttp.tapir.generic.auto._
import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.ztapir.ZEndpoint
import zio.RIO
import zio.interop.catz._

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

  val shortenRoute: HttpRoutes[RIO[BitlyEnv, *]] = ZHttp4sServerInterpreter.from(ShortenApi.getShortenEndpoint) { req =>
    val (reqCtx, url) = req
    Bitly.shortenUrl(url).mapToClientError(reqCtx)
  }.toRoutes

  // TODO what would the method signature be now?
  override def routes: HttpRoutes[RIO[BitlyEnv, *]] = shortenRoute
}
