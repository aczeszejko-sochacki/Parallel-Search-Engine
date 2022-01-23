package org.search.json

import fs2.io.file.Path
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps
import org.search.core.domain.Occurrence

trait JsonEncoders {

  implicit val pathEncoder: Encoder[Path] =
    Encoder.instance { (path: Path) =>
      path.toString.asJson
    }

  implicit val occurrenceEncoder: Encoder[Occurrence] = deriveEncoder[Occurrence]
}
