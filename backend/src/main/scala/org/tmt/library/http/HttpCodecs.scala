package org.tmt.library.http

import csw.location.api.codec.LocationCodecs
import io.bullet.borer.Codec
import io.bullet.borer.compat.AkkaHttpCompat
import io.bullet.borer.derivation.MapBasedCodecs
import org.tmt.library.models.Book

object HttpCodecs extends HttpCodecs

trait HttpCodecs extends AkkaHttpCompat with LocationCodecs {

  implicit lazy val bookCodec: Codec[Book]              = MapBasedCodecs.deriveCodec
  implicit lazy val requestCodec: Codec[LibraryRequest] = MapBasedCodecs.deriveAllCodecs
}
