package org.search.infrastructure.http

import cats.effect.{Async, Resource}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Server
import org.search.infrastructure.http.HttpServer.Config

import scala.concurrent.duration.DurationInt

class HttpServer[F[_]: Async](config: Config, router: HttpRouter[F]) {
  def run: Resource[F, Server] =
    BlazeServerBuilder[F]
      .withResponseHeaderTimeout(config.responseTimeout.seconds)
      .bindHttp(config.port, config.host)
      .withHttpApp(router.routes.orNotFound)
      .resource
}

object HttpServer {
  case class Config(responseTimeout: Int, host: String, port: Int)
}
