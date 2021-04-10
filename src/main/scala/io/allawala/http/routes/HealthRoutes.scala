package io.allawala.http.routes

import io.allawala.EvenMoreTapir.EndpointInput
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.ztapir._
import zio.interop.catz._
import zio.{Task, UIO, URIO, ZIO}

object HealthRoutes extends Routes {
  object HealthApi extends Api {
    override val basePath: EndpointInput[Unit] = "health"

    val getHealthEndpoint: ZEndpoint[Unit, Unit, String] =
      endpoint.get.in(basePath).out(stringBody).description("Health check")

    override val endpoints: Seq[ZEndpoint[_, _, _]] = Seq(getHealthEndpoint)
  }

  private val healthRoute: UIO[HttpRoutes[Task]] = HealthApi.getHealthEndpoint.toRoutesR(_ => UIO.succeed("Ok"))

  override def routes: URIO[Any, HttpRoutes[Task]] = healthRoute
}
