package org.search.core.services

import cats.effect.Concurrent
import cats.implicits._
import fs2.io.file.Path
import fs2.{Chunk, Pipe, Stream}
import org.search.core.domain.{Occurrence, Phrase}

class SearchService[F[_]: Concurrent] {

  def searchPhrase(file: Path, phrase: Phrase): Pipe[F, Byte, Occurrence] =
    prepareChunks(_, phrase)
      .evalMap {
        case (chunk, offset) => (exactPatternMatch(phrase, chunk), offset).pure[F]
      }
      .through(buildOccurrences(file))

  private def prepareChunks(input: Stream[F, Byte], phrase: Phrase): Stream[F, (Chunk[Byte], Int)] =
    input
      .sliding(phrase.length)
      .parZip(Stream.iterate(1)(_ + 1))

  private def buildOccurrences(file: Path): Pipe[F, (Boolean, Int), Occurrence] =
    _
      .filter(_._1)
      .map { case (_, offset) => Occurrence(file, offset) }

  private def exactPatternMatch(phrase: Phrase, chunk: Chunk[Byte]): Boolean =
    phrase.toBytes == chunk.toList
}
