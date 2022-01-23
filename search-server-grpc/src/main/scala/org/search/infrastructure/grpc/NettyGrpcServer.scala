package org.search.infrastructure.grpc

import cats.effect.Resource
import cats.effect.kernel.Sync
import fs2.grpc.syntax.all.fs2GrpcSyntaxServerBuilder
import io.grpc.{Server, ServerServiceDefinition}
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder

class NettyGrpcServer[F[_]: Sync](server: Server) {
  def start(): Server = server.start()
}

object NettyGrpcServer {
  case class Config(host: String, port: Int)

  def apply[F[_]: Sync](config: Config, searchRemoteService: ServerServiceDefinition): Resource[F, NettyGrpcServer[F]] =
    for {
      server <- NettyServerBuilder
        .forPort(config.port)
        .addService(searchRemoteService)
        .resource[F]
    } yield new NettyGrpcServer[F](server)
}