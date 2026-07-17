package com.pwroblew.photoed.lib

import cats.Functor
import cats.effect.Ref
import cats.syntax.functor.*

case class ImageStatus(id: String, image: Image, isShowing: Boolean, toBeShown: Boolean)

case class PhotoEdAppState[F[_]](
    history: List[String],
    commands: List[String],
    toBeContinued: Boolean,
    imagesStatus: List[ImageStatus]
)

def TO_BE_CONTINUED[F[_]: Functor](appState: Ref[F, PhotoEdAppState[F]]): F[Boolean] =
  appState.get.map(_.toBeContinued)

object PhotoEdAppState {
  private def empty[F[_]]: PhotoEdAppState[F] = PhotoEdAppState(
    history = List.empty,
    commands = List.empty,
    toBeContinued = true,
    imagesStatus = List.empty
  )
  def initialState[F[_]]: PhotoEdAppState[F]  = empty[F]
}
