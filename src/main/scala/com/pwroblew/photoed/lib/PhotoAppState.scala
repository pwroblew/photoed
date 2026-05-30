package com.pwroblew.photoed.lib

import cats.effect.{Ref, Sync}

case class PhotoAppState(
    imageDesc: Option[String],
    image: Option[Image]
)

object PhotoAppState {
  private val empty: PhotoAppState                   = PhotoAppState(
    imageDesc = Option.empty,
    image = Option.empty
  )
  def initialState[F[_]: Sync]: F[Ref[F, PhotoAppState]] = Ref.of[F, PhotoAppState](empty)
}
