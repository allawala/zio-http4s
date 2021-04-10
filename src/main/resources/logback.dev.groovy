import net.logstash.logback.encoder.LogstashEncoder

def appenders = ["CONSOLE"]

appender("CONSOLE", ConsoleAppender) {
    encoder(LogstashEncoder)
}

logger("org.http4s", INFO)
logger("io.netty", INFO)
logger("org.asynchttpclient.netty.channel.DefaultChannelPool", ERROR)

root(DEBUG, appenders)
