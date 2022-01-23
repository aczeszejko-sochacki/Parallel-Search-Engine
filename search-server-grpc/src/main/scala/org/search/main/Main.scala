package org.search.main

import cats.effect.{IO, Resource, ResourceApp}
import org.search.core.services.SearchService
import org.search.infrastructure.grpc.{NettyGrpcServer, SearchGrpcService}
import org.search.infrastructure.repository.SearchFileRepository
import org.search.main.config.Config

object Main extends ResourceApp.Forever {

  override def run(args: List[String]): Resource[IO, Unit] = {
    for {
      config               <- Resource.pure(Config.load)
      searchService        = new SearchService[IO]()
      searchFileRepository = new SearchFileRepository[IO](config.searchFileRepository, searchService)
      searchGrpcService    <- SearchGrpcService.createGrpcService(searchFileRepository)
      nettyServer          <- NettyGrpcServer[IO](config.grpcServer, searchGrpcService)
      _                    <- Resource.eval(IO.delay(nettyServer.start()))
    } yield ()
  }
}
