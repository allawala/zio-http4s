package io.allawala

import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.openapi.circe.yaml.TapirOpenAPICirceYaml
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.{Tapir, TapirAliases}

object EvenMoreTapir
    extends Tapir
    with Http4sServerInterpreter
    with TapirJsonCirce
    with TapirAliases
    with TapirOpenAPICirceYaml
    with OpenAPIDocsInterpreter
    with SttpClientInterpreter
