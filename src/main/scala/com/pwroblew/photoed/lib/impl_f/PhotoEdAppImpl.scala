package com.pwroblew.photoed.lib.impl_f

import cats.MonadThrow
import cats.data.OptionT
import cats.effect.*
import cats.effect.kernel.MonadCancel
import cats.effect.std.{Console, Dispatcher}
import cats.syntax.all.*
import com.pwroblew.photoed
import com.pwroblew.photoed.lib.*
import com.pwroblew.photoed.lib.actions.*

final case class ViewerWindow[F[_]](
    viewer: EdImageViewer[F],
    release: F[Unit]
)

final class WindowHandle[F[_]: [G[_]] =>> MonadCancel[
  G,
  Throwable
]](val windowRef: Ref[F, Option[ViewerWindow[F]]]) {

  def open(res: Resource[F, EdImageViewer[F]]): F[Unit] = {
    val result: OptionT[F, Unit] = for {
      _                 <- OptionT(windowRef.get.map {
                             case Some(_) => None
                             case None    => Some(())
                           })
      (viewer, release) <- OptionT.liftF(res.allocated[EdImageViewer[F]])
      _                 <- OptionT.liftF(windowRef.set(ViewerWindow(viewer, release).some))
    } yield ()
    result.value.void
  }

  def close(): F[Unit] = {
    val value: F[F[Unit]] = for {
      release <- windowRef.modify {
                   case None         => None -> ().pure[F]
                   case Some(window) => None -> window.release
                 }
    } yield release
    value.flatten
  }

}

object WindowHandle {
  def makeResource[F[_]: Sync]: Resource[F, WindowHandle[F]] =
    Resource.make[F, WindowHandle[F]] {
      for {
        windowRef <- Ref.of[F, Option[ViewerWindow[F]]](Option.empty[ViewerWindow[F]])
      } yield new WindowHandle(windowRef)
    } { windowHandle =>
      windowHandle.close()
    }
}

final class PhotoEdAppImpl[F[_]: {MonadThrow, Console, Async}](
    private val imageFiles: EdImageFiles[F],
    private val makeImageWindowResource: String => Resource[F, EdImageViewer[F]]
) extends PhotoEdApp[F] {

  private val dispatcherRes: Resource[F, Dispatcher[F]] = Dispatcher.parallel[F]

  private val windowHandle: F[Ref[F, Option[ViewerWindow[F]]]] =
    Ref.of[F, Option[ViewerWindow[F]]](Option.empty)

  override def nextStep(
      appState: Ref[F, PhotoEdAppState[F]],
      windowHandle: WindowHandle[F]
  ): F[Unit] = {

    given EdImageFiles[F]                           = imageFiles
    given (String => Resource[F, EdImageViewer[F]]) = makeImageWindowResource

    val getAction: String => Option[EditorActionShowable[F]] =
      EditorActions.allActionsMap[F].get

    val result: OptionT[F, Unit] = for {
      cmdLine           <- OptionT(appState.get.map(_.commands.headOption))
      commandDetails     = cmdLine.trim.split("\\s+", 10).toList
      action            <- OptionT.liftF(getCommandActionF(cmdLine, commandDetails, getAction)
                             .onError {
                               case _ => appState.update(state => state.copy(commands = state.commands.tail))
                             })
      additionalActions <-
        OptionT.liftF(action.act(appState, commandDetails, windowHandle)
          .onError {
            case _ => appState.update(state => state.copy(commands = state.commands.tail))
          })
      _                 <- OptionT.liftF(appState.update(state => state.copy(history = state.history :+ cmdLine)))
      _                 <-
        OptionT.liftF(
          appState.update(state =>
            state.copy(commands =
              additionalActions.preActions ::: state.commands.tail ::: additionalActions.postActions
            )
          )
        )
    } yield ()
    result.value.void

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

  override def readCommand(appState: Ref[F, PhotoEdAppState[F]]): F[Unit] =
    for {
      _       <- Console[F].print("Please provide a command: ")
      cmdLine <- Console[F].readLine
      _       <- appState.update { state =>
                   state.copy(commands = state.commands :+ cmdLine)
                 }
    } yield ()

}

object PhotoEdAppImpl {
  def apply[F[_]: {MonadThrow, Console, Async}](
      imageLoader: EdImageFiles[F],
      makeImageWindowResource: String => Resource[F, EdImageViewer[F]]
  ): PhotoEdAppImpl[F] =
    new PhotoEdAppImpl[F](imageLoader, makeImageWindowResource)
}
