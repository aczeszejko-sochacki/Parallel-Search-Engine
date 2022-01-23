package org.search.main

import cats.effect.{IO, Resource, ResourceApp}
import cats.implicits.toTraverseOps
import org.search.core.services.{ParSearchService, SearchService}
import org.search.infrastructure.grpc.SearchRemoteClient
import org.search.infrastructure.http.{HttpRouter, HttpServer}
import org.search.infrastructure.repository.{ParallelSearchFileRepository, SearchFileRepository}
import org.search.main.config.Config
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends ResourceApp.Forever {
  override def run(args: List[String]): Resource[IO, Unit] =
    for {
      logger                  <- Resource.eval(Slf4jLogger.create[IO])
      config                  = Config.load
      searchService           = new SearchService[IO]()
      parSearchService        = new ParSearchService[IO](config.parSearch)
      searchFileRepository    = new SearchFileRepository[IO](config.searchFileRepository, searchService)
      parSearchFileRepository = new ParallelSearchFileRepository[IO](config.parSearchFileRepository, parSearchService)
      searchRemoteClients     <- config.searchGrpcServers.traverse(SearchRemoteClient[IO])
      httpRouter              = new HttpRouter[IO](searchFileRepository, parSearchFileRepository, searchRemoteClients)
      httpServer              = new HttpServer[IO](config.httpServer, httpRouter)
      _                       <- Resource.eval(logger.info("Starting http server... "))
      _                       <- httpServer.run
  } yield ()
}
