package org.search.infrastructure.http

import cats.MonadThrow
import cats.data.EitherT
import cats.effect.kernel.Async
import cats.implicits._
import io.circe.literal._
import fs2.io.file.Files
import io.circe._, io.circe.parser._
import org.http4s._
import org.http4s.dsl.io._
import org.search.core.domain.{Occurrence, Phrase}
import org.search.infrastructure.grpc.SearchRemoteClient
import org.search.infrastructure.http.protocol.{EntityEncoders, QueryParamDecoders}
import org.search.infrastructure.repository.{ParallelSearchFileRepository, SearchFileRepository}
import org.search.json.{JsonDecoders, JsonEncoders}

class HttpRouter[F[_]: MonadThrow: Files: Async](searchFileRepository: SearchFileRepository[F],
                                                 parSearchFileRepository: ParallelSearchFileRepository[F],
                                                 searchRemoteClients: List[SearchRemoteClient[F]])
  extends JsonEncoders
    with JsonDecoders
    with EntityEncoders
    with QueryParamDecoders {

  val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "parSearch" :? PhraseQueryParamMatcher(phrase) +& OptParCompareQueryParamMatcher(parCompareEnabled) =>
        buildParSearchResponse(phrase, parCompareEnabled.getOrElse(false))
      case GET -> Root / "search" :? PhraseQueryParamMatcher(phrase) =>
        buildSearchResponse(phrase)
      case GET -> Root / "searchGrpc" :? PhraseQueryParamMatcher(phrase) =>
        buildSearchGrpcResponse(phrase)
    }

  private def buildParSearchResponse(phrase: Phrase, parCompareEnabled: Boolean): F[Response[F]] =
    parSearchFileRepository
      .parSearchFiles(phrase, parCompareEnabled)
      .map(Response[F]().withEntity(_))

  private def buildSearchResponse(phrase: Phrase): F[Response[F]] =
    searchFileRepository
      .searchFiles(phrase)
      .map(Response[F]().withEntity(_))

  private def buildSearchGrpcResponse(phrase: Phrase): F[Response[F]] =
    EitherT
      .right(Async[F].parTraverseN(searchRemoteClients.length)(searchRemoteClients)(_.searchRemote(phrase)))
      .subflatMap {
        _.traverse { r =>
          for {
            parsedResponse  <- parse(r.result)
            decodedResponse <- parsedResponse.as[List[Occurrence]]
          } yield decodedResponse
        }
      }
      .leftMap {
        case e: ParsingFailure => Response[F]()
          .withStatus(Status.BadGateway)
          .withEntity(s"Received incorrect json response from upstream grpc server: $e")
        case e: DecodingFailure => Response[F]()
          .withStatus(Status.InternalServerError)
          .withEntity(s"Internal error while decoding search occurrences: $e")
      }
      .map(responses => Response[F]().withEntity(responses.flatten))
      .merge
}
