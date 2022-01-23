package org.search.json

import cats.syntax.either._
import fs2.io.file.Path
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.search.core.domain.Occurrence

trait JsonDecoders {
  implicit val pathDecoder: Decoder[Path] =
    Decoder
      .decodeString
      .emap { str =>
        Either.catchNonFatal(Path(str)).leftMap(_ => "Invalid path format")
      }

  implicit val occurrenceDecoder: Decoder[Occurrence] = deriveDecoder[Occurrence]
}
