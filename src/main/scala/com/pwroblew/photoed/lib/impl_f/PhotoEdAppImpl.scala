package com.pwroblew.photoed.lib.impl_f

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.{IO, Ref, Resource}
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed
import com.pwroblew.photoed.lib.*
import com.pwroblew.photoed.lib.actions.*

final class PhotoEdAppImpl[F[_]: {MonadThrow, Console}](
    private val imageLoader: EdImageFiles[F]
) extends PhotoEdApp[F] {

  override def basicStep(
      command: String,
      appState: Ref[F, PhotoEdAppState]
  ): F[Unit] = {

    val commandDetails: List[String]               = command.trim.split("\\s+", 2).toList
    val actions: Map[String, EditorActionBasic[F]] = EditorActions.basicActions(imageLoader)

    for {
      action <-
        OptionT.fromOption[F](commandDetails.headOption)
          .subflatMap(actions.get)
          .getOrRaise(new IllegalArgumentException(
            s"Error: Unsupported image processing command provided: \"$command\". Please provide \"exit\" to exit the app."
          ))
      _      <- action.runB(appState, commandDetails)
    } yield ()

  }

  override def showingStep(
      command: String,
      appState: Ref[F, PhotoEdAppState],
      imageViewer: EdImageViewer[F]
  ): F[Unit] = {

    val commandDetails: List[String]                  = command.trim.split("\\s+", 2).toList
    val actions: Map[String, EditorActionShowable[F]] = EditorActions.allActions(imageLoader)

    for {
      action <-
        OptionT.fromOption[F](commandDetails.headOption)
          .subflatMap(actions.get)
          .getOrRaise(new IllegalArgumentException(
            s"Error: Unsupported image processing command provided: \"$command\". Please provide \"exit\" to exit the app."
          ))
      _      <- action.run(appState, commandDetails, imageViewer)
    } yield ()

  }

  override def readCommand(): F[String] =
    Console[F].print("Please provide a command: ") >> Console[F].readLine
}

object PhotoEdAppImpl {
  def apply[F[_]: {MonadThrow, Console}](
      imageLoader: EdImageFiles[F]
  ): PhotoEdAppImpl[F] =
    new PhotoEdAppImpl[F](imageLoader)
}
