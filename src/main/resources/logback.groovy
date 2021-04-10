def appenders = ["CONSOLE"]

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %highlight([%-5level]) [%X{correlation-id}] - %logger{36} %msg%n"
    }
}

appender("ERROR", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %highlight([%-5level]) [%X{correlation-id}] [%X{request-method}] [%X{request-url}] [%X{log-details}] %n%logger{36} %msg%n"
    }
}

logger("io.allawala", ERROR, ["ERROR"])
logger("org.http4s", INFO)
logger("io.netty", INFO)
logger("org.asynchttpclient.netty.channel.DefaultChannelPool", ERROR)

root(DEBUG, appenders)
