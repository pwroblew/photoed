package com.pwroblew.photoed

import cats.effect.{IO, IOApp}
import com.pwroblew.photoed.lib.impl.{ImageViewerImpl, PhotoEdImpl}
import com.pwroblew.photoed.lib.{Image, PhotoAppState}

object StatefulCLI extends IOApp.Simple {

  private type AppState = PhotoAppState
  private val initialState: AppState       = PhotoAppState.initialState
  private val commandProcessingStatefulApp =
    PhotoEdImpl[IO](Image.load, IO.println, new ImageViewerImpl[IO])

  override def run: IO[Unit] = for {
    state0 <- IO.pure(initialState)
    _      <- commandLoop(state0)
  } yield ()

  private def commandLoop(appState: AppState): IO[Unit] =
    for {
      command          <- IO.print("Please provide a command: ") *> IO.readLine
      (cont, newState) <- commandProcessingStatefulApp.process(command, appState)
                            .handleErrorWith { case e: Exception =>
                              IO.println(s"${e.getMessage}. To exit type 'exit'.")
                                *> IO(true, appState)
                            }
      _                <- if cont then commandLoop(newState) else IO.unit

    } yield ()

}
