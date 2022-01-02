package org.search.core.domain

import fs2.io.file.Path

case class Occurrence(file: Path, offset: Int)
