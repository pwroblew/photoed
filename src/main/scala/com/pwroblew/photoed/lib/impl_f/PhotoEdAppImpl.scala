package com.pwroblew.photoed.lib.impl_f

import cats.MonadThrow
import cats.data.{OptionT, StateT}
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
  ): StateT[F, WindowsMap[F], Unit] = {

    val actionDetailsFMaybe: OptionT[F, (EditorActionShowable[F], List[String])] = for {
      cmdLine       <- OptionT(stateRef.get.map(_.commands.headOption))
      commandDetails = cmdLine.trim.split("\\s+", 10).toList
      action        <- OptionT.liftF(getCommandActionF(
                         cmdLine,
                         commandDetails,
                         EditorActions.allActionsMap[F].get
                       ))
    } yield (action, commandDetails)

    val actionDetailsF: F[(EditorActionShowable[F], List[String])] = actionDetailsFMaybe.getOrRaise(
      new RuntimeException("FATAL - can't get command for processing")
    )

    for {
      (action, commandDetails) <- StateT.liftF(actionDetailsF)
      additionalActions        <- action.act(stateRef, commandDetails, windowsManager)
                                    .handleErrorWith(_ => StateT.liftF(AdditionalActions.empty.pure[F]))
      _                        <- StateT.liftF(for {
                                    _ <- stateRef.update(state =>
                                           state.copy(
                                             history = state.history :+ commandDetails.mkString(" "),
                                             commands =
                                               additionalActions.preActions ::: state.commands.tail ::: additionalActions.postActions
                                           )
                                         )
                                  } yield ())
    } yield ()

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
