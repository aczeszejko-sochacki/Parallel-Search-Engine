package org.search.core.services

import cats.effect.Concurrent
import cats.implicits._
import fs2.{Chunk, Pipe, Stream}
import fs2.io.file.Path
import org.search.core.domain.{Occurrence, Phrase}
import org.search.core.services.ParSearchService.Config

class ParSearchService[F[_]: Concurrent](config: Config) {
  def parSearchPhrase(file: Path, phrase: Phrase): Pipe[F, Byte, Occurrence] =
    prepareChunks(_, phrase)
      .parEvalMapUnordered(config.parallelism){
        case (chunk, offset) => (exactPatternMatch(phrase, chunk), offset).pure[F]
      }
      .through(buildOccurrences(file))

  def parSearchPhraseParCompare(file: Path, phrase: Phrase): Pipe[F, Byte, Occurrence] =
    prepareChunks(_, phrase)
      .parEvalMapUnordered(config.parallelism){
        case (chunk, offset) => exactParPatternMatch(phrase, chunk).product(offset.pure[F])
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

  private def exactParPatternMatch(phrase: Phrase, chunk: Chunk[Byte]): F[Boolean] =
    for {
      chunks         <- chunk.toList.sliding(config.phraseChunkSize, config.phraseChunkSize).pure[F]
      phraseChunks   = phrase.toBytes.sliding(config.phraseChunkSize, config.phraseChunkSize)
      comparedChunks <- Concurrent[F].parTraverseN(config.parallelism)(phraseChunks.zip(chunks).toList) {
        case (chunk, phraseChunk) => (chunk == phraseChunk).pure[F]
      }
      result         = comparedChunks.forall(identity)
    } yield result

  private def exactPatternMatch(phrase: Phrase, chunk: Chunk[Byte]): Boolean =
    phrase.toBytes == chunk.toList
}

object ParSearchService {
  case class Config(parallelism: Int, phraseChunkSize: Int)
}
