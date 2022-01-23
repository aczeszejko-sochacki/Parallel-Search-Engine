package org.search.main.config

import org.search.infrastructure.grpc.NettyGrpcServer
import org.search.infrastructure.repository.SearchFileRepository
import pureconfig._
import pureconfig.generic.auto._

case class Config(grpcServer: NettyGrpcServer.Config, searchFileRepository: SearchFileRepository.Config)

object Config {
  case class ConfigLoadException(errors: String) extends RuntimeException

  def load: Config =
    ConfigSource
      .default
      .loadOrThrow[Config]
}
