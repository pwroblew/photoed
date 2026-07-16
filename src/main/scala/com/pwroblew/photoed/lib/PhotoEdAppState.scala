package com.pwroblew.photoed.lib

import cats.Functor
import cats.effect.Ref
import cats.syntax.functor.*

case class PhotoEdAppState[F[_]](
    history: List[String],
    commands: List[String],
    toBeContinued: Boolean,
    edImage: Option[EdImage],
    imageId: Option[String],
    isShowing: Boolean,
    toBeShown: Boolean
)

def TO_BE_CONTINUED[F[_]: Functor](appState: Ref[F, PhotoEdAppState[F]]): F[Boolean] =
  appState.get.map(_.toBeContinued)

def TO_BE_CONTINUED_BUT_NOT_SHOWN[F[_]: Functor](appState: Ref[F, PhotoEdAppState[F]]): F[Boolean] =
  appState.get.map(state => state.toBeContinued && !state.toBeShown)

def TO_BE_SHOWN[F[_]: Functor](appState: Ref[F, PhotoEdAppState[F]]): F[Boolean] =
  appState.get.map(state => state.toBeContinued && state.toBeShown)

object PhotoEdAppState {
  private def empty[F[_]]: PhotoEdAppState[F] = PhotoEdAppState(
    history = List.empty,
    commands = List.empty,
    toBeContinued = true,
    edImage = Option.empty,
    imageId = Option.empty,
    isShowing = false,
    toBeShown = false
  )
  def initialState[F[_]]: PhotoEdAppState[F]  = empty[F]
}
