package io.allawala

import sttp.tapir.client.sttp.TapirSttpClient
import sttp.tapir.docs.openapi.TapirOpenAPIDocs
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.openapi.circe.yaml.TapirOpenAPICirceYaml
import sttp.tapir.server.http4s.TapirHttp4sServer
import sttp.tapir.{Tapir, TapirAliases}

object EvenMoreTapir
    extends Tapir
    with TapirHttp4sServer
    with TapirJsonCirce
    with TapirAliases
    with TapirOpenAPICirceYaml
    with TapirOpenAPIDocs
    with TapirSttpClient
