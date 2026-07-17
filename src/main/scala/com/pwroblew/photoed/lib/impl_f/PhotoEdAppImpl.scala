package com.pwroblew.photoed.lib.impl_f

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.*
import cats.effect.std.{Console, Dispatcher}
import cats.syntax.all.*
import com.pwroblew.photoed
import com.pwroblew.photoed.lib.*
import com.pwroblew.photoed.lib.actions.*

final class PhotoEdAppImpl[F[_]: {MonadThrow, Console, Async}](using
    imageFileMgmnt: ImageFileMgmnt[F],
    makeImageWindowRes: String => Resource[F, ImageWindow[F]]
) extends PhotoEdApp[F] {

  private val dispatcherRes: Resource[F, Dispatcher[F]] = Dispatcher.parallel[F]

  override def nextStep(
      stateRef: Ref[F, PhotoEdAppState[F]],
      windowsManager: WindowsManager[F]
  ): F[Unit] = {

    val getAction: ActionKeyword => Option[EditorActionShowable[F]] =
      EditorActions.allActionsMap[F].get

    val computation: OptionT[F, Unit] = for {
      cmdLine           <- OptionT(stateRef.get.map(_.commands.headOption))
      commandDetails     = cmdLine.trim.split("\\s+", 10).toList
      action            <- OptionT.liftF(getCommandActionF(cmdLine, commandDetails, getAction))
      additionalActions <- OptionT.liftF(action.act(stateRef, commandDetails, windowsManager))
      _                 <- OptionT.liftF(stateRef.update(state => state.copy(history = state.history :+ cmdLine)))
      _                 <-
        OptionT.liftF(stateRef.update(state =>
          state.copy(commands =
            additionalActions.preActions ::: state.commands.tail ::: additionalActions.postActions
          )
        ))
    } yield ()

    computation.value.onError {
      case _ => stateRef.update(state => state.copy(commands = state.commands.tail))
    }.void

  }

  private def getCommandActionF[EdAction[_[_]]](
      command: String,
      commandDetails: List[String],
      getAction: ActionKeyword => Option[EdAction[F]]
  ): F[EdAction[F]] = {
    OptionT.fromOption[F](commandDetails.headOption.flatMap(ActionKeyword.fromCmd))
      .subflatMap(getAction)
      .getOrRaise(new IllegalArgumentException(
        s"Error: Unsupported image processing command provided: \"$command\". Please provide \"exit\" to exit the app."
      ))
  }

  override def readCommand(stateRef: Ref[F, PhotoEdAppState[F]]): F[Unit] =
    for {
      _       <- Console[F].print("Please provide a command: ")
      cmdLine <- Console[F].readLine
      _       <- stateRef.update { state =>
                   state.copy(commands = state.commands :+ cmdLine)
                 }
    } yield ()

}

object PhotoEdAppImpl {
  def apply[F[_]: {MonadThrow, Console, Async}](using
      imageLoader: ImageFileMgmnt[F],
      makeImageWindowResource: String => Resource[F, ImageWindow[F]]
  ): PhotoEdAppImpl[F] = new PhotoEdAppImpl[F]
}
