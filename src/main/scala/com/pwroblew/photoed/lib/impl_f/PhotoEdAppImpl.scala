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

final class WindowsManager[F[_]: [G[_]] =>> MonadCancel[
  G,
  Throwable
]](val windowsRefs: Ref[F, Map[String, ViewerWindow[F]]]) {

  def open(id: String, res: Resource[F, EdImageViewer[F]]): F[Unit] = {
    val result: OptionT[F, Unit] = for {
      _                 <- OptionT(windowsRefs.get.map(refs => Option.when(!refs.keySet.contains(id))(())))
      (viewer, release) <- OptionT.liftF(res.allocated[EdImageViewer[F]])
      _                 <- OptionT.liftF(windowsRefs.update(windows =>
                             windows + (id -> ViewerWindow(viewer, release))
                           ))
    } yield ()
    result.value.void
  }

  def close(id: String): F[Unit] =
    windowsRefs.modify { windows =>
      windows.get(id) match {
        case None         => windows        -> ().pure[F]
        case Some(window) => (windows - id) -> window.release
      }
    }.flatten

  def closeAll(): F[Unit] = {
    windowsRefs.modify { windows =>
      windows.empty -> windows.toList.map(_._2).traverse(_.release)
    }.flatten.void
  }

}

object WindowsManager {
  def makeResource[F[_]: Sync]: Resource[F, WindowsManager[F]] =
    Resource.make[F, WindowsManager[F]] {
      for {
        windowRef <- Ref.of[F, Map[String, ViewerWindow[F]]](Map.empty[String, ViewerWindow[F]])
      } yield new WindowsManager(windowRef)
    } { windowsManager =>
      windowsManager.closeAll()
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
      windowsManager: WindowsManager[F]
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
        OptionT.liftF(action.act(appState, commandDetails, windowsManager)
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
