package io.allawala.http

import io.allawala.{AppEnv, BitlyEnv}
import io.allawala.EvenMoreTapir._
import io.allawala.config.Configuration.HttpServerConfig
import io.allawala.http.routes.{HealthRoutes, ShortenRoutes}
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.server.blaze.BlazeServerBuilder
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import zio.clock.Clock
import zio.config.getConfig
import zio.duration._
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.{RIO, Runtime, Task, URIO, ZIO}
import org.http4s.implicits._
import cats.implicits._
import org.http4s.server.middleware.{Logger, RequestId}

object Server {
  private val yaml: String = {
    import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
    import sttp.tapir.openapi.circe.yaml._
    OpenAPIDocsInterpreter
      .toOpenAPI(
        HealthRoutes.HealthApi.endpoints ++ ShortenRoutes.ShortenApi.endpoints,
        "Get shortened urls",
        "1.0"
      )
      .toYaml
  }

  private val appRoutes: HttpApp[RIO[BitlyEnv, *]] = {
//    (ShortenRoutes.routes).orNotFound // This on its own works
    val appRoutes = from(
      ShortenRoutes.endpoints.map(_.widen[BitlyEnv]) ++ HealthRoutes.endpoints.map(_.widen[BitlyEnv])
    ).toRoutes

    val swaggerRoutes = new SwaggerHttp4s(yaml).routes[RIO[BitlyEnv, *]]

    (appRoutes <+> swaggerRoutes).orNotFound
  }
  // The one below is what i want to make work in the end, so that i can aggregate multiple rest api routes within each route module
//    (ShortenRoutes.routes <+> HealthRoutes.routes <+> new SwaggerHttp4s(yaml).routes[RIO[BitlyEnv, *]]).orNotFound

  def finalHttpApp(conf: HttpServerConfig, app: HttpApp[RIO[BitlyEnv, *]]): HttpApp[RIO[BitlyEnv, *]] =
    RequestId.httpApp {
      Logger.httpApp(conf.logRequests, conf.logRequests) {
        app
      }
    }

  val runServer: ZIO[AppEnv, Throwable, Unit] = {
    for {
      config                          <- getConfig[HttpServerConfig]
      implicit0(rts: Runtime[AppEnv]) <- ZIO.runtime[AppEnv]
      ec                              <- ZIO.descriptor.map(_.executor.asEC)
      app                              = appRoutes
      _ <-
        BlazeServerBuilder[RIO[BitlyEnv, *]](ec)
          .bindHttp(config.port, config.host)
          .withConnectorPoolSize(config.poolSize)
          .withIdleTimeout(config.idleTimeout.asScala)
          .withResponseHeaderTimeout(config.responseHeaderTimeout.asScala)
          .withoutBanner
          .withHttpApp(finalHttpApp(config, app))
          .serve
          .compile
          .drain
    } yield ()
  }
}
