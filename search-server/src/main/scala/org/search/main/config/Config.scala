package org.search.main.config

import org.search.core.services.ParSearchService
import org.search.infrastructure.grpc.SearchRemoteClient
import pureconfig._
import pureconfig.generic.auto._
import org.search.infrastructure.http.HttpServer
import org.search.infrastructure.repository.{ParallelSearchFileRepository, SearchFileRepository}

case class Config(httpServer: HttpServer.Config,
                  searchFileRepository: SearchFileRepository.Config,
                  parSearchFileRepository: ParallelSearchFileRepository.Config,
                  parSearch: ParSearchService.Config,
                  searchGrpcServers: List[SearchRemoteClient.Config])

object Config {
  case class ConfigLoadException(errors: String) extends RuntimeException

  def load: Config =
    ConfigSource
      .default
      .loadOrThrow[Config]
}