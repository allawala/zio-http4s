package io.allawala.http

import org.http4s.util.CaseInsensitiveString
import sttp.tapir.model.ServerRequest

case class ReqCtx(correlationId: Option[String], method: String, url: String)

object ReqCtx {
  def apply(req: ServerRequest) =
    new ReqCtx(
      correlationId = req.header(CaseInsensitiveString("X-Request-ID").value),
      method = req.method.toString(),
      url = req.uri.toString
    )
}
