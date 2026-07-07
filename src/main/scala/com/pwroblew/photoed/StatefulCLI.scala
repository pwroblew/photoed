package com.pwroblew.photoed

import cats.effect.{IO, IOApp, Ref, Resource}
import cats.implicits.catsSyntaxApplicativeId
import com.pwroblew.photoed.lib.impl_f.PhotoEdAppImpl
import com.pwroblew.photoed.lib.impl_io.{EdImageFilesImpl, EdImageViewerImpl}
import com.pwroblew.photoed.lib.{EdImageViewer, PhotoEdAppState}

object StatefulCLI extends IOApp.Simple {

  val app = PhotoEdAppImpl[IO](EdImageFilesImpl)

  override def run: IO[Unit] = for {
    appState <- IO.ref(PhotoEdAppState.initialState)
    _        <- basicAppLoop(appState)
                  .whileM_(appState.get.map(_.toBeContinued))
  } yield ()

  private def basicAppLoop(appState: Ref[IO, PhotoEdAppState]): IO[Unit] =
    for {
      _          <- basicAppStep(appState)
                      .whileM_(appState.get.map(state => state.toBeContinued && !state.toBeShowed))
      toBeShowed <- appState.get.map(_.toBeShowed)
      _          <- if (toBeShowed)
                      EdImageViewerImpl.makeResource("photoed").use(showingAppLoop(appState, _))
                    else IO.unit
    } yield ()

  private def basicAppStep(appState: Ref[IO, PhotoEdAppState]): IO[Unit] =
    for {
      command <- app.readCommand()
      _       <- app.basicStep(command, appState).handleErrorWith(e => IO.println(e.getMessage))
    } yield ()

  private def showingAppLoop(
      appState: Ref[IO, PhotoEdAppState],
      imageViewer: EdImageViewer[IO]
  ): IO[Unit] = {
    showingAppStep(appState, imageViewer)
      .whileM_(appState.get.map(state => state.toBeContinued || state.toBeShowed))
  }

  private def showingAppStep(
      appState: Ref[IO, PhotoEdAppState],
      imageViewer: EdImageViewer[IO]
  ): IO[Unit] = for {
    command <- app.readCommand()
    _       <- app.showingStep(command, appState, imageViewer)
                 .handleErrorWith(e => IO.println(e.getMessage))
  } yield ()

}
