package io.allawala.http

import io.allawala.config.Logger
import io.allawala.httpclient.ClientError.ErrorFetchingUrl
import io.allawala.httpclient.DomainError
import io.allawala.httpclient.DomainError.UnexpectedError
import io.circe.generic.JsonCodec
import zio.logging.{Logging, log}
import zio.{Cause, ZIO}

sealed trait ErrorInfo extends Product with Serializable {
  def correlationId: String
  def message: String
}

object ErrorInfo {
  @JsonCodec
  final case class InternalServerError(correlationId: String, message: String) extends ErrorInfo

  @JsonCodec
  final case class BadRequest(correlationId: String, message: String) extends ErrorInfo

  @JsonCodec
  final case class NotFound(correlationId: String, message: String) extends ErrorInfo

  /**
    * Expose the full error cause when handling a request and recovers failures and deaths appropriately.
    */
  implicit class mapError[R, E, A](private val f: ZIO[R, E, A]) extends AnyVal {
      def mapToClientError(reqCtx: ReqCtx): ZIO[R with Logging, ErrorInfo, A] = {
      val correlationId = reqCtx.correlationId

      f.tapError { error =>
        val de: DomainError = error match {
          case e: DomainError => e
          case t: Throwable   => UnexpectedError(t)
        }
        log.locally(
          _.annotate(Logger.logDetails, de.details)
            .annotate(Logger.correlationId, correlationId)
            .annotate(Logger.requestMethod, Some(reqCtx.method))
            .annotate(Logger.requestUrl, Some(reqCtx.url))
        ) {
          log.throwable(de.message, de)
        }

      }.mapErrorCause { cause =>
        val clientFailure = cause.failureOrCause match {
          case Left(e: ErrorFetchingUrl) => InternalServerError(correlationId.getOrElse("unknown"), e.message)
          case _                         => InternalServerError(correlationId.getOrElse("unknown"), "unexpected exception")
        }
        Cause.fail(clientFailure)
      }
    }
  }
}
