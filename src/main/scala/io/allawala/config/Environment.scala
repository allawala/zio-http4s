package io.allawala.config

import enumeratum.{CirceEnum, Enum, EnumEntry}
import enumeratum.EnumEntry.Lowercase

sealed trait Environment extends EnumEntry with Lowercase

case object Environment extends Enum[Environment] with CirceEnum[Environment] {

  case object Local      extends Environment
  case object Dev        extends Environment
  case object Staging    extends Environment
  case object Production extends Environment

  val values: IndexedSeq[Environment] = findValues
}
