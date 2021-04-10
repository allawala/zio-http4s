package io.allawala.http.routes

import io.allawala.EvenMoreTapir.EndpointInput
import io.allawala.EvenMoreTapir._
import io.allawala.http.ErrorInfo._
import sttp.tapir.ztapir.ZEndpoint
import sttp.model.StatusCode
import zio.interop.catz._

trait Api {
  def basePath: EndpointInput[Unit] = ""
  def endpoints: Seq[ZEndpoint[_, _, _]] = Seq.empty

  val httpErrors = oneOf(
    statusMapping(StatusCode.InternalServerError, jsonBody[InternalServerError]),
    statusMapping(StatusCode.BadRequest, jsonBody[BadRequest]),
    statusMapping(StatusCode.NotFound, jsonBody[NotFound])
  )

}
