package com.pwroblew.photoed

import cats.effect.{IO, IOApp, Ref}
import com.pwroblew.photoed.lib.{Image, PhotoAppState}

object StatefulCLI extends IOApp.Simple {

  private type AppState = PhotoAppState
  private val initialState: IO[Ref[IO, AppState]] = PhotoAppState.initialState[IO]
  private val commandProcessingStatefulApp        = PhotoEdImpl[IO](Image.load, IO.println)

  override def run: IO[Unit] = for {
    state0 <- initialState
    _      <- commandLoop(state0)
  } yield ()

  private def commandLoop(appState: Ref[IO, AppState]): IO[Unit] =
    for {
      command       <- IO.print("Please provide a command: ") *> IO.readLine
      toBeContinued <- commandProcessingStatefulApp.process(command, appState)
                         .handleErrorWith { case e: Exception =>
                           IO.println(s"${e.getMessage}. To exit type 'exit'.") >> IO(true)
                         }
      _             <- if toBeContinued then commandLoop(appState) else IO.unit

    } yield ()

}
