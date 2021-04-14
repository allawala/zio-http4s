package io.allawala.http.routes

import sttp.tapir.ztapir.ZServerEndpoint

import scala.language.existentials

trait Routes[R] {
  // To aggregate all the endpoints within a specific rest api sub path
  def endpoints: List[ZServerEndpoint[R, _, _, _]]
}
