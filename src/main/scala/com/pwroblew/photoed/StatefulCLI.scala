package com.pwroblew.photoed

import cats.effect.{IO, IOApp, Ref, Resource}
import com.pwroblew.photoed.lib.*
import com.pwroblew.photoed.lib.impl_f.{PhotoEdAppImpl, WindowsManager}
import com.pwroblew.photoed.lib.impl_io.{EdImageFilesImpl, EdImageViewerImpl}

object StatefulCLI extends IOApp.Simple {

  val imageFiles: EdImageFiles[IO]                                       = EdImageFilesImpl
  val makeImageWindowResource: String => Resource[IO, EdImageViewer[IO]] =
    EdImageViewerImpl.makeResource
  val app                                                                = PhotoEdAppImpl[IO](imageFiles, makeImageWindowResource)

  override def run: IO[Unit] = for {
    appState                              <- IO.ref(PhotoEdAppState.initialState[IO])
    (windowHandle, windowHandleFinalizer) <- WindowsManager.makeResource[IO].allocated
    _                                     <- basicAppLoop(appState, windowHandle).whileM_(TO_BE_CONTINUED(appState))
    _                                     <- windowHandleFinalizer
  } yield ()

  private def basicAppLoop(
                            appState: Ref[IO, PhotoEdAppState[IO]],
                            windowsManager: WindowsManager[IO]
  ): IO[Unit] =
    for {
      _ <- app.readCommand(appState)
      _ <- app.nextStep(appState, windowsManager).whileM_(appState.get.map(_.commands.nonEmpty))
             .handleErrorWith(e => IO.println(e.getMessage))
    } yield ()

}
