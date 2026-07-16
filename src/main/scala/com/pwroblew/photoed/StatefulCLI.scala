package com.pwroblew.photoed

import cats.effect.{IO, IOApp, Ref}
import cats.implicits.catsSyntaxOptionId
import com.pwroblew.photoed.lib.*
import com.pwroblew.photoed.lib.impl_f.PhotoEdAppImpl
import com.pwroblew.photoed.lib.impl_io.{EdImageFilesImpl, EdImageViewerImpl}

object StatefulCLI extends IOApp.Simple {

  val imageFiles: EdImageFiles[IO] = EdImageFilesImpl
  val app                          = PhotoEdAppImpl[IO](imageFiles)

  override def run: IO[Unit] = for {
    appState <- IO.ref(PhotoEdAppState.initialState)
    _        <- basicAppLoop(appState).whileM_(TO_BE_CONTINUED(appState))
  } yield ()

  private def basicAppLoop(appState: Ref[IO, PhotoEdAppState]): IO[Unit] =
    for {
      _         <- nextAppStep(appState, None).whileM_(TO_BE_CONTINUED_BUT_NOT_SHOWN(appState))
      toBeShown <- appState.get.map(_.toBeShown)
      _         <- if (toBeShown)
                     for {
                       (viewer, finalizer) <- EdImageViewerImpl.makeResource("photoed").allocated
                       _                   <- nextAppStep(appState, viewer.some).whileM_(TO_BE_SHOWN(appState))
                       _                   <- finalizer
                     } yield ()
                   else IO.unit
    } yield ()

  private def nextAppStep(
      appState: Ref[IO, PhotoEdAppState],
      maybeImageViewer: Option[EdImageViewer[IO]]
  ): IO[Unit] = for {
    _ <- app.readCommand(appState)
    _ <- app.nextStep(appState, maybeImageViewer).whileM_(appState.get.map(_.commands.nonEmpty))
           .handleErrorWith(e => IO.println(e.getMessage))
  } yield ()

}
