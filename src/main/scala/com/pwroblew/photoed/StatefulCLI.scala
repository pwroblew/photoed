package com.pwroblew.photoed

import cats.effect.{IO, IOApp, Ref, Resource}
import com.pwroblew.photoed.lib.*
import com.pwroblew.photoed.lib.actions.ActionKeyword
import com.pwroblew.photoed.lib.impl_f.{PhotoEdAppImpl, WindowsManager}
import com.pwroblew.photoed.lib.impl_io.{ImageFileMgmntImpl, ImageWindowImpl}

object StatefulCLI extends IOApp.Simple {

  given ImageFileMgmnt[IO]                        = ImageFileMgmntImpl
  given (String => Resource[IO, ImageWindow[IO]]) = ImageWindowImpl.makeResource

  val app = PhotoEdAppImpl[IO]

  override def run: IO[Unit] = for {
    appStateRef                             <- IO.ref(PhotoEdAppState.initialState[IO])
    (windowManager, windowManagerFinalizer) <- WindowsManager.makeResource[IO].allocated
    _                                       <- basicAppLoop(appStateRef, windowManager).whileM_(TO_BE_CONTINUED(appStateRef))
    _                                       <- windowManagerFinalizer
  } yield ()

  private def basicAppLoop(
      stateRef: Ref[IO, PhotoEdAppState[IO]],
      windowsManager: WindowsManager[IO]
  ): IO[Unit] =
    for {
      _ <- app.readCommand(stateRef)
      _ <- app.nextStep(stateRef, windowsManager).whileM_(stateRef.get.map(_.commands.nonEmpty))
             .handleErrorWith(e => IO.println(e.getMessage))
    } yield ()

}
