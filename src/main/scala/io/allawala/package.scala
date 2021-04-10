package io

import io.allawala.config.{Configuration, Environment, Logger}
import io.allawala.httpclient.{BitlyClient, Client}
import io.allawala.service.Bitly
import zio.ZLayer
import zio.clock.Clock
import zio.logging.Logging

package object allawala {
  // For the server and the routes
  type BitlyEnv = Bitly with BitlyClient with Logging with Clock
  type AppEnv   = BitlyEnv with Configuration with Clock


  // For the runtime
  lazy val getAppLayer: ZLayer[Any, Throwable, AppEnv] = {
    val loggerLayer = Logger.live
    val configLayer = Configuration.serverConfig ++ Configuration.clientConfig ++ Configuration.bitlyConfig
    val bitlyClientLayer = Configuration.environment match {
      case Environment.Local => BitlyClient.mocked
      case _ =>
        val httpClientLayer = Configuration.clientConfig >>> Client.live
        httpClientLayer ++ Configuration.bitlyConfig >>> BitlyClient.live
    }
    val bitlyLayer = Bitly.live

    val appLayer: ZLayer[Any, Throwable, AppEnv] =
      configLayer ++ bitlyClientLayer ++ bitlyLayer ++ Clock.live ++ loggerLayer
    appLayer
  }
}
