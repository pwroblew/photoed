package com.pwroblew.photoed.lib

import cats.{Functor, Monad}
import cats.data.StateT
import cats.effect.Ref
import cats.syntax.functor.*
import com.pwroblew.photoed.lib.impl_f.WindowsMap

case class ImageStatus(id: String, image: Image)

case class PhotoEdAppState[F[_]](
    history: List[String],
    commands: List[String],
    toBeContinued: Boolean,
    imagesStatuses: List[ImageStatus]
)

def TO_BE_CONTINUED[F[_]: Monad](appState: Ref[F, PhotoEdAppState[F]])
    : StateT[F, WindowsMap[F], Boolean] =
  StateT.liftF(appState.get.map(_.toBeContinued))

object PhotoEdAppState {
  private def empty[F[_]]: PhotoEdAppState[F] = PhotoEdAppState(
    history = List.empty,
    commands = List.empty,
    toBeContinued = true,
    imagesStatuses = List.empty
  )
  def initialState[F[_]]: PhotoEdAppState[F]  = empty[F]
}
