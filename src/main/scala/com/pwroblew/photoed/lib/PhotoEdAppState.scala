package com.pwroblew.photoed.lib

import cats.Functor
import cats.effect.Ref
import cats.syntax.functor.*

case class PhotoEdAppState(
    history: List[String],
    commands: List[String],
    edImage: Option[EdImage],
    isShowing: Boolean,
    toBeContinued: Boolean,
    toBeShown: Boolean
)

def TO_BE_CONTINUED[F[_]: Functor](appState: Ref[F, PhotoEdAppState]): F[Boolean] =
  appState.get.map(_.toBeContinued)

def TO_BE_CONTINUED_BUT_NOT_SHOWN[F[_]: Functor](appState: Ref[F, PhotoEdAppState]): F[Boolean] =
  appState.get.map(state => state.toBeContinued && !state.toBeShown)

def TO_BE_SHOWN[F[_]: Functor](appState: Ref[F, PhotoEdAppState]): F[Boolean] =
  appState.get.map(state => state.toBeContinued && state.toBeShown)

object PhotoEdAppState {
  private val empty: PhotoEdAppState = PhotoEdAppState(
    history = List.empty,
    commands = List.empty,
    edImage = Option.empty,
    isShowing = false,
    toBeContinued = true,
    toBeShown = false
  )
  def initialState: PhotoEdAppState  = empty
}
