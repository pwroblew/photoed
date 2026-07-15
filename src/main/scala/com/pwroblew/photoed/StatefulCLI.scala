package com.pwroblew.photoed

import cats.effect.{IO, IOApp, Ref, Resource}
import cats.implicits.catsSyntaxOptionId
import com.pwroblew.photoed.lib.impl_f.PhotoEdAppImpl
import com.pwroblew.photoed.lib.impl_io.{EdImageFilesImpl, EdImageViewerImpl}
import com.pwroblew.photoed.lib.{
  EdImageViewer,
  PhotoEdAppState,
  TO_BE_CONTINUED,
  TO_BE_CONTINUED_BUT_NOT_SHOWN,
  TO_BE_SHOWN
}

object StatefulCLI extends IOApp.Simple {

  val app = PhotoEdAppImpl[IO](EdImageFilesImpl)

  override def run: IO[Unit] = for {
    appState <- IO.ref(PhotoEdAppState.initialState)
    _        <- basicAppLoop(appState).whileM_(TO_BE_CONTINUED(appState))
  } yield ()

  private def basicAppLoop(appState: Ref[IO, PhotoEdAppState]): IO[Unit] =
    for {
      _         <- nextAppStep(appState, None).whileM_(TO_BE_CONTINUED_BUT_NOT_SHOWN(appState))
      toBeShown <- appState.get.map(_.toBeShown)
      _         <- if (toBeShown) EdImageViewerImpl.makeResource("photoed").use { viewer =>
                     nextAppStep(appState, viewer.some).whileM_(TO_BE_SHOWN(appState))
                   }
                   else IO.unit
    } yield ()

  private def nextAppStep(
      appState: Ref[IO, PhotoEdAppState],
      maybeImageViewer: Option[EdImageViewer[IO]]
  ): IO[Unit] = for {
    cmd <- app.readCommand()
    _   <- app.nextStep(cmd, appState, maybeImageViewer)
             .handleErrorWith(e => IO.println(e.getMessage))
  } yield ()

}
