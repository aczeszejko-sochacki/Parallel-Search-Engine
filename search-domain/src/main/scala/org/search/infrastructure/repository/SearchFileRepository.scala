package org.search.infrastructure.repository

import cats.implicits._
import cats.Monad
import cats.effect.kernel.Async
import fs2.Stream
import fs2.io.file.{Files, Path}
import org.search.core.domain.{Occurrence, Phrase}
import org.search.core.services.SearchService
import org.search.infrastructure.repository.SearchFileRepository.Config

class SearchFileRepository[F[_]: Files: Monad: Async](config: Config,
                                                      searchService: SearchService[F]) {
  def searchFiles(phrase: Phrase): F[List[Occurrence]] =
    for {
      files   <- getAll
      results <- files.tail.traverse(searchFile(_, phrase))
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

object SearchFileRepository {
  case class Config(rootPath: String)
}
