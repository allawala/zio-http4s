package io.allawala.http

import io.allawala.AppEnv
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
import zio.{Runtime, Task, URIO, ZIO}
import org.http4s.implicits._
import cats.implicits._
import org.http4s.server.middleware.{Logger, RequestId}

object Server {
  private val yaml = (
    HealthRoutes.HealthApi.endpoints ++
        ShortenRoutes.ShortenApi.endpoints
  ).toOpenAPI("Get shortened urls", "1.0").toYaml

  private val appRoutes: URIO[AppEnv, HttpApp[Task]] =
    for {
      healthRoutes  <- HealthRoutes.routes
      shortenRoutes <- ShortenRoutes.routes
      docsRoutes     = new SwaggerHttp4s(yaml).routes[Task]
    } yield {
      (healthRoutes <+> shortenRoutes <+> docsRoutes).orNotFound
    }

  def finalHttpApp(conf: HttpServerConfig, app: HttpApp[Task]): HttpApp[Task] =
    RequestId.httpApp {
      Logger.httpApp(conf.logRequests, conf.logRequests) {
        app
      }
    }

  val runServer: ZIO[AppEnv, Throwable, Unit] = {
    for {
      app                            <- appRoutes
      config                         <- getConfig[HttpServerConfig]
      implicit0(rts: Runtime[AppEnv]) <- ZIO.runtime[AppEnv]
      ec                             <- ZIO.descriptor.map(_.executor.asEC)
      _ <-
        BlazeServerBuilder[Task](ec)
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
