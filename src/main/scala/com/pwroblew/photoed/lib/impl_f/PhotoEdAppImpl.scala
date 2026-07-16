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
    private val imageFiles: EdImageFiles[F]
) extends PhotoEdApp[F] {

  override def nextStep(
      appState: Ref[F, PhotoEdAppState],
      maybeImageViewer: Option[EdImageViewer[F]]
  ): F[Unit] = {

    maybeImageViewer.fold {

      given EdImageFiles[F]                                 = imageFiles
      val getAction: String => Option[EditorActionBasic[F]] =
        EditorActions.basicActionsMap[F].get

      for {
        cmdLine           <- appState.get.map(_.commands.head) // TODO consider headOption
        commandDetails     = cmdLine.trim.split("\\s+", 2).toList
        action            <- getCommandActionF(cmdLine, commandDetails, getAction)
        additionalActions <- action.actB(appState, commandDetails)
        _                 <-
          appState.update(state =>
            state.copy(commands =
              additionalActions.preActions ::: state.commands.tail ::: additionalActions.postActions
            )
          )

      } yield ()

    } { imageViewer =>
      given EdImageFiles[F]                                    = imageFiles
      val getAction: String => Option[EditorActionShowable[F]] =
        EditorActions.allActionsMap[F].get

      for {
        cmdLine           <- appState.get.map(_.commands.head) // TODO consider headOption
        commandDetails     = cmdLine.trim.split("\\s+", 2).toList
        action            <- getCommandActionF(cmdLine, commandDetails, getAction)
        additionalActions <- action.act(appState, commandDetails, imageViewer)
        _                 <-
          appState.update(state =>
            state.copy(commands =
              additionalActions.preActions ::: state.commands.tail ::: additionalActions.postActions
            )
          )
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

  override def readCommand(appState: Ref[F, PhotoEdAppState]): F[Unit] =
    for {
      _       <- Console[F].print("Please provide a command: ")
      cmdLine <- Console[F].readLine
      _       <- appState.update { state =>
                   state.copy(commands = state.commands :+ cmdLine)
                 }
    } yield ()

}

object PhotoEdAppImpl {
  def apply[F[_]: {MonadThrow, Console}](
      imageLoader: EdImageFiles[F]
  ): PhotoEdAppImpl[F] =
    new PhotoEdAppImpl[F](imageLoader)
}
