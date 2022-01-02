package org.search.infrastructure.http.protocol

import io.circe.{Encoder, Printer}
import org.http4s.EntityEncoder
import org.http4s.circe.CirceInstances

trait EntityEncoders {

  val circeInstances: CirceInstances =
    CirceInstances
      .withPrinter(Printer.noSpaces)
      .build

  implicit def jsonEncoder[F[_], A: Encoder]: EntityEncoder[F, A] =
    circeInstances.jsonEncoderOf[F, A]
}
