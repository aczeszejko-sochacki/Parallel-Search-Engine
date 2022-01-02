package org.search.infrastructure.http.protocol

import org.http4s.QueryParamDecoder
import org.http4s.dsl.io.{OptionalQueryParamDecoderMatcher, QueryParamDecoderMatcher}
import org.search.core.domain.Phrase

trait QueryParamDecoders {
  implicit val phraseQueryParamDecoder: QueryParamDecoder[Phrase] =
    QueryParamDecoder[String].map(Phrase)

  object PhraseQueryParamMatcher extends QueryParamDecoderMatcher[Phrase]("phrase")

  object OptParCompareQueryParamMatcher extends OptionalQueryParamDecoderMatcher[Boolean]("parCompareEnabled")
}
