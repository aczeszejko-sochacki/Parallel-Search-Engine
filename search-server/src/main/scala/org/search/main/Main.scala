package org.search.main

import cats.effect.{ExitCode, IO, IOApp, Resource, ResourceApp}
import org.search.core.services.SearchService
import org.search.infrastructure.http.{HttpRouter, HttpServer}
import org.search.infrastructure.repository.FileRepository
import org.search.main.config.Config
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends ResourceApp.Forever {
  override def run(args: List[String]) =
    for {
      logger         <- Resource.eval(Slf4jLogger.create[IO])
      config         = Config.load
      searchService  = new SearchService[IO](config.search)
      fileRepository = new FileRepository[IO](config.fileRepository, searchService)
      httpRouter     = new HttpRouter[IO](fileRepository)
      httpServer     = new HttpServer[IO](config.httpServer, httpRouter)
      _              <- Resource.eval(logger.info("Starting http server... "))
      _              <- httpServer.run
  } yield ()
}
