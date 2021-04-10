package io.allawala.httpclient

import io.allawala.config.ClientConfiguration
import io.allawala.config.Configuration.HttpClientConfig
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import sttp.client.asynchttpclient.zio.{AsyncHttpClientZioBackend, SttpClient}
import zio.ZLayer
import zio.config.getConfig

object Client {
  val live: ZLayer[ClientConfiguration, Throwable, SttpClient] =
    ZLayer.fromManaged(
      for {
        cfg <- getConfig[HttpClientConfig].toManaged_
        client <- AsyncHttpClientZioBackend.managedUsingConfig(makeAsyncClientConfig(cfg))
      } yield client
    )

  private def makeAsyncClientConfig(cfg: HttpClientConfig) = {
    new DefaultAsyncHttpClientConfig.Builder()
      .setMaxConnections(cfg.maxConnections)
      .setMaxConnectionsPerHost(cfg.maxConnectionsPerHost)
      .setConnectTimeout(cfg.connectionTimeout.toMillis.toInt)
      .setRequestTimeout(cfg.requestTimeout.toMillis.toInt)
      .setPooledConnectionIdleTimeout(cfg.pooledConnectionIdleTimeout.toMillis.toInt)
      .build()
  }
}
