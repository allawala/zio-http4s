package io.allawala.httpclient

import sttp.client.HttpError

trait DomainError extends Throwable {
  def message: String
  def cause: Throwable          = None.orNull
  def details: Map[String, Any] = Map.empty

  override def getMessage: String  = message
  override def getCause: Throwable = cause
}

object DomainError {
  final case class MissingCorrelationIdError() extends DomainError {
    override def message: String = "correlation id missing"
  }

  final case class UnexpectedError(override val cause: Throwable) extends DomainError {
    override val message: String = "unexpected error"
  }
}

sealed trait ClientError extends DomainError {
  override def details: Map[String, Any] =
    cause match {
      case httpError: HttpError => super.details ++ Map("raw" -> httpError.body)
      case _                    => super.details
    }
}

object ClientError {
  final case class RequestTimedOut(message: String) extends ClientError

  final case class ErrorFetchingUrl(url: String, override val cause: Throwable) extends Throwable with ClientError {
    override def message: String = s"Error getting shortened url for ${url}"
  }
}
