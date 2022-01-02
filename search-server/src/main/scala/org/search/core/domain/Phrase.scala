package org.search.core.domain

case class Phrase(value: String) extends AnyVal {
  def length: Int = value.length

  def toBytes: List[Byte] = value.getBytes.toList
}
