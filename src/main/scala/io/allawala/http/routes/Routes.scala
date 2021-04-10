package io.allawala.http.routes

import org.http4s.HttpRoutes
import zio.RIO

import scala.language.existentials

trait Routes {
  // To aggregate all the routes within a specific rest api sub path
  def routes: HttpRoutes[RIO[_, *]]
}
