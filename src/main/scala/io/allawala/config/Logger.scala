package io.allawala.config

import zio.ZLayer
import zio.clock.Clock
import zio.console.Console
import zio.logging.{LogAnnotation, LogFormat, LogLevel, Logging}
import zio.logging.slf4j.Slf4jLogger

object Logger {
  val correlationId: LogAnnotation[Option[String]] = LogAnnotation[Option[String]](
    name = "correlation-id",
    initialValue = None,
    combine = (_, r) => r,
    render = _.getOrElse("undefined-correlation-id")
  )

  val logDetails: LogAnnotation[Map[String, Any]] = LogAnnotation[Map[String, Any]](
    name = "log-details",
    initialValue = Map.empty,
    combine = (_, r) => r,
    render = _.toString
  )

  val requestMethod: LogAnnotation[Option[String]] = LogAnnotation[Option[String]](
    name = "request-method",
    initialValue = None,
    combine = (_, r) => r,
    render = _.getOrElse("")
  )

  val requestUrl: LogAnnotation[Option[String]] = LogAnnotation[Option[String]](
    name = "request-url",
    initialValue = None,
    combine = (_, r) => r,
    render = _.getOrElse("")
  )

  val live: ZLayer[Any, Nothing, Logging] =
    Slf4jLogger.makeWithAnnotationsAsMdc(List(correlationId, logDetails, requestMethod, requestUrl))

  val test: ZLayer[Console with Clock, Nothing, Logging] = Logging.console(
    logLevel = LogLevel.Debug,
    format = LogFormat.ColoredLogFormat(),
  )
}
