package io.allawala

import sttp.client.SttpBackend
import sttp.client.asynchttpclient.WebSocketHandler
import zio.stream.Stream
import zio.{Has, Task}

package object httpclient {
  type SttpClientService = SttpBackend[Task, Stream[Throwable, Byte], WebSocketHandler]
  type BitlyClient = Has[BitlyClient.Service]

}
