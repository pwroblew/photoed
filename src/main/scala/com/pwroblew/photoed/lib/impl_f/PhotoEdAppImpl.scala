package com.pwroblew.photoed.lib.impl_f

import cats.{MonadThrow, Show}
import cats.data.{OptionT, StateT}
import cats.effect.*
import cats.effect.std.Console
import cats.syntax.all.*
import com.pwroblew.photoed
import com.pwroblew.photoed.StatefulCLI.MakeImageWindowResource
import com.pwroblew.photoed.lib.*
import com.pwroblew.photoed.lib.actions.*

import java.nio.charset.Charset

class IndentedConsole[H[_]: Console] extends Console[H] {
  private val underlying: Console[H] = Console[H]
  private val indent: String         = " " * 4
  private val prefix: String         = s">>$indent"

  private def addLinePrefix[A: Show](a: A): String = {
    val strA: String = Show[A].show(a).replace("\n", s"\n$prefix")
    s"$prefix$strA"
  }

  override def readLineWithCharset(charset: Charset): H[String] = underlying.readLineWithCharset(charset)

  override def print[A: Show](a: A): H[Unit] = underlying.print(addLinePrefix(a))

  override def println[A: Show](a: A): H[Unit] = underlying.println(addLinePrefix(a))

  override def error[A: Show](a: A): H[Unit] = underlying.error(addLinePrefix(a))

  override def errorln[A: Show](a: A): H[Unit] = underlying.errorln(addLinePrefix(a))
}

final class PhotoEdAppImpl[F[_]: {MonadThrow, Console, Async}](using
    imageFileMgmnt: ImageFileMgmnt[F],
    makeImageWindowRes: MakeImageWindowResource[F]
) extends PhotoEdApp[F] {

  override def nextStep(
      stateRef: Ref[F, PhotoEdAppState[F]],
      windowsManager: WindowsManager[F]
  ): StateT[F, WindowsMap[F], Unit] = {

    val actionDetailsFMaybe: OptionT[F, (EditorActionShowable[F], List[String])] = for {
      cmdLine       <- OptionT(stateRef.get.map(_.commands.headOption))
      commandDetails = cmdLine.trim.split("\\s+", 10).toList
      action        <- OptionT.liftF(getCommandActionF(commandDetails, EditorActions.allActionsMap[F].get)
                         .onError(_ => stateRef.update(state => state.copy(commands = state.commands.tail))))
    } yield (action, commandDetails)

    val actionDetailsF: F[(EditorActionShowable[F], List[String])] = actionDetailsFMaybe.getOrRaise(
      new RuntimeException("FATAL - can't get command for processing")
    )

    for {
      (action, commandDetails) <- StateT.liftF(actionDetailsF)
      additionalActions        <- action.processCmd(stateRef, commandDetails, windowsManager)
                                    .handleErrorWith { e =>
                                      StateT.liftF(
                                        Console[F].println(s"An error encountered. Details: ${e.getMessage}")
                                          >> AdditionalActions.empty.pure[F]
                                      )
                                    }
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
      commandDetails: List[String],
      getAction: ActionKeyword => Option[EdAction[F]]
  ): F[EdAction[F]] = {
    val maybeCommand: Option[String] = commandDetails.headOption
    OptionT.fromOption[F](maybeCommand.flatMap(ActionKeyword.fromCmd))
      .subflatMap(getAction)
      .getOrRaise(new IllegalArgumentException(
        s"Error: Unsupported image processing command provided: \"${maybeCommand.getOrElse("")}\".\nPlease type \"help\" or \"exit\"."
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
      makeImageWindowResource: MakeImageWindowResource[F]
  ): PhotoEdAppImpl[F] = {
    given Console[F] = new IndentedConsole[F]
    new PhotoEdAppImpl[F]
  }
}
