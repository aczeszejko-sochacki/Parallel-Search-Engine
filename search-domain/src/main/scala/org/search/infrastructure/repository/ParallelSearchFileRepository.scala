package org.search.infrastructure.repository

import cats.implicits._
import cats.Monad
import cats.effect.kernel.{Async, Concurrent}
import fs2.io.file.{Files, Path}
import fs2.Stream
import org.search.core.domain.{Occurrence, Phrase}
import org.search.core.services.ParSearchService
import org.search.infrastructure.repository.ParallelSearchFileRepository.Config

class ParallelSearchFileRepository[F[_]: Files: Monad: Async](config: Config,
                                                              parSearchService: ParSearchService[F]) {

  def parSearchFiles(phrase: Phrase, parCompareEnabled: Boolean): F[List[Occurrence]] =
    for {
      files   <- getAll
      results <- Concurrent[F].parTraverseN(files.length - 1)(files.init)(parSearchFile(_, phrase, parCompareEnabled))
    } yield results.flatten

  def parSearchFile(file: Path, phrase: Phrase, parCompareEnabled: Boolean): F[List[Occurrence]] =
    readFile(file)
      .through(
        if (parCompareEnabled) parSearchService.parSearchPhrase(file, phrase)
        else                   parSearchService.parSearchPhraseParCompare(file, phrase)
      )
      .compile
      .toList

  def getAll: F[List[Path]] =
    Files[F].walk(Path(config.rootPath)).compile.toList

  def readFile(path: Path): Stream[F, Byte] =
    Files[F].readAll(path)
}

object ParallelSearchFileRepository {
  case class Config(rootPath: String)
}
