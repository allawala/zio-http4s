package io.allawala

import io.allawala.config.Configuration.{BitlyConfig, HttpClientConfig, HttpServerConfig}
import zio.Has

package object config {
  type ServerConfiguration = Has[HttpServerConfig]
  type ClientConfiguration = Has[HttpClientConfig]
  type BitlyConfiguration = Has[BitlyConfig]
  type Configuration =  ServerConfiguration with ClientConfiguration with BitlyConfiguration
}
