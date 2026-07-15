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

  override def nextStep(
      command: String,
      appState: Ref[F, PhotoEdAppState],
      maybeImageViewer: Option[EdImageViewer[F]]
  ): F[Unit] = {

    val commandDetails: List[String] = command.trim.split("\\s+", 2).toList

    maybeImageViewer.fold {

      val getAction: String => Option[EditorActionBasic[F]] =
        EditorActions.basicActions(imageLoader).get
      for {
        action <- getCommandActionF(command, commandDetails, getAction)
        _      <- action.runB(appState, commandDetails)
      } yield ()

    } { imageViewer =>
      val getAction: String => Option[EditorActionShowable[F]] =
        EditorActions.allActions(imageLoader).get
      for {
        action <- getCommandActionF(command, commandDetails, getAction)
        _      <- action.run(appState, commandDetails, imageViewer)
      } yield ()
    }

  }

  private def getCommandActionF[EdAction[_[_]]](
      command: String,
      commandDetails: List[String],
      getAction: String => Option[EdAction[F]]
  ): F[EdAction[F]] = {
    OptionT.fromOption[F](commandDetails.headOption)
      .subflatMap(getAction)
      .getOrRaise(new IllegalArgumentException(
        s"Error: Unsupported image processing command provided: \"$command\". Please provide \"exit\" to exit the app."
      ))
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
