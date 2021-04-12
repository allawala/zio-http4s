package io.allawala.http.routes

import io.allawala.EvenMoreTapir.EndpointInput
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.ztapir._
import zio.clock.Clock
import zio.interop.catz._
import zio.{RIO, UIO}

object HealthRoutes extends Routes[Any] {
  object HealthApi extends Api {
    override val basePath: EndpointInput[Unit] = "health"

    val getHealthEndpoint: ZEndpoint[Unit, Unit, String] =
      endpoint.get.in(basePath).out(stringBody).description("Health check")

    override val endpoints: Seq[ZEndpoint[_, _, _]] = Seq(getHealthEndpoint)
  }

  val healthEndpoint: ZServerEndpoint[Any, Unit, Unit, String] =
    HealthApi.getHealthEndpoint.zServerLogic(_ => UIO.succeed("Ok"))

  override def endpoints = List(healthEndpoint)
}
