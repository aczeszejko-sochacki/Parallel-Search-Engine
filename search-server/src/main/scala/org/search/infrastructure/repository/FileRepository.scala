package org.search.infrastructure.repository

import cats.implicits._
import cats.Monad
import cats.effect.kernel.{Async, Concurrent}
import fs2.io.file.{Files, Path}
import fs2.Stream
import org.search.core.domain.{Occurrence, Phrase}
import org.search.core.services.SearchService
import org.search.infrastructure.repository.FileRepository.Config

class FileRepository[F[_]: Files: Monad: Async](config: Config, searchService: SearchService[F]) {

  def parSearchFiles(phrase: Phrase, parCompareEnabled: Boolean): F[List[Occurrence]] =
    for {
      files   <- getAll
      results <- Concurrent[F].parTraverseN(files.length - 1)(files.init)(parSearchFile(_, phrase, parCompareEnabled))
    } yield results.flatten

  def parSearchFile(file: Path, phrase: Phrase, parCompareEnabled: Boolean): F[List[Occurrence]] =
    readFile(file)
      .through(
        if (parCompareEnabled) searchService.parSearchPhrase(file, phrase)
        else                   searchService.parSearchPhraseParCompare(file, phrase)
      )
      .compile
      .toList

  def searchFiles(phrase: Phrase): F[List[Occurrence]] =
    for {
      files   <- getAll
      results <- files.init.traverse(searchFile(_, phrase))
    } yield results.flatten

  def searchFile(file: Path, phrase: Phrase): F[List[Occurrence]] =
    readFile(file)
      .through(searchService.searchPhrase(file, phrase))
      .compile
      .toList

  def getAll: F[List[Path]] =
    Files[F].walk(Path(config.rootPath)).compile.toList

  def readFile(path: Path): Stream[F, Byte] =
    Files[F].readAll(path)
}

object FileRepository {
  case class Config(rootPath: String)
}
