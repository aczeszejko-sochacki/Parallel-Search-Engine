package org.search.main.config

import org.search.core.services.SearchService
import pureconfig._
import pureconfig.generic.auto._
import org.search.infrastructure.http.HttpServer
import org.search.infrastructure.repository.FileRepository

case class Config(httpServer: HttpServer.Config, fileRepository: FileRepository.Config, search: SearchService.Config)

object Config {
  case class ConfigLoadException(errors: String) extends RuntimeException

  def load: Config =
    ConfigSource
      .default
      .loadOrThrow[Config]
}