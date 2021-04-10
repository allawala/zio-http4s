package io.allawala
import io.allawala.http.Server
import zio.{ExitCode, URIO}


object Main extends zio.App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    Server.runServer
//        .tapError(err => log.throwable(s"Execution failed with ${err.getMessage}", err))
      .provideCustomLayer(getAppLayer)
      .exitCode
  }
}
