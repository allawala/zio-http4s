package io.allawala.config

import org.http4s.blaze.channel
import zio.config._
import zio.config.magnolia.DeriveConfigDescriptor
import zio.config.syntax._
import zio.config.typesafe.TypesafeConfig
import zio.duration._
import zio.{Has, Layer, ZLayer}

object Configuration {
  case class HttpServerConfig(
    host: String,
    port: Int,
    logRequests: Boolean,
    poolSize: Int = channel.DefaultPoolSize,
    responseHeaderTimeout: Duration = 30.seconds,
    idleTimeout: Duration = 60.seconds
  )

  case class HttpClientConfig(
    maxConnections: Int = 50,
    maxConnectionsPerHost: Int = 5,
    connectionTimeout: Duration = 30.seconds,
    requestTimeout: Duration = 60.seconds,
    pooledConnectionIdleTimeout: Duration = 10.minutes
  )

  case class BitlyConfig(
    protocol: String = "http",
    host: String,
    port: Int = 8080,
    jwtToken: String
  )

  val environment: Environment = sys.env.get("ENV").map(Environment.withNameInsensitive).getOrElse(Environment.Local)

  case class AppConfig(httpServer: HttpServerConfig, httpClient: HttpClientConfig, bitlyClient: BitlyConfig)

  private val configDescription = DeriveConfigDescriptor.descriptor[AppConfig].mapKey(toKebabCase)

  val live: Layer[ReadError[String], Has[AppConfig]] = {
    environment match {
      case Environment.Local => // Do nothing
      case env =>
        sys.props("logback.configurationFile") = s"logback.${env.entryName}.groovy"
        sys.props("config.resource") = s"application.${env.entryName}.conf"
    }
    TypesafeConfig.fromDefaultLoader(configDescription)
  }
  val serverConfig: ZLayer[Any, ReadError[String], Has[HttpServerConfig]] = live.narrow(_.httpServer)
  val clientConfig: ZLayer[Any, ReadError[String], Has[HttpClientConfig]] = live.narrow(_.httpClient)
  val bitlyConfig: ZLayer[Any, ReadError[String], Has[BitlyConfig]]       = live.narrow(_.bitlyClient)
}
