package org.search.infrastructure.grpc

import cats.effect.Resource
import cats.effect.kernel.Async
import cats.implicits._
import io.circe.syntax.EncoderOps
import io.grpc.{Metadata, ServerServiceDefinition}
import org.search.core.domain.Phrase
import org.search.infrastructure.repository.SearchFileRepository
import org.search.json.JsonEncoders
import org.search.protos.search.{SearchRemoteFs2Grpc, SearchRemoteReply, SearchRemoteRequest}

class SearchGrpcService[F[_]: Async](fileRepository: SearchFileRepository[F])
  extends SearchRemoteFs2Grpc[F, Metadata]
    with JsonEncoders {

  override def search(request: SearchRemoteRequest, ctx: Metadata): F[SearchRemoteReply] =
    fileRepository
      .searchFiles(Phrase(request.phrase))
      .map(occurrences => SearchRemoteReply(occurrences.asJson.noSpaces))
}

object SearchGrpcService {
  def createGrpcService[F[_]: Async](fileRepository: SearchFileRepository[F]): Resource[F, ServerServiceDefinition] =
    SearchRemoteFs2Grpc.bindServiceResource[F](new SearchGrpcService[F](fileRepository))
}
