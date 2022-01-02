package org.search.infrastructure.http

import cats.MonadThrow
import cats.effect.kernel.Async
import cats.implicits._
import fs2.io.file.Files
import org.http4s._
import org.http4s.dsl.io._
import org.search.core.domain.Phrase
import org.search.infrastructure.http.protocol.{EntityEncoders, QueryParamDecoders}
import org.search.infrastructure.repository.FileRepository
import org.search.util.json.JsonEncoders

class HttpRouter[F[_]: MonadThrow: Files: Async](fileRepository: FileRepository[F])
  extends JsonEncoders
    with EntityEncoders
    with QueryParamDecoders {

  val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "parSearch" :? PhraseQueryParamMatcher(phrase) +& OptParCompareQueryParamMatcher(parCompareEnabled) =>
        buildParSearchResponse(phrase, parCompareEnabled.getOrElse(false))
      case GET -> Root / "search"    :? PhraseQueryParamMatcher(phrase) => buildSearchResponse(phrase)
    }

  private def buildParSearchResponse(phrase: Phrase, parCompareEnabled: Boolean): F[Response[F]] =
    fileRepository
      .parSearchFiles(phrase, parCompareEnabled)
      .map(Response[F]().withEntity(_))

  private def buildSearchResponse(phrase: Phrase): F[Response[F]] =
    fileRepository
      .searchFiles(phrase)
      .map(Response[F]().withEntity(_))
}
