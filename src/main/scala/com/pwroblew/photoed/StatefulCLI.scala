package com.pwroblew.photoed

import cats.effect.{IO, IOApp, Ref, Resource}
import com.pwroblew.photoed.lib.*
import com.pwroblew.photoed.lib.impl_f.{PhotoEdAppImpl, WindowHandle}
import com.pwroblew.photoed.lib.impl_io.{EdImageFilesImpl, EdImageViewerImpl}

object StatefulCLI extends IOApp.Simple {

  val imageFiles: EdImageFiles[IO]                                       = EdImageFilesImpl
  val makeImageWindowResource: String => Resource[IO, EdImageViewer[IO]] =
    EdImageViewerImpl.makeResource
  val app                                                                = PhotoEdAppImpl[IO](imageFiles, makeImageWindowResource)

  override def run: IO[Unit] = for {
    appState                              <- IO.ref(PhotoEdAppState.initialState[IO])
    (windowHandle, windowHandleFinalizer) <- WindowHandle.makeResource[IO].allocated
    _                                     <- basicAppLoop(appState, windowHandle).whileM_(TO_BE_CONTINUED(appState))
    _                                     <- windowHandleFinalizer
  } yield ()

  private def basicAppLoop(
      appState: Ref[IO, PhotoEdAppState[IO]],
      windowHandle: WindowHandle[IO]
  ): IO[Unit] =
    for {
      _ <- app.readCommand(appState)
      _ <- app.nextStep(appState, windowHandle).whileM_(appState.get.map(_.commands.nonEmpty))
             .handleErrorWith(e => IO.println(e.getMessage))
    } yield ()

}
