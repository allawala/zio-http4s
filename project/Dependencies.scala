import sbt._

object Dependencies {
  object Circe {
    private val version = "0.13.0"

    private val core          = "io.circe" %% "circe-core"           % version
    private val generic       = "io.circe" %% "circe-generic"        % version
    private val genericExtras = "io.circe" %% "circe-generic-extras" % version

    val all: Seq[ModuleID] = Seq(core, generic, genericExtras)
  }

  object Enumeratum {
    private val version = "1.6.1"

    private val enumeratum = "com.beachape" %% "enumeratum"       % version
    private val circe      = "com.beachape" %% "enumeratum-circe" % version

    val all: Seq[ModuleID] = Seq(enumeratum, circe)
  }

  object Http4s {
    private val version = "0.21.22"

    private val dsl         = "org.http4s" %% "http4s-dsl"          % version
    private val blazeServer = "org.http4s" %% "http4s-blaze-server" % version
    private val circe       = "org.http4s" %% "http4s-circe"        % version

    val all: Seq[ModuleID] = Seq(blazeServer, circe, dsl)
  }

  object Logging {
    private val GroovyVersion                 = "3.0.7"
    private val LogbackVersion                = "1.2.3"
    private val LogStashLogbackEncoderVersion = "6.6"

    private val groovy          = "org.codehaus.groovy"  % "groovy"                   % GroovyVersion
    private val logback         = "ch.qos.logback"       % "logback-classic"          % LogbackVersion
    private val logstashEncoder = "net.logstash.logback" % "logstash-logback-encoder" % LogStashLogbackEncoderVersion

    val all: Seq[ModuleID] = Seq(groovy, logback, logstashEncoder)
  }

  object Refined {
    private val version = "0.9.15"

    private val refined = "eu.timepit" %% "refined" % version

    val all: Seq[ModuleID] = Seq(refined)
  }

  object STTP {
    private val version = "2.2.8"

    val zioClient = "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % version
    val circe     = "com.softwaremill.sttp.client" %% "circe"                         % version
    val slf4j     = "com.softwaremill.sttp.client" %% "slf4j-backend"                 % version

    val all: Seq[ModuleID] = Seq(zioClient, circe, slf4j)
  }

  object Tapir {
    private val version = "0.16.16"

    private val zio              = "com.softwaremill.sttp.tapir" %% "tapir-zio"                % version
    private val zioHttp4sServer  = "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server"  % version
    private val jsonCirce        = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % version
    private val openApiDocs      = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % version
    private val openApiCirceYaml = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % version
    private val swaggerUiHttp4s  = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % version
    private val sttpClient       = "com.softwaremill.sttp.tapir" %% "tapir-sttp-client"        % version

    val all: Seq[ModuleID] = Seq(zio, zioHttp4sServer, jsonCirce, openApiDocs, openApiCirceYaml, swaggerUiHttp4s, sttpClient)
  }

  object ZIO {
    private val version            = "1.0.5"
    private val configVersion      = "1.0.2"
    private val interopCatsVersion = "2.4.0.0"
    private val loggingVersion     = "0.5.8"

    private val zio            = "dev.zio" %% "zio"                 % version
    private val macros         = "dev.zio" %% "zio-macros"          % version
    private val config         = "dev.zio" %% "zio-config"          % configVersion
    private val configMagnolia = "dev.zio" %% "zio-config-magnolia" % configVersion
    private val configTypesafe = "dev.zio" %% "zio-config-typesafe" % configVersion
    private val catsInterop    = "dev.zio" %% "zio-interop-cats"    % interopCatsVersion
    private val logging        = "dev.zio" %% "zio-logging"         % loggingVersion
    private val loggingSlf4j   = "dev.zio" %% "zio-logging-slf4j"   % loggingVersion

    val all: Seq[ModuleID] = Seq(zio, macros, config, configMagnolia, configTypesafe, catsInterop, logging, loggingSlf4j)
  }
}
