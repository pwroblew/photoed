package com.pwroblew.photoed

import cats.effect.{IO, IOApp}
import com.pwroblew.photoed.lib.PhotoEdAppState
import com.pwroblew.photoed.lib.impl_f.PhotoEdAppImpl
import com.pwroblew.photoed.lib.impl_io.{EdImageFilesImpl, EdImageViewerImpl}

object StatefulCLI extends IOApp.Simple {

  private type AppState = PhotoEdAppState
  private val initialState: AppState       = PhotoEdAppState.initialState
  private val commandProcessingStatefulApp =
    PhotoEdAppImpl[IO](EdImageFilesImpl, EdImageViewerImpl)

  override def run: IO[Unit] = for {
    state0 <- IO.pure(initialState)
    _      <- commandLoop(state0)
  } yield ()

  private def commandLoop(appState: AppState): IO[Unit] =
    for {
      command          <- IO.print("Please provide a command: ") *> IO.readLine
      (cont, newState) <- commandProcessingStatefulApp.process(command, appState)
                            .handleErrorWith { e =>
                              IO.println(e.getMessage) *> IO.pure((true, appState))
                            }
      _                <- if !cont then IO.unit else commandLoop(newState)

    } yield ()

}
