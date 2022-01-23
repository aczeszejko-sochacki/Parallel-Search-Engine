package org.search.infrastructure.grpc

import cats.effect.Resource
import cats.effect.kernel.Async
import fs2.grpc.syntax.all.fs2GrpcSyntaxManagedChannelBuilder
import io.grpc.{ManagedChannel, Metadata}
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import org.search.core.domain.Phrase
import org.search.protos.search.{SearchRemoteFs2Grpc, SearchRemoteReply, SearchRemoteRequest}

class SearchRemoteClient[F[_]: Async](managedChannel: ManagedChannel) {
  def searchRemote(phrase: Phrase): F[SearchRemoteReply] =
    SearchRemoteFs2Grpc
      .stubResource[F](managedChannel)
      .use(_.search(SearchRemoteRequest(phrase.value), new Metadata()))
}

object SearchRemoteClient {
  case class Config(host: String, port: Int)

  def apply[F[_]: Async](config: Config): Resource[F, SearchRemoteClient[F]] =
    for {
      managedChannelResource <- NettyChannelBuilder
        .forAddress(config.host, config.port)
        .usePlaintext()
        .resource[F]
      searchRemoteClient = new SearchRemoteClient[F](managedChannelResource)
    } yield searchRemoteClient
}
