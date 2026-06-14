package com.pwroblew.photoed.lib.impl_f

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.std.Console
import cats.implicits.*
import cats.syntax.all.*
import com.pwroblew.photoed
import com.pwroblew.photoed.lib.*
import com.pwroblew.photoed.lib.actions.*

final class PhotoEdAppImpl[F[_]: {MonadThrow, Console}](
    private val imageLoader: EdImageLoader[F],
    private val imageViewer: EdImageViewer[F]
) extends PhotoEdApp[F] {

  override def process(
      command: String,
      appState: PhotoEdAppState
  ): F[(Boolean, PhotoEdAppState)] = {

    val commandDetails: List[String]          = command.split(" ").toList
    val actions: Map[String, EditorAction[F]] = EditorActions.actions(imageLoader, imageViewer)

    for {
      action           <-
        OptionT.fromOption[F](commandDetails.headOption)
          .subflatMap(actions.get)
          .getOrRaise(new IllegalArgumentException(
            s"Error: Unsupported image processing command provided: \"$command\". Please provide \"exit\" to exit the app."
          ))
      (cont, newState) <- action.run(appState, commandDetails)
    } yield (cont, newState)

  }

}

object PhotoEdAppImpl {
  def apply[F[_]: {MonadThrow, Console}](
      imageLoader: EdImageLoader[F],
      imageViewer: EdImageViewer[F]
  ): PhotoEdAppImpl[F] =
    new PhotoEdAppImpl[F](imageLoader, imageViewer)
}
