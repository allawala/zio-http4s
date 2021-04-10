package io.allawala.http.routes

import org.http4s.HttpRoutes
import zio.{Task, URIO}

trait Routes {
  def routes: URIO[_, HttpRoutes[Task]]
}
